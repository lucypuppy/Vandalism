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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.scaffold.sneak;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.scaffold.ScaffoldModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import de.nekosarekawaii.vandalism.util.player.MovementUtil;

public class LegitSneakModuleMode extends ModuleMulti<ScaffoldModule> implements PlayerUpdateListener {

    private final MSTimer startSneakDelay = new MSTimer(), stopSneakDelay = new MSTimer();
    private long randomStartSneakDelay, randomStopSneakDelay;

    private final IntegerValue minStartSneakDelay = new IntegerValue(this, "Min Start Delay", "The minimum delay before starting to sneak", 800, 0, 5000);
    private final IntegerValue maxStartSneakDelay = new IntegerValue(this, "Max Start Delay", "The maximum delay before starting to sneak", 2200, 0, 5000);

    private final IntegerValue minStopSneakDelay = new IntegerValue(this, "Min Stop Delay", "The minimum delay before stopping to sneak", 200, 0, 1000);
    private final IntegerValue maxStopSneakDelay = new IntegerValue(this, "Max Stop Delay", "The maximum delay before stopping to sneak", 400, 0, 1000);

    public LegitSneakModuleMode() {
        super("Legit Sneak");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
        this.randomStartSneakDelay = getRandomStartSneakDelay();
        this.randomStopSneakDelay = getRandomStopSneakDelay();
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onPrePlayerUpdate(PlayerUpdateEvent event) {
        final boolean onGround = mc.player.getY() % MinecraftConstants.MAGIC_ON_GROUND_MODULO_FACTOR < 0.0001;

        if (MovementUtil.getSpeed() <= 0.01 || !onGround) {
            startSneakDelay.reset();
            return;
        }

        if (mc.options.sneakKey.isPressed()) {
            if (this.stopSneakDelay.hasReached(this.randomStopSneakDelay, true)) {
                mc.options.sneakKey.setPressed(false);
                this.randomStartSneakDelay = getRandomStartSneakDelay();
            }
        } else {
            if (startSneakDelay.hasReached(this.randomStartSneakDelay, true)) {
                mc.options.sneakKey.setPressed(true);
                this.randomStopSneakDelay = getRandomStopSneakDelay();
            }
        }
    }

    private long getRandomStartSneakDelay() {
        return RandomUtils.randomLong(this.minStartSneakDelay.getValue(), this.maxStartSneakDelay.getValue());
    }

    private long getRandomStopSneakDelay() {
        return RandomUtils.randomLong(this.minStopSneakDelay.getValue(), this.maxStopSneakDelay.getValue());
    }

}
