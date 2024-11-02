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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class IntaveModuleMode extends ModuleMulti<VelocityModule> implements PlayerUpdateListener, IncomingPacketListener, AttackListener, MoveInputListener {

    private final FloatValue xzOnHit = new FloatValue(this, "XZ On Hit", "The amount of knockback to reduce on hit.", 0.6f, 0.0f, 1.0f);

    private final FloatValue xzOnHitSprint = new FloatValue(this, "XZ On Hit Sprint", "The amount of knockback to reduce on hit while sprinting.", 0.6f, 0.0f, 1.0f);

    private final BooleanValue jump = new BooleanValue(this, "Jump", "Jump when reducing knockback.", true);

    private final IntegerValue chance = new IntegerValue(this, "Chance", "The percent chance to reduce knockback.", 80, 0, 100);

    private final IntegerValue minTicks = new IntegerValue(this, "Min Ticks", "The minimum amount of ticks wait before reducing knockback.", 0, 0, 10);

    private final IntegerValue randomTicks = new IntegerValue(this, "Random Ticks", "The amount of random ticks to add to the min ticks.", 3, 0, 10);

    private boolean shouldVelocity;
    private int ticks, ticksToWait;

    public IntaveModuleMode() {
        super("Intave");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID, AttackSendEvent.ID, MoveInputEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, IncomingPacketEvent.ID, AttackSendEvent.ID, MoveInputEvent.ID);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final EntityVelocityUpdateS2CPacket packet && mc.player != null && packet.getEntityId() == mc.player.getId()) {
            if (mc.player.hurtTime == 10 && mc.crosshairTarget != null && (packet.getVelocityX() != 0 || packet.getVelocityZ() != 0)) {
                final Random random = ThreadLocalRandom.current();
                if (mc.player.fallDistance <= 3.5) {
                    this.ticksToWait = this.minTicks.getValue();
                    if (this.randomTicks.getValue() > 0) {
                        this.ticksToWait += random.nextInt(0, this.randomTicks.getValue());
                    }
                    this.shouldVelocity = true;
                }
            }
        }
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (event.target instanceof LivingEntity) {
            if (this.shouldVelocity) {
                if (mc.player.isSprinting()) {
                    mc.player.setSprinting(false);
                    mc.player.setVelocity(mc.player.getVelocity().multiply(this.xzOnHitSprint.getValue(), 1.0, this.xzOnHitSprint.getValue()));
                } else {
                    mc.player.setVelocity(mc.player.getVelocity().multiply(this.xzOnHit.getValue(), 1.0, this.xzOnHit.getValue()));
                }

                ChatUtil.chatMessage("Intave Velocity :3");
            }
        }
    }

    @Override
    public void onMoveInput(final MoveInputEvent event) {
        if (this.jump.getValue() && this.shouldVelocity && mc.player.hurtTime == 9 && mc.player.isOnGround() && mc.player.isSprinting() && this.ticks >= this.ticksToWait && ThreadLocalRandom.current().nextInt(100) < this.chance.getValue()) {
            event.jumping = true;
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.shouldVelocity) {
            if (this.ticks < this.ticksToWait) {
                this.ticks++;
            } else {
                this.shouldVelocity = false;
                this.ticks = 0;
            }
        }
        if (mc.player.hurtTime == 0) {
            this.shouldVelocity = false;
        }
    }

}
