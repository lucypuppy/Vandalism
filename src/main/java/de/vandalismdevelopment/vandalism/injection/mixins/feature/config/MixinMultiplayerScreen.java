package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.enhancedserverlist.ServerList;
import de.vandalismdevelopment.vandalism.enhancedserverlist.gui.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    @Shadow
    protected abstract void refresh();

    protected MixinMultiplayerScreen(final Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void vandalism$enhancedServerListAddConfigButton(final CallbackInfo ci) {
        if (!Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.enhancedServerList.getValue()) {
            return;
        }
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Server Lists"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ConfigScreen((MultiplayerScreen) (Object) this));
            }
        }).dimensions(2, 4, 100, 20).build());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void vandalism$enhancedServerListSyncServerList(final CallbackInfo ci) {
        if (Vandalism.getInstance().getServerListManager().hasBeenChanged()) {
            this.refresh();
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"))
    private void vandalism$enhancedServerListModifyTitle(final Args args) {
        if (!Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.enhancedServerList.getValue()) {
            return;
        }
        final ServerList selectedServerList = Vandalism.getInstance().getServerListManager().getSelectedServerList();
        final MutableText title = Text.literal(selectedServerList.isDefault() ? ServerList.DEFAULT_SERVER_LIST_NAME : selectedServerList.getName());
        title.append(" (" + selectedServerList.getSize() + ") | ");
        title.append((Text) args.get(1));
        args.set(1, title);
    }

}
