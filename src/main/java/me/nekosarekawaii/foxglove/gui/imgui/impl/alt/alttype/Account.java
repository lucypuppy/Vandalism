package me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;

public abstract class Account implements MinecraftWrapper {

    private final String type;
    private String username;

    public Account(final String type, final String username) {
        this.type = type;
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public abstract void login();

    public void onConfigSave(final JsonObject jsonObject) {
        // Nothing lol
    }

}
