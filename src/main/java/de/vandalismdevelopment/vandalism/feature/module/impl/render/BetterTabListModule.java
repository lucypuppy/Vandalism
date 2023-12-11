package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.ColorUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.base.event.InputListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.awt.ColorValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;

public class BetterTabListModule extends AbstractModule implements InputListener {

    public final Value<Boolean> toggleable = new BooleanValue("Toggleable Tab List", "Makes the Tab List toggleable.", this, false);

    public final Value<Integer> tabSize = new IntegerValue("Tab List Size", "How many players to display in the Tab List.", this, 100, 1, 1000);

    public final Value<Boolean> highlightSelf = new BooleanValue("Highlight Self", "Highlights yourself in the Tab List.", this, true);
    public final ColorValue selfColor = new ColorValue("Self Color", "The color to highlight your name with.", this, ColorUtils.withAlpha(Color.GREEN, 100)).visibleCondition(this.highlightSelf::getValue);

    public final Value<Boolean> moreInfo = new BooleanValue("More Info", "Shows the the game mode and the accurate ping right after every username.", this, true);
    public final Value<Integer> highPing = new IntegerValue("High Ping", "Sets the high ping value.", this, 500, 50, 1000).visibleCondition(this.moreInfo::getValue);

    private final ValueGroup pingColorCategory = new ValueGroup("Ping Colors", "The colors to display the ping with.", this).visibleCondition(this.moreInfo::getValue);
    public final Value<Color> lowPingColor = new ColorValue("Low Ping Color", "The color to display the minimum ping with.", this.pingColorCategory, Color.GREEN).visibleCondition(this.moreInfo::getValue);
    public final Value<Color> averagePingColor = new ColorValue("Average Ping Color", "The color to display the average ping with.", this.pingColorCategory, Color.YELLOW).visibleCondition(this.moreInfo::getValue);
    public final Value<Color> highPingColor = new ColorValue("High Ping Color", "The color to display the maximum ping with.", this.pingColorCategory, Color.RED).visibleCondition(this.moreInfo::getValue);

    private final ValueGroup gameModeColorCategory = new ValueGroup("Game Mode Colors", "The colors to display the game modes with.", this).visibleCondition(this.moreInfo::getValue);

    private final HashMap<Integer, String> gameModeColorValues;
    public boolean toggleState = false;

    public BetterTabListModule() {
        super("Better Tab List", "Improves the player tab list of the game.", FeatureCategory.RENDER, false, false);
        this.gameModeColorValues = new HashMap<>();
        for (final GameMode value : GameMode.values()) {
            final int id = value.getId();
            final Color defaultColor = switch (id) {
                case 0 -> Color.YELLOW.brighter();
                case 1 -> Color.ORANGE.brighter();
                case 2 -> Color.GREEN.darker();
                case 3 -> Color.RED.brighter();
                default -> Color.WHITE;
            };
            final String gameMode = StringUtils.normalizeEnumName(value.name());
            final ColorValue gameModeColor = new ColorValue("Game Mode " + gameMode + " Color", "The color to display the game mode " + gameMode + " with.", this.gameModeColorCategory, defaultColor).visibleCondition(this.moreInfo::getValue);
            this.gameModeColorValues.put(id, gameModeColor.getSaveIdentifier());
        }
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (this.toggleable.getValue()
                && this.mc.currentScreen == null
                && action == GLFW.GLFW_PRESS
                && key == this.mc.options.playerListKey.boundKey.getCode())
            this.toggleState = !this.toggleState;
    }

    public int getColorFromGameMode(final int id) {
        final Value<?> value = this.gameModeColorCategory.byName(this.gameModeColorValues.get(id));
        if (value instanceof ColorValue colorValue) {
            return colorValue.getValue().getRGB();
        }
        return Color.WHITE.getRGB();
    }

}
