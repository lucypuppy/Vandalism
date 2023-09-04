package de.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.KeyboardListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.util.render.ColorUtils;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.BooleanValue;
import de.nekosarekawaii.foxglove.value.values.ColorValue;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderFloatValue;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BetterTabListModule extends Module implements KeyboardListener {

    public final Value<Integer> tabSize = new SliderIntegerValue(
            "Tab List Size",
            "How many players to display in the Tab List.",
            this,
            100,
            1,
            1000
    );

    public final Value<Boolean> self = new BooleanValue(
            "Highlight Self",
            "Highlights yourself in the Tab List.",
            this,
            true
    );

    public final ColorValue selfColor = new ColorValue(
            "Self Color",
            "The color to highlight your name with.",
            this,
            ColorUtils.withAlpha(Color.GREEN, 100)
    ).visibleConsumer(this.self::getValue);

    public final Value<Boolean> accurateLatency = new BooleanValue(
            "Accurate Latency",
            "Shows the latency as a number in the Tab List.",
            this,
            true
    );

    public final Value<Integer> highPing = new SliderIntegerValue(
            "High Ping",
            "When does a player have a high ping.",
            this,
            500,
            50,
            1000
    ).visibleConsumer(this.accurateLatency::getValue);

    public final Value<Float> pingScale = new SliderFloatValue(
            "Ping Scale",
            "Changes the scale of the ping text inside.",
            this,
            1.0f,
            0.5f,
            1.0f
    ).visibleConsumer(this.accurateLatency::getValue);

    public final Value<Boolean> gamemode = new BooleanValue(
            "Game Mode",
            "Shows the the Game Mode next to the Username.",
            this,
            true
    );

    public final Value<Boolean> toggleable = new BooleanValue(
            "Toggleable Tab List",
            "Makes the Tab List toggleable.",
            this,
            false
    );

    public final Value<Color> lowPingColor = new ColorValue(
            "Low Ping Color",
            "The color to display the minimum ping with.",
            this,
            Color.GREEN
    );

    public final Value<Color> averagePingColor = new ColorValue(
            "Average Ping Color",
            "The color to display the average ping with.",
            this,
            Color.YELLOW
    );

    public final Value<Color> highPingColor = new ColorValue(
            "High Ping Color",
            "The color to display the maximum ping with.",
            this,
            Color.RED
    );

    public boolean toggleState = false;

    public BetterTabListModule() {
        super(
                "Better Tab List",
                "Improves the player tab list of the game.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

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
        if (this.toggleable.getValue() && currentScreen() == null && action == GLFW.GLFW_PRESS && key == options().playerListKey.boundKey.getCode()) {
            this.toggleState = !this.toggleState;
        }
    }

}
