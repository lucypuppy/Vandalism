package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;

public abstract class RunnableNode extends Node implements InputNode, MinecraftWrapper {

    public RunnableNode(final String name, final String description) {
        super(name, description);
    }

    public abstract void run();

}
