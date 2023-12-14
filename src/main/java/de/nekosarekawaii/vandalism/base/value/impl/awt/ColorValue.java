package de.nekosarekawaii.vandalism.base.value.impl.awt;

import com.google.gson.JsonObject;
import de.florianmichael.rclasses.common.ColorUtils;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import imgui.ImGui;

import java.awt.*;

public class ColorValue extends Value<Color> {

    public ColorValue(ValueParent parent, String name, String description, Color defaultValue) {
        super(parent, name, description, defaultValue);
    }

    @Override
    public void load(final JsonObject mainNode) {
        this.setValue(new Color(mainNode.get(getName()).getAsInt(), true));
    }

    @Override
    public void save(final JsonObject mainNode) {
        mainNode.addProperty(getName(), this.getValue().getRGB());
    }

    @Override
    public void render() {
        final float[] rgba = ColorUtils.rgba(this.getValue().getRGB());
        if (ImGui.colorEdit4("##" + this.getName(), rgba)) {
            this.setValue(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
        }
    }

}
