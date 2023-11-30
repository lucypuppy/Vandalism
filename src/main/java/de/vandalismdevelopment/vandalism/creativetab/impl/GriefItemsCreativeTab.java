package de.vandalismdevelopment.vandalism.creativetab.impl;

import de.vandalismdevelopment.vandalism.creativetab.CreativeTab;
import de.vandalismdevelopment.vandalism.util.PlayerUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GriefItemsCreativeTab extends CreativeTab {

    public GriefItemsCreativeTab() {
        super(Text.literal("Grief Items"), new ItemStack(Items.TNT), entries -> {
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createGriefCreeper(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Grief Creeper")));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(createPoweredGriefCreeper(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Powered " + Formatting.RED + Formatting.BOLD + "Grief Creeper"), true));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(PlayerUtil.createSpawnEggItemStack((SpawnEggItem) Items.SHEEP_SPAWN_EGG, "minecraft:ender_dragon"), Text.literal(Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Ender Dragon")));
            entries.add(PlayerUtil.appendClientSideInfoToItemStack(PlayerUtil.createSpawnEggItemStack((SpawnEggItem) Items.COW_SPAWN_EGG, "minecraft:wither"), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "Wither")));
        });
    }

    private static ItemStack createGriefCreeper() {
        final ItemStack item = new ItemStack(Items.PIG_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Fuse", 0);
        entityTag.putInt("ExplosionRadius", 127);
        entityTag.putString("id", "minecraft:creeper");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createPoweredGriefCreeper() {
        final ItemStack item = new ItemStack(Items.COW_SPAWN_EGG);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putInt("Fuse", 0);
        entityTag.putInt("ExplosionRadius", 127);
        entityTag.putByte("powered", (byte) 1);
        entityTag.putString("id", "minecraft:creeper");
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

}
