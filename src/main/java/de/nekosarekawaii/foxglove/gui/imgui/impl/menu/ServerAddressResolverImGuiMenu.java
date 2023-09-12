package de.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import de.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.lenni0451.mcping.ServerAddress;

public class ServerAddressResolverImGuiMenu extends ImGuiMenu {

    private final ImString hostname;

    public ServerAddressResolverImGuiMenu() {
        super("Server Address Resolver");
        this.hostname = new ImString(253);
    }

    @Override
    public void render() {
        if (ImGui.begin("Server Address Resolver", ImGuiWindowFlags.NoCollapse)) {
            ImGui.inputText("Hostname##serveraddressresolver", this.hostname);
            if (!this.hostname.isEmpty()) {
                if (ImGui.button("Copy Resolved Server Address##serveraddressresolver")) {
                    final ServerAddress serverAddress = ServerAddress.parse(this.hostname.get(), 25565);
                    final String oldAddress = serverAddress.toInetSocketAddress().toString();
                    serverAddress.resolve();
                    keyboard().setClipboard(oldAddress + "/" + serverAddress.toInetSocketAddress().toString());
                }
            }
            ImGui.end();
        }
    }

}
