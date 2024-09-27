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

package de.nekosarekawaii.vandalism.integration.rotation;

import com.mojang.datafixers.util.Function4;
import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.RotationSettings;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.player.RotationListener;
import de.nekosarekawaii.vandalism.event.player.StrafeListener;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import lombok.Data;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

@Data
public class RotationManager implements MinecraftWrapper, StrafeListener, RotationListener, CanSprintListener, OutgoingPacketListener {

    private PrioritizedRotation clientRotation, serverRotation, targetRotation;

    private Function4<PrioritizedRotation, PrioritizedRotation, Double, Boolean, PrioritizedRotation> smoothingFunc;
    private boolean movementFix;

    private long lastMillis;

    public RotationManager() {
        Vandalism.getInstance().getEventSystem().subscribe(this, OutgoingPacketEvent.ID, StrafeEvent.ID, CanSprintEvent.ID);

        // Priority is low because we want to calculate the rotation after the module set it.
        Vandalism.getInstance().getEventSystem().subscribe(RotationEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof final PlayerMoveC2SPacket packet) {
            if (this.clientRotation != null) {
                packet.yaw = this.clientRotation.getYaw();
                packet.pitch = this.clientRotation.getPitch();
                packet.changeLook = true;
            }
        }
    }

    @Override
    public void onStrafe(StrafeEvent event) {
        if (this.clientRotation == null || !this.movementFix)
            return;

        if (Vandalism.getInstance().getClientSettings().getRotationSettings().moveFixMode.getValue().equalsIgnoreCase("Silent")) {
            if (event.movementInput != null && MovementUtil.isMoving()) {
                event.movementInput = MovementUtil.silentMoveFix(this.clientRotation, event);
            }
        }

        // Thanks mojang...
        if (event.type == StrafeListener.Type.JUMP) {
            event.yaw = (float) Math.toRadians(this.clientRotation.getYaw());
            event.modified = true;
            return;
        }

        event.yaw = this.clientRotation.getYaw();
    }

    @Override
    public void onCanSprint(CanSprintEvent event) {
        if (this.clientRotation == null || !this.movementFix) return;
        event.canSprint = event.canSprint && Math.abs(MathHelper.wrapDegrees(this.clientRotation.getYaw() - MovementUtil.getInputAngle(mc.player.getYaw()))) <= 45;
    }

    @Override
    public void onRotation(RotationEvent event) {
        final long currentTime = System.currentTimeMillis();
        final double deltaTime = currentTime - this.lastMillis;
        this.lastMillis = currentTime;

        this.serverRotation = Objects.requireNonNullElseGet(this.clientRotation, () -> new PrioritizedRotation(this.mc.player.lastYaw, this.mc.player.lastPitch, RotationPriority.NORMAL));

        if (targetRotation != null) {
            if (this.smoothingFunc == null) {
                this.clientRotation = this.targetRotation;
            } else {
                this.clientRotation = this.smoothingFunc.apply(this.targetRotation, this.serverRotation, deltaTime, clientRotation != null);
            }
            return;
        }

        if (clientRotation == null)
            return;

        final float yaw = MathHelper.wrapDegrees(this.mc.player.getYaw());
        final float pitch = this.mc.player.getPitch();
        final float yawDiff = Math.abs(yaw - MathHelper.wrapDegrees(this.serverRotation.getYaw()));
        final float pitchDiff = Math.abs(pitch - this.serverRotation.getPitch());

        if (yawDiff <= 0.5 && pitchDiff <= 0.5) {
            this.clientRotation = null;
            return;
        }

        final RotationSettings settings = Vandalism.getInstance().getClientSettings().getRotationSettings();
        this.clientRotation = RotationUtil.rotateMouse(new PrioritizedRotation(yaw, pitch, RotationPriority.NORMAL),
                this.serverRotation, settings.rotateSpeed.getValue(), deltaTime, this.clientRotation != null);
    }

    public void setRotation(final PrioritizedRotation rotation, final boolean movementFix, final Function4<PrioritizedRotation, PrioritizedRotation, Double, Boolean, PrioritizedRotation> smoothingFunc) {
        if (this.clientRotation == null || rotation.getPriority().getPriority() >= this.clientRotation.getPriority().getPriority()) {
            this.targetRotation = rotation;
            this.movementFix = movementFix;
            this.smoothingFunc = smoothingFunc;
        }
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
