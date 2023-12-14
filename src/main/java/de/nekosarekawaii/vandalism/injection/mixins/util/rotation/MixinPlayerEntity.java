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
    private float vandalism$modifyRotationYaw(final PlayerEntity instance) {
        if (this.mc.player == ((PlayerEntity) (Object) this)) {
            if (Vandalism.getInstance().getRotationListener().getRotation() != null) {
                return Vandalism.getInstance().getRotationListener().getRotation().getYaw();
            }
        }
        return instance.getYaw();
    }

}