package me.nekosarekawaii.foxglove.value.values.number.slider;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImDouble;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.SliderValue;

public class SliderDoubleValue extends SliderValue<Double> {

    public SliderDoubleValue(final String name, final String description, final Module parent, final double defaultValue,
                             final double min, final double max, final String format) {
        super(name, description, parent, defaultValue, min, max, format);
    }

    public SliderDoubleValue(final String name, final String description, final Module parent, final double defaultValue, final double min, final double max) {
        super(name, description, parent, defaultValue, min, max, "%.1f");
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("value").getAsDouble());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImDouble imDouble = new ImDouble(getValue());

        if (ImGui.sliderScalar(getName(), ImGuiDataType.Double, imDouble, getMin(), getMax(), getFormat())) {
            setValue(imDouble.get());
        }
    }

}
