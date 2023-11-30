package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.BetterTabListModule;
import de.vandalismdevelopment.vandalism.util.RenderUtil;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

//TODO: Improve this mixin because it contains code which is from the game itself and this could corrupt the game in the future.
@Mixin(value = PlayerListHud.class, priority = 9999)
public abstract class MixinPlayerListHud implements MinecraftWrapper {

    @Shadow
    public abstract Text getPlayerName(final PlayerListEntry entry);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2))
    private void vandalism$cancelRenderFill(final DrawContext instance, final int x1, final int y1, final int x2, final int y2, final int color) {
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private int vandalism$cancelDrawTextWithShadow(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color) {
        return x;
    }

    @ModifyConstant(constant = @Constant(longValue = 80L), method = "collectPlayerEntries")
    private long vandalism$betterTabListTabSize(final long count) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleRegistry().getBetterTabListModule();
        return betterTabListModule.isEnabled() ? betterTabListModule.tabSize.getValue() : count;
    }

    @Unique
    private final static float SCALE = 0.5f;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 0)
    private int vandalism$betterTabListMoreInfoScale(final int width) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleRegistry().getBetterTabListModule();
        return betterTabListModule.isEnabled() && betterTabListModule.moreInfo.getValue() ? (int) (width + (SCALE * 30)) : width;
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void vandalism$betterTabListApplyAdditionalInfo(final DrawContext context, final int width, final int x, final int y, final PlayerListEntry entry, final CallbackInfo ci) {
        final int a = this.mc().isInSingleplayer() || (this.networkHandler() != null && this.networkHandler().getConnection().isEncrypted()) ? 9 : 0, w = x + a;
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleRegistry().getBetterTabListModule();
        final int color;
        if (betterTabListModule.isEnabled() && betterTabListModule.highlightSelf.getValue() && this.player() != null && entry.getProfile().getId().equals(this.player().getGameProfile().getId())) {
            color = betterTabListModule.selfColor.getValue().getRGB();
        } else {
            color = this.options().getTextBackgroundColor(0x20FFFFFF);
        }
        final boolean moreInfo = betterTabListModule.isEnabled() && betterTabListModule.moreInfo.getValue();
        context.fill(w, y, w + width - a, y + (moreInfo ? 9 : 8), color);
        context.drawTextWithShadow(this.textRenderer(), this.getPlayerName(entry), w, y, entry.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
        if (moreInfo) {
            final int pingY = (int) (y / SCALE);
            final int infoX = (int) (x / SCALE) + (int) (width / SCALE);
            final int latency = entry.getLatency();
            final int gameModeId = entry.getGameMode().getId();
            final int gameModeY = (int) ((y + (this.textRenderer().fontHeight / 2f)) / SCALE);
            final double pingPercent = Math.min((float) latency / betterTabListModule.highPing.getValue(), 1.0f);
            final Color lowPingColor = betterTabListModule.lowPingColor.getValue();
            final Color averagePingColor = betterTabListModule.averagePingColor.getValue();
            final Color highPingColor = betterTabListModule.highPingColor.getValue();
            final int pingColor = RenderUtil.interpolateColor(lowPingColor, averagePingColor, highPingColor, pingPercent);
            final String ping = latency + " ms", gameMode = gameModeId + " gm";
            context.getMatrices().push();
            context.getMatrices().scale(SCALE, SCALE, 1.0f);
            context.drawTextWithShadow(this.textRenderer(), ping, infoX - this.textRenderer().getWidth(ping), pingY, pingColor);
            context.drawTextWithShadow(this.textRenderer(), gameMode, infoX - this.textRenderer().getWidth(gameMode), gameModeY, betterTabListModule.getColorFromGameMode(gameModeId));
            context.getMatrices().pop();
            ci.cancel();
        }
    }

}
