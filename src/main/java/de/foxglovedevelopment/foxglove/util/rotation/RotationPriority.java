package de.foxglovedevelopment.foxglove.util.rotation;

public enum RotationPriority {

    LOWEST(0), //only for fun modules like spin or derp.
    LOW(1),
    NORMAL(2),
    HIGH(3), //Modules like aimbot or scaffold.
    HIGHEST(4); //Modules like killaura.

    private final int priority;

    RotationPriority(final int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

}
