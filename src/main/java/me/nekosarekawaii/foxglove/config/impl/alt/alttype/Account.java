package me.nekosarekawaii.foxglove.config.impl.alt.alttype;

import com.google.gson.JsonObject;

public abstract class Account {

    private final String type;
    private String username, uuid;

    public Account(final String type, final String username, final String uuid) {
        this.type = type;
        this.username = username;
        this.uuid = uuid;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public abstract void login();

    public void onConfigSave(final JsonObject jsonObject) {
        // Nothing lol
    }

}
