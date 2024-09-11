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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.MouseDeltaListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.CameraOverrideListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.MathUtil;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class FreeCamModule extends AbstractModule implements
        IncomingPacketListener,
        PlayerUpdateListener, CameraOverrideListener,
        MouseDeltaListener, MoveInputListener {

    private final DoubleValue motionYOffset = new DoubleValue(
            this,
            "Motion Y Offset",
            "The motion y offset of the free cam.",
            0.5,
            0.1,
            2.0
    );

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed amount of the free cam.",
            1.0,
            1.0,
            5.0
    );

    private final BooleanValue deactivateOnPositionUpdate = new BooleanValue(
            this,
            "Deactivate On Position Update",
            "Whether or not to deactivate the module when the server sends a position update.",
            true
    );

    private double x = 0, y = 0, z = 0;
    private float yaw = 0f, pitch = 0f;

    public FreeCamModule() {
        super("Free Cam", "Like spectator mode but client side only.", Category.RENDER);
        this.deactivateAfterSessionDefault();
    }

    @Override
    protected void onActivate() {
        if (this.mc.player == null) {
            this.deactivate();
            return;
        }
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                IncomingPacketEvent.ID,
                PlayerUpdateEvent.ID, CameraOverrideEvent.ID,
                MouseDeltaEvent.ID, MoveInputEvent.ID
        );
        this.x = this.mc.player.getX();
        this.y = this.mc.player.getY() + (this.mc.player.getHeight() * 2);
        this.z = this.mc.player.getZ();
        this.yaw = this.mc.player.getYaw();
        this.pitch = this.mc.player.getPitch();
        this.mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        final GameOptions options = this.mc.options;
        final KeyBinding[] bindings = {options.forwardKey, options.backKey, options.leftKey, options.rightKey, options.jumpKey, options.sneakKey};
        for (final KeyBinding keyBinding : bindings) {
            keyBinding.setPressed(InputUtil.isKeyPressed(this.mc.getWindow().getHandle(), keyBinding.boundKey.getCode()));
        }
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                IncomingPacketEvent.ID,
                PlayerUpdateEvent.ID, CameraOverrideEvent.ID,
                MouseDeltaEvent.ID, MoveInputEvent.ID
        );
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0;
        this.pitch = 0;
        if (this.mc.player != null) {
            this.mc.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    @Override
    public void onMoveInput(final MoveInputEvent event) {
        double motionX = 0, motionZ = 0;
        final double motionY = event.jumping ? this.motionYOffset.getValue() : event.sneaking ? -this.motionYOffset.getValue() : 0;
        if (event.movementForward != 0 || event.movementSideways != 0) {
            float rotationYaw = this.mc.gameRenderer.getCamera().getYaw();
            final float moveForward = event.movementForward;
            final float moveStrafing = event.movementSideways;
            if (moveForward < 0F) rotationYaw += 180F;
            float forward = 1F;
            if (moveForward < 0F) forward = -0.5F;
            else if (moveForward > 0F) forward = 0.5F;
            if (moveStrafing > 0F) rotationYaw -= 90F * forward;
            if (moveStrafing < 0F) rotationYaw += 90F * forward;
            final double direction = Math.toRadians(rotationYaw);
            motionX = -Math.sin(direction) * this.speed.getValue();
            motionZ = Math.cos(direction) * this.speed.getValue();
        }
        final Vec3d currentPosition = new Vec3d(this.x, this.y, this.z);
        final Vec3d currentVelocity = new Vec3d(motionX, motionY, motionZ);
        final Vec3d newPosition = currentPosition.add(currentVelocity);
        this.x = newPosition.x;
        this.y = newPosition.y;
        this.z = newPosition.z;
        event.cancel();
    }

    @Override
    public void onMouseDelta(final MouseDeltaEvent event) {
        final double gcd = MathUtil.getGcd();

        final double cursorDeltaX = event.cursorDeltaX * gcd;
        final double cursorDeltaY = event.cursorDeltaY * gcd;

        this.yaw += (float) (cursorDeltaX * 0.15F);
        this.pitch += (float) (cursorDeltaY * 0.15F);
        if (this.pitch > 90) this.pitch = 90;
        if (this.pitch < -90) this.pitch = -90;
        event.cancel();
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;
        if (packet instanceof final PlayerPositionLookS2CPacket positionLookPacket) {
            if (this.deactivateOnPositionUpdate.getValue()) {
                this.deactivate();
            } else {
                this.x = positionLookPacket.getX();
                this.y = positionLookPacket.getY();
                this.z = positionLookPacket.getZ();
                this.yaw = positionLookPacket.getYaw();
                this.pitch = positionLookPacket.getPitch();
            }
        } else if (packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket && velocityPacket.getEntityId() == this.mc.player.getId()) {
            if (this.deactivateOnPositionUpdate.getValue()) {
                this.deactivate();
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        this.mc.options.togglePerspectiveKey.setPressed(false);
        this.mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    @Override
    public void onCameraOverride(final CameraOverrideEvent event) {
        final Camera camera = event.camera;
        camera.setRotation(this.yaw, this.pitch);
        camera.setPos(this.x, this.y, this.z);
        camera.thirdPerson = true;
    }

}
