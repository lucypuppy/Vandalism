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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.event.normal.render.Render3DListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.PacketUtil;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BackTrackModule extends AbstractModule implements PlayerUpdateListener, IncomingPacketListener, Render3DListener {

    public final IntegerValue pingSpoof = new IntegerValue(
            this,
            "Ping Spoof",
            "The amount of ping to spoof.",
            60,
            0,
            1000);

    private final ValueGroup resyncGroup = new ValueGroup(this, "Resync", "Resync options.");

    private final BooleanValue resyncIfCloserToReal = new BooleanValue(
            this.resyncGroup,
            "Resync If Real Is Nearer Then Origin",
            "Resyncs the target if the real position is nearer than the origin.",
            true);

    private final BooleanValue resyncOnDistanceToOrigin = new BooleanValue(
            this.resyncGroup,
            "Resync On Distance To Origin",
            "Resyncs the target if the distance to the origin is higher than the selected range.",
            true);

    private final DoubleValue maxDistanceToOrigin = new DoubleValue(
            this.resyncGroup,
            "Max Distance To Origin",
            "The maximum distance to the origin to resync the target.",
            3.0,
            0.0,
            10.0
    ).visibleCondition(this.resyncOnDistanceToOrigin::getValue);

    private TrackedPosition realTargetPosition;
    private Entity targetEntity;

    private final KillAuraModule killAuraModule;

    private final ConcurrentLinkedQueue<DelayedPacket> packets = new ConcurrentLinkedQueue<>();

    public BackTrackModule(final KillAuraModule killAuraModule) {
        super(
                "Back Track",
                "Allows you to back track entities.",
                Category.COMBAT
        );
        this.killAuraModule = killAuraModule;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID,
                Render3DEvent.ID
        );

        Vandalism.getInstance().getEventSystem().subscribe(
                IncomingPacketEvent.ID,
                this,
                Priorities.HIGH
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID,
                IncomingPacketEvent.ID,
                Render3DEvent.ID
        );
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        this.targetEntity = this.killAuraModule.getTarget();

        if (!this.killAuraModule.isActive() || this.targetEntity == null) {
            this.targetEntity = null;
            this.realTargetPosition = null;

            this.handlePackets(true);
            return;
        }

        this.handlePackets(false);

        if (this.realTargetPosition == null) {
            this.realTargetPosition = new TrackedPosition();
            this.realTargetPosition.setPos(this.targetEntity.getPos());
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;

        if (//@formatter:off
                packet instanceof GameMessageS2CPacket || packet instanceof PlaySoundS2CPacket ||
                        this.targetEntity == null || this.realTargetPosition == null ||
                        this.mc.player == null || this.mc.world == null || event.isCancelled() //Ingore already cancelled packets
        ) {//@formatter:on
            return;
        }

        if (packet instanceof PlayerPositionLookS2CPacket || packet instanceof DisconnectS2CPacket) {
            this.handlePackets(true);
            return;
        }

        if (packet instanceof final HealthUpdateS2CPacket health) {
            if (health.getHealth() <= 0) {
                this.handlePackets(true);
                return;
            }
        }

        boolean move = false;
        if (packet instanceof final EntityS2CPacket entityS2CPacket && entityS2CPacket.id == this.targetEntity.getId()) {
            this.realTargetPosition.setPos(
                    this.realTargetPosition.withDelta(
                            entityS2CPacket.getDeltaX(),
                            entityS2CPacket.getDeltaY(),
                            entityS2CPacket.getDeltaZ()
                    )
            );
            move = true;
        } else if (packet instanceof final EntityPositionS2CPacket positionS2CPacket && positionS2CPacket.getId() == this.targetEntity.getId()) {
            this.realTargetPosition.setPos(new Vec3d(positionS2CPacket.getX(), positionS2CPacket.getY(), positionS2CPacket.getZ()));
            move = true;
        }

        final double distanceToOrigin = Math.sqrt(this.mc.player.squaredDistanceTo(this.targetEntity));
        final double distanceToRealPos = Math.sqrt(this.mc.player.squaredDistanceTo(this.realTargetPosition.pos));
        final double distanceOriginToRealPos = Math.abs(distanceToOrigin - distanceToRealPos);
        final boolean condition1 = this.resyncIfCloserToReal.getValue() && distanceToOrigin > distanceToRealPos;
        final boolean condition2 = this.resyncOnDistanceToOrigin.getValue() && distanceOriginToRealPos > this.maxDistanceToOrigin.getValue();

        if (move && (condition1 || condition2)) {
            handlePackets(true);
            return;
        }

        this.packets.add(new DelayedPacket(packet, System.currentTimeMillis()));
        event.cancel();
    }

    private void handlePackets(final boolean flush) {
        for (final DelayedPacket packet : this.packets) {
            if (flush || System.currentTimeMillis() > packet.time() + this.pingSpoof.getValue()) {
                PacketUtil.recievePacket(this.mc.getNetworkHandler(), packet.packet());
                this.packets.remove(packet);
            }
        }
    }

    @Override
    public void onRender3D(final float tickDelta, final long limitTime, final MatrixStack matrixStack) {
        if (this.targetEntity == null) {
            return;
        }

        final Vec3d pos = this.realTargetPosition.pos;
        if (pos.distanceTo(this.targetEntity.getPos()) < 0.1) {
            return;
        }

        if (this.targetEntity instanceof final LivingEntity entity) {
            matrixStack.push();

            final Box box = new Box(
                    pos.x - entity.getWidth() / 2f,
                    pos.y,
                    pos.z - entity.getWidth() / 2f,
                    pos.x + entity.getWidth() / 2f,
                    pos.y + entity.getHeight(),
                    pos.z + entity.getWidth() / 2f
            );

            final Vec3d center = box.getCenter();
            final double scale = 1.5;

            final Vec3d camPos = this.mc.gameRenderer.getCamera().getPos();
            matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

            final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

            matrixStack.push();
            final double minX = (box.minX - center.x) * scale + center.x;
            final double minZ = (box.minZ - center.z) * scale + center.z;
            final double maxX = (box.maxX - center.x) * scale + center.x;
            final double maxZ = (box.maxZ - center.z) * scale + center.z;
            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    minX, box.minY, minZ, maxX, box.maxY, maxZ,
                    1.0f, 0.0f, 0.0f, 0.4f
            );
            matrixStack.pop();

            immediate.draw();

            matrixStack.pop();
        }
    }

    //@formatter:off
    private record DelayedPacket(Packet<?> packet, long time) {}
    //@formatter:on
}
