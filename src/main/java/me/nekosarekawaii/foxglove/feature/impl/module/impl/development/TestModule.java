package me.nekosarekawaii.foxglove.feature.impl.module.impl.development;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.value.BooleanValue;
import me.nekosarekawaii.foxglove.value.value.ColorValue;
import me.nekosarekawaii.foxglove.value.value.ListValue;
import me.nekosarekawaii.foxglove.value.value.number.IntegerValue;
import me.nekosarekawaii.foxglove.value.value.number.slider.SliderDoubleValue;
import me.nekosarekawaii.foxglove.value.value.number.slider.SliderIntegerValue;

import java.awt.*;

@ModuleInfo(name = "Test", description = "This is just a module for development purposes.", category = FeatureCategory.DEVELOPMENT, isExperimental = true)
public class TestModule extends Module implements TickListener {

    private final BooleanValue booleanValue = new BooleanValue("Boolean Value", "Just a example boolean value.", this, false);

    private final Value<Color> colorValue = new ColorValue("Color Value", "Just a example color value.", this, Color.MAGENTA)
            .visibleConsumer(this.booleanValue::getValue);

    private final IntegerValue integerValue = new IntegerValue("Integer Value", "Just a example integer value.", this, 1, 1);

    private final SliderDoubleValue sliderDoubleValue = new SliderDoubleValue("test doubkea", "Cum", this, 2.0, 1.0, 10.0, "%.1f");

    private final SliderIntegerValue sliderIntegerValue = new SliderIntegerValue("test int", "Cum", this, 2, 1, 10);

    private final ListValue listValue = new ListValue("List Value", "Just a example list value.", this,
            "Test", "Test1", "Test2", "Test3");

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(TickListener.TickEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(TickListener.TickEvent.ID, this);
    }


    @Override
    public void onTick() {
        ChatUtils.infoChatMessage("Current Boolean Value State: " + this.booleanValue.getValue());
    }

}
