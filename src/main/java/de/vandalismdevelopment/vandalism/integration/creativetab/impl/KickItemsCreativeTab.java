package de.vandalismdevelopment.vandalism.integration.creativetab.impl;

import de.vandalismdevelopment.vandalism.integration.creativetab.AbstractCreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static de.vandalismdevelopment.vandalism.util.ItemStackUtil.withClientSide;

public class KickItemsCreativeTab extends AbstractCreativeTab {

    public KickItemsCreativeTab() {
        super(Text.literal("Kick Items"), Items.FIREWORK_ROCKET);
    }

    @Override
    public void exposeItems(List<ItemStack> items) {
        items.add(withClientSide(createKickHead(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head")));
        items.add(withClientSide(createKickHeadV2(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head V2")));
        items.add(withClientSide(createKickStand(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Stand")));
    }

    private static ItemStack createKickHead() {
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

    private static ItemStack createKickHeadV2() {
        final ItemStack item = new ItemStack(Items.PLAYER_HEAD);
        final NbtCompound base = new NbtCompound();
        final NbtCompound blockEntityTag = new NbtCompound();
        blockEntityTag.putString("note_block_sound", RandomStringUtils.randomAlphabetic(5).toLowerCase().repeat(6552));
        base.put("BlockEntityTag", blockEntityTag);
        item.setNbt(base);
        return item;
    }

    private static ItemStack createKickStand() {
        final ItemStack item = new ItemStack(Items.ARMOR_STAND);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        final NbtList armorItems = new NbtList();
        final NbtCompound firstArmorItem = new NbtCompound();
        final NbtCompound armorItemBase = new NbtCompound();
        final NbtCompound armorTrim = new NbtCompound();
        armorTrim.putString("pattern", RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase());
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
