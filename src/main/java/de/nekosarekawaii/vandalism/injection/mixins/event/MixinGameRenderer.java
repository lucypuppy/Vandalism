package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.RotationListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements MinecraftWrapper {

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V", shift = At.Shift.AFTER))
    private void callRotationListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(RotationListener.RotationEvent.ID, new RotationListener.RotationEvent());
    }

}
