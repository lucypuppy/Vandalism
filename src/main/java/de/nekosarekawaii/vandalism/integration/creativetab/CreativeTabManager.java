/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.creativetab;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.integration.creativetab.impl.*;
import de.nekosarekawaii.vandalism.util.common.Storage;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.UUID;

public class CreativeTabManager extends Storage<AbstractCreativeTab> implements OutgoingPacketListener {

    public static final String CLIENTSIDE_NAME = UUID.randomUUID().toString();

    public CreativeTabManager() {
        this.setAddConsumer(AbstractCreativeTab::publish);
    }

    @Override
    public void init() {
        this.add(
                new CrashItemsCreativeTab(),
                new KickItemsCreativeTab(),
                new ConsoleSpamItemsCreativeTab(),
                new GriefItemsCreativeTab(),
                new TrollItemsCreativeTab()
        );
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    // TODO: Fix
    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
          /*final ItemStack stack = creativeInventoryActionC2SPacket.stack.copy();
            final NbtCompound nbt = stack.getNbt();
            if (nbt == null) return;
            final boolean isClientSide = nbt.contains(CLIENTSIDE_NAME) || nbt.contains(CLIENTSIDE_GLINT);
            if (isClientSide) {
                if (nbt.contains(CLIENTSIDE_NAME)) {
                    final NbtCompound display = stack.getSubNbt(ItemStack.DISPLAY_KEY);
                    if (display != null) {
                        display.remove(ItemStack.NAME_KEY);
                        display.remove(ItemStack.LORE_KEY);
                        if (display.isEmpty()) stack.removeSubNbt(ItemStack.DISPLAY_KEY);
                    }
                    final Text name = Text.Serialization.fromJson(nbt.getString(CLIENTSIDE_NAME));
                    if (!stack.getName().equals(name)) stack.setCustomName(name);
                    nbt.remove(CLIENTSIDE_NAME);
                }
                if (nbt.contains(CLIENTSIDE_GLINT)) nbt.remove(CLIENTSIDE_GLINT);

                final NetworkingSettings networkingSettings = Vandalism.getInstance().getClientSettings().getNetworkingSettings();
                if (networkingSettings.packageCreativeItems.getValue()) {
                    if (ItemStackUtil.PackageType.isPackageItem(stack.getItem())) {
                        return;
                    }
                    creativeInventoryActionC2SPacket.stack = ItemStackUtil.packageStack(stack, networkingSettings.creativeItemsPackageType.getValue());
                    return;
                }
            }
            creativeInventoryActionC2SPacket.stack = stack;*/
        }
    }

}
