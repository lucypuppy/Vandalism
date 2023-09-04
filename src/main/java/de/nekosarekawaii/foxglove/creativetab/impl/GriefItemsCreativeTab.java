package de.nekosarekawaii.foxglove.creativetab.impl;

import de.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class GriefItemsCreativeTab extends CreativeTab {

    public GriefItemsCreativeTab() {
        super(
                Text.literal("Grief Items"),
                new ItemStack(Items.TNT)
        );
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();

        current.add(createItem(createGriefCreeper(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Grief Creeper")));
        current.add(createItem(createPoweredGriefCreeper(), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Powered " + Formatting.RED + Formatting.BOLD + "Grief Creeper"), true, "NekosAreKawaii"));
        current.add(createItem(createSpawnEgg(Items.SHEEP_SPAWN_EGG, "minecraft:ender_dragon"), Text.literal(Formatting.LIGHT_PURPLE + Formatting.BOLD.toString() + "Ender Dragon")));
        current.add(createItem(createSpawnEgg(Items.COW_SPAWN_EGG, "minecraft:wither"), Text.literal(Formatting.WHITE + Formatting.BOLD.toString() + "Wither")));

        return current;
    }

    private ItemStack createGriefCreeper() {
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

    private ItemStack createPoweredGriefCreeper() {
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

    private ItemStack createSpawnEgg(final Item origin, final String id) {
        final ItemStack item = new ItemStack(origin);
        final NbtCompound base = new NbtCompound();

        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("id", id);

        base.put("EntityTag", entityTag);
        item.setNbt(base);

        return item;
    }

}
