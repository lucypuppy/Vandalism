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
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.disconnect.DisconnectValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.target.TargetGroup;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.entity.Entity;

public class AutoDisconnectModule extends Module implements PlayerUpdateListener {

    private final DisconnectValue lavaDisconnectValue = new DisconnectValue(
            this,
            "Lava condition",
            "The settings for the lava condition.",
            () -> mc.player.isInLava()
    );

    private final DisconnectValue healthDisconnectValue = new DisconnectValue(
            this,
            "Health condition",
            "The settings for the health condition.",
            () -> mc.player.getHealth() <= this.disconnectHealth.getValue()
    );

    private final IntegerValue disconnectHealth = new IntegerValue(
            this.healthDisconnectValue,
            "Disconnect health",
            "The health the module should disconnect at.",
            12,
            1,
            19
    );

    private final DisconnectValue entityDisconnectValue = new DisconnectValue(
            this,
            "Entity condition",
            "The settings for the entity condition.",
            () -> {
                boolean disconnect = false;
                for (Entity entity : mc.world.getEntities()) {
                    if (this.entities.isTarget(entity)) {
                        final double distance = mc.player.getPos().distanceTo(entity.getPos());
                        if (distance <= this.entityDistance.getValue()) {
                            disconnect = true;
                            break;
                        }
                    }
                }
                return disconnect;
            }
    );

    private final TargetGroup entities = new TargetGroup(
            this.entityDisconnectValue,
            "Entities",
            "The entities which trigger a disconnect."
    );

    private final DoubleValue entityDistance = new DoubleValue(
            this.entityDisconnectValue,
            "Entity distance",
            "The distance to hostile mobs the module should disconnect at.",
            3D,
            0.1D,
            100D
    );

    public AutoDisconnectModule() {
        super(
                "Auto Disconnect",
                "Automatically disconnects in defined conditions.",
                Category.MISC
        );
        this.entities.getTargetListValue().getValue().clear();
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        for (Value<?> value : this.getValues()) {
            if (value instanceof DisconnectValue disconnectValue) {
                if (disconnectValue.getActiveValue().getValue() && disconnectValue.getDisconnectCondition().shouldDisconnect()) {
                    this.deactivate();
                    disconnectValue.executeDisconnect();
                }
            }
        }
    }
}
