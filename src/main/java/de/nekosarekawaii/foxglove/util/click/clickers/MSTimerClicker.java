package de.nekosarekawaii.foxglove.util.click.clickers;

import de.nekosarekawaii.foxglove.util.click.ClickGenerator;
import de.nekosarekawaii.foxglove.util.timer.impl.ms.MsTimer;
import org.apache.commons.lang3.RandomUtils;

public class MSTimerClicker extends ClickGenerator {

    private final MsTimer timer;
    private long nextClick = -1, minDelay, maxDelay;

    public MSTimerClicker(final Runnable clickAction) {
        super(clickAction);

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

    public void setMinDelay(final long minDelay) {
        this.minDelay = minDelay;
    }

    public void setMaxDelay(final long maxDelay) {
        this.maxDelay = maxDelay;
    }

}
