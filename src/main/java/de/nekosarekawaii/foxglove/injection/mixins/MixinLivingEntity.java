package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
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
        if ((Object) this == MinecraftClient.getInstance().player &&
                Foxglove.getInstance().getRotationListener().getFixedRotation() != null) {
            return Foxglove.getInstance().getRotationListener().getFixedRotation().getYaw();
        }

        return instance.getYaw();
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    private float redirectTurnHead(LivingEntity instance) {
        if ((Object) this == MinecraftClient.getInstance().player &&
                Foxglove.getInstance().getRotationListener().getFixedRotation() != null) {
            return Foxglove.getInstance().getRotationListener().getFixedRotation().getYaw();
        }

        return instance.getYaw();
    }

}
