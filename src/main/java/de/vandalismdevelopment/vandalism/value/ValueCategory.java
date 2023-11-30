package de.vandalismdevelopment.vandalism.value;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class ValueCategory extends Value<List<Value<?>>> implements IValue, MinecraftWrapper {

    public ValueCategory(final String name, final String description, final IValue parent) {
        super(name, description, parent, "category", true, new ArrayList<>());
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
    }

    @Override
    public void render() {
        this.renderValueDescription(this);
        if (ImGui.treeNodeEx(this.getName() + "##" + this.getSaveIdentifier())) {
            this.renderValues();
            ImGui.treePop();
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.getValue();
    }

    @Override
    public Config getConfig() {
        return this.getParent().getConfig();
    }

    @Override
    public void resetValue() {
        for (final Value<?> value : this.getValue()) {
            value.resetValue();
        }
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

}
