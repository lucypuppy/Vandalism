package me.nekosarekawaii.foxglove.feature.impl.module;

public abstract class Mode<T extends Module> {

    private final String name;
    private final T parent;

    protected Mode(final String name, final T parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    public T getParent() {
        return this.parent;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

}