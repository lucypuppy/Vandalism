package de.vandalismdevelopment.vandalism.creativetab.impl;

import de.vandalismdevelopment.vandalism.creativetab.CreativeTab;
import net.minecraft.item.Item;
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
        super(
                Text.literal("Troll Items"),
                new ItemStack(Items.END_CRYSTAL)
        );
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        for (final Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            current.add(createItem(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
        }
        current.add(createItem(createKillArea(), Text.literal(Formatting.RED + "Kill Area"), "EnZaXD"));
        current.add(createItem(createWhiteHole(), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole"), "NekosAreKawaii"));
        current.add(createItem(createBlackHole(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole"), "AdvancedCode"));
        current.add(createItem(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area"), "NekosAreKawaii"));
        current.add(createItem(createStargazer(), Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"), true, "ShyDev"));
        current.add(createItem(createShyDevVibing(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "ShyDev Vibing"), true, "ShyDev"));

        return current;
    }

    private ItemStack createShyDevVibing() {
        final ItemStack item = new ItemStack(Items.DONKEY_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();

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
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();

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
        final NbtCompound base = new NbtCompound();

        final NbtList customPotionEffects = new NbtList();
        customPotionEffects.add(createEffect("instant_health", 2000, 125, false));
        base.put("custom_potion_effects", customPotionEffects);

        origin.setNbt(base);

        return origin;
    }

    private ItemStack createKillArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();

        final NbtList effects = new NbtList();
        effects.add(createEffect("instant_health", 20, 125, false));
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

    private ItemStack createWhiteHole() {
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

    private ItemStack createBlackHole() {
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

    private ItemStack createEventHorizonArea() {
        final ItemStack item = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();

        final NbtList effects = new NbtList();
        effects.add(createEffect("slowness", 170, 125, false));
        effects.add(createEffect("mining_fatigue", 150, 125, false));
        effects.add(createEffect("resistance ", 170, 125, false));
        effects.add(createEffect("invisibility", 130, 1, false));
        effects.add(createEffect("weakness", 170, 125, false));
        effects.add(createEffect("wither", 160, 1, false));
        effects.add(createEffect("levitation", 19, 125, false));
        effects.add(createEffect("darkness", 170, 125, false));
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

}
