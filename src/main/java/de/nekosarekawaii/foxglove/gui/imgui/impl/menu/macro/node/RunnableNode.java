package de.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node;

import net.minecraft.client.MinecraftClient;

public abstract class RunnableNode extends Node implements InputNode {

    protected final static MinecraftClient mc = MinecraftClient.getInstance();

    public RunnableNode(final String name, final String description) {
        super(name, description);
    }

    public abstract void run();

}
