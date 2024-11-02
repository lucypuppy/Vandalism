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
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.TimeTravelListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import de.nekosarekawaii.vandalism.util.player.prediction.PredictionSystem;
import lombok.Getter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LagRangeModule extends Module implements TimeTravelListener, PlayerUpdateListener, IncomingPacketListener, OutgoingPacketListener {

    private final IntegerValue tickLimit = new IntegerValue(this, "Tick Limit", "The maximum amount of ticks you can charge.", 3, 1, 10);

    private final IntegerValue ticksToWait = new IntegerValue(this, "Ticks to wait", "Ticks to wait before being able to charge again.", 10, 0, 20);

    private final BooleanValue delayIncoming = new BooleanValue(this, "Delay Incoming", "Delays incoming packets too.", false);

    private final BooleanValue onlyOnGround = new BooleanValue(this, "Only On Ground", "Only charge when you are on the ground.", false);

    public final BooleanValue noChargeHit = new BooleanValue(this, "No Charge Hit", "Don't hit while charging.", true);

    private State state = State.IDLE;
    private long shifted, prevShifted;
    private boolean canShift;

    private final ConcurrentLinkedQueue<DelayedPacket> packets = new ConcurrentLinkedQueue<>();

    private KillAuraModule killAuraModule;

    @Getter
    private boolean stopAttack, prevStopAttack;
    private int limit = 0;
    private int ticksWaited = 0;

    public LagRangeModule() {
        super("Lag Range", "Allows you to manipulate how minecraft handles ticks and speedup the game.", Category.COMBAT);
    }

    private void reset() {
        this.limit = 0;
        this.ticksWaited = 0;
        this.state = State.IDLE;
        this.canShift = false;
        this.prevStopAttack = false;
        this.stopAttack = false;
    }

    @Override
    protected void onActivate() {
        this.reset();
        this.killAuraModule = Vandalism.getInstance().getModuleManager().getKillAuraModule();
        Vandalism.getInstance().getEventSystem().subscribe(this, TimeTravelEvent.ID, PlayerUpdateEvent.ID, IncomingPacketEvent.ID, OutgoingPacketEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TimeTravelEvent.ID, PlayerUpdateEvent.ID, IncomingPacketEvent.ID, OutgoingPacketEvent.ID);
        this.reset();
    }

    @Override
    public void onTimeTravel(final TimeTravelEvent event) {
        if (mc.player == null) {
            this.shifted = 0;
            return;
        }

        if (mc.player.age % 2 == 0) {
            this.prevStopAttack = this.stopAttack;
        }

        if (this.killAuraModule.isActive() && this.killAuraModule.getTarget() instanceof final PlayerEntity target) {
            this.calculateTicks(target);
        }

        if (this.canShift) {
            if (this.getCharge() <= this.limit) {
                this.stopAttack = true;
                this.state = State.CHARGING;
            } else {
                this.prevStopAttack = this.stopAttack;
                this.stopAttack = false;
                this.ticksWaited = 0;
                this.canShift = false;
                this.state = State.UNCHARGING;
            }
        }

        switch (this.state) {
            case CHARGING -> this.shifted += event.time - this.prevShifted;
            case UNCHARGING -> {
                if (this.shifted > 0) {
                    this.shifted = 0;
                    sendPackets();
                } else {
                    this.state = State.IDLE;
                }
            }
            default -> {
            }
        }

        this.prevShifted = event.time;
        event.time -= this.shifted;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.state == State.IDLE && this.killAuraModule.getTarget() != null) {
            this.ticksWaited++;
        }
    }

    @Override
    public void onIncomingPacket(IncomingPacketEvent event) {
        synchronized (packets) {
            if (mc.player == null || mc.world == null || event.isCancelled() || this.state != State.CHARGING || !this.delayIncoming.getValue()) {
                return;
            }
            packets.add(new DelayedPacket(event.packet, DelayedPacket.Direction.INCOMING));
            event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        synchronized (packets) {
            if (mc.player == null || mc.world == null || event.isCancelled() || this.state != State.CHARGING) {
                return;
            }
            packets.add(new DelayedPacket(event.packet, DelayedPacket.Direction.OUTGOING));
            event.cancel();
        }
    }

    private enum State {
        IDLE,
        CHARGING,
        UNCHARGING
    }

    private int getCharge() {
        return (int) (this.shifted / ((RenderTickCounter.Dynamic) mc.getRenderTickCounter()).tickTime);
    }

//    private void dynamicShit(final PlayerEntity target) {
//        for (int i = 2; i <= this.tickLimit.getValue(); i++) {
//            this.limit = (float) (i + (int) (Math.random() * 1.55f));
//
//            final Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictedPair = PredictionSystem.predictState((int) this.limit, target);
//            final ClientPlayerEntity predictedTarget = predictedPair.getLeft();
//            final ClientPlayerEntity predictedPlayer = PredictionSystem.predictState((int) this.limit).getLeft();
//
//            final Vec3d targetBHV = this.killAuraModule.hitPoint;
//            final double playerRange = predictedPlayer.getEyePos().distanceTo(targetBHV);
//
//            final Vec3d playerBHV = this.getHitPoint(predictedTarget, predictedPlayer);
//            final double targetRange = predictedTarget.getEyePos().distanceTo(playerBHV);
//
//            final double currentTargetRange = PredictionSystem.predictState(this.getCharge(), target).getLeft().getEyePos().distanceTo(playerBHV);
//
//            final boolean inRange = playerRange <= 3 && playerRange > 0 && targetRange > 3 && currentTargetRange > 3;
//
//            if (inRange && this.ticksWaited >= this.ticksToWait.getValue() && mc.player.hurtTime - this.limit <= 7 && (!this.onlyOnGround.getValue() || mc.player.isOnGround())) {
//                this.canShift = true;
//                break;
//            }
//        }
//    }

    private void calculateTicks(final PlayerEntity target) {
        Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictedPlayerPair = PredictionSystem.predictState(this.tickLimit.getValue() + 2);
        Pair<ClientPlayerEntity, ArrayList<Vec3d>> predictedTargetPair = PredictionSystem.predictState(this.tickLimit.getValue() + 2, target);

        for (int i = 2; i <= this.tickLimit.getValue(); i++) {
            this.limit = i + (int) (Math.random() * 1.55f);

            final Vec3d playerEyePos = predictedPlayerPair.getRight().get(limit - 1).add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
            final Vec3d targetEyePos = predictedTargetPair.getRight().get(limit - 1).add(0, target.getEyeHeight(target.getPose()), 0);
            final Vec3d playerHitPoint = this.getHitPoint(playerEyePos, mc.player.getYaw(), mc.player.getPitch(), targetEyePos);

            final Vec3d currentTargetEyePos = PredictionSystem.predictState(this.getCharge(), target).getLeft().getEyePos();
            final Vec3d currentTargetHitPoint = this.getHitPoint(currentTargetEyePos, target.getYaw(), target.getPitch(), mc.player.getEyePos());

            final boolean inRange = playerEyePos.distanceTo(playerHitPoint) <= 3 && playerEyePos.distanceTo(playerHitPoint) > 0 && currentTargetEyePos.distanceTo(currentTargetHitPoint) > 3;

            if (inRange && this.ticksWaited >= this.ticksToWait.getValue() && mc.player.hurtTime - this.limit <= 7 && (!this.onlyOnGround.getValue() || mc.player.isOnGround())) {
                this.canShift = true;
                break;
            }
        }
    }

    private void sendPackets() {
        synchronized (packets) {
            packets.removeIf(packet -> {
                mc.renderTaskQueue.add(() -> {
                    if (packet.direction == DelayedPacket.Direction.INCOMING && delayIncoming.getValue()) {
                        PacketHelper.receivePacket(packet.packet);
                    } else if (packet.direction == DelayedPacket.Direction.OUTGOING) {
                        PacketHelper.sendImmediately(packet.packet, null, true);
                    }
                });
                return true;
            });
        }
    }

    private record DelayedPacket(Packet<?> packet, DelayedPacket.Direction direction) {
        private enum Direction {
            INCOMING,
            OUTGOING
        }
    }

    private Vec3d getHitPoint(final PlayerEntity origin, final PlayerEntity target) {
        final Vec3d opponentPosition = origin.getEyePos();
        final Vec3d yourPosition = target.getEyePos();
        final Vec3d opponentDirection = Vec3d.fromPolar(origin.getPitch(), origin.getYaw());
        final double distance = yourPosition.distanceTo(opponentPosition);

        return opponentPosition.add(opponentDirection.multiply(distance));
    }

    private Vec3d getHitPoint(final Vec3d origin, final float yaw, final float pitch, final Vec3d target) {
        final Vec3d opponentDirection = Vec3d.fromPolar(pitch, yaw);
        final double distance = target.distanceTo(origin);

        return origin.add(opponentDirection.multiply(distance));
    }

}
