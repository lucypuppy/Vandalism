package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
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
    private void vandalism$callInGameRender2DEvent(final DrawContext context, final float tickDelta, final CallbackInfo ci) {
        if (this.debugHud.shouldShowDebugHud()) return;
        context.getMatrices().push();
        DietrichEvents2.global().postInternal(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, tickDelta));
        context.getMatrices().pop();
    }

}