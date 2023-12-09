package de.vandalismdevelopment.vandalism.base.value.impl.number.slider;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.SliderNumberValue;
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
        valueObject.addProperty("value", this.getValue());
    }

    @Override
    public void render() {
        final ImFloat imFloat = new ImFloat(this.getValue());
        if (ImGui.sliderScalar("##" + this.getSaveIdentifier(), ImGuiDataType.Float, imFloat, this.getMin(), this.getMax(), this.getFormat())) {
            this.setValue(imFloat.get());
        }
    }

}
