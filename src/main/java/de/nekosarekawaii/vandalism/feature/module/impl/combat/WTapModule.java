/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.event.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import de.nekosarekawaii.vandalism.util.render.util.InputType;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;

import java.util.Random;

public class WTapModule extends AbstractModule implements AttackListener, PlayerUpdateListener, CanSprintListener {

    private State state = State.IDLE;
    private final Random random = new Random();
    private LivingEntity targetEntity;
    private int ticks;
    private int addTicks;

    public WTapModule() {
        super("W Tap",
                "Automatically sprints and un-sprints when you are in combat which applies more velocity to your target.",
                Category.COMBAT);
    }

    private final ModeValue tapMode = new ModeValue(this, "Tap Mode", "The type of tap to use.", "W Tap", "S Tap");
    private final IntegerValue stopTicks = new IntegerValue(this, "Stop Ticks", "The amount of ticks to wait before stopping.", 3, 0, 20);
    private final IntegerValue startTicks = new IntegerValue(this, "Start Ticks", "The amount of ticks to wait before starting.", 3, 0, 20);
    private final IntegerValue randomTicks = new IntegerValue(this, "Random Ticks", "The random ticks which will be added to the delay.", 2, 0, 10);
    private final IntegerValue chance = new IntegerValue(this, "Chance", "The chance to tap.", 60, 0, 100);

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID, CanSprintEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID, CanSprintEvent.ID);
        if (this.state == State.IDLE) {
            resetKeys();
        }
        this.state = State.IDLE;
    }

    @Override
    public void onAttackSend(AttackSendEvent event) {
        if (event.target instanceof LivingEntity && this.state == State.IDLE && random.nextDouble() < chance.getValue() * 0.1 && MovementUtil.isMovingForwards() && mc.player.isSprinting()) {
            this.state = State.START;
            targetEntity = (LivingEntity) event.target;
            addTicks = getRandomTicks();
        }
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        if (this.state == State.START) {
            ticks++;
            if (ticks >= stopTicks.getValue() + addTicks) {
                doTap(tapMode.getSelectedIndex());
                this.state = State.STOP;
                ticks = 0;
                addTicks = getRandomTicks();
            }
        } else if (this.state == State.STOP) {
            ticks++;
            if (ticks >= startTicks.getValue() + addTicks) {
                resetKeys();
                this.state = State.IDLE;
            }
        }
    }

    @Override
    public void onCanSprint(CanSprintEvent event) {
        if (this.state == State.START && ticks >= stopTicks.getValue()) {
            event.canSprint = false;
        } else if (this.state == State.STOP && ticks >= startTicks.getValue()) {
            event.canSprint = isPressed(mc.options.sprintKey);
        }
    }

    private void doTap(final int mode) {
        mc.options.sprintKey.setPressed(false);
        mc.options.forwardKey.setPressed(false);
        if (mode == 1) {
            mc.options.backKey.setPressed(true);
        }
    }

    private int getRandomTicks() {
        return randomTicks.getValue() > 0 ? random.nextInt(randomTicks.getValue() + 1) : 0;
    }

    private boolean isPressed(KeyBinding keyBinding) {
        return InputType.isPressed(keyBinding.boundKey.getCode());
    }

    private void resetKeys() {
        mc.options.sprintKey.setPressed(isPressed(mc.options.sprintKey));
        mc.options.forwardKey.setPressed(isPressed(mc.options.forwardKey));
        mc.options.backKey.setPressed(isPressed(mc.options.backKey));
    }

    private enum State {
        IDLE,
        START,
        STOP
    }
}
