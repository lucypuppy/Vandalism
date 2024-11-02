/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.integration.serverlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import de.nekosarekawaii.vandalism.Vandalism;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerListManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Getter
    private final List<ServerList> serverLists;
    private final File configFile;
    private ServerList selectedServerList;
    private String lastSelectedServerList;

    public ServerListManager(final File dir) {
        this.serverLists = new ArrayList<>();
        this.configFile = new File(dir, "enhancedserverlist.json");
        this.serverLists.add(new ServerList());
        this.selectedServerList = this.serverLists.getFirst();
        this.lastSelectedServerList = this.selectedServerList.getName();
    }

    public ServerList getSelectedServerList() {
        if (this.selectedServerList == null) {
            this.setSelectedServerList(ServerList.DEFAULT_SERVER_LIST_NAME);
        }
        return this.selectedServerList;
    }

    public void setSelectedServerList(final String name) {
        final ServerList lastSelectedServerList = this.selectedServerList;
        this.selectedServerList = this.get(name);
        this.lastSelectedServerList = lastSelectedServerList == null ? this.selectedServerList.getName() : lastSelectedServerList.getName();
    }

    public boolean hasBeenChanged() {
        final boolean changed = !this.lastSelectedServerList.equals(this.selectedServerList.getName());
        this.lastSelectedServerList = this.selectedServerList.getName();
        return changed;
    }

    public ServerList get(final String name) {
        if (name.equals(ServerList.DEFAULT_SERVER_LIST_NAME)) {
            return this.serverLists.getFirst();
        }
        for (final ServerList serverList : this.serverLists) {
            if (serverList.getName().equalsIgnoreCase(name)) {
                return serverList;
            }
        }
        return null;
    }

    public boolean add(final String name) {
        if (this.get(name) != null) {
            return false;
        }
        this.serverLists.add(new ServerList(name));
        this.saveConfig();
        return true;
    }

    public void remove(final String name) {
        final ServerList serverList = this.get(name);
        if (serverList == null) {
            return;
        }
        final File runDirectory = MinecraftClient.getInstance().runDirectory;
        final File serverListFile = new File(runDirectory, serverList.getName() + ".dat");
        if (serverListFile.exists()) {
            serverListFile.delete();
        }
        final File serverListFileOld = new File(runDirectory, serverList.getName() + ".dat_old");
        if (serverListFileOld.exists()) {
            serverListFileOld.delete();
        }
        this.serverLists.remove(serverList);
        if (this.selectedServerList != null && this.selectedServerList.getName().equals(name)) {
            this.setSelectedServerList(ServerList.DEFAULT_SERVER_LIST_NAME);
        }
        this.saveConfig();
    }

    public void loadConfig() {
        if (!this.configFile.exists()) return;
        try (final FileReader fileReader = new FileReader(this.configFile)) {
            final JsonReader jsonReader = new JsonReader(fileReader);
            final JsonElement jsonElement = JsonParser.parseReader(jsonReader);
            if (!jsonElement.isJsonNull()) {
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    fileReader.close();
                    jsonReader.close();
                    throw new JsonSyntaxException("Did not consume the entire document.");
                } else {
                    final JsonObject configObject = jsonElement.getAsJsonObject();
                    final JsonArray serverListsArray = configObject.getAsJsonArray("serverLists");
                    for (final JsonElement serverListElement : serverListsArray) {
                        final JsonObject serverListObject = serverListElement.getAsJsonObject();
                        final String name = serverListObject.get("name").getAsString();
                        final int size = serverListObject.get("size").getAsInt();
                        final ServerList existingServerList = this.get(name);
                        if (existingServerList != null) {
                            if (existingServerList.isDefault()) {
                                existingServerList.setSize(size);
                                continue;
                            }
                            Vandalism.getInstance().getLogger().warn("Detected duplicated server list in enhanced server list config: {}", name);
                            continue;
                        }
                        this.serverLists.add(new ServerList(name).setSize(size));
                    }
                    this.setSelectedServerList(configObject.get("selectedServerList").getAsString());
                }
            }
            jsonReader.close();
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to load enhanced server list config.", e);
        }
    }

    public void saveConfig() {
        try (final FileWriter fileWriter = new FileWriter(this.configFile)) {
            final PrintWriter printWriter = new PrintWriter(fileWriter);
            final JsonObject configObject = new JsonObject();
            final JsonArray serverListsArray = new JsonArray();
            for (final ServerList serverList : this.serverLists) {
                final JsonObject serverListObject = new JsonObject();
                serverListObject.addProperty("name", serverList.getName());
                serverListObject.addProperty("size", serverList.getSize());
                serverListsArray.add(serverListObject);
            }
            configObject.addProperty("selectedServerList", this.selectedServerList.getName());
            configObject.add("serverLists", serverListsArray);
            printWriter.println(GSON.toJson(configObject));
            printWriter.close();
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to save enhanced server list config.", e);
        }
    }

}
