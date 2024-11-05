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

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.target.TargetGroup;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.PacketHelper;
import de.nekosarekawaii.vandalism.util.SyncPosition;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class BackTrackModule extends Module implements PlayerUpdateListener,
        IncomingPacketListener, Render3DListener, WorldListener {

    private final TargetGroup targetGroup = new TargetGroup(this, "Target", "The target to back track.");

    public final IntegerValue minDuration = new IntegerValue(
            this,
            "Min Duration",
            "The minimum duration to back track the target.",
            60,
            0,
            1000);

    public final IntegerValue maxDuration = new IntegerValue(
            this,
            "Max Duration",
            "The maximum duration to back track the target.",
            60,
            0,
            1000);

    public final FloatValue backTrackRange = new FloatValue(
            this,
            "Back Track Range",
            "The range to back track the target.",
            10.0f,
            3.0f,
            20.0f);

    private final ColorValue realColor = new ColorValue(
            this,
            "Real Color",
            "The color of the real position.",
            new Color(255, 0, 0, 102));

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

    private final BooleanValue resyncOnDistanceToPlayer = new BooleanValue(
            this.resyncGroup,
            "Resync On Distance To Player",
            "Resyncs the target if the distance to the player is higher than the selected range.",
            true);

    private final DoubleValue maxDistanceToPlayer = new DoubleValue(
            this.resyncGroup,
            "Max Distance To Player",
            "The maximum distance to the player to resync the target.",
            3.0,
            0.0,
            10.0
    ).visibleCondition(this.resyncOnDistanceToPlayer::getValue);

    private final HashMap<Integer, SyncPosition> backTrackedEntities = new HashMap<>();
    private final ConcurrentLinkedQueue<DelayedPacket> packets = new ConcurrentLinkedQueue<>();
    private long delay;

    public BackTrackModule() {
        super(
                "Back Track",
                "Allows you to back track entities.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID,
                Render3DEvent.ID,
                WorldLoadEvent.ID
        );

        Vandalism.getInstance().getEventSystem().subscribe(
                IncomingPacketEvent.ID,
                this,
                Priorities.HIGH
        );

        updateDelay();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID,
                IncomingPacketEvent.ID,
                Render3DEvent.ID,
                WorldLoadEvent.ID
        );
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        for (final Entity entity : mc.world.getEntities()) {
            if (
                    this.targetGroup.isTarget(entity)
                            && entity.getWidth() > 0.0
                            && entity.getHeight() > 0.0
                            && entity instanceof final LivingEntity livingEntity
            ) {
                if (mc.player.distanceTo(livingEntity) <= backTrackRange.getValue()) {
                    if (!this.backTrackedEntities.containsKey(livingEntity.getId())) {
                        this.backTrackedEntities.put(livingEntity.getId(), new SyncPosition(entity.getPos()));
                    } else {
                        this.backTrackedEntities.get(livingEntity.getId()).onLivingUpdate();
                    }
                } else {
                    this.backTrackedEntities.remove(livingEntity.getId());
                }
            }
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;
        if (
                packet instanceof GameMessageS2CPacket || packet instanceof PlaySoundS2CPacket ||
                        packet instanceof LightUpdateS2CPacket ||
                        packet instanceof ChunkDataS2CPacket ||
                        packet instanceof PlayerRespawnS2CPacket ||
                        packet instanceof ChunkRenderDistanceCenterS2CPacket ||
                        packet instanceof TeamS2CPacket ||
                        packet instanceof GameJoinS2CPacket ||
                        packet instanceof EnterReconfigurationS2CPacket ||
                        packet instanceof BundleS2CPacket ||
                        packet instanceof BundleDelimiterS2CPacket ||
                        packet instanceof ScoreboardObjectiveUpdateS2CPacket ||
                        this.backTrackedEntities.isEmpty() || mc.player == null ||
                        mc.world == null || event.networkPhase != NetworkPhase.PLAY || event.isCancelled() //Ignore already cancelled packets
        ) {
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

        boolean shouldCancel = false;
        if (packet instanceof final EntityS2CPacket entityS2CPacket && entityS2CPacket.isPositionChanged() && this.backTrackedEntities.containsKey(entityS2CPacket.id)) {
            final SyncPosition trackedPosition = this.backTrackedEntities.get(entityS2CPacket.id);
            trackedPosition.setPos(
                    trackedPosition.withDelta(
                            entityS2CPacket.getDeltaX(),
                            entityS2CPacket.getDeltaY(),
                            entityS2CPacket.getDeltaZ()
                    ), false
            );

            if (this.checkForResync(trackedPosition, entityS2CPacket.id)) {
                shouldCancel = true;
            }
        } else if (packet instanceof final EntityPositionS2CPacket positionS2CPacket && this.backTrackedEntities.containsKey(positionS2CPacket.getEntityId())) {
            final SyncPosition trackedPosition = this.backTrackedEntities.get(positionS2CPacket.getEntityId());
            trackedPosition.setPos(new Vec3d(positionS2CPacket.getX(), positionS2CPacket.getY(), positionS2CPacket.getZ()), false);

            if (this.checkForResync(trackedPosition, positionS2CPacket.getEntityId())) {
                shouldCancel = true;
            }
        }

        if (shouldCancel) {
            return;
        }

        this.packets.add(new DelayedPacket(packet, System.currentTimeMillis()));
        event.cancel();
    }

    private void handlePackets(final boolean flush) {
        for (final DelayedPacket packet : this.packets) {
            if (flush || System.currentTimeMillis() > packet.time() + this.delay) {
                PacketHelper.receivePacket(packet.packet());
                this.packets.remove(packet);
                updateDelay();
            }
        }
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        if (mc.interactionManager == null) return;
        this.handlePackets(this.backTrackedEntities.isEmpty());

        for (final Map.Entry<Integer, SyncPosition> entry : this.backTrackedEntities.entrySet()) {
            final Entity entity = mc.world.getEntityById(entry.getKey());
            final Vec3d pos = entry.getValue().pos;

            if (entity == null) {
                this.backTrackedEntities.remove(entry.getKey());
                return;
            }

            if (pos.distanceTo(entity.getPos()) < 0.1) {
                return;
            }

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

            final Vec3d camPos = mc.gameRenderer.getCamera().getPos();
            matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

            final VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

            matrixStack.push();
            final double minX = (box.minX - center.x) * scale + center.x;
            final double minZ = (box.minZ - center.z) * scale + center.z;
            final double maxX = (box.maxX - center.x) * scale + center.x;
            final double maxZ = (box.maxZ - center.z) * scale + center.z;
            DebugRenderer.drawBox(
                    matrixStack,
                    immediate,
                    minX, box.minY, minZ, maxX, box.maxY, maxZ,
                    (float) realColor.getColor().getRed() / 255, (float) realColor.getColor().getGreen() / 255, (float) realColor.getColor().getBlue() / 255, (float) realColor.getColor().getAlpha() / 255
            );
            matrixStack.pop();

            immediate.draw();

            matrixStack.pop();
        }
    }

    private boolean checkForResync(final TrackedPosition trackedPosition, final int id) {
        final Entity entity = mc.world.getEntityById(id);
        if (entity == null)
            return false;

        final double distanceToOrigin = mc.player.distanceTo(entity);
        final double distanceToRealPos = mc.player.getPos().distanceTo(trackedPosition.pos);
        final double distanceOriginToRealPos = entity.getPos().distanceTo(trackedPosition.pos);

        final boolean condition1 = this.resyncIfCloserToReal.getValue() && distanceToOrigin > distanceToRealPos;
        final boolean condition2 = this.resyncOnDistanceToOrigin.getValue() && distanceOriginToRealPos > this.maxDistanceToOrigin.getValue();
        final boolean condition3 = this.resyncOnDistanceToPlayer.getValue() && distanceToRealPos > this.maxDistanceToPlayer.getValue();

        if (condition1 || condition2 || condition3) {
            handlePackets(true);
            return true;
        }

        return false;
    }

    @Override
    public void onPreWorldLoad() {
        //handlePackets(true);
        this.backTrackedEntities.clear();
    }

    private void updateDelay() {
        this.delay = (int) (ThreadLocalRandom.current().nextGaussian() * (minDuration.getValue() - maxDuration.getValue())) + maxDuration.getValue();
    }

    private record DelayedPacket(Packet<?> packet, long time) {
    }

}
