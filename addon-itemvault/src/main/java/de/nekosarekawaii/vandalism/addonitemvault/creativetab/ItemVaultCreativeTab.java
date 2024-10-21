/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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
import com.itemvault.fabric_platform_api.WrappedItemStack;
import com.itemvault.file_format.WrappedItem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;

import java.util.List;

public class ItemVaultCreativeTab extends CreativeTab implements OutgoingPacketListener {

    private final ItemVaultFabricBase instance;

    public ItemVaultCreativeTab(final ItemVaultFabricBase instance) {
        super(Text.of("Item Vault"), Items.CHEST);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this);

        this.instance = instance;
    }

    @Override
    public void exposeItems(List<ItemStack> items) {
        for (WrappedItem<WrappedItemStack> item : instance.getPartialItems().values()) {
            items.add(instance.displayStack(item));
        }
    }

    @Override
    public void onOutgoingPacket(OutgoingPacketEvent event) {
        if (event.packet instanceof CreativeInventoryActionC2SPacket packet) {
            packet.stack = instance.finalizeStack(packet.stack());
        }
    }
}
