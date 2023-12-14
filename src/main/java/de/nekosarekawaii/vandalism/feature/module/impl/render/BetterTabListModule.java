package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.ColorUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.base.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;

public class BetterTabListModule extends AbstractModule implements KeyboardInputListener {

    public final BooleanValue toggleable = new BooleanValue(this, "Toggleable Tab List", "Makes the Tab List toggleable.", false);

    public final IntegerValue tabSize = new IntegerValue(this, "Tab List Size", "How many players to display in the Tab List.", 100, 1, 1000);

    public final BooleanValue highlightSelf = new BooleanValue(this, "Highlight Self", "Highlights yourself in the Tab List.", true);
    public final ColorValue selfColor = new ColorValue(this, "Self Color", "The color to highlight your name with.", ColorUtils.withAlpha(Color.GREEN, 100)).visibleCondition(this.highlightSelf::getValue);

    public final BooleanValue moreInfo = new BooleanValue(this, "More Info", "Shows the the game mode and the accurate ping right after every username.", true);
    public final IntegerValue highPing = new IntegerValue(this, "High Ping", "Sets the high ping value.", 500, 50, 1000).visibleCondition(this.moreInfo::getValue);

    private final ValueGroup pingColorGroup = new ValueGroup(this, "Ping Colors", "The colors to display the ping with.").visibleCondition(this.moreInfo::getValue);
    public final Value<Color> lowPingColor = new ColorValue(this.pingColorGroup, "Low Ping Color", "The color to display the minimum ping with.", Color.GREEN).visibleCondition(this.moreInfo::getValue);
    public final Value<Color> averagePingColor = new ColorValue(this.pingColorGroup, "Average Ping Color", "The color to display the average ping with.", Color.YELLOW).visibleCondition(this.moreInfo::getValue);
    public final Value<Color> highPingColor = new ColorValue(this.pingColorGroup, "High Ping Color", "The color to display the maximum ping with.", Color.RED).visibleCondition(this.moreInfo::getValue);

    private final ValueGroup gameModeColorGroup = new ValueGroup(this, "Game Mode Colors", "The colors to display the game modes with.").visibleCondition(this.moreInfo::getValue);

    private final HashMap<Integer, String> gameModeColorValues;
    public boolean toggleState = false;

    public BetterTabListModule() {
        super("Better Tab List", "Improves the player tab list of the game.", Category.RENDER);
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
            final ColorValue gameModeColor = new ColorValue(this.gameModeColorGroup, "Game Mode " + gameMode + " Color", "The color to display the game mode " + gameMode + " with.", defaultColor).visibleCondition(this.moreInfo::getValue);
            this.gameModeColorValues.put(id, gameModeColor.getName());
        }
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(KeyboardInputEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(KeyboardInputEvent.ID, this);
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
        final Value<?> value = this.gameModeColorGroup.byName(this.gameModeColorValues.get(id));
        if (value instanceof ColorValue colorValue) {
            return colorValue.getValue().getRGB();
        }
        return Color.WHITE.getRGB();
    }

}
