package de.nekosarekawaii.vandalism.feature.module.template;

import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;

public abstract class ModuleMulti<T extends AbstractModule> implements MinecraftWrapper {

    private final String name;
    private final T parent;

    public ModuleMulti(final String name, final T parent) {
        this.name = name;
        this.parent = parent;
    }

    public void onEnable() {
    }
    public void onDisable() {
    }

    public String getName() {
        return this.name;
    }

    public T getParent() {
        return this.parent;
    }

}