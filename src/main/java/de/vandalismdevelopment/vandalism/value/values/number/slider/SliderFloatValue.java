package de.vandalismdevelopment.vandalism.value.values.number.slider;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.SliderNumberValue;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImFloat;

public class SliderFloatValue extends SliderNumberValue<Float> {

    public SliderFloatValue(final String name, final String description, final IValue parent, final float defaultValue,
                            final float min, final float max, final String format) {
        super(name, description, parent, "float", defaultValue, min, max, format);
    }

    public SliderFloatValue(final String name, final String description, final IValue parent, final float defaultValue, final float min, final float max) {
        super(name, description, parent, "float", defaultValue, min, max, "%.1f");
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsFloat());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", getValue());
    }

    @Override
    public void render() {
        final ImFloat imFloat = new ImFloat(getValue());
        if (ImGui.sliderScalar(this.getName() + "##" + this.getSaveIdentifier(), ImGuiDataType.Float, imFloat, this.getMin(), this.getMax(), this.getFormat())) {
            this.setValue(imFloat.get());
        }
    }

}
