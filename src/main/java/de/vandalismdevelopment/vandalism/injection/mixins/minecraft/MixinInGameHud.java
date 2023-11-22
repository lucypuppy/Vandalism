package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.BetterTabListModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    @Final
    private DebugHud debugHud;

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;F)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", ordinal = 3))
    private void onRender(final DrawContext context, final float tickDelta, final CallbackInfo ci) {
        if (this.debugHud.shouldShowDebugHud()) return;
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(RenderListener.Render2DEvent.ID, new RenderListener.Render2DEvent(context, tickDelta));
        context.getMatrices().pop();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    public boolean redirectIsPressed(final KeyBinding instance) {
        final BetterTabListModule betterTabListModule = Vandalism.getInstance().getModuleRegistry().getBetterTabListModule();
        if (betterTabListModule.isEnabled() && betterTabListModule.toggleable.getValue()) {
            return betterTabListModule.toggleState;
        }
        return instance.isPressed();
    }

    @Redirect(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private void redirectChatHudClear(final ChatHud instance, final boolean clearHistory) {
        if (Vandalism.getInstance().getConfigManager().getMainConfig().chatCategory.dontClearChatHistory.getValue()) return;
        instance.clear(clearHistory);
    }

    @Inject(method = "renderOverlay", at = @At(value = "HEAD"), cancellable = true)
    public void injectRender(final DrawContext context, final Identifier texture, final float opacity, final CallbackInfo ci) {
        final MainConfig config = Vandalism.getInstance().getConfigManager().getMainConfig();

        if (texture.getPath().equals("textures/misc/pumpkinblur.png") && !config.visualCategory.pumpkinOverlay.getValue())
            ci.cancel();

        if (texture.getPath().equals("textures/misc/powder_snow_outline.png") && !config.visualCategory.freezeOverlay.getValue())
            ci.cancel();
    }

    @Inject(method = "renderSpyglassOverlay", at = @At(value = "HEAD"), cancellable = true)
    public void injectRenderSpyglassOverlay(final DrawContext context, final float scale, final CallbackInfo ci) {
        if (!Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.spyGlassOverlay.getValue()) ci.cancel();
    }

}
