package de.nekosarekawaii.foxglove.creativetab.impl;

import de.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Collection;

public class TrollItemsCreativeTab extends CreativeTab {

    public TrollItemsCreativeTab() {
        super(new ItemStack(Items.END_CRYSTAL).setCustomName(Text.literal("Troll Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        for (var item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            current.add(createItem(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
        }
        current.add(createItem(createKillArea(), Text.literal(Formatting.RED + "Kill Area")));
        current.add(createItem(createWhiteHole(), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole")));
        current.add(createItem(createBlackHole(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole")));
        current.add(createItem(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")));
        current.add(createItem(createStargazer(), Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"), true));
        current.add(createItem(createShyDevVibing(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "ShyDev Vibing"), true));

        return current;
    }

    private ItemStack createShyDevVibing() {
        final var item = new ItemStack(Items.DONKEY_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();

        entityTag.putInt("TreasurePosX", 0);
        entityTag.putInt("TreasurePosY", 0);
        entityTag.putInt("TreasurePosZ", 0);
        entityTag.putInt("Moistness", 1999980);
        entityTag.putString("id", "minecraft:dolphin");
        entityTag.putString("CustomName", Text.Serializer.toJson(Text.literal("\u2728 ShyDev \u2728").formatted(Formatting.GOLD, Formatting.BOLD)));
        entityTag.putByte("CustomNameVisible", (byte) 1);
        entityTag.putByte("Glowing", (byte) 1);
        entityTag.putByte("GotFish", (byte) 1);
        entityTag.putByte("CanFindTreasure", (byte) 1);

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createStargazer() {
        final var item = new ItemStack(Items.COW_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();

        entityTag.putInt("Steps", Integer.MIN_VALUE);
        entityTag.putString("id", "minecraft:shulker_bullet");
        entityTag.putString("CustomName", Text.Serializer.toJson(Text.literal("\u2728").formatted(Formatting.YELLOW, Formatting.BOLD)));
        entityTag.putByte("CustomNameVisible", (byte) 1);
        entityTag.putByte("NoGravity", (byte) 1);

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createKillPotion(final ItemStack origin) {
        final var base = new NbtCompound();

        final var customPotionEffects = new NbtList();

        final var customPotionEffect = new NbtCompound();
        customPotionEffect.putByte("Amplifier", (byte) 125);
        customPotionEffect.putInt("Duration", 2000);
        customPotionEffect.putInt("Id", 6);

        customPotionEffects.add(customPotionEffect);
        base.put("CustomPotionEffects", customPotionEffects);

        origin.setNbt(base);

        return origin;
    }

    private ItemStack createKillArea() {
        final var item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
        final var effects = new NbtList();

        final var firstEffect = new NbtCompound();
        firstEffect.putInt("Id", 6);
        firstEffect.putByte("ShowParticles", (byte) 0);
        firstEffect.putInt("Duration", 20);
        firstEffect.putByte("Amplifier", (byte) 125);

        effects.add(firstEffect);

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

    private ItemStack createWhiteHole() {
        final var item = new ItemStack(Items.PANDA_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
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

    private ItemStack createBlackHole() {
        final var item = new ItemStack(Items.BAT_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
        entityTag.putByte("shadow", (byte) 1);
        entityTag.putFloat("shadow_strength", 10000000f);
        entityTag.putFloat("shadow_radius", 10000000f);
        entityTag.putFloat("view_range", 10000000f);
        entityTag.putString("id", "minecraft:text_display");

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createEventHorizonArea() {
        final var item = new ItemStack(Items.BAT_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
        final var effects = new NbtList();

        effects.add(createEffect(2, 170, 125, false));
        effects.add(createEffect(4, 150, 125, false));
        effects.add(createEffect(11, 170, 125, false));
        effects.add(createEffect(14, 130, 1, false));
        effects.add(createEffect(18, 170, 125, false));
        effects.add(createEffect(20, 160, 1, false));
        effects.add(createEffect(25, 19, 125, false));
        effects.add(createEffect(33, 170, 125, false));

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

}
