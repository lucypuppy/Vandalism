package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro;

import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.InputNode;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.Node;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.NodeType;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.OutputNode;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.impl.OnDisableNode;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.impl.OnEnableNode;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.macro.node.impl.SendChatMessageNode;
import imgui.ImGui;
import imgui.extension.nodeditor.NodeEditor;
import imgui.extension.nodeditor.NodeEditorConfig;
import imgui.extension.nodeditor.NodeEditorContext;
import imgui.extension.nodeditor.flag.NodeEditorPinKind;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImLong;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MacrosImGuiMenu extends ImGuiMenu {

    private final NodeEditorContext context;

    private final List<Macro> macros;

    public MacrosImGuiMenu() {
        super("Macros");
        this.macros = new ArrayList<>();
        final NodeEditorConfig config = new NodeEditorConfig();
        config.setSettingsFile(null);
        this.context = new NodeEditorContext(config);
        this.macros.add(new Macro("Test", "Just a test."));
        this.macros.add(new Macro("Test 2", "Just a second test."));
    }

    @Override
    public void render() {
        if (ImGui.begin("Macros", ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.beginTabBar("configTabBar")) {
                for (final Macro macro : this.macros) {
                    if (ImGui.beginTabItem(macro.getName() + "##macrostabitem")) {
                        ImGui.separator();
                        renderNodeEditor(macro);
                        ImGui.endTabItem();
                    }
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

    private void renderNodeEditor(final Macro macro) {
        final String id = macro.getName().toLowerCase();
        ImGui.setWindowSize(500, 400, ImGuiCond.Once);
        ImGui.setWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 200, ImGuiCond.Once);

        ImGui.alignTextToFramePadding();

        if (!macro.getNodes().isEmpty()) {
            if (ImGui.button("Clear Nodes##" + id)) {
                macro.getNodes().clear();
            }

            ImGui.sameLine();

            if (ImGui.button("Navigate to content##" + id)) {
                NodeEditor.navigateToContent(1);
            }
        } else ImGui.textWrapped("Info: Do a right click to add nodes.");

        NodeEditor.setCurrentEditor(this.context);
        NodeEditor.begin(macro.getName() + " Node Editor##" + id);

        final Collection<Node> nodes = macro.getNodes().values();

        if (!nodes.isEmpty()) {
            for (final Node node : nodes) {

                NodeEditor.beginNode(node.getNodeId());

                ImGui.text(node.getName());

                if (node instanceof InputNode) {
                    NodeEditor.beginPin(node.getInputPinId(), NodeEditorPinKind.Input);
                    ImGui.text("(In)");
                    NodeEditor.endPin();
                }

                ImGui.sameLine();

                if (node instanceof OutputNode) {
                    NodeEditor.beginPin(node.getOutputPinId(), NodeEditorPinKind.Output);
                    ImGui.text("(Out)");
                    NodeEditor.endPin();
                }

                node.render();

                NodeEditor.endNode();
            }
        } else macro.resetNodeIds();

        if (NodeEditor.beginCreate()) {
            final ImLong a = new ImLong(), b = new ImLong();
            if (NodeEditor.queryNewLink(a, b)) {
                final Node source = macro.findByOutput(a.get()), target = macro.findByInput(b.get());
                if (source != null && target != null && source.getOutputNodeId() != target.getNodeId() && NodeEditor.acceptNewItem()) {
                    source.setOutputNodeId(target.getNodeId());
                }
            }
        }
        NodeEditor.endCreate();

        int uniqueLinkId = 1;
        for (final Node node : macro.getNodes().values()) {
            if (macro.getNodes().containsKey(node.getOutputNodeId())) {
                NodeEditor.link(uniqueLinkId++, node.getOutputPinId(), macro.getNodes().get(node.getOutputNodeId()).getInputPinId());
            }
        }

        NodeEditor.suspend();

        final long nodeWithContextMenu = NodeEditor.getNodeWithContextMenu();
        if (nodeWithContextMenu != -1) {
            ImGui.openPopup("node_context_" + id);
            ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id_" + id), (int) nodeWithContextMenu);
        }

        if (ImGui.isPopupOpen("node_context_" + id)) {
            final int targetNodeId = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id_" + id));
            if (ImGui.beginPopup("node_context_" + id)) {
                final Node targetNode = macro.getNodes().get(targetNodeId);
                if (ImGui.button("Delete " + targetNode.getName())) {
                    macro.getNodes().remove(targetNodeId);
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
        }

        if (NodeEditor.showBackgroundContextMenu()) {
            ImGui.openPopup("node_editor_context_" + id);
        }

        if (ImGui.beginPopup("node_editor_context_" + id)) {
            for (final NodeType nodeType : NodeType.values()) {
                if (ImGui.button("Create New " + nodeType.normalName() + " Node##" + id)) {
                    Node node = null;
                    switch (nodeType) {
                        case ON_ENABLE -> node = new OnEnableNode();
                        case ON_DISABLE -> node = new OnDisableNode();
                        case SEND_CHAT_MESSAGE -> node = new SendChatMessageNode();
                        default -> {
                        }
                    }
                    if (node != null) {
                        macro.addNode(node);
                        NodeEditor.setNodePosition(node.getNodeId(), NodeEditor.toCanvasX(ImGui.getMousePosX()), NodeEditor.toCanvasY(ImGui.getMousePosY()));
                    }
                    ImGui.closeCurrentPopup();
                }
            }
            ImGui.endPopup();
        }

        NodeEditor.resume();
        NodeEditor.end();
    }

}
