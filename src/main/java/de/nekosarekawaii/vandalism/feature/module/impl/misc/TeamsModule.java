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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.internal.TargetListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.entity.player.PlayerEntity;

public class TeamsModule extends Module implements TargetListener {

    public TeamsModule() {
        super("Teams", "Prevents you from targeting teammates with certain modules.", Category.MISC);
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TargetEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TargetEvent.ID);
    }

    @Override
    public void onTarget(TargetEvent event) {
        if (event.entity instanceof final PlayerEntity player) {
            if (player.isTeammate(mc.player)) {
                event.isTarget = false;
            }
        }
    }
}
