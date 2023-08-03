package me.nekosarekawaii.foxglove.util.minecraft.inventory;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class InventoryUtil {
    private final static MinecraftClient mc = MinecraftClient.getInstance();

    public static Int2ObjectMap<ItemStack> createDummyModifiers() {
        final var int2ObjectMap = new Int2ObjectOpenHashMap<ItemStack>();
        if (mc.player == null) return int2ObjectMap;

        final var defaultedList = mc.player.currentScreenHandler.slots;

        final List<ItemStack> list = Lists.newArrayListWithCapacity(defaultedList.size());
        for (Slot slot : defaultedList) {
            list.add(slot.getStack().copy());
        }

        for (int i = 0; i < defaultedList.size(); i++) {
            final var original = list.get(i);
            final var copy = defaultedList.get(i).getStack();

            if (!ItemStack.areEqual(original, copy)) {
                int2ObjectMap.put(i, copy.copy());
            }
        }

        return int2ObjectMap;
    }
}
