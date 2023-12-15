package de.nekosarekawaii.vandalism.feature.creativetab.impl;

import de.nekosarekawaii.vandalism.feature.creativetab.AbstractCreativeTab;
import de.nekosarekawaii.vandalism.util.minecraft.ItemStackUtil;
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

import static de.nekosarekawaii.vandalism.util.minecraft.ItemStackUtil.withClientSide;

public class TrollItemsCreativeTab extends AbstractCreativeTab {

    public TrollItemsCreativeTab() {
        super(Text.literal("Troll Items"), Items.END_CRYSTAL);
    }

    @Override
    public void exposeItems(List<ItemStack> items) {
        for (Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            items.add(withClientSide(createTrollPotion(new ItemStack(item)), Text.literal(Formatting.GOLD + "Troll Potion")));
        }
        for (Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            items.add(withClientSide(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
        }
        items.add(withClientSide(createKillArea(), Text.literal(Formatting.RED + "Kill Area")));
        items.add(withClientSide(createWhiteHole(), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole")));
        items.add(withClientSide(createBlackHole(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole")));
        items.add(withClientSide(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")));
        items.add(withClientSide(createStargazer(), Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"), true));
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

    private static ItemStack createKillPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        customPotionEffects.add(ItemStackUtil.createEffectNBT("instant_health", 2000, 125, false));
        base.put("custom_potion_effects", customPotionEffects);
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
