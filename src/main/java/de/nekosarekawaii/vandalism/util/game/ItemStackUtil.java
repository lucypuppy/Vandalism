/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.util.game;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemStackUtil implements MinecraftWrapper {

    private static final SimpleCommandExceptionType NOT_IN_GAME = new SimpleCommandExceptionType(Text.literal("You need to be in-game to get items!"));
    private static final SimpleCommandExceptionType NOT_IN_CREATIVE_MODE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

    public static ItemStack appendEnchantmentToItemStack(final ItemStack stack, final Enchantment enchantment, final int level) {
        return appendEnchantmentToItemStack(stack, EnchantmentHelper.getEnchantmentId(enchantment), level);
    }

    public static ItemStack appendEnchantmentToItemStack(final ItemStack stack, final Identifier enchantmentId, final int level) {
        final NbtCompound tag = stack.getOrCreateNbt();
        if (!tag.contains(ItemStack.ENCHANTMENTS_KEY, 9)) tag.put(ItemStack.ENCHANTMENTS_KEY, new NbtList());
        final NbtList nbtList = tag.getList(ItemStack.ENCHANTMENTS_KEY, 10);
        nbtList.add(EnchantmentHelper.createNbt(enchantmentId, level));
        stack.setNbt(tag);
        return stack;
    }

    public static ItemStack createSpawnEggItemStack(final SpawnEggItem originSpawnEgg, final String spawnEggID) {
        final ItemStack item = new ItemStack(originSpawnEgg);
        final NbtCompound base = new NbtCompound();
        final NbtCompound entityTag = new NbtCompound();
        entityTag.putString("id", spawnEggID);
        base.put("EntityTag", entityTag);
        item.setNbt(base);
        return item;
    }

    public static NbtCompound createEffectNBT(final String id, final int duration, final int amplifier, final boolean showParticles) {
        final NbtCompound effect = new NbtCompound();
        effect.putString("id", "minecraft:" + id);
        effect.putByte("show_particles", showParticles ? (byte) 1 : (byte) 0);
        effect.putInt("duration", duration);
        effect.putByte("amplifier", (byte) amplifier);
        return effect;
    }

    public static NbtCompound createOldEffectNBT(final int id, final int duration, final int amplifier, final boolean showParticles) {
        final NbtCompound effect = new NbtCompound();
        effect.putInt("Id", id);
        effect.putByte("ShowParticles", showParticles ? (byte) 1 : (byte) 0);
        effect.putInt("Duration", duration);
        effect.putByte("Amplifier", (byte) amplifier);
        return effect;
    }

    public static ItemStack createItemStack(final Item item, final String nbt) {
        return createItemStack(item, 1, nbt);
    }

    public static ItemStack createItemStack(final Item item, final int count, final String nbt) {
        final ItemStack stack = new ItemStack(item, count);
        try {
            if (!nbt.isBlank()) {
                stack.setNbt(NbtHelper.fromNbtProviderString(nbt));
            }
        } catch (CommandSyntaxException e) {
            Vandalism.getInstance().getLogger().error("Failed to create item stack with nbt: " + nbt, e);
        }
        return stack;
    }

    public static ItemStack createItemStack(final Block block, final String nbt) {
        return createItemStack(block, 1, nbt);
    }

    public static ItemStack createItemStack(final Block block, final int count, final String nbt) {
        final ItemStack stack = new ItemStack(block, count);
        try {
            if (!nbt.isBlank()) {
                stack.setNbt(NbtHelper.fromNbtProviderString(nbt));
            }
        } catch (CommandSyntaxException e) {
            Vandalism.getInstance().getLogger().error("Failed to create block item stack with nbt: " + nbt, e);
        }
        return stack;
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
            this.id = new Identifier(this.name().toLowerCase());
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
                final NbtCompound base = new NbtCompound();
                final NbtCompound blockEntityTag = new NbtCompound();
                final NbtList items = new NbtList();
                final NbtCompound child = new NbtCompound();
                child.putByte("Slot", (byte) 0);
                child.putString("id", getId(stack).toString());
                child.putByte("Count", (byte) stack.getCount());
                if (stack.getNbt() != null ) child.put("tag", stack.getNbt());
                items.add(child);
                blockEntityTag.put("Items", items);
                base.put("BlockEntityTag", blockEntityTag);
                item.setNbt(base);
                yield item;
            }
            case JUKEBOX -> {
                final ItemStack item = new ItemStack(Items.JUKEBOX);
                final NbtCompound base = new NbtCompound();
                final NbtCompound blockEntityTag = new NbtCompound();
                final NbtCompound child = new NbtCompound();
                child.putString("id", getId(stack).toString());
                child.putByte("Count", (byte) stack.getCount());
                if (stack.getNbt() != null ) child.put("tag", stack.getNbt());
                blockEntityTag.put("RecordItem", child);
                base.put("BlockEntityTag", blockEntityTag);
                item.setNbt(base);
                yield item;
            }
            case LECTERN -> {
                final ItemStack item = new ItemStack(Items.LECTERN);
                final NbtCompound base = new NbtCompound();
                final NbtCompound blockEntityTag = new NbtCompound();
                final NbtCompound child = new NbtCompound();
                child.putString("id", getId(stack).toString());
                child.putByte("Count", (byte) stack.getCount());
                if (stack.getNbt() != null ) child.put("tag", stack.getNbt());
                blockEntityTag.put("Book", child);
                base.put("BlockEntityTag", blockEntityTag);
                item.setNbt(base);
                yield item;
            }
            case BUNDLE -> {
                final ItemStack item = new ItemStack(Items.BUNDLE);
                final NbtCompound base = new NbtCompound();
                final NbtList items = new NbtList();
                final NbtCompound child = new NbtCompound();
                child.putString("id", getId(stack).toString());
                child.putByte("Count", (byte) stack.getCount());
                if (stack.getNbt() != null ) child.put("tag", stack.getNbt());
                items.add(child);
                base.put("Items", items);
                item.setNbt(base);
                yield item;
            }
        };
    }

    public static ItemStack withClientSide(final ItemStack stack, @Nullable final Text name, final boolean glint, @Nullable final Text... description) {
        final NbtCompound base = stack.getOrCreateNbt();
        base.putString(CreativeTabManager.CLIENTSIDE_NAME, Text.Serialization.toJsonString(stack.getName()));
        if (glint) base.put(CreativeTabManager.CLIENTSIDE_GLINT, new NbtCompound());
        if (name != null) stack.setCustomName(name);
        if (description != null) {
            final NbtList lore = new NbtList();
            for (final Text text : description) {
                if (text != null) {
                    lore.add(NbtString.of(Text.Serialization.toJsonString(text)));
                }
            }
            stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
        }
        return stack;
    }

    public static ItemStack withClientSide(final ItemStack stack, final Text name, @Nullable final Text... description) {
        return withClientSide(stack, name, false, description);
    }

    public static ItemStack withClientSide(final ItemStack stack) {
        return withClientSide(stack, null, false);
    }

}
