package me.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.impl;

import me.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.Node;
import me.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.OutputNode;

public class OnEnableNode extends Node implements OutputNode {

    public OnEnableNode() {
        super("onEnable", "The action that will be executed when the macro gets enabled.");
    }

}
