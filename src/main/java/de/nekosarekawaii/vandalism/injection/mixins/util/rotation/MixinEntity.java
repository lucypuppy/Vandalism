package de.nekosarekawaii.vandalism.injection.mixins.util.rotation;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.minecraft.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = -1)
public abstract class MixinEntity implements MinecraftWrapper {

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void injectGetRotationVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (this.mc.player == (Object) this) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();

            // Checking if we have rotations and arent raytracing with the client
            if (rotation != null && !WorldUtil.doingRaytrace)
                cir.setReturnValue(rotation.getVector());
        }
    }

}