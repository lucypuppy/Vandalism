package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.util.CustomServerList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    @Shadow
    private ServerList serverList;

    @Shadow @Final private Screen parent;

    protected MixinMultiplayerScreen(final Text title) {
        super(title);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;loadFile()V"))
    private void redirectInit(final ServerList instance) {
        if (Foxglove.getInstance().getSelectedServerList() != null) {
            serverList = Foxglove.getInstance().getSelectedServerList();
            serverList.loadFile();
            title = Text.literal(Foxglove.getInstance().getSelectedServerList().getName() + " | ").append(title);
            return;
        }

        title = Text.literal("Default | ").append(title);
        instance.loadFile();
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void addButtons(final CallbackInfo ci) {
        final ButtonWidget.Builder defaultList = ButtonWidget.builder(Text.literal("Default Server List"), button -> {
            Foxglove.getInstance().setSelectedServerList(null);
            MinecraftClient.getInstance().setScreen(new MultiplayerScreen(parent));
        }).position(5, 5).size(98, 20);

        final ButtonWidget.Builder furryList = ButtonWidget.builder(Text.literal("Furry Server List"), button -> {
            Foxglove.getInstance().setSelectedServerList(new CustomServerList("Furry"));
            MinecraftClient.getInstance().setScreen(new MultiplayerScreen(parent));
        }).position(103, 5).size(98, 20);

        this.addDrawableChild(defaultList.build());
        this.addDrawableChild(furryList.build());
    }

}
