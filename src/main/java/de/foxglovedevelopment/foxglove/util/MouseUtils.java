package de.foxglovedevelopment.foxglove.util;

public class MouseUtils {

    public static boolean isHovered(final int x1, final int y1, final int x2, final int y2, final int mouseX, final int mouseY) {
        return mouseY <= y2 && mouseY >= y1 && mouseX <= x2 && mouseX >= x1;
    }

    public static boolean isHoveredWithinBounds(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isHoveredCircle(final double x, final double y, final double radius, final int mouseX, final int mouseY) {
        return Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2)) < radius / 2.d;
    }

}
