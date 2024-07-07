/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.entity.LivingEntity;

import java.util.Random;

public class WTapModule extends AbstractModule implements AttackListener, PlayerUpdateListener {

    private final Random random = new Random();
    private boolean wasSprinting, wasForward, wasBackwards;
    private boolean shouldTap, shouldStopTap;
    private LivingEntity targetEntity;
    private long timeSinceAttack;
    private long randomStopDelay, randomStartDelay;

    public WTapModule() {
        super("W Tap",
                "Automatically sprints and un-sprints when you are in combat which applies more velocity to your target.",
                Category.COMBAT);
    }

    private final ModeValue tapMode = new ModeValue(this, "Tap Mode", "The type of tap to use.", "W Tap", "S Tap");
    private final IntegerValue stopDelay = new IntegerValue(this, "Stop Delay", "The delay between hitting and stopping.", 100, 0, 1000);
    private final IntegerValue startDelay = new IntegerValue(this, "Start Delay", "The delay between stopping and starting.", 100, 0, 1000);
    private final IntegerValue randomDelayRange = new IntegerValue(this, "Random Delay Range", "The random delay range which will be added to the delay.", 30, 0, 100);
    private final IntegerValue chance = new IntegerValue(this, "Chance", "The chance to tap.", 60, 0, 100);

    @Override
    protected void onActivate() {
        wasSprinting = mc.options.sprintKey.isPressed();
        wasForward = mc.options.forwardKey.isPressed();
        wasBackwards = mc.options.backKey.isPressed();
        Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID);
        if (shouldTap) {
            mc.options.forwardKey.setPressed(wasForward);
            mc.options.backKey.setPressed(wasBackwards);
            mc.options.sprintKey.setPressed(wasSprinting);
        }
        shouldTap = false;
        shouldStopTap = false;
    }

    @Override
    public void onAttackSend(AttackSendEvent event) {
        if (event.target instanceof LivingEntity && !shouldTap && random.nextDouble() < chance.getValue() * 0.1 && mc.player.isSprinting()) {
            shouldTap = true;
            timeSinceAttack = System.currentTimeMillis();
            targetEntity = (LivingEntity) event.target;
            randomStopDelay = stopDelay.getValue() + getRandomDelay();
            randomStartDelay = Math.max(randomStopDelay, startDelay.getValue() + getRandomDelay());
        }
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final long currentTime = System.currentTimeMillis();
        if (shouldTap) {
            if (currentTime - timeSinceAttack > randomStopDelay && !shouldStopTap) {
                wasSprinting = mc.options.sprintKey.isPressed();
                wasForward = mc.options.forwardKey.isPressed();
                wasBackwards = mc.options.backKey.isPressed();
                doTap(tapMode.getSelectedIndex());
                shouldTap = false;
                shouldStopTap = true;
            }
        }
        if (shouldStopTap) {
            if (currentTime - timeSinceAttack > randomStopDelay + randomStartDelay) {
                mc.options.sprintKey.setPressed(wasSprinting);
                mc.options.forwardKey.setPressed(wasForward);
                mc.options.backKey.setPressed(wasBackwards);
                shouldStopTap = false;
            }
        }
    }

    private void doTap(final int mode) {
        switch (mode) {
            case 0:
                mc.options.sprintKey.setPressed(false);
                mc.options.forwardKey.setPressed(false);
                break;
            case 1:
                mc.options.sprintKey.setPressed(false);
                mc.options.forwardKey.setPressed(false);
                mc.options.backKey.setPressed(true);
                break;
        }
    }

    private int getRandomDelay() {
        return randomDelayRange.getValue() > 0 ? random.nextInt(randomDelayRange.getValue()) : 0;
    }
}
