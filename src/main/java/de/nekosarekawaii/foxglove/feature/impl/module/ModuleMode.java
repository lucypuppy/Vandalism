package de.nekosarekawaii.foxglove.feature.impl.module;

import net.minecraft.client.MinecraftClient;

public abstract class ModuleMode<T extends Module> {

    protected final static MinecraftClient mc = MinecraftClient.getInstance();

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