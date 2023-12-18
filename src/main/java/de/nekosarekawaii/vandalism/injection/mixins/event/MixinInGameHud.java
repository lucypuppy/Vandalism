package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow
    @Final
    private DebugHud debugHud;

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;F)V", at = @At(value = "TAIL"))
    private void callRender2DListener(final DrawContext context, final float tickDelta, final CallbackInfo ci) {
        if (this.debugHud.shouldShowDebugHud()) { // We never want to render anything when this is true, so /shrug
            return;
        }
        Vandalism.getInstance().getEventSystem().postInternal(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, tickDelta));
    }

}
