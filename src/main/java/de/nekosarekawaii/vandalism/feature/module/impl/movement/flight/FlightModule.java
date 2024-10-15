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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.flight;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.impl.*;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleModeValue;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import lombok.Getter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class FlightModule extends Module implements OutgoingPacketListener, PlayerUpdateListener {

    @Getter
    private double flownDistance;
    private double lastX, lastZ;

    private final ModuleModeValue<FlightModule> mode = new ModuleModeValue<>(
            this,
            "Mode",
            "The current flight mode.",
            new MotionModuleMode(),
            new CreativeModuleMode(),
            new BukkitModuleMode(),
            new CubeCraftModuleMode(),
            new CubeCraft2ModuleMode(),
            new CollisionModuleMode(),
            new OldAACModuleMode(),
            new VulcanGlideModuleMode(this),
            new SpartanFlagModuleMode(),
            new SpartanRoflModuleMode(),
            new SpartanOMFGLOL(),
            new NegativityV2ModuleMode(),
            new AutoeyeModuleMode(),
            new MatrixModuleMode()
    );

    private final BooleanValue antiKick = new BooleanValue(
            this,
            "Anti Kick",
            "Bypasses the vanilla flight check.",
            true
    ).visibleCondition(() -> this.mode.getValue() instanceof CreativeModuleMode || this.mode.getValue() instanceof MotionModuleMode);

    public final BooleanValue resetSpeedOnDeactivate = new BooleanValue(
            this,
            "Reset Speed On Deactivate",
            "Resets the speed on deactivate.",
            true
    );

    public FlightModule() {
        super("Flight", "Allows you to fly.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        this.lastX = Double.NaN;
        this.lastZ = Double.NaN;
        this.flownDistance = 0.0;
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
        this.flownDistance = 0.0;

        if (this.resetSpeedOnDeactivate.getValue() && mc.player != null) {
            mc.player.setVelocity(Vec3d.ZERO);
            MovementUtil.setSpeed(MovementUtil.getBaseSpeed());
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final double x = this.mc.player.getX();
        final double z = this.mc.player.getZ();

        if (!Double.isNaN(this.lastX) && !Double.isNaN(this.lastZ)) {
            this.flownDistance += Math.hypot(x - this.lastX, z - this.lastZ);
        }

        this.lastX = x;
        this.lastZ = z;

        if (!this.antiKick.getValue()) {
            return;
        }
        if (this.mode.getValue() instanceof CreativeModuleMode || this.mode.getValue() instanceof MotionModuleMode) {
            if (!WorldUtil.isOnGround(0.1)) {
                this.mc.player.ticksSinceLastPositionPacketSent = 20;
            }
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (!this.antiKick.getValue()) {
            return;
        }
        if (this.mode.getValue() instanceof CreativeModuleMode || this.mode.getValue() instanceof MotionModuleMode) {
            if (event.packet instanceof final PlayerMoveC2SPacket playerMoveC2SPacket) {
                if (!WorldUtil.isOnGround(0.1) && this.mc.player.age % 2 == 0) {
                    playerMoveC2SPacket.y = playerMoveC2SPacket.y - 0.1;
                }
            }
        }
    }

}
