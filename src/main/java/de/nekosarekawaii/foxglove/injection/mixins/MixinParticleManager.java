package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.feature.impl.module.impl.render.VisualThrottleModule;
import de.nekosarekawaii.foxglove.util.ParticleTracker;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {

    @Redirect(method = "createParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleFactory;createParticle(Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;"))
    private @Nullable Particle redirectCreateParticle(
            final ParticleFactory<ParticleEffect> particleFactory,
            final ParticleEffect parameters,
            final ClientWorld world,
            final double x,
            final double y,
            final double z,
            final double velocityX,
            final double velocityY,
            final double velocityZ
    ) {
        final VisualThrottleModule visualThrottleModule = Foxglove.getInstance().getModuleRegistry().getVisualThrottleModule();
        if (visualThrottleModule.isEnabled() && visualThrottleModule.blockTooManyParticles.getValue()) {
            final String particleId = parameters.asString();
            if (visualThrottleModule.particleTrackerMap.containsKey(particleId)) {
                final ParticleTracker particleTracker = visualThrottleModule.particleTrackerMap.get(particleId);
                particleTracker.increaseCount();
                if (particleTracker.getCount() > visualThrottleModule.countToBlockParticles.getValue()) {
                    return null; //TODO: Make NullPointerException crash fix! ~ NekosAreKawaii
                }
            } else visualThrottleModule.particleTrackerMap.put(particleId, new ParticleTracker(particleId));
        }
        return particleFactory.createParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectTick(final CallbackInfo ci) {
        final VisualThrottleModule visualThrottleModule = Foxglove.getInstance().getModuleRegistry().getVisualThrottleModule();
        if (visualThrottleModule.isEnabled() && visualThrottleModule.blockTooManyParticles.getValue()) {
            for (final ParticleTracker particleTracker : visualThrottleModule.particleTrackerMap.values()) {
                if (particleTracker.getTimer().hasReached(visualThrottleModule.particleBlockingCountResetDelay.getValue(), true)) {
                    particleTracker.resetCount();
                }
            }
        }
    }

    @Inject(method = "clearParticles", at = @At("HEAD"))
    private void injectClearParticles(final CallbackInfo ci) {
        Foxglove.getInstance().getModuleRegistry().getVisualThrottleModule().particleTrackerMap.clear();
    }

}
