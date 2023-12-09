package de.vandalismdevelopment.vandalism.account_v2.config;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.config.Config;

import java.io.File;
import java.io.IOException;

public class AccountsConfig extends Config {

    public AccountsConfig(File configDir, String name) {
        super(configDir, name);
    }

    @Override
    public JsonObject save() throws IOException {
        return null;
    }

    @Override
    public void load(JsonObject jsonObject) throws IOException {

    }

}
