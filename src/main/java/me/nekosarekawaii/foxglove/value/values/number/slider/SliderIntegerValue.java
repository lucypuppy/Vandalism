package me.nekosarekawaii.foxglove.value.values.number.slider;

import com.google.gson.JsonObject;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImInt;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.SliderNumberValue;

public class SliderIntegerValue extends SliderNumberValue<Integer> {

    public SliderIntegerValue(final String name, final String description, final Module parent, final int defaultValue, final int min, final int max) {
        super(name, description, parent, defaultValue, min, max, "");
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("value").getAsInt());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImInt imInt = new ImInt(getValue());

        if (ImGui.sliderScalar(getName(), ImGuiDataType.S32, imInt, getMin(), getMax())) {
            setValue(imInt.get());
        }
    }

}
