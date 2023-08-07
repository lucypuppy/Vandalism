package me.nekosarekawaii.foxglove.creativetab.impl;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Arrays;
import java.util.Collection;

public class TrollItemsCreativeTab extends CreativeTab {

    public TrollItemsCreativeTab() {
        super(new ItemStack(Items.END_CRYSTAL).setCustomName(Text.literal("Troll Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        final VersionEnum targetVersion = ProtocolHack.getTargetVersion();

        for (Item item : Arrays.asList(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            current.add(createItem(createKillPotion(new ItemStack(item)), Text.literal(Formatting.RED + "Kill Potion")));
        }
        current.add(createItem(createKillArea(), Text.literal(Formatting.RED + "Kill Area")));
        current.add(createItem(createWhiteHole(), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "White Hole")));
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            current.add(createItem(createBlackHole(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Black Hole")));
        }
        current.add(createItem(createEventHorizonArea(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Event Horizon Area")));
        current.add(createItem(createConsoleErrorEntity(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Console Error Entity"), Text.literal(Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Works on Scissors")));
        current.add(createItem(createConsoleErrorHead(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Console Error Head"), Text.literal(Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Works on Scissors")));
        return current;
    }

    private ItemStack createKillPotion(final ItemStack origin) {
        final var base = new NbtCompound();

        final NbtList customPotionEffects = new NbtList();

        final NbtCompound customPotionEffect = new NbtCompound();
        customPotionEffect.putInt("Amplifier", 125);
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

    private NbtCompound createEffect(final int id, final int duration, final int amplifier) {
        final NbtCompound effect = new NbtCompound();
        effect.putInt("Id", id);
        effect.putByte("ShowParticles", (byte) 0);
        effect.putInt("Duration", duration);
        effect.putByte("Amplifier", (byte) amplifier);

        return effect;
    }

    private ItemStack createEventHorizonArea() {
        final var item = new ItemStack(Items.BAT_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
        final var effects = new NbtList();

        effects.add(createEffect(2, 170, 125));
        effects.add(createEffect(4, 150, 125));
        effects.add(createEffect(11, 170, 125));
        effects.add(createEffect(14, 130, 1));
        effects.add(createEffect(18, 170, 125));
        effects.add(createEffect(20, 160, 1));
        effects.add(createEffect(25, 19, 125));
        effects.add(createEffect(33, 170, 125));

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

    private ItemStack createConsoleErrorEntity() {
        final var item = new ItemStack(Items.HORSE_SPAWN_EGG);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();
        entityTag.putByte("pickup", (byte) 3);
        entityTag.putString("id", "minecraft:arrow");

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

    private ItemStack createConsoleErrorHead() {
        final var item = new ItemStack(Items.PLAYER_HEAD);
        final var base = new NbtCompound();

        final var skullOwner = new NbtCompound();
        skullOwner.putIntArray("Id", new int[]{1, 2, 3, 4});
        skullOwner.putString("Name", " ");

        base.put("SkullOwner", skullOwner);
        item.setNbt(base);

        return item;
    }
}
