package de.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import de.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

//TODO: Finish this.
public class PortScannerImGuiMenu extends ImGuiMenu {

    private final ImString hostname;
    private final ImInt startPort, endPort, threads;
    private State currentState;

    public PortScannerImGuiMenu() {
        super("Port Scanner");
        this.hostname = new ImString(253);
        this.startPort = new ImInt(25565);
        this.endPort = new ImInt(25566);
        this.threads = new ImInt(128);
        this.currentState = State.WAITING_INPUT;
    }

    @Override
    public void render() {
        if (ImGui.begin("Port Scanner", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State: " + this.currentState.getMessage());
            ImGui.inputText("Hostname##portscanner", this.hostname);
            ImGui.sameLine();
            if (ImGui.button("Clear##Hostnameportscanner")) {
                this.hostname.clear();
            }
            if (!this.hostname.get().isEmpty()) {
                if (ImGui.inputInt("Start Port##portscanner", this.startPort, 1)) {
                    this.startPort.set(Math.max(1, Math.min(this.startPort.get(), this.endPort.get() - 1)));
                }
                ImGui.sameLine();
                if (ImGui.button("Reset##StartPortportscanner")) {
                    this.startPort.set(25565);
                }
                if (ImGui.inputInt("End Port##portscanner", this.endPort, 1)) {
                    this.endPort.set(Math.max(this.startPort.get() + 1, Math.min(this.endPort.get(), 65535)));
                }
                ImGui.sameLine();
                if (ImGui.button("Reset##EndPortportscanner")) {
                    this.endPort.set(25566);
                }
                ImGui.inputInt("Threads##portscanner", this.threads, 1);
                ImGui.sameLine();
                if (ImGui.button("Reset##Threadsportscanner")) {
                    this.threads.set(128);
                }
            }
            if (ImGui.button("Clear##portscanner")) {
                this.clear();
            }
            ImGui.end();
        }
    }

    private void clear() {
        this.currentState = State.WAITING_INPUT;
    }

    private enum State {
        FAILED("There was an error fetching the server info."),
        BIND_FAILED("Cannot assign requested address."),
        UNKNOWN_HOST("Unknown Host."),
        CONNECTION_REFUSED("Connection Refused."),
        CONNECTION_TIMED_OUT("Connection timed out."),
        DATA_READ_FAILED("Failed to read data."),
        PACKET_READ_FAILED("Failed to read packet."),
        SUCCESS("Successfully fetched the server info."),
        WAITING_RESPONSE("Waiting for response..."),
        WAITING_INPUT("Waiting for input...");

        private final String message;

        State(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

    }

}
