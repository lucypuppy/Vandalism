/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.VisualThrottleModule;
import de.nekosarekawaii.vandalism.util.ParticleTracker;
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
    private @Nullable Particle hookVisualThrottle(final ParticleFactory<ParticleEffect> particleFactory, final ParticleEffect parameters, final ClientWorld world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockTooManyParticles.getValue()) {
            final String particleId = parameters.asString();
            if (visualThrottleModule.particleTrackerMap.containsKey(particleId)) {
                final ParticleTracker particleTracker = visualThrottleModule.particleTrackerMap.get(particleId);
                particleTracker.increaseCount();
                if (particleTracker.getCount() > visualThrottleModule.countToBlockParticles.getValue()) {
                    return null;
                }
            } else visualThrottleModule.particleTrackerMap.put(particleId, new ParticleTracker(particleId));
        }
        return particleFactory.createParticle(parameters, world, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void hookVisualThrottle(final CallbackInfo ci) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockTooManyParticles.getValue()) {
            for (final ParticleTracker particleTracker : visualThrottleModule.particleTrackerMap.values()) {
                if (particleTracker.getTimer().hasReached(visualThrottleModule.particleBlockingCountResetDelay.getValue(), true)) {
                    particleTracker.resetCount();
                }
            }
        }
    }

    @Inject(method = "clearParticles", at = @At("HEAD"))
    private void hookVisualThrottle_Clear(final CallbackInfo ci) {
        Vandalism.getInstance().getModuleManager().getVisualThrottleModule().particleTrackerMap.clear();
    }

}
