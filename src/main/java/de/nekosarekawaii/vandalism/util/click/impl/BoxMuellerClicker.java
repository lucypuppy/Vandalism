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

package de.nekosarekawaii.vandalism.util.click.impl;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.Arithmetics;
import de.florianmichael.rclasses.math.timer.MSTimer;
import de.florianmichael.rclasses.pattern.evicting.EvictingList;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.KillAuraModule;
import de.nekosarekawaii.vandalism.util.click.Clicker;
import de.nekosarekawaii.vandalism.util.common.MathUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BoxMuellerClicker extends Clicker {

    private KillAuraModule killAuraModule;

    private int delay;
    private float mean;
    private float std;
    private float cps;
    private int clicks;
    private int minCps;
    private int maxCps;
    private final MSTimer msTimer = new MSTimer();
    private float partialDelays;
    private float cpsUpdatePossibility;
    private final EvictingList<Vector4d> cpsHistory = new EvictingList<>(new ArrayList<>(), 200);

    @Override
    public void onUpdate() {
        if (this.killAuraModule != null) {
            if (!this.killAuraModule.getPreHit().getValue()) {
                if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) {
                    this.clickAction.accept(false);
                    this.clicks = 0;
                    return;
                }

                final EntityHitResult entityHitResult = (EntityHitResult) mc.crosshairTarget;
                if (entityHitResult.getEntity().distanceTo(mc.player) > killAuraModule.getAimRange()) {
                    this.clickAction.accept(false);
                    this.clicks = 0;
                    return;
                }
            } else {
                if (killAuraModule.getTarget() == null) {
                    this.clickAction.accept(false);
                    this.clicks = 0;
                    return;
                }
            }
        }

        final int extra = RandomUtils.randomInt(-1, 1);
        while (this.clicks + extra > 0) {
            this.clickAction.accept(true);
            this.clicks--;
        }
    }


    @Override
    public void onRotate() {
        if (this.msTimer.hasReached(this.delay, true)) {
            this.clickAction.accept(false);

            if (RandomUtils.randomInt(0, 100) <= this.cpsUpdatePossibility || this.cps < this.minCps) {
                int depth = 0;

                while (true) {
                    final double gaussian = ThreadLocalRandom.current().nextGaussian(this.mean, this.std);
                    final double gaussianPercentage = MathUtil.normalizeGaussian(gaussian, this.mean, this.std);
                    final double gaussianDensity = MathUtil.densityFunction(gaussian, this.mean, this.std) * this.mean;
                    depth++;

                    if (depth > 5 || gaussianPercentage < gaussianDensity) {
                        this.cps = (float) Arithmetics.interpolate(this.minCps, this.maxCps, gaussianPercentage);
                        this.cpsHistory.add(new Vector4d(this.cps, gaussian, gaussianPercentage, gaussianDensity));
                        break;
                    }
                }
            }

            final float delay = 1000.0f / this.cps;
            this.delay = (int) Math.floor(delay + this.partialDelays);
            this.partialDelays += delay - this.delay;

            this.clicks++;
        }
    }

    public void setMaxCps(final int maxCps) {
        this.maxCps = maxCps;
    }

    public void setMinCps(final int minCps) {
        this.minCps = minCps;
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

    public EvictingList<Vector4d> getCpsHistory() {
        return cpsHistory;
    }

    public void setKillAuraModule(KillAuraModule killAuraModule) {
        this.killAuraModule = killAuraModule;
    }

}