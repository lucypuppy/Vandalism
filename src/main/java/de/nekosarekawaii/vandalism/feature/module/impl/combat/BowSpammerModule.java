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

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MSTimer;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.raphimc.vialoader.util.VersionRange;

public class BowSpammerModule extends Module implements PlayerUpdateListener {

    private final IntegerValue maxPacketsPerTick = new IntegerValue(
            this,
            "Max Packets Per Tick",
            "The maximum amount of packets sent per tick.",
            10,
            5,
            100
    );

    private final IntegerValue shootDelay = new IntegerValue(
            this,
            "Shoot Delay",
            "The delay between shots.",
            100,
            0,
            2000
    );

    private final MSTimer shootTimer = new MSTimer();

    public BowSpammerModule() {
        super(
                "Bow Spammer",
                "Lets you spam arrows with a bow.",
                Category.COMBAT,
                VersionRange.single(ProtocolVersion.v1_8)
        );
        this.deactivateAfterSessionDefault();
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
        if (mc.player.getMainHandStack().isOf(Items.BOW)) {
            if (this.shootTimer.hasReached(this.shootDelay.getValue(), true)) {
                for (int i = 0; i < this.maxPacketsPerTick.getValue(); i++) {
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                }
            }
        }
    }

}
