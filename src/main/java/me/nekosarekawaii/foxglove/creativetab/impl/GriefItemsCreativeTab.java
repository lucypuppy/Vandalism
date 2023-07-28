package me.nekosarekawaii.foxglove.creativetab.impl;

import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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

        final ItemStack griefCreeperSpawnEgg = new ItemStack(Items.PIG_SPAWN_EGG);
        final NbtCompound griefCreeperSpawnEggNBT = new NbtCompound();
        final NbtCompound griefCreeperSpawnEggEntityTag = new NbtCompound();
        griefCreeperSpawnEggEntityTag.putInt("Fuse", 0);
        griefCreeperSpawnEggEntityTag.putInt("ExplosionRadius", 127);
        griefCreeperSpawnEggEntityTag.putString("id", "minecraft:creeper");
        griefCreeperSpawnEggNBT.put("EntityTag", griefCreeperSpawnEggEntityTag);
        griefCreeperSpawnEgg.setNbt(griefCreeperSpawnEggNBT);
        this.putClientsideName(griefCreeperSpawnEgg,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Grief Creeper"
                )
        );
        current.add(griefCreeperSpawnEgg);

        final ItemStack enchantedGriefCreeperSpawnEgg = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound enchantedGriefCreeperSpawnEggNBT = new NbtCompound();
        final NbtCompound enchantedGriefCreeperSpawnEggEntityTag = new NbtCompound();
        enchantedGriefCreeperSpawnEggEntityTag.putInt("Fuse", 0);
        enchantedGriefCreeperSpawnEggEntityTag.putInt("ExplosionRadius", 127);
        enchantedGriefCreeperSpawnEggEntityTag.putByte("powered", (byte) 1);
        enchantedGriefCreeperSpawnEggEntityTag.putString("id", "minecraft:creeper");
        enchantedGriefCreeperSpawnEggNBT.put("EntityTag", enchantedGriefCreeperSpawnEggEntityTag);
        enchantedGriefCreeperSpawnEgg.setNbt(enchantedGriefCreeperSpawnEggNBT);
        this.putClientsideGlint(enchantedGriefCreeperSpawnEgg);
        this.putClientsideName(enchantedGriefCreeperSpawnEgg,
                Text.literal(
                        Formatting.GOLD + Formatting.BOLD.toString() + "Powered " +
                                Formatting.RED + Formatting.BOLD + "Grief Creeper"
                )
        );
        current.add(enchantedGriefCreeperSpawnEgg);

        final ItemStack enderDragonSpawnEgg = new ItemStack(Items.SHEEP_SPAWN_EGG);
        final NbtCompound enderDragonSpawnEggNBT = new NbtCompound();
        final NbtCompound enderDragonSpawnEggEntityTag = new NbtCompound();
        enderDragonSpawnEggEntityTag.putString("id", "minecraft:ender_dragon");
        enderDragonSpawnEggNBT.put("EntityTag", enderDragonSpawnEggEntityTag);
        enderDragonSpawnEgg.setNbt(enderDragonSpawnEggNBT);
        this.putClientsideName(enderDragonSpawnEgg,
                Text.literal(
                        Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Ender Dragon"
                )
        );
        current.add(enderDragonSpawnEgg);

        final ItemStack witherSpawnEgg = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound witherSpawnEggNBT = new NbtCompound();
        final NbtCompound witherSpawnEggEntityTag = new NbtCompound();
        witherSpawnEggEntityTag.putString("id", "minecraft:wither");
        witherSpawnEggNBT.put("EntityTag", witherSpawnEggEntityTag);
        witherSpawnEgg.setNbt(witherSpawnEggNBT);
        this.putClientsideName(witherSpawnEgg,
                Text.literal(
                        Formatting.WHITE + Formatting.BOLD.toString() + "Wither"
                )
        );
        current.add(witherSpawnEgg);

        return current;
    }

}
