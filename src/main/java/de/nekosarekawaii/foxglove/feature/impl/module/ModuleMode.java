package de.nekosarekawaii.foxglove.feature.impl.module;

import de.nekosarekawaii.foxglove.util.MinecraftWrapper;

public abstract class ModuleMode<T extends Module> implements MinecraftWrapper {

    private final String name;
    private final T parent;

    public ModuleMode(final String name, final T parent) {
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