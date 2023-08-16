package de.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.impl;

import de.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.Node;
import de.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.OutputNode;

public class OnDisableNode extends Node implements OutputNode {

    public OnDisableNode() {
        super("onDisable", "The action that will be executed when the macro gets disabled.");
    }

}
