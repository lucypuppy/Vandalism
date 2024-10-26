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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class HenkelPortModule extends Module implements PlayerUpdateListener {

    private final FloatValue maxDistance = new FloatValue(
            this,
            "Max Distance",
            "Max teleport distance.",
            10.0f,
            0.0f,
            100.0f
    );

    private final LongValue duration = new LongValue(
            this,
            "Duration",
            "The duration of the henkel.",
            100L,
            0L,
            10000L
    );

    private Vec3d startPos;

    public HenkelPortModule() {
        super("Henkel Port", "Allows you to henkel from further distances.", Category.MISC);
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
        teleportTime = -1L;
        startPos = null;
    }

    private long teleportTime = -1L;

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        HenklerSprenklerModule henklerSprenklerModule = Vandalism.getInstance().getModuleManager().getHenklerSprenklerModule();
        if (teleportTime == -1L) {
            if (!this.mc.player.isUsingItem() && !this.mc.options.useKey.isPressed()) return;
            final Vec3d target = this.getBlockHitResult();
            if (target == null) return;
            startPos = mc.player.getPos();
            MovementUtil.bypassClip(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    target.getX() + 0.5,
                    target.getY() + 1,
                    target.getZ() + 0.5
            );
            if (!henklerSprenklerModule.isActive()) {
                henklerSprenklerModule.toggle();
            }
            teleportTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - teleportTime >= duration.getValue()) {
            if (startPos != null) {
                if (henklerSprenklerModule.isActive()) {
                    henklerSprenklerModule.toggle();
                }
                MovementUtil.bypassClip(
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        startPos.getX(),
                        startPos.getY(),
                        startPos.getZ()
                );
            }
            teleportTime = -1;
        }
    }

    private Vec3d getBlockHitResult() {
        final HitResult result = this.mc.player.raycast(this.maxDistance.getValue(), this.mc.getRenderTickCounter().getTickDelta(false), false);
        if (result instanceof BlockHitResult) {
            return result.getPos();
        }
        return null;
    }
}
