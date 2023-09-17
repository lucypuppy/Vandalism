package de.foxglovedevelopment.foxglove.gui.imgui.impl.menu.macro;

import de.foxglovedevelopment.foxglove.gui.imgui.impl.menu.macro.node.Node;
import de.foxglovedevelopment.foxglove.gui.imgui.impl.menu.macro.node.impl.OnDisableNode;
import de.foxglovedevelopment.foxglove.gui.imgui.impl.menu.macro.node.impl.OnEnableNode;

import java.util.HashMap;
import java.util.Map;

public class Macro {

    private int nextNodeId, nextPinId;

    private final String name, description;

    private final Map<Integer, Node> nodes;

    private boolean state;

    public Macro(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.nodes = new HashMap<>();
        this.state = false;
        this.resetNodeIds();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void addNode(final Node node) {
        this.nextNodeId++;
        node.setNodeId(this.nextNodeId);
        this.nextPinId++;
        node.setInputPinId(this.nextPinId);
        this.nextPinId++;
        node.setOutputPinId(this.nextPinId);
        this.nodes.put(node.getNodeId(), node);
    }

    public Node findByInput(final long inputPinId) {
        for (final Node node : this.nodes.values()) {
            if (node.getInputPinId() == inputPinId) {
                return node;
            }
        }
        return null;
    }

    public Node findByOutput(final long outputPinId) {
        for (final Node node : this.nodes.values()) {
            if (node.getOutputPinId() == outputPinId) {
                return node;
            }
        }
        return null;
    }

    public Map<Integer, Node> getNodes() {
        return this.nodes;
    }

    public void resetNodeIds() {
        this.nextNodeId = 1;
        this.nextPinId = 100;
    }

    public void toggle() {
        this.setState(!this.state);
    }

    public void setState(final boolean state) {
        if (this.state == state) return;
        for (final Node node : this.nodes.values()) {
            if (state) {
                if (node instanceof final OnEnableNode onEnableNode) {

                }
            } else {
                if (node instanceof final OnDisableNode onDisableNode) {

                }
            }
        }
    }

}