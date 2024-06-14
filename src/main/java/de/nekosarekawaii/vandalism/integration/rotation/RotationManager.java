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

package de.nekosarekawaii.vandalism.integration.rotation;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.RotationSettings;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.player.StrafeListener;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationGCD;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import de.nekosarekawaii.vandalism.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class RotationManager implements MinecraftWrapper, OutgoingPacketListener, StrafeListener, RotationListener, CanSprintListener {

    @Getter
    private Rotation rotation;
    private Rotation targetRotation;

    private double partialIterations;

    private float rotateSpeed;
    private float correlationStrength;
    private boolean movementFix;

    public RotationManager() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, StrafeEvent.ID, CanSprintEvent.ID);

        // Priority is low because we want to calculate the rotation after the module set it.
        Vandalism.getInstance().getEventSystem().subscribe(RotationEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) { // Sanity check; if somewhere in the code is a packet and we didnt inject
            if (this.rotation != null) {
                packet.yaw = this.rotation.getYaw();
                packet.pitch = this.rotation.getPitch();
                packet.changeLook = true;
            }
        }
    }

    @Override
    public void onRotation(RotationEvent event) {
        final float partialTicks = this.mc.getRenderTickCounter().getTickDelta(false);
        final Rotation lastRotation = new Rotation(this.mc.player.lastYaw, this.mc.player.lastPitch);

        if (this.targetRotation != null) {
            final Rotation smoothedRotation = RotationUtil.rotationDistribution(this.targetRotation, lastRotation,
                    this.rotateSpeed, this.correlationStrength);

            this.rotation = this.applyGCDFix(smoothedRotation, lastRotation, partialTicks);
            return;
        }

        if (this.rotation == null)
            return;

        final float yaw = MathHelper.wrapDegrees(this.mc.player.getYaw());
        final float pitch = this.mc.player.getPitch();
        final float yawDiff = Math.abs(yaw - MathHelper.wrapDegrees(this.rotation.getYaw()));
        final float pitchDiff = Math.abs(pitch - this.rotation.getPitch());

        if (yawDiff <= 0.5 && pitchDiff <= 0.5) {
            this.rotation = null;
            return;
        }

        final RotationSettings settings = Vandalism.getInstance().getClientSettings().getRotationSettings();
        final Rotation smoothedRotation = RotationUtil.rotationDistribution(new Rotation(yaw, pitch), lastRotation,
                settings.rotateSpeed.getValue(), settings.correlationStrength.getValue());

        this.rotation = this.applyGCDFix(smoothedRotation, lastRotation, partialTicks);
    }

    @Override
    public void onStrafe(StrafeEvent event) {
        if (this.rotation == null || !this.movementFix)
            return;

        if (Vandalism.getInstance().getClientSettings().getRotationSettings().moveFixMode.getValue().equalsIgnoreCase("Silent")) {
            if (event.movementInput != null && MovementUtil.isMoving()) {
                event.movementInput = MovementUtil.silentMoveFix(this.rotation, event);
            }
        }

        // Thanks mojang...
        if (event.type == StrafeListener.Type.JUMP) {
            event.yaw = (float) Math.toRadians(this.rotation.getYaw());
            event.modified = true;
            return;
        }

        event.yaw = this.rotation.getYaw();
    }

    @Override
    public void onCanSprint(CanSprintEvent event) {
        if(this.rotation == null || !this.movementFix) return;
        event.canSprint = Math.abs(MathHelper.wrapDegrees(this.rotation.getYaw() - MovementUtil.getInputAngle(mc.player.getYaw()))) <= 45;
    }

    public void setRotation(final Rotation rotation, final float rotateSpeed, final float correlationStrength, final boolean movementFix) {
        if (this.rotation == null || rotation.getPriority().getPriority() >= this.rotation.getPriority().getPriority()) {
            this.targetRotation = rotation;

            // Settings
            this.rotateSpeed = rotateSpeed;
            this.correlationStrength = correlationStrength;
            this.movementFix = movementFix;
        }
    }

    private Rotation applyGCDFix(final Rotation rotation, final Rotation lastRotation, final float partialTicks) {
        final boolean disallowGCD = this.mc.options.getPerspective().isFirstPerson() && this.mc.player.isUsingSpyglass();
        final double f = this.mc.options.getMouseSensitivity().getValue() * 0.6f + 0.2f;
        final double g = f * f * f;
        final double gcd = g * 8.0;

        final double iterationsNeeded = (RenderUtil.getFps() / 20.0) * partialTicks;
        final int iterations = MathHelper.floor(iterationsNeeded + this.partialIterations);
        this.partialIterations += iterationsNeeded - iterations;

        final RotationGCD gcdMode = Vandalism.getInstance().getClientSettings().getRotationSettings().gcdMode.getValue();
        final Rotation fixedRotation = gcdMode.getLambda().apply(rotation, lastRotation, disallowGCD ? g : gcd, iterations);

        fixedRotation.setYaw(lastRotation.getYaw() + MathHelper.wrapDegrees(fixedRotation.getYaw() - lastRotation.getYaw()));
        fixedRotation.setPitch(MathHelper.clamp(fixedRotation.getPitch(), -90.0f, 90.0f));

        // Hand over the rotation Priority
        fixedRotation.setPriority(rotation.getPriority());
        return fixedRotation;
    }

    public void resetRotation(final RotationPriority priority) {
        if (targetRotation != null && targetRotation.getPriority().getPriority() <= priority.getPriority()) {
            this.targetRotation = null;
        }
    }

    public void resetRotation() {
        resetRotation(RotationPriority.NORMAL);
    }

}
