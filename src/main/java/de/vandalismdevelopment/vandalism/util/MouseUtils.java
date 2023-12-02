package de.vandalismdevelopment.vandalism.util;

public final class MouseUtils {

    public static boolean isHovered(final int mouseX, final int mouseY, final double x, final double y, final double width, final double height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isHoveredWithinBounds(final int mouseX, final int mouseY, final double x, final double y, final double x2, final double y2) {
        return mouseX >= x && mouseX < x2 && mouseY >= y && mouseY < y2;
    }

    public static boolean isHoveredCircle(final int mouseX, final int mouseY, final double x, final double y, final double radius) {
        return Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2)) < radius / 2.d;
    }

}