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

package de.nekosarekawaii.vandalism.util;

import com.google.common.collect.Lists;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.function.BiFunction;

public class InventoryUtil implements MinecraftWrapper {

    public static Int2ObjectMap<ItemStack> createDummyModifiers() {
        final Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        if (mc.player == null) return int2ObjectMap;
        final DefaultedList<Slot> defaultedList = mc.player.currentScreenHandler.slots;
        final List<ItemStack> list = Lists.newArrayListWithCapacity(defaultedList.size());
        for (final Slot slot : defaultedList) list.add(slot.getStack().copy());
        for (int i = 0; i < defaultedList.size(); i++) {
            final ItemStack original = list.get(i), copy = defaultedList.get(i).getStack();
            if (!ItemStack.areEqual(original, copy)) int2ObjectMap.put(i, copy.copy());
        }
        return int2ObjectMap;
    }

    public static <T extends ScreenHandler> void quickMoveInventory(final HandledScreen<T> screen, final int from, final int to) {
        for (int i = from; i < to; i++) {
            final T handler = screen.getScreenHandler();
            if (handler.slots.size() <= i || mc.currentScreen == null || mc.interactionManager == null) {
                break;
            }
            final Slot slot = handler.slots.get(i);
            if (slot.getStack().isEmpty()) continue;
            mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
        }
    }

    public static RecipeEntry<Recipe<?>> createDummyRecipeEntry(final Identifier identifier) {
        return new RecipeEntry<>(
                identifier,
                new RecipeNoop()
        );
    }

    public static boolean isBestArmor(final ItemStack itemStack) {
        if (mc.currentScreen instanceof InventoryScreen screen) {
            if (itemStack.getItem() instanceof final ArmorItem oldArmorItem) {

                for (int i = 5; i <= MinecraftConstants.LAST_SLOT_IN_HOTBAR; i++) {
                    final ItemStack stack = screen.getScreenHandler().getSlot(i).getStack();

                    if (stack.isEmpty() || !(stack.getItem() instanceof final ArmorItem armorItem) || armorItem.getType() != oldArmorItem.getType()) {
                        continue;
                    }

                    if (getProtection(itemStack) < getProtection(stack)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private static int getProtection(ItemStack is) {
        if (is.getItem() instanceof final ArmorItem armorItem) {
            int prot = 0;
            int blastMultiplier = 1;
            int protectionMultiplier = 2;

            if (is.hasEnchantments()) {
                final ItemEnchantmentsComponent enchants = EnchantmentHelper.getEnchantments(is);

                if (enchants.getEnchantments().contains(mc.world.getRegistryManager().get(Enchantments.PROTECTION.getRegistryRef()).getEntry(Enchantments.PROTECTION).get()))
                    prot += enchants.getLevel(mc.world.getRegistryManager().get(Enchantments.PROTECTION.getRegistryRef()).getEntry(Enchantments.PROTECTION).get()) * protectionMultiplier;

                if (enchants.getEnchantments().contains(mc.world.getRegistryManager().get(Enchantments.BLAST_PROTECTION.getRegistryRef()).getEntry(Enchantments.BLAST_PROTECTION).get()))
                    prot += enchants.getLevel(mc.world.getRegistryManager().get(Enchantments.BLAST_PROTECTION.getRegistryRef()).getEntry(Enchantments.BLAST_PROTECTION).get()) * blastMultiplier;

                if (enchants.getEnchantments().contains(mc.world.getRegistryManager().get(Enchantments.BLAST_PROTECTION.getRegistryRef()).getEntry(Enchantments.BINDING_CURSE).get()))
                    prot = -999;
            }

            return (armorItem.getProtection() + (int) Math.ceil(armorItem.getToughness())) * 10 + prot;
        }

        return -1;
    }


    public static int getHotbarSlotForItem(final ItemStack itemStack) {
        if (itemStack.getItem() instanceof SwordItem)
            return 0;

        if (itemStack.getItem() instanceof BowItem)
            return 1;

        if (itemStack.getItem() instanceof PickaxeItem)
            return 2;

        if (itemStack.getItem() instanceof AxeItem)
            return 3;

        if (itemStack.getItem() instanceof ShovelItem)
            return 4;

        if (itemStack.getItem() instanceof EnderPearlItem)
            return 5;

        if (itemStack.getItem().getComponents().contains(DataComponentTypes.FOOD))
            return 6;

        if (itemStack.getItem() instanceof BucketItem item && item.fluid == Fluids.WATER)
            return 7;

        if (itemStack.getItem() instanceof BlockItem)
            return 8;

        return -1;
    }

    public static boolean isItemBetter(final ItemStack newItem, final ItemStack oldItem) {
        if (newItem.getItem() instanceof ArmorItem armorItem && oldItem.getItem() instanceof ArmorItem oldArmorItem) {
            if (armorItem.getProtection() > oldArmorItem.getProtection()) {
                return true;
            }
        }

        if (newItem.getItem() instanceof SwordItem swordItem && oldItem.getItem() instanceof SwordItem oldSwordItem) {
            if (swordItem.getMaterial().getAttackDamage() > oldSwordItem.getMaterial().getAttackDamage()) {
                return true;
            }
        }

        if (newItem.getItem() instanceof PickaxeItem pickaxeItem && oldItem.getItem() instanceof PickaxeItem oldPickaxeItem) {
            if (pickaxeItem.getMaterial().getMiningSpeedMultiplier() > oldPickaxeItem.getMaterial().getMiningSpeedMultiplier()) {
                return true;
            }
        }

        if (newItem.getItem() instanceof AxeItem axeItem && oldItem.getItem() instanceof AxeItem oldAxeItem) {
            if (axeItem.getMaterial().getMiningSpeedMultiplier() > oldAxeItem.getMaterial().getMiningSpeedMultiplier()) {
                return true;
            }
        }

        if (newItem.getItem() instanceof ShovelItem shovelItem && oldItem.getItem() instanceof ShovelItem oldShovelItem) {
            return shovelItem.getMaterial().getMiningSpeedMultiplier() > oldShovelItem.getMaterial().getMiningSpeedMultiplier();
        }

        return false;
    }

    public static void setSlot(final int slot) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (mc.player == null || mc.interactionManager == null || networkHandler == null || !PlayerInventory.isValidHotbarIndex(slot)) {
            return;
        }
        mc.player.getInventory().selectedSlot = slot;
        mc.interactionManager.syncSelectedSlot();
    }

    public static int countTotalItemsInInventory(final Inventory inventory, final BiFunction<ItemStack, Integer, Boolean> condition) {
        int totalItemCount = 0;

        for (int slot = 0; slot < inventory.size(); slot++) {
            final ItemStack stack = inventory.getStack(slot);

            if (condition.apply(stack, slot)) {
                totalItemCount += stack.getCount();
            }
        }

        return totalItemCount;
    }

    public static boolean isHotbarSlot(final int slot) {
        return slot >= 0 && slot <= 8;
    }

}
