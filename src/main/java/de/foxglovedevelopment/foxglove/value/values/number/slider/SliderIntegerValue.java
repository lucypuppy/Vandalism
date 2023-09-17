package de.foxglovedevelopment.foxglove.value.values.number.slider;

import com.google.gson.JsonObject;
import de.foxglovedevelopment.foxglove.value.IValue;
import de.foxglovedevelopment.foxglove.value.SliderNumberValue;
import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImInt;

public class SliderIntegerValue extends SliderNumberValue<Integer> {

    public SliderIntegerValue(final String name, final String description, final IValue parent, final int defaultValue, final int min, final int max) {
        super(name, description, parent, defaultValue, min, max, "");
    }

    @Override
    public void onConfigLoad(final JsonObject valueObject) {
        this.setValue(valueObject.get("value").getAsInt());
    }

    @Override
    public void onConfigSave(final JsonObject valueObject) {
        valueObject.addProperty("value", this.getValue());
    }

    @Override
    public void render() {
        final ImInt imInt = new ImInt(getValue());
        if (ImGui.sliderScalar(this.getName() + "##" + this.getHashIdent(), ImGuiDataType.S32, imInt, this.getMin(), this.getMax())) {
            this.setValue(imInt.get());
        }
    }

}
