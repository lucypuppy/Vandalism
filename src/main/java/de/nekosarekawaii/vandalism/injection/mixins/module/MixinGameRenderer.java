package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.util.minecraft.WorldUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getPos()Lnet/minecraft/util/math/Vec3d;")))
    public double hookReach(Vec3d instance, Vec3d vec) {
        if (WorldUtil.raytraceRange != -1.0)
            return Math.pow(instance.distanceTo(vec) / WorldUtil.raytraceRange * 3.0, 2);

        return instance.squaredDistanceTo(vec);
    }

}
