package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.hud;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.BetterTabModule;
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
        final BetterTabModule betterTabModule = Foxglove.getInstance().getModuleRegistry().getBetterTabModule();
        return betterTabModule.isEnabled() ? betterTabModule.tabSize.getValue() : count;
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void injectGetPlayerName(final PlayerListEntry entry, final CallbackInfoReturnable<Text> cir) {
        final BetterTabModule betterTabModule = Foxglove.getInstance().getModuleRegistry().getBetterTabModule();
        if (betterTabModule.isEnabled() && betterTabModule.gamemode.getValue()) {
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
        final BetterTabModule betterTabModule = Foxglove.getInstance().getModuleRegistry().getBetterTabModule();
        return betterTabModule.isEnabled() && betterTabModule.accurateLatency.getValue() ? (int) (width + (betterTabModule.pingScale.getValue() * 30)) : width;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2))
    private void redirectEntryRect(final DrawContext instance, final int x1, final int y1, final int x2, final int y2, final int color) {
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private int redirectEntryRect(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int x, final int y, final int color) {
        return x;
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void injectRenderLatencyIcon(final DrawContext context, final int width, final int x, final int y, final PlayerListEntry entry, final CallbackInfo ci) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        final int a = mc.isInSingleplayer() || (networkHandler != null && networkHandler.getConnection().isEncrypted()) ? 9 : 0, w = x + a;
        final BetterTabModule betterTabModule = Foxglove.getInstance().getModuleRegistry().getBetterTabModule();
        int color = mc.options.getTextBackgroundColor(0x20FFFFFF);
        if (betterTabModule.isEnabled() && betterTabModule.self.getValue() && mc.player != null && entry.getProfile().getId().equals(mc.player.getGameProfile().getId())) {
            color = betterTabModule.selfColor.getValue().getRGB();
        }
        context.fill(w, y, w + width - a, y + 8, color);
        final TextRenderer textRenderer = mc.textRenderer;
        context.drawTextWithShadow(textRenderer, this.getPlayerName(entry), w, y, entry.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
        if (betterTabModule.isEnabled() && betterTabModule.accurateLatency.getValue()) {
            final float scale = betterTabModule.pingScale.getValue();
            final int latency = entry.getLatency();
            final String text = latency + " ms";
            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, scale);
            context.drawTextWithShadow(
                    textRenderer,
                    text,
                    (int) (x / scale) + (int) (width / scale) - textRenderer.getWidth(text),
                    (int) (y / scale),
                    ColorUtils.mixColors(
                            Color.RED,
                            Color.YELLOW,
                            Color.GREEN,
                            Math.min((float) latency / betterTabModule.highPing.getValue(), 1.0f)
                    ).getRGB()
            );
            context.getMatrices().pop();
            ci.cancel();
        }
    }

}
