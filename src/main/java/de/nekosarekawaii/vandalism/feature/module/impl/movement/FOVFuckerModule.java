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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.stream.Stream;

public class FOVFuckerModule extends Module implements PlayerUpdateListener {

    private final FloatValue maxDistance = new FloatValue(
            this,
            "Max Distance",
            "The maximum distance to find targets.",
            5f,
            2f,
            10f
    );

    private final DoubleValue targetYPosOffset = new DoubleValue(
            this,
            "Target Y Pos Offset",
            "The offset for the y position you will be teleported to.",
            0.2,
            -5.0,
            5.0
    );
    private final DoubleValue targetHPosOffset = new DoubleValue(
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

    private final BooleanValue useSneakFromTarget = new BooleanValue(
            this,
            "Use Sneak From Target",
            "Uses the sneak from the target.",
            true
    );

    private final BooleanValue useSwingFromTarget = new BooleanValue(
            this,
            "Use Swing From Target",
            "Uses the swing from the target.",
            true
    );

    private final BooleanValue alwaysFOV = new BooleanValue(
            this,
            "Always FOV",
            "This will always teleport you into the fov of the target.",
            false
    );

    private final BooleanValue sneakSpam = new BooleanValue(
            this,
            "Sneak Spam",
            "Spams sneak while moving.",
            true
    ).visibleCondition(() -> !this.useSneakFromTarget.getValue());

    private final IntegerValue sneakSpamDelay = new IntegerValue(
            this,
            "Sneak Spam Delay",
            "The delay for the sneak spam.",
            250,
            0,
            1000
    ).visibleCondition(this.sneakSpam.isVisible());

    private final MSTimer sneakTimer = new MSTimer();
    private boolean sneaking;
    private Arm serverArm;
    private boolean swung;

    private AbstractClientPlayerEntity target;
    private double x, y, z;

    private void reset() {
        final SyncedClientOptions options = mc.options.getSyncedOptions();
        if (this.serverArm != null && this.serverArm != options.mainArm()) {
            mc.player.networkHandler.sendPacket(new ClientOptionsC2SPacket(new SyncedClientOptions(
                    options.language(),
                    options.viewDistance(),
                    options.chatVisibility(),
                    options.chatColorsEnabled(),
                    options.playerModelParts(),
                    options.mainArm(),
                    options.filtersText(),
                    options.allowsServerListing()
            )));
        }
        this.serverArm = options.mainArm();
        this.swung = false;
        this.sneaking = false;
        this.target = null;
        this.x = -1;
        this.y = -1;
        this.z = -1;
    }

    public FOVFuckerModule() {
        super("FOV Fucker", "Teleports you into the nearest player to mess up their fov.", Category.MOVEMENT);
        this.deactivateAfterSessionDefault();
        this.reset();
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.reset();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.target == null) {
            final Stream<AbstractClientPlayerEntity> players = mc.world.getPlayers().stream();
            this.target = players.sorted(Comparator.comparingDouble(player -> mc.player.distanceTo(player))).
                    filter(player -> {
                        final boolean playerIsNotCurrentPlayer = mc.player != player;
                        final boolean distanceCheck = mc.player.distanceTo(player) <= this.maxDistance.getValue();
                        final boolean isNotFriend = !Vandalism.getInstance().getFriendsManager().isFriend(player.getGameProfile().getName(), true);
                        return playerIsNotCurrentPlayer && distanceCheck && isNotFriend;
                    }).
                    findFirst().
                    orElse(null);

            return;
        }

        final boolean targetIsDead = this.target.isDead();
        final boolean targetIsNotInWorld = mc.world.getEntityById(this.target.getId()) == null;
        final boolean targetIsNotFriend = Vandalism.getInstance().getFriendsManager().isFriend(this.target.getGameProfile().getName(), true);
        if (targetIsDead || targetIsNotInWorld || targetIsNotFriend) {
            this.reset();
            return;
        }

        final double direction = (Math.atan2(
                this.target.forwardSpeed,
                this.target.sidewaysSpeed
        ) / Math.PI * 180.0f + this.target.getYaw()) * Math.PI / 180.0f;

        if (this.useSwingFromTarget.getValue()) {
            //0.16666667
            //0.33333334
            //0.5
            //0.6666667
            //0.8333333
            if (this.target.handSwingProgress > 0.16666667) {
                if (!this.swung) {
                    this.swung = true;
                    boolean switchHand = mc.player.preferredHand != this.target.preferredHand;
                    final SyncedClientOptions options = mc.options.getSyncedOptions();
                    mc.player.networkHandler.sendPacket(new ClientOptionsC2SPacket(new SyncedClientOptions(
                            options.language(),
                            options.viewDistance(),
                            options.chatVisibility(),
                            options.chatColorsEnabled(),
                            options.playerModelParts(),
                            this.target.getMainArm(),
                            options.filtersText(),
                            options.allowsServerListing()
                    )));
                    this.serverArm = this.target.getMainArm();
                    mc.player.swingHand(switchHand ? Hand.OFF_HAND : Hand.MAIN_HAND);
                }
            } else {
                this.swung = false;
            }
        }

        if (this.useYawFromTarget.getValue()) {
            mc.player.setYaw(this.target.getHeadYaw());
            mc.player.setBodyYaw(this.target.getBodyYaw());
            mc.player.setHeadYaw(this.target.getHeadYaw());
        }

        if (this.usePitchFromTarget.getValue()) {
            mc.player.setPitch(this.target.getPitch());
        }

        if (this.useSneakFromTarget.getValue()) {
            mc.options.sneakKey.setPressed(this.target.isSneaking());
        } else if (this.sneakSpam.getValue()) {
            if (this.sneakTimer.hasReached(this.sneakSpamDelay.getValue(), true)) {
                this.sneaking = !this.sneaking;
            }

            mc.options.sneakKey.setPressed(this.sneaking);
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
            final float yawDiff = MathHelper.wrapDegrees(yaw - this.target.getYaw() - 90f);

            if (yawDiff >= -67.5f && yawDiff <= 67.5f) ++forward;
            if (yawDiff <= -112.5f || yawDiff >= 112.5f) --forward;
            if (yawDiff >= 22.5f && yawDiff <= 157.5f) --strafe;
            if (yawDiff >= -157.5f && yawDiff <= -22.5f) ++strafe;
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

        mc.player.setVelocity(new Vec3d(this.x, this.y, this.z).subtract(mc.player.getPos()));
    }

}
