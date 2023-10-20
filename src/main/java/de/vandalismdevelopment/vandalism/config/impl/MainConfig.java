package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.config.impl.main.*;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;

import java.io.IOException;

public class MainConfig extends ValueableConfig implements MinecraftWrapper {

    public MainConfig() {
        super(Vandalism.getInstance().getDir(), "main");
    }

    public final MenuCategory menuCategory = new MenuCategory(this);

    public final ChatCategory chatCategory = new ChatCategory(this);

    public final AccessibilityCategory accessibilityCategory = new AccessibilityCategory(this);

    public final VisualCategory visualCategory = new VisualCategory(this);

    public final MovementCategory movementCategory = new MovementCategory(this);

    public final RotationCategory rotationCategory = new RotationCategory(this);

    @Override
    public JsonObject save() throws IOException {
        final JsonObject configObject = new JsonObject(), valuesArray = new JsonObject();
        this.saveValues(valuesArray, this.getValues());
        configObject.add("values", valuesArray);
        return configObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        if (jsonObject.has("values")) {
            this.loadValues(jsonObject.getAsJsonObject("values"), this.getValues());
        }
    }

}
