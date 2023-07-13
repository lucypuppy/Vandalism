package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class GriefItemsCreativeTab extends CreativeTab {

    public GriefItemsCreativeTab() {
        super(new ItemStack(Items.TNT).setCustomName(Text.literal("Grief Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        final ItemStack griefCreeperSpawnEgg = new ItemStack(Items.CREEPER_SPAWN_EGG);
        final NbtCompound griefCreeperSpawnEggNBT = new NbtCompound();
        final NbtCompound griefCreeperSpawnEggEntityTag = new NbtCompound();
        griefCreeperSpawnEggEntityTag.putInt("Fuse", 0);
        griefCreeperSpawnEggEntityTag.putInt("ExplosionRadius", 127);
        griefCreeperSpawnEggNBT.put("EntityTag", griefCreeperSpawnEggEntityTag);
        griefCreeperSpawnEgg.setNbt(griefCreeperSpawnEggNBT);
        this.putClientsideName(griefCreeperSpawnEgg,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Grief Creeper"
                )
        );
        current.add(griefCreeperSpawnEgg);

        final ItemStack enchantedGriefCreeperSpawnEgg = new ItemStack(Items.CREEPER_SPAWN_EGG);
        final NbtCompound enchantedGriefCreeperSpawnEggNBT = new NbtCompound();
        final NbtCompound enchantedGriefCreeperSpawnEggEntityTag = new NbtCompound();
        enchantedGriefCreeperSpawnEggEntityTag.putInt("Fuse", 0);
        enchantedGriefCreeperSpawnEggEntityTag.putInt("ExplosionRadius", 127);
        enchantedGriefCreeperSpawnEggEntityTag.putByte("powered", (byte) 1);
        enchantedGriefCreeperSpawnEggNBT.put("EntityTag", enchantedGriefCreeperSpawnEggEntityTag);
        enchantedGriefCreeperSpawnEgg.setNbt(enchantedGriefCreeperSpawnEggNBT);
        this.putClientsideGlint(enchantedGriefCreeperSpawnEgg);
        this.putClientsideName(enchantedGriefCreeperSpawnEgg,
                Text.literal(
                        Formatting.DARK_PURPLE + Formatting.BOLD.toString() + "Enchanted " +
                                Formatting.RED + Formatting.BOLD + "Grief Creeper"
                )
        );
        current.add(enchantedGriefCreeperSpawnEgg);

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

        return current;
    }

}
