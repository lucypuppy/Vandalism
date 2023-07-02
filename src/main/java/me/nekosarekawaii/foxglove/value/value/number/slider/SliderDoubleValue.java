package me.nekosarekawaii.foxglove.value.value.number.slider;

import com.google.gson.JsonObject;
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

}
