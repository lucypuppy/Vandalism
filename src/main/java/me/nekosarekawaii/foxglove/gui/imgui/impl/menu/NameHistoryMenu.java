package me.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.util.Http;
import net.minecraft.client.MinecraftClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NameHistoryMenu {

    private final static DateFormat formatter = new SimpleDateFormat("hh:mm:ss a, dd/MM/yyyy");

    private final static ObjectArrayList<String> currentData = new ObjectArrayList<>();

    private final static ImString username = new ImString(16);

    private static String lastUsername = "", lastUUID = "";

    private static State currentState = State.WAITING_INPUT;

    private static Thread thread;

    public static void render() {
        if (ImGui.begin("Name History", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State: " + currentState.getMessage());
            ImGui.inputText("Username##namehistory", username);
            final String usernameValue = username.get().replace(" ", "");
            if (usernameValue.length() > 2) {
                if (currentState.equals(State.WAITING_INPUT) || !currentState.name().contains("WAITING")) {
                    if (ImGui.button("Get##namehistory")) {
                        currentData.clear();
                        thread = new Thread(() -> {
                            String uuid = "";
                            try {
                                currentState = State.WAITING_MOJANG_RESPONSE;
                                final Player player = Http.get("https://api.mojang.com/users/profiles/minecraft/" + usernameValue).sendJson(Player.class);
                                if (player != null) uuid = player.id();
                                else currentState = State.MOJANG_FETCH_ERROR;
                            } catch (final Exception ignored) {
                                currentState = State.MOJANG_FETCH_ERROR;
                                return;
                            }
                            if (uuid.isEmpty()) {
                                currentState = State.FAILED;
                                return;
                            }
                            if (currentState == State.FAILED) return;
                            currentState = State.WAITING_LABY_RESPONSE;
                            final String labyUrl = "https://laby.net/api/v2/user/" + uuid + "/get-profile";
                            final NameHistory history = Http.get(labyUrl).sendJson(NameHistory.class);
                            if (history == null || history.username_history == null || history.username_history.length == 0) {
                                currentState = State.FAILED;
                                return;
                            }
                            currentState = State.SUCCESS;
                            lastUsername = usernameValue;
                            lastUUID = uuid;
                            for (final Name entry : history.username_history) {
                                final StringBuilder entryBuilder = new StringBuilder(entry.name);
                                if (entry.changed_at != null && entry.changed_at.getTime() != 0) {
                                    entryBuilder.append(" | Changed at: ").append(formatter.format(entry.changed_at));
                                }
                                entryBuilder.append(" | Accurate: ").append(entry.accurate);
                                currentData.add(entryBuilder.toString());
                            }
                        });
                        thread.start();
                    }
                }
            }
            if (!currentData.isEmpty()) {
                ImGui.text("UUID: " + lastUUID);
                ImGui.sameLine();
                if (ImGui.button("Copy UUID##namehistory"))
                    MinecraftClient.getInstance().keyboard.setClipboard(lastUUID);
                if (ImGui.button("Copy Name History##namehistory")) {
                    final StringBuilder dataBuilder = new StringBuilder(lastUsername + "'s Name History\n\n");
                    for (int i = 0; i < currentData.size(); i++) {
                        final int currentIndex = i + 1;
                        dataBuilder.append(currentIndex < 10 ? "0" + currentIndex : currentIndex).append(". ").append(currentData.get(i)).append('\n');
                    }
                    MinecraftClient.getInstance().keyboard.setClipboard(dataBuilder.toString());
                }
                if (ImGui.button("Clear##namehistory")) {
                    currentData.clear();
                    currentState = State.WAITING_INPUT;
                }
                ImGui.text(lastUsername + "'s Name History");
                if (ImGui.beginListBox("##Usersnamehistory", 600, 500)) {
                    for (int i = 0; i < currentData.size(); i++) {
                        final String dataEntry = currentData.get(i);
                        ImGui.text(dataEntry);
                        ImGui.sameLine();
                        if (ImGui.button("Copy Entry##" + i + "namehistory")) {
                            MinecraftClient.getInstance().keyboard.setClipboard(dataEntry);
                        }
                    }
                    ImGui.endListBox();
                }
            }
            switch (currentState) {
                case FAILED, ABORTED, MOJANG_FETCH_ERROR, WAITING_INPUT, SUCCESS -> {
                }
                default -> {
                    if (ImGui.button("Cancel##namehistory")) {
                        currentState = State.ABORTED;
                        if (thread.isAlive()) thread.interrupt();
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
