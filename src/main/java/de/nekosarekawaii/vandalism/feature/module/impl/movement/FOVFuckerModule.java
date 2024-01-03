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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.stream.Stream;

public class FOVFuckerModule extends AbstractModule implements TickGameListener {

    private final Value<Float> maxDistance = new FloatValue(
            this,
            "Max Distance",
            "The maximum distance to find targets.",
            5F,
            2F,
            10F
    );

    private final Value<Double> targetYPosOffset = new DoubleValue(
            this,
            "Target Y Pos Offset",
            "The offset for the y position you will be teleported to.",
            0.2,
            -5.0,
            5.0
    );
    private final Value<Double> targetHPosOffset = new DoubleValue(
            this,
            "Target H Pos Offset",
            "The offset for the horizontal position you will be teleported to.",
            0.5,
            -5.0,
            5.0
    );

    private final BooleanValue useYawFromTarget = new BooleanValue(
            this,
            "Use Yaw From Target",
            "Uses the yaw from the target.",
            true
    );

    private final BooleanValue usePitchFromTarget = new BooleanValue(
            this,
            "Use Pitch From Target",
            "Uses the pitch from the target.",
            true
    );

    private final BooleanValue alwaysFOV = new BooleanValue(
            this,
            "Always FOV",
            "This will always teleport you into the fov of the target.",
            false
    );

    private final ValueGroup sneakGroup = new ValueGroup(
            this,
            "Sneak Spam Configuration",
            "The settings for the sneak spam."
    );

    private final BooleanValue sneakSpam = new BooleanValue(
            this.sneakGroup,
            "Sneak Spam",
            "You are sus with the target.",
            true
    );

    private final IntegerValue sneakSpamDelay = new IntegerValue(
            this.sneakGroup,
            "Sneak Spam Delay",
            "The delay for the sneak spam.",
            250,
            0,
            1000
    );

    private final MSTimer sneakTimer = new MSTimer();
    private boolean sneaking;
    private AbstractClientPlayerEntity target;
    private double x, y, z;

    private void reset() {
        this.sneaking = false;
        this.target = null;
        this.x = -1;
        this.y = -1;
        this.z = -1;
    }

    public FOVFuckerModule() {
        super("FOV Fucker", "Teleports you into the nearest player to mess up their fov.", Category.MOVEMENT);
        this.reset();
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
        this.reset();
    }

    @Override
    public void onTick() {
        if (this.mc.world == null || this.mc.player == null) return;

        if (this.target == null) {
            final Stream<AbstractClientPlayerEntity> players = this.mc.world.getPlayers().stream();

            this.target = players.sorted(Comparator.comparingDouble(player -> this.mc.player.distanceTo(player))).
                    filter(player -> this.mc.player != player && this.mc.player.distanceTo(player) <= this.maxDistance.getValue()).
                    findFirst().
                    orElse(null);

            return;
        }

        if (this.target.isDead() || this.mc.world.getEntityById(this.target.getId()) == null) {
            this.reset();
            return;
        }

        final double direction = (Math.atan2(
                this.target.forwardSpeed,
                this.target.sidewaysSpeed
        ) / Math.PI * 180.0F + this.target.getYaw()) * Math.PI / 180.0F;

        if (this.useYawFromTarget.getValue()) {
            this.mc.player.setYaw(this.target.getHeadYaw());
            this.mc.player.setBodyYaw(this.target.getBodyYaw());
            this.mc.player.setHeadYaw(this.target.getHeadYaw());
        }

        if (this.usePitchFromTarget.getValue()) {
            this.mc.player.setPitch(this.target.getPitch());
        }

        if (this.sneakSpam.getValue()) {
            if (this.sneakTimer.hasReached(this.sneakSpamDelay.getValue(), true)) {
                this.sneaking = !this.sneaking;
            }

            this.mc.options.sneakKey.setPressed(this.sneaking);
        }

        double diffZ = (this.target.getZ() - this.target.prevZ);
        double diffX = (this.target.getX() - this.target.prevX);

        final float targetPitch = this.target.getPitch();
        double targetEyePosY = Math.abs((targetPitch < 0) ? targetPitch / this.target.getEyePos().y : 0);
        if (targetEyePosY > 0) {
            targetEyePosY += RandomUtils.randomFloat(
                    -(float) (targetEyePosY * this.target.getStandingEyeHeight()),
                    (float) (targetEyePosY * this.target.getStandingEyeHeight())
            ) * 0.6;
        }

        if (!this.alwaysFOV.getValue()) {
            targetEyePosY = 0;
        }

        float strafe = 0, forward = 0;
        if (diffZ != 0 || diffX != 0) {
            final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX));
            final float yawDiff = MathHelper.wrapDegrees(yaw - this.target.getYaw() - 90F);

            if (yawDiff >= -67.5F && yawDiff <= 67.5F) ++forward;
            if (yawDiff <= -112.5F || yawDiff >= 112.5) --forward;
            if (yawDiff >= 22.5F && yawDiff <= 157.5F) --strafe;
            if (yawDiff >= -157.5F && yawDiff <= -22.5F) ++strafe;
        }

        // offsets
        final double hOffset = this.targetHPosOffset.getValue();
        final double xOffset = Math.sin(direction) * hOffset;
        final double zOffset = Math.cos(direction) * hOffset;

        // positions
        double x = this.target.getX(), z = this.target.getZ();
        if (!this.alwaysFOV.getValue()) {
            x += xOffset;
            z -= zOffset;
        } else {
            x -= xOffset;
            z += zOffset;
            if (forward > 0) {
                if (strafe != 0) {
                    diffX /= 1.2f;
                    diffZ /= 1.2f;
                }
                x += diffX * 17;
                z += diffZ * 21;
            } else {
                if (strafe != 0) {
                    diffX *= 4;
                    diffZ *= 4;
                }
                x += (diffX * 1.75);
                z += (diffZ * 1.75);
            }
        }

        this.x = x;
        this.y = this.target.getY() + this.targetYPosOffset.getValue() + targetEyePosY;
        this.z = z;

        this.mc.player.setVelocity(new Vec3d(this.x, this.y, this.z).subtract(this.mc.player.getPos()));
    }

}
