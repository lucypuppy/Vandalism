package me.nekosarekawaii.foxglove.value;

import com.google.gson.JsonObject;
import imgui.ImGui;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.config.Config;

public class ValueCategory extends Value<ObjectArrayList<Value<?>>> implements IValue {

    public ValueCategory(final String name, final String description, final IValue parent) {
        super(name, description, parent, new ObjectArrayList<>());
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
    }

    @Override
    public void render() {
        if (ImGui.treeNodeEx(this.getName() + "###" + this.getHashIdent())) {
            for (final Value<?> value : this.getValue()) {
                if (value.isVisible() != null && !value.isVisible().getAsBoolean()) {
                    continue;
                }
                value.render();
            }

            ImGui.treePop();
        }
    }

    @Override
    public ObjectArrayList<Value<?>> getValues() {
        return this.getValue();
    }

    @Override
    public Config getConfig() {
        return this.getParent().getConfig();
    }

}
