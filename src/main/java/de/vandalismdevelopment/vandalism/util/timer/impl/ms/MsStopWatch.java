package de.vandalismdevelopment.vandalism.util.timer.impl.ms;

import de.vandalismdevelopment.vandalism.util.timer.Timer;

public class MsStopWatch implements Timer {

    private long startTime, elapsedTime;
    private boolean running = false;

    public MsStopWatch() {
        this.reset();
    }

    public void start() {
        if (this.running) return;
        this.running = true;
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;
        this.elapsedTime = System.currentTimeMillis() - this.startTime;
    }

    @Override
    public void reset() {
        this.startTime = this.elapsedTime = 0L;
        this.running = false;
    }

    @Override
    public long getElapsedTime() {
        return this.running ? System.currentTimeMillis() - this.startTime : this.elapsedTime;
    }

}
