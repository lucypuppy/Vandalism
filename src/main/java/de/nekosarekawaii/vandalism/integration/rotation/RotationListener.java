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

package de.nekosarekawaii.vandalism.integration.rotation;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.base.event.normal.render.Render2DListener;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class RotationListener implements OutgoingPacketListener, Render2DListener, MinecraftWrapper {

    private Rotation rotation, targetRotation, lastRotation;
    private double partialIterations;
    private float rotateSpeed;
    private RotationPriority currentPriority;

    public RotationListener() {
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) {
            if (this.rotation != null) {
                packet.yaw = this.rotation.getYaw();
                packet.pitch = this.rotation.getPitch();
                packet.changeLook = true;
            }
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        this.lastRotation = new Rotation(this.mc.player.lastYaw, this.mc.player.lastPitch);

        if (this.targetRotation != null) {
            this.rotation = this.applyGCDFix(rotationDistribution(this.targetRotation, this.lastRotation, 0.2f, true), delta);
            return;
        }

        if (this.rotation == null) {
            return;
        }

        final float yaw = MathHelper.wrapDegrees(this.mc.player.getYaw());
        final float pitch = this.mc.player.getPitch();
        final float yawDiff = Math.abs(yaw - MathHelper.wrapDegrees(this.rotation.getYaw()));
        final float pitchDiff = Math.abs(pitch - this.rotation.getPitch());

        if (yawDiff <= 0.5 && pitchDiff <= 0.5) {
            this.rotation = null;
            return;
        }

        if (!Vandalism.getInstance().getClientSettings().getRotationSettings().rotateBack.getValue()) {
            this.rotation = this.applyGCDFix(new Rotation(yaw, pitch), delta);
            return;
        }

        this.rotation = this.applyGCDFix(rotationDistribution(new Rotation(yaw, pitch), this.lastRotation, 0.2f, true), delta);
    }

    public void setRotation(final Rotation rotation, final float rotateSpeed, final RotationPriority priority) {
        if (this.currentPriority == null || priority.getPriority() >= this.currentPriority.getPriority()) {
            this.targetRotation = rotation;
            this.rotateSpeed = rotateSpeed;
            this.currentPriority = priority;
        }
    }

    public void resetRotation() {
        this.targetRotation = null;
    }

    private Rotation applyGCDFix(final Rotation rotation, final float partialTicks) {
        final double f = this.mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
        final double g = f * f * f;
        final double gcd = g * 8.0;
        final boolean disallowGCD = this.mc.options.getPerspective().isFirstPerson() && this.mc.player.isUsingSpyglass();
        final double iterationsNeeded = (RenderUtil.getFps() / 20.0) * partialTicks;
        final int iterations = MathHelper.floor(iterationsNeeded + this.partialIterations);
        this.partialIterations += iterationsNeeded - iterations;
        final RotationGCD gcdMode = Vandalism.getInstance().getClientSettings().getRotationSettings().gcdMode.getValue();
        final Rotation fixedRotation = gcdMode.getLambda().apply(rotation, this.lastRotation, disallowGCD ? g : gcd, iterations);
        fixedRotation.setYaw(this.lastRotation.getYaw() + MathHelper.wrapDegrees(fixedRotation.getYaw() - this.lastRotation.getYaw()));
        fixedRotation.setPitch(MathHelper.clamp(fixedRotation.getPitch(), -90.0F, 90.0F));
        return fixedRotation;
    }

    public Rotation rotationDistribution(final Rotation rotation, final Rotation lastRotation, float correlationStrength, boolean correlation) {
        //TODO: Code a better calculation for the rotate speed.
        //correlation can overaim/underaim, i recomend to set it at around 0.2
        double rotateSpeed = this.rotateSpeed + Math.random() * 5;
        //if ((Math.random() * 100) <= 10) {
        //    // rotateSpeed = Math.random() * 35;
        //}
        //if (KillauraModule.getTarget() != null) {
        //    if (KillauraModule.getTarget().hurtTime >= 4) {
        //        // rotateSpeed = MathUtil.getRandomFloat(4, 10);
        //    }
        //}
        //if (mc.objectMouseOver != null) {
        //    if (mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)) {
        //        // rotateSpeed = MathUtil.getRandomFloat(0, (float) (2 + Math.random() * 5));
        //    }
        //}
        if (rotateSpeed > 0) {
            final float lastYaw = lastRotation.getYaw();
            final float lastPitch = lastRotation.getPitch();
            final float deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw);
            final float deltaPitch = rotation.getPitch() - lastPitch;
            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            if (distance > 0) {
                final double distributionYaw = Math.abs(deltaYaw / distance);
                final double distributionPitch = Math.abs(deltaPitch / distance);
                final double maxYaw = rotateSpeed * distributionYaw;
                final double maxPitch = (rotateSpeed) * distributionPitch;

                // Introduce correlation between yaw and pitch
                final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
                final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

                // Apply correlation (reverse the effect)
                float correlatedMoveYaw = moveYaw;
                float correlatedMovePitch = movePitch;
                if(correlation){
                    correlatedMoveYaw = moveYaw + movePitch * correlationStrength;
                    correlatedMovePitch = movePitch + moveYaw * correlationStrength;
                }

                return new Rotation(lastYaw + correlatedMoveYaw, lastPitch + correlatedMovePitch);
            }
        }
        return rotation;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public Rotation getTargetRotation() {
        return this.targetRotation;
    }

}
