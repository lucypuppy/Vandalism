package de.vandalismdevelopment.vandalism.creativetab.impl;

import de.vandalismdevelopment.vandalism.creativetab.CreativeTab;
import de.vandalismdevelopment.vandalism.util.ItemUtil;
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
            current.add(ItemUtil.createItemStack(
                    this.createKillPotion(new ItemStack(item)),
                    Text.literal(Formatting.RED + "Kill Potion")
            ));
        }
        current.add(ItemUtil.createItemStack(
                this.createKillArea(),
                Text.literal(Formatting.RED + "Kill Area")
        ));
        current.add(ItemUtil.createItemStack(
                this.createWhiteHole(),
                Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole")
        ));
        current.add(ItemUtil.createItemStack(
                this.createBlackHole(),
                Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole")
        ));
        current.add(ItemUtil.createItemStack(
                this.createEventHorizonArea(),
                Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")
        ));
        current.add(ItemUtil.createItemStack(
                this.createStargazer(),
                Text.literal(Formatting.YELLOW + Formatting.BOLD.toString() + "Stargazer"),
                true
        ));
        return current;
    }

    private ItemStack createStargazer() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Steps", Integer.MIN_VALUE);
        entityTag.putString("id", "minecraft:shulker_bullet");
        entityTag.putString(
                "CustomName",
                Text.Serializer.toJson(Text.literal("*").formatted(Formatting.YELLOW, Formatting.BOLD))
        );
        entityTag.putByte("CustomNameVisible", (byte) 1);
        entityTag.putByte("NoGravity", (byte) 1);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private ItemStack createKillPotion(final ItemStack origin) {
        final NbtCompound base = new NbtCompound();
        final NbtList customPotionEffects = new NbtList();
        customPotionEffects.add(ItemUtil.createEffectNBT("instant_health", 2000, 125, false));
        base.put("custom_potion_effects", customPotionEffects);
        origin.setNbt(base);
        return origin;
    }

    private ItemStack createKillArea() {
        final ItemStack item = new ItemStack(Items.SALMON_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList effects = new NbtList();
        effects.add(ItemUtil.createEffectNBT("instant_health", 20, 125, false));
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
        effects.add(ItemUtil.createEffectNBT("slowness", 170, 125, false));
        effects.add(ItemUtil.createEffectNBT("mining_fatigue", 150, 125, false));
        effects.add(ItemUtil.createEffectNBT("resistance ", 170, 125, false));
        effects.add(ItemUtil.createEffectNBT("invisibility", 130, 1, false));
        effects.add(ItemUtil.createEffectNBT("weakness", 170, 125, false));
        effects.add(ItemUtil.createEffectNBT("wither", 160, 1, false));
        effects.add(ItemUtil.createEffectNBT("levitation", 19, 125, false));
        effects.add(ItemUtil.createEffectNBT("darkness", 170, 125, false));
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
