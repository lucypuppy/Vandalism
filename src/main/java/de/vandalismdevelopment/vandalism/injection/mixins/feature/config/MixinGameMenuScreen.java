package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.ServerUtil;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen implements MinecraftWrapper {

    @Shadow
    @Final
    private static Text SEND_FEEDBACK_TEXT;

    @Shadow
    @Final
    private static Text REPORT_BUGS_TEXT;

    protected MixinGameMenuScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "createUrlButton", at = @At(value = "HEAD"), cancellable = true)
    private void vandalism$addMoreButtons(final Text text, final String url, final CallbackInfoReturnable<ButtonWidget> cir) {
        if (!Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.replaceGameMenuScreenButtons.getValue()) {
            return;
        }
        if (text == SEND_FEEDBACK_TEXT) {
            cir.setReturnValue(ButtonWidget.builder(Text.translatable("menu.multiplayer"), b -> this.setScreen(new MultiplayerScreen(this))).width(98).build());
        } else if (text == REPORT_BUGS_TEXT && !this.mc().isInSingleplayer()) {
            final ButtonWidget button = ButtonWidget.builder(Text.literal("Reconnect"), b -> ServerUtil.connectToLastServer()).width(98).build();
            cir.setReturnValue(button);
        }
    }

}
