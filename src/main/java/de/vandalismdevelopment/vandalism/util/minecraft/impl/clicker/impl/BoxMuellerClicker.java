package de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.impl;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.BoxMullerTransform;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.florianmichael.rclasses.pattern.evicting.EvictingList;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.clicker.Clicker;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BoxMuellerClicker extends Clicker {

    private final MSTimer timer;
    private long nextClick;
    private int cps, updatePossibility;
    private float mean, std;
    private final EvictingList<Pair<Long, Integer>> delays;

    public BoxMuellerClicker() {
        this.timer = new MSTimer();
        this.delays = new EvictingList<>(new ArrayList<>(), 100);
        this.cps = RandomUtils.randomInt(8, 14);
        this.nextClick = this.cpsToMs(this.cps);
    }

    @Override
    public void update() {
        if (this.timer.hasReached(this.nextClick, true)) {
            if (this.updatePossibility == 100 || (Math.random() * 100) >= (100 - this.updatePossibility)) {
                this.cps = (int) BoxMullerTransform.distribution(ThreadLocalRandom.current(), 1, 20, this.mean, this.std);
            }
            this.nextClick = this.cpsToMs(this.cps);
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
