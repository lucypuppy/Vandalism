package me.nekosarekawaii.foxglove.event;

public enum EventPriorities {

    HIGHEST(2),
    HIGH(1),
    NORMAL(0),
    LOW(-1),
    LOWEST(-2);

    private final int priority;

    EventPriorities(final int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

}
