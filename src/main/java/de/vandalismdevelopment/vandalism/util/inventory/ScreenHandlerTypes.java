package de.vandalismdevelopment.vandalism.util.inventory;

import net.minecraft.screen.ScreenHandlerType;

import java.util.HashMap;

public class ScreenHandlerTypes {

    private static final HashMap<Integer, String> SCREEN_HANDLER_TYPE_ID_MAP = new HashMap<>();

    public static String getId(final ScreenHandlerType<?> screenHandlerType) {
        if (screenHandlerType != null) {
            final int hashCode = screenHandlerType.hashCode();
            if (SCREEN_HANDLER_TYPE_ID_MAP.containsKey(hashCode)) {
                return SCREEN_HANDLER_TYPE_ID_MAP.get(hashCode);
            }
        }
        return "";
    }

    public static void registerType(final ScreenHandlerType<?> screenHandlerType, final String id) {
        SCREEN_HANDLER_TYPE_ID_MAP.put(screenHandlerType.hashCode(), id);
    }

}
