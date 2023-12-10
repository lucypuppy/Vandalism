package de.vandalismdevelopment.vandalism.injection.mixins.util.rotation;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import de.vandalismdevelopment.vandalism.integration.rotation.Rotation;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class MixinEntity implements MinecraftWrapper {

    @ModifyVariable(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float vandalism$modifyRotationYaw(final float yaw) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return yaw;
    }

    @ModifyVariable(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float vandalism$modifyRotationPitch(final float pitch) {
        if (this.mc.player == ((Entity) (Object) this)) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getPitch();
        }
        return pitch;
    }

}