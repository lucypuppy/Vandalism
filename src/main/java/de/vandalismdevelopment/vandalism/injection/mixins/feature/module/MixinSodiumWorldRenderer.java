package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.VisualThrottleModule;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me/jellysquid/mods/sodium/client/render/SodiumWorldRenderer", remap = false)
public abstract class MixinSodiumWorldRenderer {

    @Inject(method = "isEntityVisible", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/SodiumWorldRenderer;isBoxVisible(DDDDDD)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void vandalism$visualThrottleFixSodiumCrash(final Entity entity, final CallbackInfoReturnable<Boolean> cir) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleRegistry().getVisualThrottleModule();
        if (visualThrottleModule.isEnabled()) {
            if (entity.getVisibilityBoundingBox().getAverageSideLength() > visualThrottleModule.minSodiumEntityAverageSideLength.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

}
