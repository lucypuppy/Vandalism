package de.nekosarekawaii.vandalism.injection.mixins.util.rotation;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements MinecraftWrapper {

    @Redirect(method = "tickNewAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float modifyRotationYaw(final PlayerEntity instance) {
        if (this.mc.player == (Object) this) {
            final var rotation = Vandalism.getInstance().getRotationListener().getRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return instance.getYaw();
    }

}