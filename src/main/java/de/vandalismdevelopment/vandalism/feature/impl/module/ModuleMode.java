package de.vandalismdevelopment.vandalism.feature.impl.module;

import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;

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