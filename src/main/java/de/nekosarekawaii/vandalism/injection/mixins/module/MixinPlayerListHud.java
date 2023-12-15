package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.BetterTabListModule;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.RenderUtil;
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

@Mixin(value = PlayerListHud.class, priority = 9999)
public abstract class MixinPlayerListHud implements MinecraftWrapper {

    @Shadow
    public abstract Text getPlayerName(final PlayerListEntry entry);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2))
    private void cancelRenderFill(final DrawContext instance, final int x1, final int y1, final int x2, final int y2, final int color) {
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private int cancelDrawTextWithShadow(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color) {
        return x;
    }

    @ModifyConstant(constant = @Constant(longValue = 80L), method = "collectPlayerEntries")
    private long hookBetterTabListModule(final long count) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        return betterTabListModule.isActive() ? betterTabListModule.tabSize.getValue() : count;
    }

    @Unique
    private static final float vandalism$SCALE = 0.5f;

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 0)
    private int hookBetterTabListModule(final int width) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        return betterTabListModule.isActive() && betterTabListModule.moreInfo.getValue() ? (int) (width + (vandalism$SCALE * 30)) : width;
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void hookBetterTabListModule(final DrawContext context, final int width, final int x, final int y, final PlayerListEntry entry, final CallbackInfo ci) {
        final int a = this.mc.isInSingleplayer() || (this.mc.getNetworkHandler() != null && this.mc.getNetworkHandler().getConnection().isEncrypted()) ? 9 : 0, w = x + a;
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleManager().getBetterTabListModule();
        final int color;
        if (betterTabListModule.isActive() && betterTabListModule.highlightSelf.getValue() && this.mc.player != null && entry.getProfile().getId().equals(this.mc.player.getGameProfile().getId())) {
            color = betterTabListModule.selfColor.getValue().getRGB();
        } else {
            color = this.mc.options.getTextBackgroundColor(0x20FFFFFF);
        }
        final boolean moreInfo = betterTabListModule.isActive() && betterTabListModule.moreInfo.getValue();
        context.fill(w, y, w + width - a, y + (moreInfo ? 9 : 8), color);
        context.drawTextWithShadow(this.mc.textRenderer, this.getPlayerName(entry), w, y, entry.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
        if (moreInfo) {
            final int pingY = (int) (y / vandalism$SCALE);
            final int infoX = (int) (x / vandalism$SCALE) + (int) (width / vandalism$SCALE);
            final int latency = entry.getLatency();
            final int gameModeId = entry.getGameMode().getId();
            final int gameModeY = (int) ((y + (this.mc.textRenderer.fontHeight / 2f)) / vandalism$SCALE);
            final double pingPercent = Math.min((float) latency / betterTabListModule.highPing.getValue(), 1.0f);
            final Color lowPingColor = betterTabListModule.lowPingColor.getValue();
            final Color averagePingColor = betterTabListModule.averagePingColor.getValue();
            final Color highPingColor = betterTabListModule.highPingColor.getValue();
            final Color pingColor = RenderUtil.interpolateColor(lowPingColor, averagePingColor, highPingColor, pingPercent);
            final String ping = latency + " ms", gameMode = gameModeId + " gm";
            context.getMatrices().push();
            context.getMatrices().scale(vandalism$SCALE, vandalism$SCALE, 1.0f);
            context.drawTextWithShadow(this.mc.textRenderer, ping, infoX - this.mc.textRenderer.getWidth(ping), pingY, pingColor.getRGB());
            context.drawTextWithShadow(this.mc.textRenderer, gameMode, infoX - this.mc.textRenderer.getWidth(gameMode), gameModeY, betterTabListModule.getColorFromGameMode(gameModeId));
            context.getMatrices().pop();
            ci.cancel();
        }
    }

}
