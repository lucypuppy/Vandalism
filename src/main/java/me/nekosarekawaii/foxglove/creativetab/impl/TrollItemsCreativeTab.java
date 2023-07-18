package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class TrollItemsCreativeTab extends CreativeTab {

    public TrollItemsCreativeTab() {
        super(new ItemStack(Items.END_CRYSTAL).setCustomName(Text.literal("Troll Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        final NbtCompound killPotionEffectNbt = new NbtCompound();
        killPotionEffectNbt.putInt("Amplifier", 125);
        killPotionEffectNbt.putInt("Duration", 2000);
        killPotionEffectNbt.putInt("Id", 6);
        final NbtList effects = new NbtList();
        effects.add(killPotionEffectNbt);
        final NbtCompound killPotionNbt = new NbtCompound();
        killPotionNbt.put("CustomPotionEffects", effects);

        final ItemStack killPotion = new ItemStack(Items.POTION);
        killPotion.setNbt(killPotionNbt);
        this.putClientsideName(
                killPotion,
                Text.literal(Formatting.RED + "Kill Potion")
        );
        current.add(killPotion);

        final ItemStack killPotionSplash = new ItemStack(Items.SPLASH_POTION);
        killPotionSplash.setNbt(killPotionNbt);
        this.putClientsideName(
                killPotionSplash,
                Text.literal(Formatting.RED + "Kill Potion")
        );
        current.add(killPotionSplash);

        final ItemStack killPotionLingering = new ItemStack(Items.LINGERING_POTION);
        killPotionLingering.setNbt(killPotionNbt);
        this.putClientsideName(
                killPotionLingering,
                Text.literal(Formatting.RED + "Kill Potion")
        );
        current.add(killPotionLingering);

        //bat_spawn_egg{EntityTag:{id:"minecraft:text_display",view_range:10000000f,shadow_radius:10000000f,shadow_strength:10000000f,shadow:1b}} 1

        final ItemStack blackHoleSpawnEgg = new ItemStack(Items.BAT_SPAWN_EGG);
        final NbtCompound blackHoleSpawnEggNBT = new NbtCompound();
        final NbtCompound blackHoleSpawnEggEntityTag = new NbtCompound();
        blackHoleSpawnEggEntityTag.putByte("shadow", (byte) 1);
        blackHoleSpawnEggEntityTag.putFloat("shadow_strength", 10000000f);
        blackHoleSpawnEggEntityTag.putFloat("shadow_radius", 10000000f);
        blackHoleSpawnEggEntityTag.putFloat("view_range", 10000000f);
        blackHoleSpawnEggEntityTag.putString("id", "minecraft:text_display");
        blackHoleSpawnEggNBT.put("EntityTag", blackHoleSpawnEggEntityTag);
        blackHoleSpawnEgg.setNbt(blackHoleSpawnEggNBT);
        this.putClientsideName(blackHoleSpawnEgg,
                Text.literal(
                        Formatting.WHITE + Formatting.BOLD.toString() + "Black Hole"
                )
        );
        current.add(blackHoleSpawnEgg);

        return current;
    }

}
