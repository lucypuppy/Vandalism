/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.addonitemvault.creativetab;

import com.itemvault.fabric_platform_api.ItemVaultFabricBase;
import com.itemvault.fabric_platform_api.utils.WrappedItemStack;
import com.itemvault.file_format.WrappedItem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemVaultCreativeTab implements OutgoingPacketListener {

    private final List<ItemStack> TEMP_ITEMS = new ArrayList<>();

    private final Text name;
    private final ItemStack icon;
    private final ItemVaultFabricBase itemVault;

    public ItemVaultCreativeTab(final ItemVaultFabricBase itemVault) {
        this.name = Text.of("Item Vault");
        this.icon = new ItemStack(Items.CHEST);
        this.itemVault = itemVault;

        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket packet) {
            packet.stack = this.itemVault.finalizeStack(packet.stack());
        }
    }

    public void exposeItems(final List<ItemStack> items) {
        for (final WrappedItem<WrappedItemStack> item : this.itemVault.getPartialItems().values()) {
            items.add(this.itemVault.displayStack(item));
        }
    }

    public void publish() {
        final ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> this.icon).displayName(this.name).entries(((displayContext, entries) -> {
            if (this.TEMP_ITEMS.isEmpty()) {
                exposeItems(this.TEMP_ITEMS);
            }
            entries.addAll(this.TEMP_ITEMS);
        })).build();
        Registry.register(Registries.ITEM_GROUP, Identifier.of(
                FabricBootstrap.MOD_ID,
                this.name.getString().toLowerCase().replace(" ", "_")
        ), itemGroup);
    }

}
