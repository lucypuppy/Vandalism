package de.vandalismdevelopment.vandalism.injection.mixins.util.rotation;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements MinecraftWrapper {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F", ordinal = 1)))
    private float vandalism$modifyRotationYaw(final LivingEntity instance) {
        if (this.player() == ((LivingEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getYaw();
            }
        }
        return instance.getYaw();
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float vandalism$modifyRotationHeadYaw(final LivingEntity instance) {
        if (this.player() == ((LivingEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getYaw();
            }
        }
        return instance.getYaw();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float vandalism$modifyRotationPitch(final LivingEntity instance) {
        if (this.player() == ((LivingEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getPitch();
            }
        }
        return instance.getPitch();
    }

}
