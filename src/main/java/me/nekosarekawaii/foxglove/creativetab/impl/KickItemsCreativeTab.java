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

            final ItemStack kickStand = new ItemStack(Items.ARMOR_STAND);
            final NbtCompound kickStandNbt = new NbtCompound();
            final NbtCompound kickStandEntityTag = new NbtCompound();
            final NbtList kickStandEntityTagArmorItems = new NbtList();
            final NbtCompound kickStandEntityTagArmorItemsItem = new NbtCompound();
            final NbtCompound kickStandEntityTagArmorItemsItemTag = new NbtCompound();
            final NbtCompound kickStandEntityTagArmorItemsItemTagTrim = new NbtCompound();
            kickStandEntityTagArmorItemsItemTagTrim.putString("pattern", RandomStringUtils.random(5).toLowerCase() + ":" + RandomStringUtils.random(5).toUpperCase());
            kickStandEntityTagArmorItemsItemTagTrim.putString("material", "minecraft:amethyst");
            kickStandEntityTagArmorItemsItemTag.put("Trim", kickStandEntityTagArmorItemsItemTagTrim);
            kickStandEntityTagArmorItemsItem.put("tag", kickStandEntityTagArmorItemsItemTag);
            kickStandEntityTagArmorItemsItem.putByte("Count", (byte) 1);
            kickStandEntityTagArmorItemsItem.putString("id", "minecraft:diamond_helmet");
            kickStandEntityTagArmorItems.add(kickStandEntityTagArmorItemsItem);
            kickStandEntityTag.put("ArmorItems", kickStandEntityTagArmorItems);
            kickStandNbt.put("EntityTag", kickStandEntityTag);
            kickStand.setNbt(kickStandNbt);
            this.putClientsideName(kickStand,
                    Text.literal(
                            Formatting.RED + Formatting.BOLD.toString() + "Kick Stand"
                    )
            );
            current.add(kickStand);

        }

        return current;
    }

}
