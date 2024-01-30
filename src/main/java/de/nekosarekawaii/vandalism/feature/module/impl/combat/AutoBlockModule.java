/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;

public class AutoBlockModule extends AbstractModule {

    private boolean isBlocking;

    public AutoBlockModule() {
        super(
                "Auto Block",
                "Automatically blocks attacks.",
                Category.COMBAT
        );
    }

    @Override
    public void onDeactivate() {
        stopBlock();
    }

    public void startBlock() {
        if (!this.isBlocking && this.isActive() && canBlock(this.mc.player.getMainHandStack())) {
            this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
            this.isBlocking = true;
        }
    }

    public void stopBlock() {
        if (this.isBlocking && canBlock(this.mc.player.getMainHandStack())) {
            this.mc.interactionManager.stopUsingItem(this.mc.player);
            this.isBlocking = false;
        }
    }

    private boolean canBlock(final ItemStack itemStack) {
        return itemStack != null && itemStack.getItem().getUseAction(itemStack) == UseAction.BLOCK;
    }

    public boolean isBlocking() {
        return this.isBlocking;
    }

}
