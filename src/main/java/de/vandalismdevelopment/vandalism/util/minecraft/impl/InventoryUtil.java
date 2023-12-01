package de.vandalismdevelopment.vandalism.util.minecraft.impl;

import com.google.common.collect.Lists;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class InventoryUtil extends MinecraftUtil {

    public static Int2ObjectMap<ItemStack> createDummyModifiers() {
        final Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        if (player() == null) return int2ObjectMap;
        final DefaultedList<Slot> defaultedList = player().currentScreenHandler.slots;
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
            if (handler.slots.size() <= i || currentScreen() == null || interactionManager() == null) {
                break;
            }
            final Slot slot = handler.slots.get(i);
            if (slot.getStack().isEmpty()) continue;
            interactionManager().clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player());
        }
    }

}
