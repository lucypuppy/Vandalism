package de.vandalismdevelopment.vandalism.util.minecraft.impl;

import de.florianmichael.rclasses.math.integration.MSTimer;

public class ParticleTracker {

    private final String particleId;
    private final MSTimer timer;
    private int count;

    public ParticleTracker(final String particleId) {
        this.particleId = particleId;
        this.timer = new MSTimer();
        this.count = 1;
    }

    public String getParticleId() {
        return this.particleId;
    }

    public void increaseCount() {
        this.count++;
    }

    public void resetCount() {
        this.count = 1;
    }

    public MSTimer getTimer() {
        return this.timer;
    }

    public int getCount() {
        return this.count;
    }

}
