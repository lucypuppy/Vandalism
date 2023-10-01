package de.vandalismdevelopment.vandalism.util;

import de.vandalismdevelopment.vandalism.util.timer.impl.ms.MsTimer;

public class ParticleTracker {

    private final String particleId;
    private final MsTimer timer;
    private int count;

    public ParticleTracker(final String particleId) {
        this.particleId = particleId;
        this.timer = new MsTimer();
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

    public MsTimer getTimer() {
        return this.timer;
    }

    public int getCount() {
        return this.count;
    }

}
