package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.gui.hud;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.Render2DListener;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.BetterTabListModule;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud implements MinecraftWrapper {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/gui/DrawContext;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectRender(final DrawContext context, float tickDelta, CallbackInfo ci, final Window window) {
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, tickDelta, window));
        context.getMatrices().pop();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    public boolean redirectIsPressed(final KeyBinding instance) {
        final BetterTabListModule betterTabListModule = Foxglove.getInstance().getModuleRegistry().getBetterTabListModule();
        if (betterTabListModule.isEnabled() && betterTabListModule.toggleable.getValue()) {
            return betterTabListModule.toggleState;
        }
        return instance.isPressed();
    }

}
