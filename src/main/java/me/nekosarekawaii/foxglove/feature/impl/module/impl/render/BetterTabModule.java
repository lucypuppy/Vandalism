package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.KeyboardListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.ColorValue;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderFloatValue;
import me.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@ModuleInfo(name = "Better Tab", description = "Shows you an improved version of the Game's Tab List.", category = FeatureCategory.RENDER)
public class BetterTabModule extends Module implements KeyboardListener {

    public final Value<Integer> tabSize = new SliderIntegerValue("Tab List Size", "How many players to display in the Tab List.", this, 100, 1, 1000);

    public final Value<Boolean> self = new BooleanValue("Highlight Self", "Highlights yourself in the Tab List.", this, true);

    public final Value<Color> selfColor = new ColorValue("Self Color", "The color to highlight your name with.", this, new Color(8, 210, 26, 100)).visibleConsumer(this.self::getValue);

    public final Value<Boolean> accurateLatency = new BooleanValue("Accurate Latency", "Shows the latency as a number in the Tab List.", this, true);

    public final Value<Integer> highPing = new SliderIntegerValue("High Ping", "When does a player have a high ping.", this, 500, 50, 1000).visibleConsumer(this.accurateLatency::getValue);

    public final Value<Float> pingScale = new SliderFloatValue("Ping Scale", "Changes the scale of the ping text inside.", this, 1.0f, 0.5f, 1.0f).visibleConsumer(this.accurateLatency::getValue);

    public final Value<Boolean> gamemode = new BooleanValue("Game Mode", "Shows the the Game Mode next to the Username.", this, true);

    public final Value<Boolean> toggleable = new BooleanValue("Toggleable Tab List", "Makes the Tab List toggleable.", this, false);

    public boolean toggleState = false;

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (this.toggleable.getValue() && mc().currentScreen == null && action == GLFW.GLFW_PRESS && key == mc().options.playerListKey.boundKey.getCode()) {
            this.toggleState = !this.toggleState;
        }
    }

}
