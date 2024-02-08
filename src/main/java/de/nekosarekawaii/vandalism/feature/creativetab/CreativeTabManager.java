/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.feature.creativetab;

import de.florianmichael.dietrichevents2.Priorities;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;

import java.util.UUID;

public class CreativeTabManager extends Storage<AbstractCreativeTab> implements OutgoingPacketListener {

    public static final String CLIENTSIDE_NAME = UUID.randomUUID().toString();
    public static final String CLIENTSIDE_GLINT = UUID.randomUUID().toString();

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

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            final ItemStack itemStack = creativeInventoryActionC2SPacket.getStack();
            final NbtCompound nbt = itemStack.getNbt();
            if (nbt != null) {
                if (nbt.contains(CLIENTSIDE_NAME)) {
                    final NbtCompound display = itemStack.getSubNbt(ItemStack.DISPLAY_KEY);
                    if (display != null) {
                        display.remove(ItemStack.NAME_KEY);
                        display.remove(ItemStack.LORE_KEY);
                        if (display.isEmpty()) {
                            itemStack.removeSubNbt(ItemStack.DISPLAY_KEY);
                        }
                    }
                    itemStack.setCustomName(Text.Serialization.fromJson(nbt.getString(CLIENTSIDE_NAME)));
                    nbt.remove(CLIENTSIDE_NAME);
                }
                if (nbt.contains(CLIENTSIDE_GLINT)) {
                    nbt.remove(CLIENTSIDE_GLINT);
                }
            }
        }
    }
    
}
