package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.hud;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.BetterTabListModule;
import me.nekosarekawaii.foxglove.util.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {

    @Shadow
    public abstract Text getPlayerName(final PlayerListEntry entry);

    @ModifyConstant(constant = @Constant(longValue = 80L), method = "collectPlayerEntries")
    private long modifyCount(final long count) {
        final BetterTabListModule betterTabListModule = Foxglove.getInstance().getModuleRegistry().getBetterTabListModule();
        return betterTabListModule.isEnabled() ? betterTabListModule.tabSize.getValue() : count;
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void injectGetPlayerName(final PlayerListEntry entry, final CallbackInfoReturnable<Text> cir) {
        final BetterTabListModule betterTabListModule = Foxglove.getInstance().getModuleRegistry().getBetterTabListModule();
        if (betterTabListModule.isEnabled() && betterTabListModule.gamemode.getValue()) {
            final int gameModeId = entry.getGameMode().getId();
            Formatting gameModeFormatting;
            switch (gameModeId) {
                case 0 -> gameModeFormatting = Formatting.DARK_GREEN;
                case 1 -> gameModeFormatting = Formatting.RED;
                case 2 -> gameModeFormatting = Formatting.GREEN;
                case 3 -> gameModeFormatting = Formatting.DARK_RED;
                default -> gameModeFormatting = Formatting.WHITE;
            }
            final MutableText mutableText = Text.literal(
                    Formatting.DARK_GRAY +
                            "[" + gameModeFormatting + gameModeId + Formatting.DARK_GRAY + "] " +
                            Formatting.RESET
            );
            cir.setReturnValue(mutableText.append(cir.getReturnValue()));
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 0)
    private int injectRenderWidth(final int width) {
        final BetterTabListModule betterTabListModule = Foxglove.getInstance().getModuleRegistry().getBetterTabListModule();
        return betterTabListModule.isEnabled() && betterTabListModule.accurateLatency.getValue() ? (int) (width + (betterTabListModule.pingScale.getValue() * 30)) : width;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2))
    private void redirectEntryRect(final DrawContext instance, final int x1, final int y1, final int x2, final int y2, final int color) {
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private int redirectEntryRect(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color) {
        return x;
    }

    private final Color[] pingColors = new Color[]{
            Color.GREEN,
            Color.YELLOW,
            Color.ORANGE,
            Color.RED
    };

    private final float[] pingSteps = new float[]{
            0.0f, 0.34f, 0.66f, 1.0f
    };

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void injectRenderLatencyIcon(final DrawContext context, final int width, final int x, final int y, final PlayerListEntry entry, final CallbackInfo ci) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        final int a = mc.isInSingleplayer() || (networkHandler != null && networkHandler.getConnection().isEncrypted()) ? 9 : 0, w = x + a;
        final BetterTabListModule betterTabListModule = Foxglove.getInstance().getModuleRegistry().getBetterTabListModule();
        int color = mc.options.getTextBackgroundColor(0x20FFFFFF);

        if (betterTabListModule.isEnabled() && betterTabListModule.self.getValue() && mc.player != null && entry.getProfile().getId().equals(mc.player.getGameProfile().getId())) {
            color = betterTabListModule.selfColor.getValue().getRGB();
        }

        context.fill(w, y, w + width - a, y + 8, color);

        final TextRenderer textRenderer = mc.textRenderer;
        context.drawTextWithShadow(textRenderer, this.getPlayerName(entry), w, y, entry.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);

        if (betterTabListModule.isEnabled() && betterTabListModule.accurateLatency.getValue()) {
            final float scale = betterTabListModule.pingScale.getValue();
            final int latency = entry.getLatency();
            final String text = latency + " ms";

            context.getMatrices().push();
            context.getMatrices().translate(w + width, y, 0.0);
            context.getMatrices().scale(scale, scale, 1.0f);

            context.drawTextWithShadow(
                    textRenderer,
                    text,
                    -textRenderer.getWidth(text),
                    0,
                    ColorUtils.interpolate(Math.min((float) latency / betterTabListModule.highPing.getValue(), 1.0f), pingColors, pingSteps)
            );

            context.getMatrices().pop();

            ci.cancel();
        }
    }

}
