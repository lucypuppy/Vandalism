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

import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;

public class TriggerBotModule extends ClickerModule {

    public TriggerBotModule() {
        super("Trigget Bot", "Automatically attacks players as soon as they are in reach and on the crosshair. ", Category.COMBAT);
    }

    @Override
    public void onClick() {
        mc.doAttack();
    }

    @Override
    public void onFailClick() {
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public boolean shouldClick() {
        return mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY;
    }

}
