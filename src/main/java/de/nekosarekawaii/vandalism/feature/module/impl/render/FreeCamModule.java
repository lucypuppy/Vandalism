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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.BlockCollisionShapeListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.EntityPushListener;
import de.nekosarekawaii.vandalism.event.player.FluidPushListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.render.Render3DListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import de.nekosarekawaii.vandalism.util.render.ColorUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Matrix4f;

import java.awt.*;

public class FreeCamModule extends AbstractModule implements
        PlayerUpdateListener, OutgoingPacketListener, IncomingPacketListener,
        BlockCollisionShapeListener, EntityPushListener,
        FluidPushListener, Render3DListener {

    private final DoubleValue motionYOffset = new DoubleValue(
            this,
            "Motion Y Offset",
            "The motion y offset of the free cam.",
            1.0,
            0.1,
            2.0
    );

    private final DoubleValue speed = new DoubleValue(
            this,
            "Speed",
            "The speed amount of the free cam.",
            2.0,
            1.0,
            5.0
    );

    private final BooleanValue allowInteraction = new BooleanValue(
            this,
            "Allow Interaction",
            "Whether or not to allow interaction with the world.",
            true
    );

    private final BooleanValue sendRotations = new BooleanValue(
            this,
            "Send Rotations",
            "Whether or not to send rotations to the server.",
            true
    );

    private final BooleanValue deactivateOnPositionUpdate = new BooleanValue(
            this,
            "Deactivate On Position Update",
            "Whether or not to deactivate the module when the server sends a position update.",
            true
    );

    private final ColorValue serverSidePosColor = new ColorValue(
            this,
            "Server Side Position Color",
            "The color of the server side position.",
            ColorUtils.withAlpha(Color.RED, 127)
    );

    private double x = 0, y = 0, z = 0;
    private float yaw = 0f, pitch = 0f, width = 0f, height = 0f;
    private boolean isOnGround = false;

    private boolean receivedPositionUpdate = false;

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
                PlayerUpdateEvent.ID, OutgoingPacketEvent.ID, IncomingPacketEvent.ID,
                BlockCollisionShapeEvent.ID, EntityPushEvent.ID,
                FluidPushEvent.ID, Render3DEvent.ID
        );
        this.x = this.mc.player.getX();
        this.y = this.mc.player.getY();
        this.z = this.mc.player.getZ();
        this.yaw = this.mc.player.getYaw();
        this.pitch = this.mc.player.getPitch();
        this.width = this.mc.player.getWidth();
        this.height = this.mc.player.getHeight();
        this.isOnGround = this.mc.player.isOnGround();
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
                PlayerUpdateEvent.ID, OutgoingPacketEvent.ID, IncomingPacketEvent.ID,
                BlockCollisionShapeEvent.ID, EntityPushEvent.ID,
                FluidPushEvent.ID, Render3DEvent.ID
        );
        if (this.mc.player != null) {
            this.mc.player.setVelocity(Vec3d.ZERO);
            this.mc.player.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
        }
        if (this.mc.worldRenderer != null) {
            this.mc.worldRenderer.reload();
        }
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0;
        this.pitch = 0;
        this.width = 0;
        this.height = 0;
        this.isOnGround = false;
        this.receivedPositionUpdate = false;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        double motionX = 0, motionZ = 0;
        final double motionY = this.mc.options.jumpKey.isPressed() ? this.motionYOffset.getValue() : this.mc.options.sneakKey.isPressed() ? -this.motionYOffset.getValue() : 0;
        if (MovementUtil.isMoving()) {
            final Vec3d speedVelocity = MovementUtil.setSpeed(this.speed.getValue());
            motionX = speedVelocity.x;
            motionZ = speedVelocity.z;
        }
        this.mc.player.setVelocity(motionX, motionY, motionZ);
        this.mc.player.getAbilities().flying = false;
        this.mc.player.setOnGround(false);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        final Packet<?> packet = event.packet;
        if (this.allowInteraction.getValue()) {
            if (
                    packet instanceof PlayerInteractBlockC2SPacket ||
                            packet instanceof PlayerInteractItemC2SPacket ||
                            packet instanceof PlayerInteractEntityC2SPacket ||
                            packet instanceof PlayerActionC2SPacket ||
                            packet instanceof CreativeInventoryActionC2SPacket ||
                            packet instanceof UpdateSelectedSlotC2SPacket ||
                            packet instanceof CloseHandledScreenC2SPacket ||
                            packet instanceof ClickSlotC2SPacket ||
                            packet instanceof ButtonClickC2SPacket ||
                            packet instanceof UpdateCommandBlockMinecartC2SPacket ||
                            packet instanceof UpdateCommandBlockC2SPacket ||
                            packet instanceof UpdateBeaconC2SPacket ||
                            packet instanceof RenameItemC2SPacket ||
                            packet instanceof UpdateStructureBlockC2SPacket ||
                            packet instanceof UpdateJigsawC2SPacket ||
                            packet instanceof HandSwingC2SPacket ||
                            packet instanceof SignEditorOpenS2CPacket ||
                            packet instanceof UpdateSignC2SPacket ||
                            packet instanceof BookUpdateC2SPacket ||
                            packet instanceof CraftRequestC2SPacket
            ) {
                return;
            }
        }
        if (packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket) {
            playerMoveC2SPacket.x = this.x;
            playerMoveC2SPacket.y = this.y;
            playerMoveC2SPacket.z = this.z;
            playerMoveC2SPacket.onGround = this.isOnGround;
            if (!this.sendRotations.getValue()) {
                playerMoveC2SPacket.yaw = this.yaw;
                playerMoveC2SPacket.pitch = this.pitch;
            } else {
                this.yaw = playerMoveC2SPacket.yaw;
                this.pitch = playerMoveC2SPacket.pitch;
            }
            return;
        }
        if (
                packet instanceof CommandExecutionC2SPacket ||
                        packet instanceof UpdateDifficultyLockC2SPacket ||
                        packet instanceof UpdateDifficultyC2SPacket ||
                        packet instanceof ChatMessageC2SPacket ||
                        packet instanceof ChatCommandSignedC2SPacket ||
                        packet instanceof RequestCommandCompletionsC2SPacket ||
                        packet instanceof CommonPongC2SPacket ||
                        packet instanceof QueryPingC2SPacket ||
                        packet instanceof KeepAliveC2SPacket ||
                        packet instanceof TeleportConfirmC2SPacket ||
                        packet instanceof PlayerRespawnS2CPacket
        ) {
            return;
        }
        event.cancel();
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        final Packet<?> packet = event.packet;
        if (packet instanceof final PlayerPositionLookS2CPacket positionLookPacket) {
            if (this.deactivateOnPositionUpdate.getValue()) {
                this.receivedPositionUpdate = true;
            } else {
                // On Ground could flag, but we ignore it for now because this module will be recoded in the future.
                this.x = positionLookPacket.getX();
                this.y = positionLookPacket.getY();
                this.z = positionLookPacket.getZ();
                this.yaw = positionLookPacket.getYaw();
                this.pitch = positionLookPacket.getPitch();
            }
        } else if (packet instanceof final EntityVelocityUpdateS2CPacket velocityPacket && velocityPacket.getId() == this.mc.player.getId()) {
            if (this.deactivateOnPositionUpdate.getValue()) {
                this.receivedPositionUpdate = true;
            }
        }
    }

    @Override
    public void onBlockCollisionShape(final BlockCollisionShapeEvent event) {
        event.shape = VoxelShapes.empty();
    }

    @Override
    public void onEntityPush(final EntityPushEvent event) {
        event.cancel();
    }

    @Override
    public void onFluidPush(final FluidPushEvent event) {
        event.cancel();
    }

    @Override
    public void onRender3D(final float tickDelta, final MatrixStack matrixStack) {
        matrixStack.push();
        final Vec3d camPos = this.mc.gameRenderer.getCamera().getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
        matrixStack.push();
        matrixStack.push();
        final double scale = 1.5;
        final double x = this.x;
        final double y = this.y;
        final double z = this.z;
        final double halfWidth = this.width / 2;
        final double height = this.height;
        matrixStack.translate(x, y + scale + 0.65, z);
        matrixStack.multiply(this.mc.getEntityRenderDispatcher().getRotation());
        matrixStack.scale(0.025F, -0.025F, 0.025F);
        final Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        final TextRenderer textRenderer = this.mc.textRenderer;
        final Text text = Text.literal("Server Side Position");
        final float g = (float) (-textRenderer.getWidth(text) / 2);
        final VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        textRenderer.draw(text, g, 0f, Colors.WHITE, false, matrix4f, immediate, TextRenderer.TextLayerType.NORMAL, 0, 1);
        matrixStack.pop();
        final float[] startPosColor = ColorUtils.rgba(this.serverSidePosColor.getColor().getRGB());
        final Box box = new Box(
                x - halfWidth,
                y,
                z - halfWidth,
                x + halfWidth,
                y + height,
                z + halfWidth
        );
        final Vec3d center = box.getCenter();
        final double minX = (box.minX - center.x) * scale + center.x;
        final double minZ = (box.minZ - center.z) * scale + center.z;
        final double maxX = (box.maxX - center.x) * scale + center.x;
        final double maxZ = (box.maxZ - center.z) * scale + center.z;
        DebugRenderer.drawBox(
                matrixStack,
                immediate,
                minX,
                box.minY,
                minZ,
                maxX,
                box.maxY,
                maxZ,
                startPosColor[0],
                startPosColor[1],
                startPosColor[2],
                startPosColor[3]
        );

        matrixStack.pop();
        immediate.draw();
        matrixStack.pop();
        if (this.receivedPositionUpdate) {
            this.deactivate();
        }
    }

}
