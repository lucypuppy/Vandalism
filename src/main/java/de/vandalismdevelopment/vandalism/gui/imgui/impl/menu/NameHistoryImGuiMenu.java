package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.florianmichael.rclasses.io.WebUtils;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.util.JsonDateDeserializer;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.util.Uuids;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class NameHistoryImGuiMenu extends ImGuiMenu {

    private final DateFormat formatter;

    private final List<String> currentData;

    private final ImString username;

    private String lastUsername, lastUUID;

    private State currentState;

    private Thread thread;

    private final Gson gson;

    public NameHistoryImGuiMenu() {
        super("Name History");
        this.formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");
        this.currentData = new ArrayList<>();
        this.username = new ImString(16);
        this.lastUsername = this.lastUUID = "";
        this.currentState = State.WAITING_INPUT;
        this.thread = null;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDateDeserializer())
                .create();
    }

    @Override
    public void render() {
        if (ImGui.begin("Name History", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State: " + this.currentState.getMessage());
            ImGui.inputText("Username##namehistory", this.username);
            final String usernameValue = this.username.get().replace(" ", "");
            if (usernameValue.length() > 2) {
                if (this.currentState.equals(State.WAITING_INPUT) || !this.currentState.name().contains("WAITING")) {
                    if (ImGui.button("Get##namehistory")) {
                        this.currentData.clear();
                        this.thread = new Thread(() -> {
                            UUID uuid = null;
                            String uuidIntArray = "";
                            try {
                                this.currentState = State.WAITING_MOJANG_RESPONSE;

                                final String content = WebUtils.DEFAULT.get("https://api.mojang.com/users/profiles/minecraft/" + usernameValue);
                                final Player player = gson.fromJson(content, Player.class);

                                if (player != null) {
                                    uuid = UUID.fromString(player.id()
                                            .replaceFirst(
                                                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                                    "$1-$2-$3-$4-$5"
                                            )
                                    );
                                    uuidIntArray = Arrays.toString(Uuids.toIntArray(uuid));
                                } else this.currentState = State.MOJANG_FETCH_ERROR;
                            } catch (final Exception ignored) {
                                this.currentState = State.MOJANG_FETCH_ERROR;
                                return;
                            }
                            if (uuid == null) {
                                this.currentState = State.FAILED;
                                return;
                            }
                            if (this.currentState == State.FAILED) return;
                            this.currentState = State.WAITING_LABY_RESPONSE;
                            final String labyUrl = "https://laby.net/api/v2/user/" + uuid + "/get-profile";

                            NameHistory history = null;

                            try {
                                final String content = WebUtils.DEFAULT.get(labyUrl);
                                history = gson.fromJson(content, NameHistory.class);
                            } catch (final IOException ignored) {

                            }

                            if (history == null || history.username_history == null || history.username_history.length == 0) {
                                this.currentState = State.FAILED;
                                return;
                            }
                            this.currentState = State.SUCCESS;
                            this.lastUsername = usernameValue;
                            this.lastUUID = uuid + " | " + uuidIntArray;
                            for (final Name entry : history.username_history) {
                                final StringBuilder entryBuilder = new StringBuilder(entry.name);
                                if (entry.changed_at != null && entry.changed_at.getTime() != 0) {
                                    entryBuilder.append(" | Changed at: ").append(formatter.format(entry.changed_at));
                                }
                                entryBuilder.append(" | Accurate: ").append(entry.accurate);
                                this.currentData.add(entryBuilder.toString());
                            }
                        });
                        this.thread.start();
                    }
                }
            }
            if (!this.currentData.isEmpty()) {
                ImGui.text("UUID Data: " + this.lastUUID);
                ImGui.sameLine();
                if (ImGui.button("Copy UUID Data##namehistory")) {
                    keyboard().setClipboard(this.lastUUID);
                }
                if (ImGui.button("Copy Name History##namehistory")) {
                    final StringBuilder dataBuilder = new StringBuilder(this.lastUsername + "'s Name History\n\n");
                    for (int i = 0; i < this.currentData.size(); i++) {
                        final int currentIndex = i + 1;
                        dataBuilder.append(currentIndex < 10 ? "0" + currentIndex : currentIndex).append(". ").append(this.currentData.get(i)).append('\n');
                    }
                    keyboard().setClipboard(dataBuilder.toString());
                }
                if (ImGui.button("Clear##namehistory")) {
                    this.currentData.clear();
                    this.currentState = State.WAITING_INPUT;
                }
                ImGui.text(this.lastUsername + "'s Name History");
                if (ImGui.beginListBox("##Usersnamehistory", 600, 500)) {
                    for (int i = 0; i < this.currentData.size(); i++) {
                        final String dataEntry = this.currentData.get(i);
                        ImGui.text(dataEntry);
                        ImGui.sameLine();
                        if (ImGui.button("Copy Entry##" + i + "namehistory")) {
                            keyboard().setClipboard(dataEntry);
                        }
                    }
                    ImGui.endListBox();
                }
            }
            switch (this.currentState) {
                case FAILED, ABORTED, MOJANG_FETCH_ERROR, WAITING_INPUT, SUCCESS -> {
                }
                default -> {
                    if (ImGui.button("Cancel##namehistory")) {
                        this.currentState = State.ABORTED;
                        if (this.thread.isAlive()) this.thread.interrupt();
                    }
                }
            }
            ImGui.end();
        }
    }

    private enum State {
        FAILED("There was an error fetching that user's name history."),
        ABORTED("The last request has been aborted by you!"),
        MOJANG_FETCH_ERROR("There was an error fetching the uuid from the mojang api."),
        SUCCESS("Successfully fetched the name history for that user."),
        WAITING_LABY_RESPONSE("Waiting for Laby API response..."),
        WAITING_MOJANG_RESPONSE("Waiting for Mojang API response..."),
        WAITING_INPUT("Waiting for input...");

        private final String message;

        State(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    private static class NameHistory {
        public Name[] username_history;
    }

    private static class Name {
        public String name;
        public Date changed_at;
        public boolean accurate;
    }

    private record Player(String id, String name) {
    }

}
