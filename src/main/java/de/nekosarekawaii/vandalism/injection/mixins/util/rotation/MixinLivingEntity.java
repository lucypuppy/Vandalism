package de.nekosarekawaii.vandalism.injection.mixins.util.rotation;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements MinecraftWrapper {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F", ordinal = 1)))
    private float modifyRotationYaw(final LivingEntity instance) {
        if (this.mc.player == (Object) this) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return instance.getYaw();
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float modifyRotationHeadYaw(final LivingEntity instance) {
        if (this.mc.player == (Object) this) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return instance.getYaw();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float modifyRotationPitch(final LivingEntity instance) {
        if (this.mc.player == (Object) this) {
            final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getPitch();
        }
        return instance.getPitch();
    }

}
