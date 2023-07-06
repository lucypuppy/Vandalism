package me.nekosarekawaii.foxglove.gui.imgui.impl.alt.alttype;

import com.google.gson.JsonObject;

public abstract class Account {

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

    public abstract boolean login();

    public void onConfigSave(final JsonObject jsonObject) {
        // Nothing lol
    }

}
