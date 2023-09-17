package de.foxglovedevelopment.foxglove.util.click.clickers;

import de.foxglovedevelopment.foxglove.util.click.ClickGenerator;
import de.foxglovedevelopment.foxglove.util.timer.impl.ms.MsTimer;
import org.apache.commons.lang3.RandomUtils;

public class MSTimerClicker extends ClickGenerator {

    private final MsTimer timer;
    private long nextClick = -1, minDelay, maxDelay;
    private int highs, lows;
    private boolean fastClicks;

    public MSTimerClicker() {
        this.timer = new MsTimer();
    }

    @Override
    public void update() {
        if (this.minDelay == 0 && this.maxDelay == 0) { //We dont need to calculate the delay if its 0
            this.clickAction.run();
        } else {
            if (this.nextClick == -1) //Generate random for the first click when client is started
                this.nextClick = RandomUtils.nextLong(minDelay, maxDelay);

            if (this.timer.hasReached(this.nextClick, true)) {
                this.nextClick = RandomUtils.nextLong(minDelay, maxDelay);
                this.clickAction.run();
            }
        }
    }

    //Todo stammina
    /*private void calculateNewDelay() {
        if (fastClicks) {
            this.nextClick = RandomUtils.nextLong(minDelay, maxDelay / 2);
        } else {
            this.nextClick = RandomUtils.nextLong(minDelay, maxDelay);
        }

        final float minDiff = Math.abs(nextClick - minDelay);
        final float maxDiff = Math.abs(nextClick - maxDelay);

        if (maxDiff > minDiff) {
            highs++;
        } else {
            lows++;
        }

        final int clicks = highs + lows;
        final float highPercent = (float) highs / clicks * 100;
        final float lowPercent = (float) lows / clicks * 100;

        if (clicks > 30) {
            //ChatUtils.chatMessage(highPercent + " " + lowPercent + " " + fastClicks);
            fastClicks = lowPercent > highPercent;
            highs = 0;
            lows = 0;
        }
    }*/

    public void setMinDelay(final long minDelay) {
        this.minDelay = minDelay;
    }

    public void setMaxDelay(final long maxDelay) {
        this.maxDelay = maxDelay;
    }

}
