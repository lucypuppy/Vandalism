package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.gui.widgets.DropDownWidget;
import de.nekosarekawaii.foxglove.util.CustomServerList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    @Shadow
    private ServerList serverList;

    @Shadow
    @Final
    private Screen parent;

    @Unique
    private final String[] serverLists = new String[]{"TestServers", "Grief", "Crash"};

    @Unique
    private final ButtonWidget[] buttons = new ButtonWidget[serverLists.length + 1];

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
        final ButtonWidget defaultBtn = ButtonWidget.builder(Text.literal("Default"), button -> {
            Foxglove.getInstance().setSelectedServerList(null);
            MinecraftClient.getInstance().setScreen(new MultiplayerScreen(parent));
        }).size(0, 13).build();

        if (Foxglove.getInstance().getSelectedServerList() == null)
            defaultBtn.active = false;

        buttons[0] = defaultBtn;

        for (int i = 0; i < serverLists.length; i++) {
            final String list = serverLists[i];
            final ButtonWidget btn = ButtonWidget.builder(Text.literal(list), button -> {
                Foxglove.getInstance().setSelectedServerList(new CustomServerList(list));
                MinecraftClient.getInstance().setScreen(new MultiplayerScreen(parent));
            }).size(0, 13).build();

            if (Foxglove.getInstance().getSelectedServerList() != null &&
                    Foxglove.getInstance().getSelectedServerList().getName().equalsIgnoreCase(list))
                btn.active = false;

            buttons[i + 1] = btn;
        }

        addDrawableChild(new DropDownWidget(5, 5, 65, 16, Text.literal("Server List"), this, buttons));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (final ButtonWidget buttonWidget : buttons) {
            if (buttonWidget.mouseClicked(mouseX, mouseY, button))
                return false;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

}
