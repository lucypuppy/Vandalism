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
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.entity.projectile.FishingBobberEntity;

public class AutoFishModule extends Module implements PlayerUpdateListener {

    public final IntegerValue throwDelayValue = new IntegerValue(
            this,
            "Throw Delay",
            "Here you can input the custom throw delay value.",
            1000,
            0,
            5000
    );

    public final IntegerValue retractDelayValue = new IntegerValue(
            this,
            "Retract Delay",
            "Here you can input the custom retract delay value.",
            500,
            0,
            1000
    );

    private final MSTimer retractDelayTimer = new MSTimer();
    private final MSTimer throwDelayTimer = new MSTimer();

    private boolean hasFish = false;

    public AutoFishModule() {
        super("Auto Fish", "Automatically fishes for you.", Category.MISC);
    }

    @Override
    public void onActivate() {
        this.hasFish = false;
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        this.hasFish = false;
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final FishingBobberEntity fishHook = mc.player.fishHook;
        if (fishHook != null) {
            if (!this.hasFish && fishHook.caughtFish && fishHook.getVelocity().y < -0.2) {
                this.hasFish = true;
                this.retractDelayTimer.reset();
            }
            if (this.hasFish && this.retractDelayTimer.hasReached(this.retractDelayValue.getValue(), true)) {
                mc.doItemUse();
                this.throwDelayTimer.reset();
            }
        } else if (this.throwDelayTimer.hasReached(this.throwDelayValue.getValue(), true)) {
            mc.doItemUse();
            this.hasFish = false;
        }
    }

}
