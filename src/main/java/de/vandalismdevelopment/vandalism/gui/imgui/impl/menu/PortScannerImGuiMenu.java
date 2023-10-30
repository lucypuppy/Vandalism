package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PortScannerImGuiMenu extends ImGuiMenu {

    private final static ImGuiInputTextCallback HOSTNAME_FILTER = new ImGuiInputTextCallback() {

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

    private final ImString hostname, result, progress;
    private final ImInt minPort, maxPort, threads;
    private final List<Integer> ports;
    private int currentPort, checkedPort;
    private State currentState;

    public PortScannerImGuiMenu() {
        super("Port Scanner");
        this.hostname = new ImString(253);
        this.result = new ImString(40000);
        this.progress = new ImString(200);
        this.minPort = new ImInt(1);
        this.maxPort = new ImInt(65535);
        this.threads = new ImInt(500);
        this.currentState = State.WAITING_INPUT;
        this.ports = new ArrayList<>();
    }

    private void reset() {
        this.result.clear();
        this.ports.clear();
        this.currentPort = this.minPort.get() - 1;
        this.checkedPort = this.minPort.get();
        this.progress.clear();
    }

    @Override
    public void render() {
        if (ImGui.begin("Port Scanner", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("Current State: " + this.currentState.getMessage());
            if (this.currentState == State.RUNNING) {
                this.progress.set(this.checkedPort + " / " + this.maxPort.get() +
                        " (" + (int) (((double) this.checkedPort / (double) this.maxPort.get()) * 100) + "%)"
                );
                ImGui.inputText(
                        "Progress##portscannerprogress",
                        this.progress,
                        ImGuiInputTextFlags.ReadOnly
                );
            }
            ImGui.separator();
            ImGui.inputText(
                    "Hostname##portscannerhostname",
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
                if (this.currentState == State.WAITING_INPUT) {
                    ImGui.inputInt("Min Port##portscannerminport", this.minPort, 1);
                    this.minPort.set(Math.max(1, Math.min(this.minPort.get(), this.maxPort.get() - 1)));
                    ImGui.inputInt("Max Port##portscannermaxport", this.maxPort, 1);
                    this.maxPort.set(Math.max(this.minPort.get() + 1, Math.min(this.maxPort.get(), 65535)));
                    if (ImGui.inputInt("Threads##portscannerthreads", this.threads, 1)) {
                        this.threads.set(Math.max(1, Math.min(this.threads.get(), 1000)));
                    }
                    if (this.minPort.get() != 1 || this.maxPort.get() != 65535 || this.threads.get() != 500) {
                        if (ImGui.button("Reset Values##portscannerresetvalues")) {
                            this.minPort.set(1);
                            this.maxPort.set(65535);
                            this.threads.set(500);
                        }
                        ImGui.sameLine();
                    }
                    if (!this.ports.isEmpty()) {
                        if (ImGui.button("Clear##portscannerclear")) {
                            this.reset();
                        }
                        ImGui.sameLine();
                    }
                    if (ImGui.button("Start##portscannerstart")) {
                        this.currentState = State.RUNNING;
                        this.reset();
                        this.result.set("Result of the port scan from " + this.hostname.get() + ":");
                        for (int i = 0; i < this.threads.get(); i++) {
                            new Thread(() -> {
                                try {
                                    while (this.currentState == State.RUNNING && this.currentPort < this.maxPort.get()) {
                                        this.currentPort++;
                                        final int port = this.currentPort;
                                        try {
                                            final Socket socket = new Socket();
                                            socket.connect(new InetSocketAddress(this.hostname.get(), port), 500);
                                            socket.close();
                                            synchronized (this.ports) {
                                                if (!this.ports.contains(port)) {
                                                    this.ports.add(port);
                                                    this.result.set(this.result.get() + "\n" + port);
                                                }
                                            }
                                        } catch (final Exception ignored) {
                                        }
                                        if (this.checkedPort < port) {
                                            this.checkedPort = port;
                                        }
                                    }
                                    this.currentState = State.WAITING_INPUT;
                                } catch (final Exception e) {
                                    this.result.set(this.result.get() + e.getClass().getSimpleName() + ": " + e.getMessage());
                                }
                            }).start();
                        }
                    }
                }
            }
            if (this.currentState != State.WAITING_INPUT) {
                if (ImGui.button("Stop##portscannerstop")) {
                    this.currentState = State.WAITING_INPUT;
                }
            }
            if (!this.ports.isEmpty()) {
                ImGui.sameLine();
                if (ImGui.button("Copy Result##portscannercopyresult")) {
                    final StringBuilder builder = new StringBuilder();
                    for (final int port : this.ports) {
                        builder.append('\n').append(this.hostname.get()).append(':').append(port);
                    }
                    keyboard().setClipboard(builder.toString());
                }
                ImGui.separator();
                ImGui.inputTextMultiline(
                        "##portscannerresult",
                        this.result,
                        -1,
                        -1,
                        ImGuiInputTextFlags.ReadOnly
                );
            }
            ImGui.end();
        }
    }

    private enum State {

        RUNNING("Running..."),
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
