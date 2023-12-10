package de.vandalismdevelopment.vandalism.gui.impl.menu.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui_v2.ImWindow;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.lenni0451.mcping.ServerAddress;
import net.minecraft.client.gui.DrawContext;

import java.util.concurrent.Executors;

public class ServerAddressResolverImGuiMenu extends ImWindow {

    private static final ImGuiInputTextCallback HOSTNAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '.' &&
                            imGuiInputTextCallbackData.getEventChar() != '-'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString hostname, lastData;

    public ServerAddressResolverImGuiMenu() {
        super("Server Address Resolver");
        this.hostname = new ImString(253);
        this.lastData = new ImString();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (ImGui.begin(
                "Server Address Resolver##serveraddressresolver",
                ImGuiWindowFlags.NoCollapse
        )) {
            ImGui.inputText(
                    "Hostname##serveraddressresolverhostname",
                    this.hostname,
                    ImGuiInputTextFlags.CallbackCharFilter,
                    HOSTNAME_FILTER
            );
            if (
                    !this.hostname.get().isBlank() &&
                            this.hostname.get().length() >= 4 &&
                            this.hostname.get().contains(".") &&
                            this.hostname.get().indexOf(".") < this.hostname.get().length() - 2
            ) {
                if (ImGui.button("Resolve Server Address##serveraddressresolverresolve")) {
                    this.lastData.clear();
                    Executors.newSingleThreadExecutor().submit(() -> {
                        try {
                            final ServerAddress serverAddress = ServerAddress.parse(this.hostname.get(), 25565);
                            String oldAddress = serverAddress.toInetSocketAddress().toString();
                            if (oldAddress.contains("./")) oldAddress = oldAddress.replace("./", "/");
                            if (oldAddress.contains("/")) oldAddress = oldAddress.replace("/", "\n");
                            serverAddress.resolve();
                            String newAddress = serverAddress.toInetSocketAddress().toString();
                            if (newAddress.contains("./")) newAddress = newAddress.replace("./", "/");
                            if (newAddress.contains("/")) newAddress = newAddress.replace("/", "\n");
                            this.lastData.set(oldAddress + "\n\n" + newAddress);
                        } catch (final Exception e) {
                            this.lastData.set("Error: " + e.getMessage());
                        }
                    });
                }
                ImGui.sameLine();
            }
            if (!this.lastData.get().isBlank()) {
                if (ImGui.button("Clear##serveraddressresolverclear")) {
                    this.lastData.clear();
                }
                ImGui.separator();
                ImGui.text("Data");
                ImGui.setNextItemWidth(-1);
                ImGui.inputTextMultiline("##serveraddressresolverdata", this.lastData, -1, 100, ImGuiInputTextFlags.ReadOnly);
            }
            ImGui.end();
        }
    }

}
