package de.vandalismdevelopment.vandalism.injection.mixins.feature.module;

import de.vandalismdevelopment.vandalism.util.minecraft.impl.player.TimerUtil;
import net.minecraft.client.render.RenderTickCounter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public abstract class MixinRenderTickCounter {

    @Shadow
    public float lastFrameDuration;

    /**
     * This injection modifies the Timer speed by injecting at the point where time is calculated.
     *
     * @param timeMillis The time in milliseconds.
     * @param info       The mixin callback info.
     */

    @Inject(
            method = "beginRenderTick",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J",
                    opcode = Opcodes.PUTFIELD)
    )
    private void vandalism$beginRenderTick(final long timeMillis, final CallbackInfoReturnable<Integer> info) {
        lastFrameDuration *= TimerUtil.getSpeed();
    }
}
