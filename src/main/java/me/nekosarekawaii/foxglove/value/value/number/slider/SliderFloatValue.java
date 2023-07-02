package me.nekosarekawaii.foxglove.value.value.number.slider;

import com.google.gson.JsonObject;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.SliderValue;

public class SliderFloatValue extends SliderValue<Float> {

    public SliderFloatValue(final String name, final String description, final Module parent, final float defaultValue,
                            final float min, final float max, final String format) {
        super(name, description, parent, defaultValue, min, max, format);
    }

    public SliderFloatValue(final String name, final String description, final Module parent, final float defaultValue,
                            final float min, final float max) {
        super(name, description, parent, defaultValue, min, max, "%.1f");
    }

    @Override
    public void onConfigLoad(JsonObject valueObject) {
        setValue(valueObject.get("value").getAsFloat());
    }

    @Override
    public void onConfigSave(JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

}
