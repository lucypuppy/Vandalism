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
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MSTimer;

public class AutoRespawnModule extends Module implements PlayerUpdateListener {

    private final BooleanValue instantRespawn = new BooleanValue(
            this,
            "Instant Respawn",
            "Instantly respawns you when you die.",
            false
    );

    private final IntegerValue delay = new IntegerValue(
            this,
            "Delay",
            "The delay in ticks before respawning.",
            2000,
            0,
            10000
    ).visibleCondition(() -> !this.instantRespawn.getValue());

    private final BooleanValue autoBack = new BooleanValue(
            this,
            "Auto Back",
            "Automatically uses the /back command when you die.",
            false
    );

    private final MSTimer delayTimer = new MSTimer();

    public AutoRespawnModule() {
        super(
                "Auto Respawn",
                "Respawns you when you die without having to press the respawn button.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!mc.player.isDead() || !this.instantRespawn.getValue() && !this.delayTimer.hasReached(this.delay.getValue(), true)) {
            return;
        }
        mc.player.requestRespawn();
        if (this.autoBack.getValue()) {
            mc.getNetworkHandler().sendChatCommand("back");
        }
    }

}
