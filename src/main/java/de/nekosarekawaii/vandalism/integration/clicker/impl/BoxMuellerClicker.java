package de.nekosarekawaii.vandalism.integration.clicker.impl;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.BoxMullerTransform;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.florianmichael.rclasses.pattern.evicting.EvictingList;
import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BoxMuellerClicker extends Clicker {

    private int delay;
    private float mean;
    private float std;
    private float cps;
    private final MSTimer msTimer = new MSTimer();
    private float partialDelays;
    private float cpsUpdatePossibility;
    private final EvictingList<Pair<Integer, Integer>> delayHistory = new EvictingList<>(new ArrayList<>(), 100);

    @Override
    public void onUpdate() {
        if (!this.msTimer.hasReached(this.delay, true)) {
            this.clickAction.accept(false);
            return;
        }

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        if (RandomUtils.randomInt(0, 100) <= this.cpsUpdatePossibility || this.cps < 3) {
            this.cps = BoxMullerTransform.distribution(random, 1, 20, this.mean, this.std);
        }

        final float delay = 1000.0f / this.cps;
        this.delay = (int) Math.floor(delay + this.partialDelays);
        this.partialDelays += delay - this.delay;

        this.delayHistory.add(new Pair<>(this.delay, (int) this.cps));
        this.clickAction.accept(true);
    }

    public void setMean(final float mean) {
        this.mean = mean;
    }

    public void setStd(final float std) {
        this.std = std;
    }

    public void setCpsUpdatePossibility(final float possibility) {
        this.cpsUpdatePossibility = possibility;
    }

    public EvictingList<Pair<Integer, Integer>> getDelayHistory() {
        return delayHistory;
    }

}