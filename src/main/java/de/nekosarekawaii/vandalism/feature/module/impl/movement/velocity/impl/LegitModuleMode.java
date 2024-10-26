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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.ModeValue;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;

import java.util.concurrent.ThreadLocalRandom;

public class LegitModuleMode extends ModuleMulti<VelocityModule> implements MoveInputListener {

    public LegitModuleMode() {
        super("Legit");
    }

    private final ModeValue chanceMode = new ModeValue(
            this,
            "Chance Mode",
            "The mode of the chance to reduce knockback.",
            "Percent", "Hit Amount", "Delay");

    private final IntegerValue percent = new IntegerValue(this,
            "Percent",
            "The percent chance to reduce knockback.",
            80, 0, 100)
            .visibleCondition(() -> chanceMode.getValue().equalsIgnoreCase("Percent"));
    private final IntegerValue hitAmount = new IntegerValue(this,
            "Hit Amount",
            "The amount of hits to reduce knockback.",
            3, 0, 10)
            .visibleCondition(() -> chanceMode.getValue().equalsIgnoreCase("Hit Amount"));
    private final LongValue delay = new LongValue(this,
            "Delay",
            "The delay in milliseconds to reduce knockback.",
            1000L, 0L, 10000L)
            .visibleCondition(() -> chanceMode.getValue().equalsIgnoreCase("Delay"));

    private int hits;
    private long lastJumpTime;

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, MoveInputEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, MoveInputEvent.ID);
        this.hits = 0;
        this.lastJumpTime = 0;
    }

    @Override
    public void onMoveInput(MoveInputEvent event) {
        if (mc.player == null) {
            return;
        }
        if (mc.player.hurtTime == 9 && mc.player.isOnGround() && mc.player.isSprinting() && !mc.player.isInLava() && !mc.player.isTouchingWater() && (!mc.player.isOnFire() || mc.player.getFireTicks() > 0)) {
            if (canJump()) {
                event.jumping = true;
                this.hits = 0;
                this.lastJumpTime = System.currentTimeMillis();
            } else {
                this.hits++;
            }
        }
    }

    private boolean canJump() {
        return switch (chanceMode.getValue().toLowerCase()) {
            case "percent" -> ThreadLocalRandom.current().nextInt(100) < percent.getValue();
            case "hit amount" -> hitAmount.getValue() <= hits;
            case "delay" -> System.currentTimeMillis() - lastJumpTime < delay.getValue();
            default -> false;
        };
    }

}
