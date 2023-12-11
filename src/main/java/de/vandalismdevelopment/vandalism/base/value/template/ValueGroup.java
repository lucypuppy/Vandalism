package de.vandalismdevelopment.vandalism.base.value.template;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class ValueGroup extends Value<List<Value<?>>> implements ValueParent, MinecraftWrapper {

    public ValueGroup(ValueParent parent, String name, String description) {
        super(parent, name, description, new ArrayList<>());
    }

    @Override
    public void load(final JsonObject valueObject) {
    }

    @Override
    public void save(final JsonObject valueObject) {
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
