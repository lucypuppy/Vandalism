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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public class IllegalInteractionModule extends AbstractModule implements PlayerUpdateListener {

    private final DoubleValue reach = new DoubleValue(
            this,
            "Reach",
            "The reach of illegal interactions.",
            3.0,
            0.0,
            5.0
    );

    public BooleanValue viaVersionBug = new BooleanValue(
            this,
            "ViaVersion Bug",
            "Allows you to place blocks inside your-self on versions lower than 1.9.0 on servers that are using the plugin ViaVersion.",
            true
    );

    public IllegalInteractionModule() {
        super(
                "Illegal Interaction",
                "Let's you interact with illegal block hit results.",
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
        final Entity cameraEntity = this.mc.getCameraEntity();
        final HitResult hitResult = cameraEntity.raycast(this.reach.getValue(), 0, false);
        final ItemStack mainHandStack = this.mc.player.getMainHandStack();
        final Item item = mainHandStack.getItem();
        final boolean invalidItem = mainHandStack.isEmpty() || !(item instanceof BlockItem || item instanceof SpawnEggItem);
        if (!(hitResult instanceof final BlockHitResult blockHitResult) || invalidItem) {
            return;
        }
        final Block block = this.mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
        if ((block instanceof AirBlock || block instanceof FluidBlock) && this.mc.options.useKey.isPressed()) {
            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, blockHitResult);
        }
    }

}
