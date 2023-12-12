package de.vandalismdevelopment.vandalism.gui_v2.impl.namehistory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.florianmichael.rclasses.io.WebUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui_v2.ImWindow;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NameHistoryImGuiMenu extends ImWindow {

    private static final ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (
                    !Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                            imGuiInputTextCallbackData.getEventChar() != '_' &&
                            imGuiInputTextCallbackData.getEventChar() != '§'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString username, state;

    private final ExecutorService executor;

    private final Gson gson;

    private final List<Name> names;

    private String lastUsername, lastUUID;

    public NameHistoryImGuiMenu() {
        super("Name History");
        this.username = new ImString(16);
        this.state = new ImString(200);
        this.resetState();
        this.executor = Executors.newSingleThreadExecutor();
        this.gson = new Gson();
        this.names = new ArrayList<>();
        this.lastUsername = "";
        this.lastUUID = "";
    }

    private void clear() {
        this.names.clear();
        this.lastUsername = "";
        this.lastUUID = "";
    }

    private void resetState() {
        this.state.set("Waiting for input...");
    }

    private void delayedResetState() {
        try {
            Thread.sleep(10000);
        } catch (final InterruptedException ignored) {
        }
        this.resetState();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (ImGui.begin("Name History##namehistory", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##namehistorystate", this.state, ImGuiInputTextFlags.ReadOnly);
            ImGui.spacing();
            ImGui.text("Username");
            ImGui.setNextItemWidth(-1);
            ImGui.inputText("##namehistoryusername", this.username,
                    ImGuiInputTextFlags.CallbackCharFilter,
                    USERNAME_NAME_FILTER
            );
            final String usernameValue = this.username.get();
            if (!usernameValue.isBlank() && usernameValue.length() > 2 && usernameValue.length() < 17) {
                if (ImGui.button("Get##namehistoryget")) {
                    this.clear();
                    this.lastUsername = usernameValue;
                    this.executor.submit(() -> {
                        try {
                            this.state.set("Getting uuid by username from mojang api...");
                            final String mojangApiContent = WebUtils.DEFAULT.get("https://api.mojang.com/users/profiles/minecraft/" + this.lastUsername);
                            if (!mojangApiContent.isBlank()) {
                                this.lastUUID = mojangApiContent.split("\"id\" : \"")[1].split("\",")[0].replaceFirst(
                                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                        "$1-$2-$3-$4-$5"
                                );
                            } else {
                                this.state.set("Invalid response for the uuid from the mojang api (Content is blank).");
                                this.clear();
                            }
                        } catch (final Exception e) {
                            this.state.set("Error while getting uuid from mojang api: " + e);
                        }
                        if (this.lastUUID.isBlank() && this.mc.player != null) {
                            this.state.set("Fallback: Trying to get the uuid from the user on the server...");
                            for (final PlayerListEntry entry : this.mc.getNetworkHandler().getPlayerList()) {
                                if (entry.getProfile().getName().equalsIgnoreCase(this.lastUsername)) {
                                    this.lastUUID = entry.getProfile().getId().toString();
                                    break;
                                }
                            }
                        }
                        if (!this.lastUUID.isBlank()) {
                            try {
                                this.state.set("Getting name history by uuid...");
                                final String labyNetApiContent = WebUtils.DEFAULT.get("https://laby.net/api/v2/user/" + this.lastUUID + "/get-profile");
                                if (!labyNetApiContent.isBlank()) {
                                    final JsonObject jsonObject = this.gson.fromJson(labyNetApiContent, JsonObject.class);
                                    if (jsonObject.has("username_history")) {
                                        final JsonArray usernameHistory = jsonObject.getAsJsonArray("username_history");
                                        for (final JsonElement element : usernameHistory) {
                                            final JsonObject usernameObject = element.getAsJsonObject();
                                            String date =
                                                    usernameObject.get("changed_at").isJsonNull() ? "" :
                                                            usernameObject.get("changed_at").getAsString();
                                            if (!date.isBlank()) {
                                                date = date.replaceFirst(
                                                        "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(.*)",
                                                        "$3.$2.$1 $4:$5:$6"
                                                );
                                            } else date = "Unknown";
                                            String accurate = usernameObject.get("accurate").getAsString();
                                            if (accurate.equals("true")) accurate = "Yes";
                                            else if (accurate.equals("false")) accurate = "No";
                                            else accurate = "Unknown";
                                            this.names.add(new Name(
                                                    usernameObject.get("username").getAsString(),
                                                    date,
                                                    accurate
                                            ));
                                        }
                                        if (this.names.isEmpty()) {
                                            this.state.set("No name history found for " + this.lastUsername + ".");
                                            this.clear();
                                        } else this.state.set("Got name history.");
                                    }
                                } else {
                                    this.state.set("Invalid response for the name history from the laby.net api (Content is blank).");
                                    this.clear();
                                }
                            } catch (final Exception e) {
                                this.state.set("Error while getting name history from mojang api: " + e);
                                this.clear();
                            }
                        } else {
                            this.state.set("Invalid uuid.");
                            this.clear();
                        }
                        this.delayedResetState();
                    });
                }
                ImGui.sameLine();
            }
            if (!this.lastUsername.isBlank() && !this.lastUUID.isBlank()) {
                if (ImGui.button("Copy UUID##namehistorycopyuuid")) {
                    this.mc.keyboard.setClipboard(this.lastUUID + " | " + Arrays.toString(Uuids.toIntArray(UUID.fromString(this.lastUUID))));
                }
                ImGui.sameLine();
                if (ImGui.button("Copy Data##namehistorycopydata")) {
                    final StringBuilder dataBuilder = new StringBuilder("[Data for " + this.lastUsername + "]\n\n");
                    dataBuilder
                            .append("[UUID]\n")
                            .append(this.lastUUID)
                            .append("\n")
                            .append(Arrays.toString(Uuids.toIntArray(UUID.fromString(this.lastUUID)))).append("\n\n[Name History]\n");
                    for (final Name name : this.names) {
                        dataBuilder.append("Username: ").append(name.username());
                        dataBuilder.append(" | Date: ").append(name.date());
                        dataBuilder.append(" | Accurate: ").append(name.accurate()).append("\n");
                    }
                    this.mc.keyboard.setClipboard(dataBuilder.toString());
                }
            }
            if (!this.names.isEmpty()) {
                ImGui.sameLine();
                if (ImGui.button("Clear##namehistoryclear")) {
                    this.clear();
                }
                ImGui.separator();
                ImGui.text("Name History");
                final NamesTableColumn[] namesTableColumns = NamesTableColumn.values();
                final int maxTableColumns = namesTableColumns.length;
                if (ImGui.beginTable("names##namestable", maxTableColumns,
                        ImGuiTableFlags.Borders |
                                ImGuiTableFlags.Resizable |
                                ImGuiTableFlags.RowBg |
                                ImGuiTableFlags.ContextMenuInBody
                )) {
                    for (final NamesTableColumn namesTableColumn : namesTableColumns) {
                        ImGui.tableSetupColumn(namesTableColumn.getName());
                    }
                    ImGui.tableHeadersRow();
                    for (final Name name : this.names) {
                        ImGui.tableNextRow();
                        for (int i = 0; i < maxTableColumns; i++) {
                            ImGui.tableSetColumnIndex(i);
                            final NamesTableColumn accountsTableColumn = namesTableColumns[i];
                            switch (accountsTableColumn) {
                                case USERNAME -> ImGui.textWrapped(name.username());
                                case DATE -> ImGui.textWrapped(name.date());
                                case ACCURATE -> ImGui.textWrapped(name.accurate());
                                case ACTIONS -> {
                                    ImGui.spacing();
                                    if (ImGui.button("Copy Data##namehistorycopydata" + name.username(), 0, 28)) {
                                        this.mc.keyboard.setClipboard(
                                                "Username: " + name.username() +
                                                        " | Date: " + name.date() +
                                                        " | Accurate: " + name.accurate()
                                        );
                                    }
                                    ImGui.spacing();
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    ImGui.endTable();
                }
            }
            ImGui.end();
        }
    }

}
