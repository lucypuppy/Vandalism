package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F", ordinal = 1)))
    private float redirectTick(LivingEntity instance) {
        if (MinecraftClient.getInstance().player == ((LivingEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getYaw();
            }
        }
        return instance.getYaw();
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float redirectTurnHead(final LivingEntity instance) {
        if (MinecraftClient.getInstance().player == ((LivingEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getYaw();
            }
        }
        return instance.getYaw();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float redirectTravel(final LivingEntity instance) {
        if (MinecraftClient.getInstance().player == ((LivingEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getPitch();
            }
        }
        return instance.getPitch();
    }

}
