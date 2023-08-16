package de.nekosarekawaii.foxglove.util.timer.impl.ms;

import de.nekosarekawaii.foxglove.util.timer.Timer;

public class MsTimer implements Timer {

    private long millis;

    public MsTimer() {
        this.reset();
    }

    public boolean hasReached(final long delay) {
        return this.hasReached(delay, false);
    }

    public boolean hasReached(final long delay, final boolean reset) {
        final boolean reached = System.currentTimeMillis() - delay >= this.millis;
        if (reached && reset) this.reset();
        return reached;
    }

    @Override
    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getMillis() {
        return this.millis;
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }

}
