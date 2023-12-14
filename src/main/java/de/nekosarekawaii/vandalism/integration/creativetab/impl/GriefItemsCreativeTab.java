package de.nekosarekawaii.vandalism.integration.creativetab.impl;

import de.nekosarekawaii.vandalism.integration.creativetab.AbstractCreativeTab;
import de.nekosarekawaii.vandalism.util.minecraft.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static de.nekosarekawaii.vandalism.util.minecraft.ItemStackUtil.withClientSide;

public class GriefItemsCreativeTab extends AbstractCreativeTab {

    public GriefItemsCreativeTab() {
        super(Text.literal("Grief Items"), Items.TNT);
    }

    @Override
    public void exposeItems(List<ItemStack> items) {
        items.add(withClientSide(createGriefCreeper(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Grief Creeper")));
        items.add(withClientSide(createPoweredGriefCreeper(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Powered " + Formatting.RED + Formatting.BOLD + "Grief Creeper"), true));
        items.add(withClientSide(ItemStackUtil.createSpawnEggItemStack((SpawnEggItem) Items.SHEEP_SPAWN_EGG, "minecraft:ender_dragon"), Text.literal(Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Ender Dragon")));
        items.add(withClientSide(ItemStackUtil.createSpawnEggItemStack((SpawnEggItem) Items.COW_SPAWN_EGG, "minecraft:wither"), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "Wither")));
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
