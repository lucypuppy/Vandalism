package de.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.impl;

import de.nekosarekawaii.foxglove.gui.imgui.impl.menu.macro.node.RunnableNode;
import imgui.ImGui;
import imgui.type.ImString;

public class SendChatMessageNode extends RunnableNode {

    private final ImString message;

    public SendChatMessageNode() {
        super("sendChatMessage", "Sends a chat message.");
        this.message = new ImString(256);
    }

    @Override
    public void render() {
        ImGui.setNextItemWidth(400);
        ImGui.inputText("message##sendchatmessagenode" + this.getNodeId(), this.message);
    }

    @Override
    public void run() {
        if (networkHandler() != null) {
            networkHandler().sendChatMessage(this.message.get());
        }
    }

}
