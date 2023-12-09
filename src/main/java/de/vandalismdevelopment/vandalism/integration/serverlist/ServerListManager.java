package de.vandalismdevelopment.vandalism.integration.serverlist;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import de.vandalismdevelopment.vandalism.Vandalism;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerListManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final List<ServerList> serverLists;
    private final File configFile;
    private ServerList selectedServerList;
    private String lastSelectedServerList;

    public ServerListManager(final File dir) {
        this.serverLists = new ArrayList<>();
        this.configFile = new File(dir, "enhancedserverlist.json");
        this.serverLists.add(new ServerList());
        this.selectedServerList = this.serverLists.get(0);
        this.lastSelectedServerList = this.selectedServerList.getName();
    }

    public ServerList getSelectedServerList() {
        return this.selectedServerList;
    }

    public void setSelectedServerList(final String name) {
        final ServerList lastSelectedServerList = this.selectedServerList;
        this.selectedServerList = this.get(name);
        this.lastSelectedServerList = lastSelectedServerList.getName();
    }

    public boolean hasBeenChanged() {
        final boolean changed = !this.lastSelectedServerList.equals(this.selectedServerList.getName());
        this.lastSelectedServerList = this.selectedServerList.getName();
        return changed;
    }

    public ServerList get(final String name) {
        if (name.equals(ServerList.DEFAULT_SERVER_LIST_NAME)) {
            return this.serverLists.get(0);
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
        this.serverLists.remove(serverList);
        if (this.serverLists.size() == 1) {
            this.setSelectedServerList(this.serverLists.get(0).getName());
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
                            Vandalism.getInstance().getLogger().warn("Detected duplicated server list in enhanced server list config: " + name);
                            continue;
                        }
                        this.serverLists.add(new ServerList(name).setSize(size));
                    }
                    this.setSelectedServerList(configObject.get("selectedServerList").getAsString());
                    Vandalism.getInstance().getLogger().info("Loaded enhanced server list config.");
                }
            }
            jsonReader.close();
        } catch (final IOException e) {
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
                serverListObject.addProperty("size", serverList.getSize()); //TODO
                serverListsArray.add(serverListObject);
            }
            configObject.addProperty("selectedServerList", this.selectedServerList.getName());
            configObject.add("serverLists", serverListsArray);
            printWriter.println(GSON.toJson(configObject));
            printWriter.close();
        } catch (final IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to save enhanced server list config.", e);
        }
    }

    public List<ServerList> getServerLists() {
        return this.serverLists;
    }

}
