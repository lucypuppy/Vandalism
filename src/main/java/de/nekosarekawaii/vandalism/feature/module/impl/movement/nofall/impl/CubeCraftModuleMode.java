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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.GameTickListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import net.minecraft.block.AirBlock;

public class CubeCraftModuleMode extends ModuleMulti<NoFallModule> implements GameTickListener {

    public CubeCraftModuleMode() {
        super("CubeCraft");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(GameTickEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(GameTickEvent.ID, this);
    }

    @Override
    public void onGameTick(GameTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (mc.player.fallDistance > 3) {
            for (int i = 1; i <= 4; i++) {
                if (!(mc.world.getBlockState(mc.player.getBlockPos().down(i)).getBlock() instanceof AirBlock)) {
                    if (i == 4) {
                        mc.player.setVelocity(0, 0, 0);
                    }
                    mc.player.setVelocity(0, 0.01, 0);
                    mc.player.fallDistance = 0;
                    return;
                }
            }
        }
    }
}
