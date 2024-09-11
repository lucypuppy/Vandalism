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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBrightModule extends Module implements PlayerUpdateListener {

    public final BooleanValue useEffect = new BooleanValue(
            this,
            "Use Effect",
            "Whether or not to use client side night vision effect.",
            false
    );

    public FullBrightModule() {
        super("Full Bright", "Increases the brightness of the game which than allows you to see more in the dark.", Category.RENDER);
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
        if (this.mc.player != null) {
            this.mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final ClientPlayerEntity player = this.mc.player;
        if (player == null) {
            return;
        }
        if (this.useEffect.getValue()) {
            if (!player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 2, true, false, false), player);
            }
        } else {
            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

}
