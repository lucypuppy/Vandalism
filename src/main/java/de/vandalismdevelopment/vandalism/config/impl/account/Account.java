package de.vandalismdevelopment.vandalism.config.impl.account;

import com.google.gson.JsonObject;

import java.util.UUID;

public abstract class Account {

    private final String type;
    private String username;
    private UUID uuid;

    public Account(final String type, final String username, final UUID uuid) {
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }

    public abstract void login() throws Throwable;

    public void onConfigSave(final JsonObject jsonObject) {}

}