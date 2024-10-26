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

package de.nekosarekawaii.vandalism.util;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

public class ItemStackUtil implements MinecraftWrapper {

    private static final SimpleCommandExceptionType NOT_IN_GAME = new SimpleCommandExceptionType(Text.literal("You need to be in-game to get items!"));

    private static final SimpleCommandExceptionType NOT_IN_CREATIVE_MODE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

    public static ItemStack appendEnchantmentToItemStack(final ItemStack stack, final RegistryKey<Enchantment> enchantment, final int level) {
        EnchantmentHelper.apply(stack, builder -> builder.add(getEnchantment(enchantment), level));
        return stack;
    }

    public static RegistryEntry<Enchantment> getEnchantment(final RegistryKey<Enchantment> enchantment) {
        if (mc.world == null) {
            throw new IllegalStateException("World is null!");
        }
        final RegistryWrapper.Impl<Enchantment> registry = mc.world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return registry.getOptional(enchantment).orElse(null);
    }

    public static ItemStack createSpawnEggItemStack(final SpawnEggItem originSpawnEgg, final String spawnEggID) {
        final ItemStack itemStack = new ItemStack(originSpawnEgg);
        final NbtCompound entityData = new NbtCompound();
        entityData.putString("id", spawnEggID);
        itemStack.set(DataComponentTypes.ENTITY_DATA, NbtComponent.of(entityData));
        return itemStack;
    }

    public static ItemStack createItemStack(final Item item, final String nbt) {
        return createItemStack(item, 1, nbt);
    }

    public static ItemStack createItemStack(final Item item, final int count, final String nbt) {
        final ItemStack itemStack = new ItemStack(item, count);
        if (!nbt.isBlank()) {
            try {
                itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(NbtHelper.fromNbtProviderString(nbt)));
            } catch (final Exception e) {
                ChatUtil.errorChatMessage("Failed to parse NBT: " + e.getMessage());
            }
        }
        return itemStack;
    }

    public static ItemStack createItemStack(final Block block, final String nbt) {
        return createItemStack(block, 1, nbt);
    }

    public static ItemStack createItemStack(final Block block, final int count, final String nbt) {
        final ItemStack itemStack = new ItemStack(block, count);
        if (!nbt.isBlank()) {
            try {
                itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(NbtHelper.fromNbtProviderString(nbt)));
            } catch (final Exception e) {
                ChatUtil.errorChatMessage("Failed to parse NBT: " + e.getMessage());
            }
        }
        return itemStack;
    }

    public static Identifier getId(final ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem());
    }

    public static void giveItemStack(final ItemStack itemStack) {
        giveItemStack(itemStack, true);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage) {
        return giveItemStack(itemStack, receiveMessage, mc.player.getInventory().selectedSlot + MinecraftConstants.FIRST_SLOT_IN_HOTBAR);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage, final int selectedSlot) {
        try {
            if (mc.player == null || mc.getNetworkHandler() == null) throw NOT_IN_GAME.create();
            if (!mc.player.getAbilities().creativeMode) throw NOT_IN_CREATIVE_MODE.create();
            mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(selectedSlot, itemStack));
            if (receiveMessage) {
                ChatUtil.infoChatMessage("You should have received " + Formatting.GRAY + "'" + Formatting.DARK_AQUA + itemStack.getName().getString() + Formatting.GRAY + "'");
            }
            return true;
        } catch (Throwable throwable) {
            ChatUtil.errorChatMessage("Failed to give item cause of: " + throwable.getMessage());
        }
        return false;
    }

    public enum PackageType implements IName {

        CHEST,
        TRAPPED_CHEST,
        BARREL,
        SHULKER_BOX,
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        DISPENSER,
        DROPPER,
        HOPPER,
        CAMPFIRE,
        SOUL_CAMPFIRE,
        CHISELED_BOOKSHELF,
        BREWING_STAND,
        JUKEBOX,
        LECTERN,
        BUNDLE;

        public final Identifier id;
        private final String name;

        PackageType() {
            this.id = Identifier.of(this.name().toLowerCase());
            this.name = Text.translatable(Registries.ITEM.get(this.id).getTranslationKey()).getString();
        }

        @Override
        public String getName() {
            return this.name;
        }

        public static boolean isPackageItem(final Item item) {
            if (item instanceof BlockItem blockItem) {
                final Block block = blockItem.getBlock();
                for (final PackageType value : values()) {
                    if (block.equals(Registries.BLOCK.get(value.id))) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    public static ItemStack packageStack(final ItemStack stack, final PackageType type) {
        return switch (type) {
            case CHEST, TRAPPED_CHEST, BARREL, SHULKER_BOX,
                 FURNACE, BLAST_FURNACE, SMOKER,
                 DISPENSER, DROPPER, HOPPER,
                 CAMPFIRE, SOUL_CAMPFIRE,
                 CHISELED_BOOKSHELF, BREWING_STAND -> {
                final ItemStack item = new ItemStack(Registries.ITEM.get(type.id));
                item.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(Collections.singletonList(stack)));
                yield item;
            }
            case JUKEBOX -> {
                final ItemStack item = new ItemStack(Registries.ITEM.get(type.id));
                final NbtCompound blockEntityData = new NbtCompound();
                blockEntityData.putString("id", type.id.toString());
                blockEntityData.put("RecordItem", stack.encode(DynamicRegistryManager.EMPTY));
                item.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(blockEntityData));
                yield item;
            }
            case LECTERN -> {
                final ItemStack item = new ItemStack(Registries.ITEM.get(type.id));
                final NbtCompound blockEntityData = new NbtCompound();
                blockEntityData.putString("id", type.id.toString());
                blockEntityData.put("Book", stack.encode(DynamicRegistryManager.EMPTY));
                item.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(blockEntityData));
                yield item;
            }
            case BUNDLE -> {
                final ItemStack item = new ItemStack(Registries.ITEM.get(type.id));
                item.set(DataComponentTypes.BUNDLE_CONTENTS, new BundleContentsComponent(Collections.singletonList(stack)));
                yield item;
            }
        };
    }

    public static ItemStack withClientSide(final ItemStack stack, @Nullable final Text name, final boolean glint, @Nullable final Text... description) {
        final NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        final NbtCompound clientSideData = customData != null ? customData.copyNbt() : new NbtCompound();
        clientSideData.putString(CreativeTabManager.CLIENTSIDE_NAME, Text.Serialization.toJsonString(stack.getName(), DynamicRegistryManager.EMPTY));
        if (glint) {
            clientSideData.put(CreativeTabManager.CLIENTSIDE_GLINT, new NbtCompound());
            stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (name != null) stack.set(DataComponentTypes.CUSTOM_NAME, name);
        if (description != null) {
            stack.set(DataComponentTypes.LORE, new LoreComponent(Arrays.asList(description)));
        }
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(clientSideData));
        return stack;
    }

    public static ItemStack withClientSide(final ItemStack stack, final Text name, @Nullable final Text... description) {
        return withClientSide(stack, name, false, description);
    }

    public static ItemStack withClientSide(final ItemStack stack) {
        return withClientSide(stack, null, false);
    }

}
