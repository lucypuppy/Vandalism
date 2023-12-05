package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @Inject(method = "protocolVersionMatches", at = @At(value = "RETURN"), cancellable = true)
    private void vandalism$forceProtocolVersionMatches(final CallbackInfoReturnable<Boolean> cir) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.multiplayerScreenServerInformation.getValue()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private final static String VANDALISM_VERSION_TEXT = Formatting.GOLD + Formatting.BOLD.toString() + "Version" + Formatting.DARK_GRAY + Formatting.BOLD + "> " + Formatting.GRAY;

    @Unique
    private final static String VANDALISM_PROTOCOL_TEXT = Formatting.DARK_AQUA + Formatting.BOLD.toString() + "Protocol" + Formatting.DARK_GRAY + Formatting.BOLD + "> " + Formatting.AQUA;

    @Unique
    private final static String VANDALISM_INCOMPATIBLE_PROTOCOL_TEXT = Formatting.DARK_GRAY + Formatting.BOLD.toString() + "(" + Formatting.RED + Formatting.BOLD + "Incompatible Protocol!" + Formatting.DARK_GRAY + Formatting.BOLD + ")";

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"))
    private int vandalism$applyAdditionalServerInformation(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color, final boolean shadow) {
        instance.drawText(textRenderer, text, x, y, color, shadow);
        if (Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.multiplayerScreenServerInformation.getValue()) {
            final int textX = x + textRenderer.getWidth(text) + 22;
            String versionName = this.server.version.getString();
            final int maxServerVersionLength = Vandalism.getInstance().getConfigManager().getMainConfig().menuCategory.maxServerVersionLength.getValue();
            if (versionName.length() > maxServerVersionLength) {
                versionName = versionName.substring(0, maxServerVersionLength);
            }
            instance.drawTextWithShadow(textRenderer, VANDALISM_VERSION_TEXT + versionName, textX, y, -1);
            instance.drawTextWithShadow(textRenderer, VANDALISM_PROTOCOL_TEXT + this.server.protocolVersion, textX, y + textRenderer.fontHeight, -1);
            if (this.server.protocolVersion != ProtocolHack.getTargetVersion().getVersion()) {
                instance.drawTextWithShadow(textRenderer, VANDALISM_INCOMPATIBLE_PROTOCOL_TEXT, textX, y + (textRenderer.fontHeight * 2), -1);
            }
        }
        return x;
    }

}
