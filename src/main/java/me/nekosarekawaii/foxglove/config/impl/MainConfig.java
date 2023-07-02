package me.nekosarekawaii.foxglove.config.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.Config;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class MainConfig extends Config {

    private int mainMenuKeyCode;
    private String commandPrefix;
    private final Object2ObjectOpenHashMap<String, Integer> chatMacros;

    public MainConfig() {
        super(Foxglove.getInstance().getDir(), "main");
        this.mainMenuKeyCode = GLFW.GLFW_KEY_RIGHT_SHIFT;
        this.commandPrefix = ".";
        this.chatMacros = new Object2ObjectOpenHashMap<>();
        this.chatMacros.put(".togglemodule test", GLFW.GLFW_KEY_J);
    }

    public int getMainMenuKeyCode() {
        return this.mainMenuKeyCode;
    }


    public String getCommandPrefix() {
        return this.commandPrefix;
    }


    public Object2ObjectOpenHashMap<String, Integer> getChatMacros() {
        return this.chatMacros;
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mainMenuKeyCode", this.mainMenuKeyCode);
        jsonObject.addProperty("commandPrefix", this.commandPrefix);
        final JsonArray chatMacrosJsonArray = new JsonArray();
        for (final Object2ObjectOpenHashMap.Entry<String, Integer> entry : this.chatMacros.object2ObjectEntrySet()) {
            final JsonObject chatMacroJsonObject = new JsonObject();
            chatMacroJsonObject.addProperty("command", entry.getKey());
            chatMacroJsonObject.addProperty("keyCode", entry.getValue());
            chatMacrosJsonArray.add(chatMacroJsonObject);
        }
        jsonObject.add("chatMacros", chatMacrosJsonArray);
        return jsonObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        this.mainMenuKeyCode = jsonObject.get("mainMenuKeyCode").getAsInt();
        this.commandPrefix = jsonObject.get("commandPrefix").getAsString();
        final JsonArray chatMacrosJsonArray = jsonObject.get("chatMacros").getAsJsonArray();
        this.chatMacros.clear();
        for (final JsonElement jsonElement : chatMacrosJsonArray) {
            final JsonObject chatMacroJsonObject = jsonElement.getAsJsonObject();
            this.chatMacros.put(chatMacroJsonObject.get("command").getAsString(), chatMacroJsonObject.get("keyCode").getAsInt());
        }
    }

}
