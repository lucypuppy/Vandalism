package de.nekosarekawaii.vandalism.integration.rotation;

public enum RotationPriority {

    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4);

    private final int priority;

    RotationPriority(final int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

}
