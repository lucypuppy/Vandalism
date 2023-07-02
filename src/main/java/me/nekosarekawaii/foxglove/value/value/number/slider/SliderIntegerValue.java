package me.nekosarekawaii.foxglove.value.value.number.slider;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.SliderValue;

public class SliderIntegerValue extends SliderValue<Integer> {

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

}
