package de.vandalismdevelopment.vandalism.creativetab.impl;

import de.vandalismdevelopment.vandalism.creativetab.CreativeTab;
import de.vandalismdevelopment.vandalism.util.PlayerUtil;
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

public class TrollItemsCreativeTab extends CreativeTab {

    public TrollItemsCreativeTab() {
        super(Text.literal("Troll Items"), new ItemStack(Items.END_CRYSTAL), entries -> {
            for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
                entries.add(PlayerUtil.appendClientSideInfoToItemStack(createTrollPotion(new ItemStack(item)), Text.literal(Formatting.GOLD + "Troll Potion")));
            }
            for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
                entries.add(PlayerUtil.appendClientSideInfoToItemStack(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
            }
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createKillArea(), Text.literal(Formatting.RED + "Kill Area")));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createWhiteHole(), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole")));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createBlackHole(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole")));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createStargazer(), Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"), true));
        });
    }

    private static ItemStack createTrollPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        for (final StatusEffect statusEffect : Registries.STATUS_EFFECT) {
            final Identifier id = Registries.STATUS_EFFECT.getId(statusEffect);
            if (id != null && id.getNamespace().equals("minecraft")) {
                customPotionEffects.add(PlayerUtil.createEffectNBT(id.getPath(), 10000, 255, false));
            }
        }
        base.put("custom_potion_effects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private static ItemStack createKillPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        customPotionEffects.add(PlayerUtil.createEffectNBT("instant_health", 2000, 125, false));
        base.put("custom_potion_effects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private static ItemStack createKillArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList effects = new NbtList();
        effects.add(PlayerUtil.createEffectNBT("instant_health", 20, 125, false));
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

    private static ItemStack createWhiteHole() {
        final ItemStack item = new ItemStack(Items.PANDA_SPAWN_EGG);
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
        effects.add(PlayerUtil.createEffectNBT("slowness", 170, 125, false));
        effects.add(PlayerUtil.createEffectNBT("mining_fatigue", 150, 125, false));
        effects.add(PlayerUtil.createEffectNBT("resistance ", 170, 125, false));
        effects.add(PlayerUtil.createEffectNBT("invisibility", 130, 1, false));
        effects.add(PlayerUtil.createEffectNBT("weakness", 170, 125, false));
        effects.add(PlayerUtil.createEffectNBT("wither", 160, 1, false));
        effects.add(PlayerUtil.createEffectNBT("levitation", 19, 125, false));
        effects.add(PlayerUtil.createEffectNBT("darkness", 170, 125, false));
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

    private static ItemStack createStargazer() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Steps", Integer.MIN_VALUE);
        entityTag.putString("id", "minecraft:shulker_bullet");
        entityTag.putString("CustomName", Text.Serializer.toJson(Text.literal("*").formatted(Formatting.YELLOW, Formatting.BOLD)));
        entityTag.putByte("CustomNameVisible", (byte) 1);
        entityTag.putByte("NoGravity", (byte) 1);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

}
