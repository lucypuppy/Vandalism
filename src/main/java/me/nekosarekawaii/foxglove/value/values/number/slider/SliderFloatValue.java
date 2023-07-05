package me.nekosarekawaii.foxglove.value.values.number.slider;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImFloat;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.SliderNumberValue;

public class SliderFloatValue extends SliderNumberValue<Float> {

    public SliderFloatValue(final String name, final String description, final IValue parent, final float defaultValue,
                            final float min, final float max, final String format) {
        super(name, description, parent, defaultValue, min, max, format);
    }

    public SliderFloatValue(final String name, final String description, final IValue parent, final float defaultValue, final float min, final float max) {
        super(name, description, parent, defaultValue, min, max, "%.1f");
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        setValue(valueObject.get("value").getAsFloat());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImFloat imFloat = new ImFloat(getValue());
        if (ImGui.sliderScalar(getName(), ImGuiDataType.Float, imFloat, getMin(), getMax(), getFormat())) {
            setValue(imFloat.get());
        }
    }

}
