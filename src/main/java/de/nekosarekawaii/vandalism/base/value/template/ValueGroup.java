package de.nekosarekawaii.vandalism.base.value.template;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class ValueGroup extends Value<List<Value<?>>> implements ValueParent, MinecraftWrapper {

    public ValueGroup(ValueParent parent, String name, String description) {
        super(parent, name, description, new ArrayList<>());
    }

    @Override
    public void load(final JsonObject valueObject) {
        final var valueNode = valueObject.getAsJsonObject(getName());

        for (Value<?> value : getValues()) {
            value.load(valueNode);
        }
    }

    @Override
    public void save(final JsonObject valueObject) {
        final var valueNode = new JsonObject();
        for (Value<?> value : getValues()) {
            value.save(valueNode);
        }

        valueObject.add(getName(), valueNode);
    }

    @Override
    public void render() {
        this.renderValueDescription(this);
        if (ImGui.treeNodeEx(this.getName())) {
            this.renderValues();
            ImGui.treePop();
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.getValue();
    }

    @Override
    public void resetValue() {
        for (final Value<?> value : this.getValue()) {
            value.resetValue();
        }
    }

}
