package de.nekosarekawaii.vandalism.base.value.impl.number;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.template.ValueNumber;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImInt;

public class IntegerValue extends ValueNumber<Integer> {

    public IntegerValue(ValueParent parent, String name, String description, Integer defaultValue, Integer minValue, Integer maxValue) {
        super(parent, name, description, defaultValue, minValue, maxValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        this.setValue(mainNode.get(getName()).getAsInt());
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(getName(), this.getValue());
    }

    @Override
    public void render() {
        final ImInt nextValue = new ImInt(this.getValue());
        if (ImGui.sliderScalar("##" + this.getName(), ImGuiDataType.S32, nextValue, this.getMinValue(), this.getMaxValue())) {
            this.setValue(nextValue.get());
        }
    }

}
