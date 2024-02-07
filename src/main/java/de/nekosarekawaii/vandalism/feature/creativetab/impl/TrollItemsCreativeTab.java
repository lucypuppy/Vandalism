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

package de.nekosarekawaii.vandalism.feature.creativetab.impl;

import de.nekosarekawaii.vandalism.feature.creativetab.AbstractCreativeTab;
import de.nekosarekawaii.vandalism.util.game.ItemStackUtil;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

import static de.nekosarekawaii.vandalism.util.game.ItemStackUtil.withClientSide;

public class TrollItemsCreativeTab extends AbstractCreativeTab {

    public TrollItemsCreativeTab() {
        super(Text.literal("Troll Items"), Items.END_CRYSTAL);
    }

    @Override
    public void exposeItems(final List<ItemStack> items) {
        for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            items.add(withClientSide(createTrollPotion(new ItemStack(item)), Text.literal(Formatting.GOLD + "Troll Potion")));
        }
        for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            items.add(withClientSide(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
        }
        items.add(withClientSide(createKillArea(), Text.literal(Formatting.RED + "Kill Area")));
        items.add(withClientSide(createWhiteHole(), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole")));
        items.add(withClientSide(createBlackHole(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole")));
        items.add(withClientSide(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")));
        for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            items.add(withClientSide(createOldTrollPotion(new ItemStack(item)), Text.literal(Formatting.GOLD + "Troll Potion (Old)")));
        }
        for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            items.add(withClientSide(createOldKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion (Old)")));
        }
        items.add(withClientSide(createOldKillArea(), Text.literal(Formatting.RED + "Kill Area (Old)")));
        items.add(withClientSide(createOldEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area (Old)")));
        items.add(withClientSide(createStargazer(), Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"), true));
        items.add(withClientSide(createGhostBlock(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Ghost Block")));
    }

    private static ItemStack createTrollPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        for (final StatusEffect statusEffect : Registries.STATUS_EFFECT) {
            final Identifier id = Registries.STATUS_EFFECT.getId(statusEffect);
            if (id != null && id.getNamespace().equals("minecraft")) {
                customPotionEffects.add(ItemStackUtil.createEffectNBT(id.getPath(), 10000, 255, false));
            }
        }
        base.put("custom_potion_effects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private static ItemStack createOldTrollPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        int i = 0;
        for (final StatusEffect statusEffect : Registries.STATUS_EFFECT) {
            i++;
            final Identifier id = Registries.STATUS_EFFECT.getId(statusEffect);
            if (id != null && id.getNamespace().equals("minecraft")) {
                customPotionEffects.add(ItemStackUtil.createOldEffectNBT(i, 10000, 255, false));
            }
        }
        base.put("CustomPotionEffects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private static ItemStack createKillPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        customPotionEffects.add(ItemStackUtil.createEffectNBT("instant_health", 2000, 125, false));
        base.put("custom_potion_effects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private static ItemStack createOldKillPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        customPotionEffects.add(ItemStackUtil.createOldEffectNBT(6, 2000, 125, false));
        base.put("CustomPotionEffects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private static ItemStack createKillArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList effects = new NbtList();
        effects.add(ItemStackUtil.createEffectNBT("instant_health", 20, 125, false));
        entityTag.put("effects", effects);
        entityTag.putFloat("RadiusOnUse", 0.1f);
        entityTag.putFloat("RadiusPerTick", 0.01f);
        entityTag.putInt("Duration", 20000);
        entityTag.putFloat("Radius", 100f);
        entityTag.putInt("ReapplicationDelay", 40);
        entityTag.putString("Particle", "block cave_air");
        entityTag.putString("id", "minecraft:area_effect_cloud");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createOldKillArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList effects = new NbtList();
        effects.add(ItemStackUtil.createOldEffectNBT(6, 20, 125, false));
        entityTag.put("Effects", effects);
        entityTag.putFloat("RadiusOnUse", 0.1f);
        entityTag.putFloat("RadiusPerTick", 0.01f);
        entityTag.putInt("Duration", 20000);
        entityTag.putFloat("Radius", 100f);
        entityTag.putInt("ReapplicationDelay", 40);
        entityTag.putString("Particle", "block cave_air");
        entityTag.putString("id", "minecraft:area_effect_cloud");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createWhiteHole() {
        final ItemStack item = new ItemStack(Items.CHICKEN_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putFloat("RadiusOnUse", 0.1f);
        entityTag.putFloat("RadiusPerTick", 0.01f);
        entityTag.putInt("Duration", 20000);
        entityTag.putFloat("Radius", 100f);
        entityTag.putInt("ReapplicationDelay", 40);
        entityTag.putString("Particle", "flash");
        entityTag.putString("id", "minecraft:area_effect_cloud");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createBlackHole() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putByte("shadow", (byte) 1);
        entityTag.putFloat("shadow_strength", 10000000f);
        entityTag.putFloat("shadow_radius", 10000000f);
        entityTag.putFloat("view_range", 10000000f);
        entityTag.putString("id", "minecraft:text_display");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createEventHorizonArea() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList effects = new NbtList();
        effects.add(ItemStackUtil.createEffectNBT("slowness", 170, 125, false));
        effects.add(ItemStackUtil.createEffectNBT("mining_fatigue", 150, 125, false));
        effects.add(ItemStackUtil.createEffectNBT("resistance ", 170, 125, false));
        effects.add(ItemStackUtil.createEffectNBT("invisibility", 130, 1, false));
        effects.add(ItemStackUtil.createEffectNBT("weakness", 170, 125, false));
        effects.add(ItemStackUtil.createEffectNBT("wither", 160, 1, false));
        effects.add(ItemStackUtil.createEffectNBT("levitation", 19, 125, false));
        effects.add(ItemStackUtil.createEffectNBT("darkness", 170, 125, false));
        entityTag.put("effects", effects);
        entityTag.putFloat("RadiusOnUse", 0.1f);
        entityTag.putFloat("RadiusPerTick", 0.01f);
        entityTag.putInt("Duration", 20000);
        entityTag.putFloat("Radius", 100f);
        entityTag.putInt("ReapplicationDelay", 40);
        entityTag.putString("Particle", "item air");
        entityTag.putString("id", "minecraft:area_effect_cloud");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createOldEventHorizonArea() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList effects = new NbtList();
        effects.add(ItemStackUtil.createOldEffectNBT(2, 170, 125, false));
        effects.add(ItemStackUtil.createOldEffectNBT(4, 150, 125, false));
        effects.add(ItemStackUtil.createOldEffectNBT(11, 170, 125, false));
        effects.add(ItemStackUtil.createOldEffectNBT(14, 130, 1, false));
        effects.add(ItemStackUtil.createOldEffectNBT(18, 170, 125, false));
        effects.add(ItemStackUtil.createOldEffectNBT(20, 160, 1, false));
        effects.add(ItemStackUtil.createOldEffectNBT(25, 19, 125, false));
        effects.add(ItemStackUtil.createOldEffectNBT(33, 170, 125, false));
        entityTag.put("Effects", effects);
        entityTag.putFloat("RadiusOnUse", 0.1f);
        entityTag.putFloat("RadiusPerTick", 0.01f);
        entityTag.putInt("Duration", 20000);
        entityTag.putFloat("Radius", 100f);
        entityTag.putInt("ReapplicationDelay", 40);
        entityTag.putString("Particle", "item air");
        entityTag.putString("id", "minecraft:area_effect_cloud");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createStargazer() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Steps", Integer.MIN_VALUE);
        entityTag.putString("id", "minecraft:shulker_bullet");
        entityTag.putString("CustomName", Text.Serialization.toJsonString(Text.literal("*").formatted(Formatting.YELLOW, Formatting.BOLD)));
        entityTag.putByte("CustomNameVisible", (byte) 1);
        entityTag.putByte("NoGravity", (byte) 1);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createGhostBlock() {
        final ItemStack item = new ItemStack(Items.JIGSAW);
        final NbtCompound base = new NbtCompound();
        final NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.putString("pool", "funny:Funny");
        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);
        return item;
    }

}
