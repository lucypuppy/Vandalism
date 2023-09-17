package de.foxglovedevelopment.foxglove.gui.imgui.impl.menu.macro.node;

public abstract class Node {

    private final String name, description;

    private int nodeId, inputPinId, outputPinId;

    private int outputNodeId;

    public Node(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.outputNodeId = -1;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() { //TODO: Implement this.
        return this.description;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(final int nextNodeId) {
        this.nodeId = nextNodeId;
    }

    public int getOutputNodeId() {
        return outputNodeId;
    }

    public void setOutputNodeId(final int outputNodeId) {
        this.outputNodeId = outputNodeId;
    }

    public int getInputPinId() {
        return this.inputPinId;
    }

    public void setInputPinId(final int nextPinId) {
        this.inputPinId = nextPinId;
    }

    public int getOutputPinId() {
        return this.outputPinId;
    }

    public void setOutputPinId(final int nextPinId) {
        this.outputPinId = nextPinId;
    }

    public void render() {
    }

}