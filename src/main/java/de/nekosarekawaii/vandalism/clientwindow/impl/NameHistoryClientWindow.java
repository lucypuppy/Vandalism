/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.clientwindow.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.util.MathUtil;
import de.nekosarekawaii.vandalism.util.MinecraftConstants;
import de.nekosarekawaii.vandalism.util.UUIDUtil;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Pair;
import net.minecraft.util.Uuids;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NameHistoryClientWindow extends StateClientWindow implements DataListWidget {

    private static final HttpClient REQUESTER = HttpClient.newHttpClient();

    private static final ImGuiInputTextCallback USERNAME_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            final int eventCharInt = imGuiInputTextCallbackData.getEventChar();
            if (eventCharInt == 0) return;
            final char eventChar = (char) eventCharInt;
            if (!Character.isLetterOrDigit(eventChar) && eventChar != '_') {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final ImString username = new ImString(MinecraftConstants.MAX_USERNAME_LENGTH);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();
    private String lastUsername = "", lastUUID = "";
    private final CopyOnWriteArrayList<ListDataEntry> nameHistoryDataEntries = new CopyOnWriteArrayList<>();

    public NameHistoryClientWindow() {
        super("Name History", Category.MISC, 600f, 500f);
    }

    private void clear() {
        this.nameHistoryDataEntries.clear();
        this.lastUsername = "";
        this.lastUUID = "";
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        super.onRender(context, mouseX, mouseY, delta);
        ImGui.text("Username");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(id + "username", this.username,
                ImGuiInputTextFlags.CallbackCharFilter,
                USERNAME_NAME_FILTER
        );
        final String usernameValue = this.username.get();
        if (!usernameValue.isBlank() && MathUtil.isBetween(usernameValue.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
            if (ImGui.button("Get" + id + "get", ImGui.getColumnWidth() / (this.nameHistoryDataEntries.isEmpty() ? 1f : 2f), ImGui.getTextLineHeightWithSpacing())) {
                this.clear();
                this.lastUsername = usernameValue;
                this.executor.submit(() -> {
                    try {
                        this.setState("Getting uuid by username from mojang api...");
                        this.lastUUID = UUIDUtil.getUUIDFromName(this.lastUsername);
                    } catch (final Exception e) {
                        this.clear();
                        this.setState("Error while getting uuid for name '" + this.lastUsername + "' from mojang api: " + e);
                    }
                    if (this.lastUUID.isBlank() && this.mc.player != null) {
                        this.setState("Fallback: Trying to get the uuid from the user on the server...");
                        for (final PlayerListEntry entry : Objects.requireNonNull(this.mc.getNetworkHandler()).getPlayerList()) {
                            if (entry.getProfile().getName().equalsIgnoreCase(this.lastUsername)) {
                                this.lastUUID = entry.getProfile().getId().toString();
                                break;
                            }
                        }
                    }
                    if (!this.lastUUID.isBlank()) {
                        try {
                            this.setState("Getting name history by uuid...");
                            final HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create("https://laby.net/api/v2/user/" + this.lastUUID + "/get-profile"))
                                    .GET()
                                    .build();
                            final HttpResponse<String> response = REQUESTER.send(request, HttpResponse.BodyHandlers.ofString());
                            final String labyNetApiContent = response.body();
                            if (!labyNetApiContent.isBlank()) {
                                final JsonObject jsonObject = this.gson.fromJson(labyNetApiContent, JsonObject.class);
                                if (jsonObject.has("username_history")) {
                                    final JsonArray usernameHistory = jsonObject.getAsJsonArray("username_history");
                                    for (final JsonElement element : usernameHistory) {
                                        final CopyOnWriteArrayList<Pair<String, String>> list = new CopyOnWriteArrayList<>();
                                        final JsonObject usernameObject = element.getAsJsonObject();
                                        final String username = usernameObject.get("username").getAsString();
                                        if (username.hashCode() == 65293) {
                                            continue;
                                        }
                                        list.add(new Pair<>("Username", username));
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
                                        list.add(new Pair<>("Date", date));
                                        list.add(new Pair<>("Accurate", accurate));
                                        this.nameHistoryDataEntries.add(new ListDataEntry(list));
                                    }
                                    if (this.nameHistoryDataEntries.isEmpty()) {
                                        this.setState("No name history found for " + this.lastUsername + ".");
                                        this.clear();
                                    } else {
                                        this.setState("Got name history.");
                                    }
                                }
                            } else {
                                this.setState("Invalid response for the name history from the laby.net api (Content is blank).");
                                this.clear();
                            }
                        } catch (final Exception e) {
                            this.setState("Error while getting name history from mojang api: " + e);
                            this.clear();
                        }
                    } else {
                        this.setState("Invalid uuid.");
                        this.clear();
                    }
                    this.delayedResetState(10000);
                });
            }
        }
        if (!this.lastUsername.isBlank() && !this.lastUUID.isBlank()) {
            if (!this.nameHistoryDataEntries.isEmpty()) {
                if (!usernameValue.isBlank() && MathUtil.isBetween(usernameValue.length(), MinecraftConstants.MIN_USERNAME_LENGTH, MinecraftConstants.MAX_USERNAME_LENGTH)) {
                    ImGui.sameLine();
                }
                if (ImGui.button("Clear" + id + "clear", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                    this.clear();
                }
            }
            if (ImGui.button("Copy UUID" + id + "copyUuid", ImGui.getColumnWidth() / 3f, ImGui.getTextLineHeightWithSpacing())) {
                this.mc.keyboard.setClipboard(this.lastUUID);
            }
            ImGui.sameLine();
            if (ImGui.button("Copy UUID Array" + id + "copyUuidArray", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
                this.mc.keyboard.setClipboard(Arrays.toString(Uuids.toIntArray(UUID.fromString(this.lastUUID))));
            }
            ImGui.sameLine();
            if (ImGui.button("Copy Data" + id + "copyData", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                final StringBuilder dataBuilder = new StringBuilder("[Data for " + this.lastUsername + "]\n\n");
                dataBuilder
                        .append("[UUID]\n")
                        .append(this.lastUUID)
                        .append("\n")
                        .append(Arrays.toString(Uuids.toIntArray(UUID.fromString(this.lastUUID)))).append("\n\n[Name History]\n");
                for (final ListDataEntry nameHistoryDataEntry : this.nameHistoryDataEntries) {
                    dataBuilder.append(nameHistoryDataEntry.getData()).append("\n\n");
                }
                this.mc.keyboard.setClipboard(dataBuilder.toString());
            }
            if (!this.nameHistoryDataEntries.isEmpty()) {
                ImGui.separator();
                ImGui.text("Name History of " + this.lastUsername + " (" + this.nameHistoryDataEntries.size() + ")");
                this.renderDataList(id + "nameHistoryList", -1, 55f, this.nameHistoryDataEntries);
            }
        }
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
        return false;
    }

    @Override
    public boolean shouldHighlightDataEntry(final DataEntry dataEntry) {
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        return null;
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            this.mc.keyboard.setClipboard(listDataEntry.getFirst().getRight());
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String playerName = listDataEntry.getFirst().getRight();
            if (ImGui.button("Copy Data" + id + "copyData" + playerName, ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                this.mc.keyboard.setClipboard(listDataEntry.getData());
            }
        }
    }

}
