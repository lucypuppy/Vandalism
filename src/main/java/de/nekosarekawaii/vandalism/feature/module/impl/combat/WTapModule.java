/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.event.game.HandleInputListener;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.event.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public class WTapModule extends Module implements AttackListener, PlayerUpdateListener, CanSprintListener, HandleInputListener, KeyboardInputListener {

    private final ModeValue tapMode = new ModeValue(this, "Tap Mode", "The type of tap to use.", "Sprint Reset", "W Tap", "S Tap");

    private final IntegerValue stopTicks = new IntegerValue(this, "Stop Ticks", "The amount of ticks to wait before stopping.", 3, 0, 20);

    private final IntegerValue startTicks = new IntegerValue(this, "Start Ticks", "The amount of ticks to wait before starting.", 3, 0, 20);

    private final IntegerValue randomTicks = new IntegerValue(this, "Random Ticks", "The random ticks which will be added to the delay.", 2, 0, 10);

    private final IntegerValue chance = new IntegerValue(this, "Chance", "The chance to tap.", 60, 0, 100);

    private State state = State.IDLE;
    private LivingEntity targetEntity;
    private int ticks;
    private int addTicks;
    private boolean sprint, forward, back;

    public WTapModule() {
        super("W Tap",
                "Automatically sprints and un-sprints when you are in combat which applies more velocity to your target.",
                Category.COMBAT);
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID, CanSprintEvent.ID, HandleInputEvent.ID, KeyboardInputEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, PlayerUpdateEvent.ID, CanSprintEvent.ID, HandleInputEvent.ID, KeyboardInputEvent.ID);
        if (this.state == State.IDLE) {
            this.resetKeys();
        }
        this.state = State.IDLE;
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (event.target instanceof final LivingEntity target && this.state == State.IDLE && ThreadLocalRandom.current().nextDouble() < this.chance.getValue() * 0.01 && MovementUtil.isMovingForwards() && mc.player.isSprinting() && target.hurtTime <= 7) {
            this.state = State.START;
            this.targetEntity = target;
            this.addTicks = this.getRandomTicks();
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.state != State.IDLE) {
            if (this.state == State.START && this.ticks >= this.stopTicks.getValue() + this.addTicks) {
                this.doTap(this.tapMode.getSelectedIndex());
                this.state = State.STOP;
                this.ticks = 0;
                this.addTicks = this.getRandomTicks();
            } else if (this.state == State.STOP && this.ticks >= this.startTicks.getValue() + this.addTicks) {
                this.resetKeys();
                this.state = State.IDLE;
            }
            this.ticks++;
        }
    }


    @Override
    public void onHandleInputEvent(final HandleInputEvent event) {
        if (this.state == State.START) {
            if (this.ticks >= this.stopTicks.getValue() + this.addTicks) {
                this.doTap(this.tapMode.getSelectedIndex());
                this.state = State.STOP;
                this.ticks = 0;
                this.addTicks = this.getRandomTicks();
            }
        } else if (this.state == State.STOP) {
            if (this.ticks >= this.startTicks.getValue() + this.addTicks) {
                this.resetKeys();
                this.state = State.IDLE;
            }
        }
    }

    @Override
    public void onKeyInput(final long window, final int key, final  int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS && action != GLFW.GLFW_RELEASE) {
            return;
        }

        final boolean isPress = action == GLFW.GLFW_PRESS;
        if (key == mc.options.sprintKey.boundKey.getCode()) {
            this.sprint = isPress;
        } else if (key == mc.options.forwardKey.boundKey.getCode()) {
            this.forward = isPress;
        } else if (key == mc.options.backKey.boundKey.getCode()) {
            this.back = isPress;
        }
    }

    @Override
    public void onCanSprint(final CanSprintEvent event) {
        if (this.state == State.START && this.ticks >= this.stopTicks.getValue()) {
            event.canSprint = false;
        }
    }

    private void doTap(final int mode) {
        mc.options.sprintKey.setPressed(false);
        if (mode == 1) {
            mc.options.forwardKey.setPressed(false);
        } else if (mode == 2) {
            mc.options.forwardKey.setPressed(false);
            mc.options.backKey.setPressed(true);
        }
    }

    private int getRandomTicks() {
        return this.randomTicks.getValue() > 0 ? ThreadLocalRandom.current().nextInt(this.randomTicks.getValue() + 1) : 0;
    }

    private void resetKeys() {
        mc.options.sprintKey.setPressed(this.sprint || Vandalism.getInstance().getModuleManager().getAutoSprintModule().isActive());
        mc.options.forwardKey.setPressed(this.forward);
        mc.options.backKey.setPressed(this.back);
    }

    private enum State {
        IDLE,
        START,
        STOP
    }

}
