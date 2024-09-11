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

package de.nekosarekawaii.vandalism.feature.creativetab;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.NetworkingSettings;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.ConsoleSpamItemsCreativeTab;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.GriefItemsCreativeTab;
import de.nekosarekawaii.vandalism.feature.creativetab.impl.TrollItemsCreativeTab;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.ItemStackUtil;
import de.nekosarekawaii.vandalism.util.Storage;
import lombok.Getter;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;

import java.util.UUID;

public class CreativeTabManager extends Storage<CreativeTab> implements OutgoingPacketListener {

    public static final String CLIENTSIDE_NAME = UUID.randomUUID().toString();
    public static final String CLIENTSIDE_GLINT = UUID.randomUUID().toString();

    @Getter
    private static final CreativeTabManager instance = new CreativeTabManager();

    @Override
    public void init() {
        this.setAddConsumer(CreativeTab::publish);
        this.add(
                // new CrashItemsCreativeTab(),
                // new KickItemsCreativeTab(),
                new ConsoleSpamItemsCreativeTab(),
                new GriefItemsCreativeTab(),
                new TrollItemsCreativeTab()
        );
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (event.packet instanceof final CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
            ItemStack stack = creativeInventoryActionC2SPacket.stack.copy();

            final ComponentMap components = stack.getComponents();
            if (components == null) {
                return;
            }

            if (components.contains(DataComponentTypes.CUSTOM_DATA)) {
                final NbtComponent customData = components.get(DataComponentTypes.CUSTOM_DATA);
                if (customData != null) {
                    final NbtCompound customDataCompound = customData.copyNbt();
                    if (customData.contains(CLIENTSIDE_GLINT)) {
                        customDataCompound.remove(CLIENTSIDE_GLINT);
                        stack.remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
                    }

                    if (customData.contains(CLIENTSIDE_NAME)) {
                        final Text name = Text.Serialization.fromJson(customDataCompound.getString(CLIENTSIDE_NAME), DynamicRegistryManager.EMPTY);
                        if (!stack.getName().equals(name)) {
                            stack.remove(DataComponentTypes.CUSTOM_NAME);
                        }

                        customDataCompound.remove(CLIENTSIDE_NAME);

                        if (customDataCompound.isEmpty()) {
                            stack.remove(DataComponentTypes.CUSTOM_DATA);
                        }  else {
                            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(customDataCompound));
                        }

                        final NetworkingSettings networkingSettings = Vandalism.getInstance().getClientSettings().getNetworkingSettings();
                        if (networkingSettings.packageCreativeItems.getValue()) {
                            if (ItemStackUtil.PackageType.isPackageItem(stack.getItem())) {
                                return;
                            }

                            try {
                                stack = ItemStackUtil.packageStack(stack, networkingSettings.creativeItemsPackageType.getValue());
                            } catch (final Exception ignored) {
                                ChatUtil.errorChatMessage("Failed to package item: " + stack.getName().getString());
                            }
                        }
                    }
                }
            }
            creativeInventoryActionC2SPacket.stack = stack;
        }
    }

}
