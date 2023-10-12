package de.vandalismdevelopment.vandalism.util.click.clickers;

import de.florianmichael.rclasses.math.MathUtils;
import de.florianmichael.rclasses.pattern.evicting.EvictingList;
import de.vandalismdevelopment.vandalism.util.MathUtil;
import de.vandalismdevelopment.vandalism.util.click.ClickGenerator;
import de.vandalismdevelopment.vandalism.util.timer.impl.ms.MsTimer;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BoxMuellerClicker extends ClickGenerator {

    private final MsTimer timer;
    private long nextClick;
    private int cps, updatePossibility;
    private float mean, std;
    private final EvictingList<Pair<Long, Integer>> delays;

    public BoxMuellerClicker() {
        this.timer = new MsTimer();
        this.delays = new EvictingList<>(new ArrayList<>(), 100);
        this.cps = RandomUtils.nextInt(8, 14);
        this.nextClick = MathUtil.cpsToMs(this.cps);
    }

    //TODO: Fix tick bug (cps is not accurate)
    @Override
    public void update() {
        if (this.timer.hasReached(this.nextClick, true)) {
            if (this.updatePossibility == 100 || (Math.random() * 100) >= (100 - this.updatePossibility)) {
                this.cps = (int) MathUtils.boxMuellerDistribution(ThreadLocalRandom.current(), 1, 20, this.mean, this.std);
            }

            this.nextClick = MathUtil.cpsToMs(this.cps);
            this.delays.add(new Pair<>(this.nextClick, this.cps));

            this.clickAction.run();
        }
    }

    public void setMean(final float mean) {
        this.mean = mean;
    }

    public void setStd(final float std) {
        this.std = std;
    }

    public void setUpdatePossibility(final int updatePossibility) {
        this.updatePossibility = updatePossibility;
    }

    public EvictingList<Pair<Long, Integer>> getDelays() {
        return delays;
    }

}
