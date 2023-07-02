package me.nekosarekawaii.foxglove.util;

/**
 * The MouseUtils class provides utility methods for mouse-related operations.
 */
public class MouseUtils {

    /**
     * Checks if the mouse cursor is hovering over a specified area.
     *
     * @param x1     The x-coordinate of the top-left corner of the area.
     * @param y1     The y-coordinate of the top-left corner of the area.
     * @param x2     The x-coordinate of the bottom-right corner of the area.
     * @param y2     The y-coordinate of the bottom-right corner of the area.
     * @param mouseX The x-coordinate of the mouse cursor.
     * @param mouseY The y-coordinate of the mouse cursor.
     * @return {@code true} if the mouse is hovering over the area, {@code false} otherwise.
     */
    public static boolean isMouseHovering(final int x1, final int y1, final int x2, final int y2, final int mouseX, final int mouseY) {
        return mouseY <= y2 && mouseY >= y1 && mouseX <= x2 && mouseX >= x1;
    }

}
