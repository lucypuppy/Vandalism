package de.vandalismdevelopment.vandalism.base.value.impl.number;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.template.ValueNumber;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImFloat;

public class FloatValue extends ValueNumber<Float> {

    public FloatValue(ValueParent parent, String name, String description, Float minValue, Float defaultValue, Float maxValue) {
        super(parent, name, description, minValue, defaultValue, maxValue);
    }

    @Override
    public void load(final JsonObject valueObject) {
        this.setValue(valueObject.get(getName()).getAsFloat());
    }

    @Override
    public void save(final JsonObject valueObject) {
        valueObject.addProperty(getName(), this.getValue());
    }

    @Override
    public void render() {
        final ImFloat nextValue = new ImFloat(this.getValue());
        if (ImGui.sliderScalar("##" + this.getName(), ImGuiDataType.Float, nextValue, this.getMinValue(), this.getMaxValue(), "%.1f")) {
            this.setValue(nextValue.get());
        }
    }

}
