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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potions;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

import static de.nekosarekawaii.vandalism.util.ItemStackUtil.withClientSide;

public class TrollItemsCreativeTab extends AbstractCreativeTab {

    public TrollItemsCreativeTab() {
        super(Text.literal("Troll Items"), Items.END_CRYSTAL);
    }

    @Override
    public void exposeItems(final List<ItemStack> items) {
        final List<Item> potionTypes = Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        for (final Item item : potionTypes) {
            items.add(withClientSide(createTrollPotion(new ItemStack(item)), Text.literal(Formatting.GOLD + "Troll Potion")));
        }
        for (final Item item : potionTypes) {
            items.add(withClientSide(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
        }
        items.add(withClientSide(createKillArea(), Text.literal(Formatting.RED + "Kill Area")));
        items.add(withClientSide(createWhiteHole(), Text.literal(Formatting.WHITE + "White Hole")));
        items.add(withClientSide(createBlackHole(), Text.literal(Formatting.WHITE + "Black Hole")));
        items.add(withClientSide(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")));
        items.add(withClientSide(createStargazer(), Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"), true));
        items.add(withClientSide(createGhostBlock(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Ghost Block")));
        items.add(withClientSide(createWardenSummonBlock(), Text.literal(Formatting.DARK_AQUA + Formatting.BOLD.toString() + "Warden Summon Block")));
        items.add(withClientSide(createUnstableTNTBlock(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Unstable TNT Block")));
        items.add(withClientSide(createGroundBugBoots(), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Ground Bug Boots")));
        items.add(withClientSide(createCreativeItemControlItem(true), Text.literal(Formatting.DARK_RED + Formatting.BOLD.toString() + "Creative Item Control (Kick)")));
        items.add(withClientSide(createCreativeItemControlItem(false), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Creative Item Control (Clear Chat)")));
    }


    private static ItemStack createTrollPotion(final ItemStack origin) {
        final List<StatusEffectInstance> statusEffects = new ArrayList<>();
        for (final StatusEffect statusEffect : Registries.STATUS_EFFECT) {
            final Identifier id = Registries.STATUS_EFFECT.getId(statusEffect);
            if (id != null && id.getNamespace().equals("minecraft")) {
                statusEffects.add(new StatusEffectInstance(
                        Registries.STATUS_EFFECT.getEntry(statusEffect),
                        10000,
                        255,
                        false,
                        false
                ));
            }
        }
        origin.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(
                Optional.of(Potions.LUCK),
                Optional.empty(),
                statusEffects
        ));
        return origin;
    }

    private static ItemStack createKillPotion(final ItemStack origin) {
        final List<StatusEffectInstance> statusEffects = List.of(new StatusEffectInstance(
                StatusEffects.INSTANT_HEALTH,
                2000,
                125,
                false,
                false
        ));
        origin.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(
                Optional.of(Potions.HEALING),
                Optional.empty(),
                statusEffects
        ));
        return origin;
    }

    private static ItemStack createKillArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        final NbtCompound potionContents = new NbtCompound();
        potionContents.putString("potion", "minecraft:healing");
        final NbtList customEffects = new NbtList();
        final NbtCompound customEffect = new NbtCompound();
        customEffect.putString("id", "minecraft:instant_health");
        customEffect.putByte("show_particles", (byte) 0);
        customEffect.putByte("show_icon", (byte) 0);
        customEffect.putByte("ambient", (byte) 0);
        customEffect.putInt("duration", 20);
        customEffect.putByte("amplifier", (byte) 125);
        customEffects.add(customEffect);
        potionContents.put("custom_effects", customEffects);
        entityData.put("potion_contents", potionContents);
        entityData.putFloat("RadiusOnUse", 0.1f);
        entityData.putFloat("RadiusPerTick", 0.01f);
        entityData.putInt("Duration", 20000);
        entityData.putFloat("Radius", 100f);
        entityData.putInt("ReapplicationDelay", 40);
        final NbtCompound particle = new NbtCompound();
        particle.putString("type", "block");
        particle.putString("block_state", "minecraft:cave_air");
        entityData.put("Particle", particle);
        entityData.putString("id", "minecraft:area_effect_cloud");
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createWhiteHole() {
        final ItemStack item = new ItemStack(Items.PANDA_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        final NbtCompound particle = new NbtCompound();
        particle.putString("type", "flash");
        entityData.put("Particle", particle);
        entityData.putFloat("RadiusOnUse", 0.1f);
        entityData.putFloat("RadiusPerTick", 0.01f);
        entityData.putInt("Duration", 20000);
        entityData.putFloat("Radius", 100f);
        entityData.putInt("ReapplicationDelay", 40);
        entityData.putString("id", "minecraft:area_effect_cloud");
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createBlackHole() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        entityData.putByte("shadow", (byte) 1);
        entityData.putFloat("shadow_strength", 10000000f);
        entityData.putFloat("shadow_radius", 10000000f);
        entityData.putFloat("view_range", 10000000f);
        entityData.putString("id", "minecraft:text_display");
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createEventHorizonArea() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        final NbtCompound potionContents = new NbtCompound();
        potionContents.putString("potion", "minecraft:healing");
        final NbtList customEffects = new NbtList();

        final NbtCompound slownessEffect = new NbtCompound();
        slownessEffect.putString("id", "minecraft:slowness");
        slownessEffect.putByte("show_particles", (byte) 0);
        slownessEffect.putByte("show_icon", (byte) 0);
        slownessEffect.putByte("ambient", (byte) 0);
        slownessEffect.putInt("duration", 170);
        slownessEffect.putByte("amplifier", (byte) 125);
        customEffects.add(slownessEffect);

        final NbtCompound miningFatigueEffect = new NbtCompound();
        miningFatigueEffect.putString("id", "minecraft:mining_fatigue");
        miningFatigueEffect.putByte("show_particles", (byte) 0);
        miningFatigueEffect.putByte("show_icon", (byte) 0);
        miningFatigueEffect.putByte("ambient", (byte) 0);
        miningFatigueEffect.putInt("duration", 150);
        miningFatigueEffect.putByte("amplifier", (byte) 125);
        customEffects.add(miningFatigueEffect);

        final NbtCompound resistanceEffect = new NbtCompound();
        resistanceEffect.putString("id", "minecraft:resistance");
        resistanceEffect.putByte("show_particles", (byte) 0);
        resistanceEffect.putByte("show_icon", (byte) 0);
        resistanceEffect.putByte("ambient", (byte) 0);
        resistanceEffect.putInt("duration", 170);
        resistanceEffect.putByte("amplifier", (byte) 125);
        customEffects.add(resistanceEffect);

        final NbtCompound invisibilityEffect = new NbtCompound();
        invisibilityEffect.putString("id", "minecraft:invisibility");
        invisibilityEffect.putByte("show_particles", (byte) 0);
        invisibilityEffect.putByte("show_icon", (byte) 0);
        invisibilityEffect.putByte("ambient", (byte) 0);
        invisibilityEffect.putInt("duration", 130);
        invisibilityEffect.putByte("amplifier", (byte) 1);
        customEffects.add(invisibilityEffect);

        final NbtCompound weaknessEffect = new NbtCompound();
        weaknessEffect.putString("id", "minecraft:weakness");
        weaknessEffect.putByte("show_particles", (byte) 0);
        weaknessEffect.putByte("show_icon", (byte) 0);
        weaknessEffect.putByte("ambient", (byte) 0);
        weaknessEffect.putInt("duration", 170);
        weaknessEffect.putByte("amplifier", (byte) 125);
        customEffects.add(weaknessEffect);

        final NbtCompound witherEffect = new NbtCompound();
        witherEffect.putString("id", "minecraft:wither");
        witherEffect.putByte("show_particles", (byte) 0);
        witherEffect.putByte("show_icon", (byte) 0);
        witherEffect.putByte("ambient", (byte) 0);
        witherEffect.putInt("duration", 160);
        witherEffect.putByte("amplifier", (byte) 1);
        customEffects.add(witherEffect);

        final NbtCompound levitationEffect = new NbtCompound();
        levitationEffect.putString("id", "minecraft:levitation");
        levitationEffect.putByte("show_particles", (byte) 0);
        levitationEffect.putByte("show_icon", (byte) 0);
        levitationEffect.putByte("ambient", (byte) 0);
        levitationEffect.putInt("duration", 19);
        levitationEffect.putByte("amplifier", (byte) 125);
        customEffects.add(levitationEffect);

        final NbtCompound darknessEffect = new NbtCompound();
        darknessEffect.putString("id", "minecraft:darkness");
        darknessEffect.putByte("show_particles", (byte) 0);
        darknessEffect.putByte("show_icon", (byte) 0);
        darknessEffect.putByte("ambient", (byte) 0);
        darknessEffect.putInt("duration", 170);
        darknessEffect.putByte("amplifier", (byte) 125);
        customEffects.add(darknessEffect);

        potionContents.put("custom_effects", customEffects);
        entityData.put("potion_contents", potionContents);
        final NbtCompound particle = new NbtCompound();
        particle.putString("type", "reverse_portal"); // R.I.P. bug particle
        entityData.put("Particle", particle);
        entityData.putFloat("RadiusOnUse", 0.1f);
        entityData.putFloat("RadiusPerTick", 0.01f);
        entityData.putInt("Duration", 20000);
        entityData.putFloat("Radius", 100f);
        entityData.putInt("ReapplicationDelay", 40);
        entityData.putString("id", "minecraft:area_effect_cloud");
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createStargazer() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", "minecraft:shulker_bullet");
        entityData.putInt("Steps", Integer.MIN_VALUE);
        entityData.putByte("NoGravity", (byte) 1);
        entityData.putByte("CustomNameVisible", (byte) 1);
        entityData.putString("CustomName", Text.Serialization.toJsonString(Text.literal("*").formatted(Formatting.YELLOW, Formatting.BOLD), DynamicRegistryManager.EMPTY));
        item.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return item;
    }

    private static ItemStack createGhostBlock() {
        final ItemStack item = new ItemStack(Items.JIGSAW);
        final NbtCompound blockEntityData = new NbtCompound();
        blockEntityData.putString("pool", "Hacked:YouHaveBeenHacked");
        blockEntityData.putString("id", "minecraft:jigsaw");
        item.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(blockEntityData));
        return item;
    }

    private static ItemStack createWardenSummonBlock() {
        final ItemStack item = new ItemStack(Blocks.SCULK_SHRIEKER);
        final NbtCompound blockEntityData = new NbtCompound();
        blockEntityData.putString("id", "minecraft:sculk_shrieker");
        blockEntityData.putInt("warning_level", 4);
        item.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(blockEntityData));
        final Map<String, String> blockStateMap = new HashMap<>();
        blockStateMap.put("can_summon", "true");
        blockStateMap.put("shrieking", "true");
        item.set(DataComponentTypes.BLOCK_STATE, new BlockStateComponent(blockStateMap));
        return item;
    }

    private static ItemStack createUnstableTNTBlock() {
        final ItemStack item = new ItemStack(Blocks.TNT);
        final Map<String, String> blockStateMap = new HashMap<>();
        blockStateMap.put("unstable", "true");
        item.set(DataComponentTypes.BLOCK_STATE, new BlockStateComponent(blockStateMap));
        return item;
    }

    private static ItemStack createGroundBugBoots() {
        final ItemStack item = new ItemStack(Items.DIAMOND_BOOTS);
        final AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
        final EntityAttributeModifier attributeModifier = new EntityAttributeModifier(
                Identifier.of("minecraft:movement_speed"),
                Double.NaN,
                EntityAttributeModifier.Operation.ADD_VALUE
        );
        builder.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, attributeModifier, AttributeModifierSlot.ANY);
        item.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        return item;
    }

    private static ItemStack createCreativeItemControlItem(final boolean kick) {
        final ItemStack item = new ItemStack(Items.COMMAND_BLOCK);
        final NbtCompound blockEntityData = new NbtCompound();
        blockEntityData.putString("id", "minecraft:command_block");
        blockEntityData.putDouble(UUID.randomUUID().toString(), Double.NaN);
        final StringBuilder hacked = new StringBuilder(), toAdd = new StringBuilder();
        for (int i = 0; i < (kick ? 5 : 8); i++) toAdd.append(' ').append(toAdd);
        for (int i = 0; i < (kick ? 900 : 2000); i++) hacked.append(kick ? "§c§l" : "").append(toAdd);
        blockEntityData.putString("z", hacked.toString());
        item.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(blockEntityData));
        return item;
    }

}
