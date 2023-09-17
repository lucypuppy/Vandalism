package de.foxglovedevelopment.foxglove.util.inventory;

import net.minecraft.screen.ScreenHandlerType;

import java.util.HashMap;

public class ScreenHandlerTypes {

    private final static HashMap<Integer, String> screenHandlerTypeIdMap = new HashMap<>();

    public static String getId(final ScreenHandlerType<?> screenHandlerType) {
        if (screenHandlerType != null) {
            final int hashCode = screenHandlerType.hashCode();
            if (screenHandlerTypeIdMap.containsKey(hashCode)) {
                return screenHandlerTypeIdMap.get(hashCode);
            }
        }
        return "";
    }

    public static void registerType(final ScreenHandlerType<?> screenHandlerType, final String id) {
        screenHandlerTypeIdMap.put(screenHandlerType.hashCode(), id);
    }

}
