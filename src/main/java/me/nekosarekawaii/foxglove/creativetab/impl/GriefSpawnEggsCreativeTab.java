package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class GriefSpawnEggsCreativeTab extends CreativeTab {

    public GriefSpawnEggsCreativeTab() {
        super(new ItemStack(Items.CREEPER_SPAWN_EGG).setCustomName(Text.literal("Grief Spawn Eggs")));
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

        return current;
    }

}
