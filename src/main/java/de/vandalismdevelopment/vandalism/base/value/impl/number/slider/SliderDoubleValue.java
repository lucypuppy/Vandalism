package de.vandalismdevelopment.vandalism.base.value.impl.number.slider;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.SliderNumberValue;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImDouble;

public class SliderDoubleValue extends SliderNumberValue<Double> {

    public SliderDoubleValue(final String name, final String description, final IValue parent, final double defaultValue,
                             final double min, final double max, final String format) {
        super(name, description, parent, "double", defaultValue, min, max, format);
    }

    public SliderDoubleValue(final String name, final String description, final IValue parent, final double defaultValue, final double min, final double max) {
        super(name, description, parent, "double", defaultValue, min, max, "%.1f");
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsDouble());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", this.getValue());
    }

    @Override
    public void render() {
        final ImDouble imDouble = new ImDouble(this.getValue());
        if (ImGui.sliderScalar("##" + this.getSaveIdentifier(), ImGuiDataType.Double, imDouble, this.getMin(), this.getMax(), this.getFormat())) {
            this.setValue(imDouble.get());
        }
    }

}
