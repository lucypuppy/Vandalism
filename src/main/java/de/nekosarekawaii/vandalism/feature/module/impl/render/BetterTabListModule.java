/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;

public class BetterTabListModule extends Module implements KeyboardInputListener {

    public final BooleanValue toggleable = new BooleanValue(this, "Toggleable Tab List", "Makes the Tab List toggleable.", false);

    public final IntegerValue tabSize = new IntegerValue(this, "Tab List Size", "How many players to display in the Tab List.", 100, 1, 1000);

    public final BooleanValue highlightSelf = new BooleanValue(this, "Highlight Self", "Highlights yourself in the Tab List.", true);
    public final ColorValue selfColor = new ColorValue(this, "Self Color", "The color to highlight your name with.", ColorUtils.withAlpha(Color.CYAN, 100)).visibleCondition(this.highlightSelf::getValue);

    public final BooleanValue highlightFriends = new BooleanValue(this, "Highlight Friends", "Highlights friends in the Tab List.", true);
    public final ColorValue friendsColor = new ColorValue(this, "Friends Color", "The color to highlight your friends names with.", ColorUtils.withAlpha(Color.GREEN, 100)).visibleCondition(this.highlightFriends::getValue);

    public final BooleanValue showAccuratePing = new BooleanValue(this, "Show Accurate Ping", "Shows the the game mode and the accurate ping right after every username.", true);
    public final LongValue maxPing = new LongValue(this, "Max Ping", "Sets the maximum ping value.", 999L, 100L, 9999L).visibleCondition(this.showAccuratePing::getValue);
    public final IntegerValue highPing = new IntegerValue(this, "High Ping", "Sets the high ping value.", 500, 50, 1000).visibleCondition(this.showAccuratePing::getValue);
    private final ValueGroup pingColorGroup = new ValueGroup(this, "Ping Colors", "The colors to display the ping with.").visibleCondition(this.showAccuratePing::getValue);
    public final ColorValue lowPingColor = new ColorValue(this.pingColorGroup, "Low Ping Color", "The color to display the minimum ping with.", Color.GREEN).visibleCondition(this.showAccuratePing::getValue);
    public final ColorValue averagePingColor = new ColorValue(this.pingColorGroup, "Average Ping Color", "The color to display the average ping with.", Color.YELLOW).visibleCondition(this.showAccuratePing::getValue);
    public final ColorValue highPingColor = new ColorValue(this.pingColorGroup, "High Ping Color", "The color to display the maximum ping with.", Color.RED).visibleCondition(this.showAccuratePing::getValue);

    public final BooleanValue showGameMode = new BooleanValue(this, "Show Game Mode", "Shows the the game mode and the accurate ping right after every username.", true);
    private final ValueGroup gameModeColorGroup = new ValueGroup(this, "Game Mode Colors", "The colors to display the game modes with.").visibleCondition(this.showGameMode::getValue);

    private final HashMap<Integer, String> gameModeColorValues;
    public boolean toggleState = false;

    public BetterTabListModule() {
        super("Better Tab List", "Improves the player tab list of the game.", Category.RENDER);
        this.gameModeColorValues = new HashMap<>();
        for (final GameMode value : GameMode.values()) {
            final int id = value.getId();
            final Color defaultColor = switch (id) {
                case 0 -> Color.YELLOW.brighter();
                case 1 -> new Color(255, 120, 0, 255);
                case 2 -> Color.GREEN.darker();
                case 3 -> Color.RED.brighter();
                default -> Color.WHITE;
            };
            final String gameMode = StringUtils.normalizeEnumName(value.name());
            final ColorValue gameModeColor = new ColorValue(this.gameModeColorGroup, "Game Mode " + gameMode + " Color", "The color to display the game mode " + gameMode + " with.", defaultColor).visibleCondition(this.showGameMode::getValue);
            this.gameModeColorValues.put(id, gameModeColor.getName());
        }
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(KeyboardInputEvent.ID, this);
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (this.toggleable.getValue()
                && mc.currentScreen == null
                && action == GLFW.GLFW_PRESS
                && key == mc.options.playerListKey.boundKey.getCode())
            this.toggleState = !this.toggleState;
    }

    public int getColorFromGameMode(final int id) {
        final Value<?> value = this.gameModeColorGroup.byName(this.gameModeColorValues.get(id));
        if (value instanceof final ColorValue colorValue) {
            return colorValue.getValue().getColor().getRGB();
        }
        return Color.WHITE.getRGB();
    }

    public int getColorFromPing(final long ping) {
        final Color a = this.lowPingColor.getValue().getColor();
        final Color b = this.averagePingColor.getValue().getColor();
        final Color c = this.highPingColor.getValue().getColor();
        return RenderUtil.interpolateColor(a, b, c, Math.min((float) ping / this.highPing.getValue(), 1f)).getRGB();
    }

}
