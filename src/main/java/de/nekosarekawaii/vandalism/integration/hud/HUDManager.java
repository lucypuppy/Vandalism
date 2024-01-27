/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.hud;

import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.cancellable.render.ScreenListener;
import de.nekosarekawaii.vandalism.base.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.event.normal.render.Render2DListener;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.integration.hud.config.HUDConfig;
import de.nekosarekawaii.vandalism.integration.hud.gui.HUDClientMenuWindow;
import de.nekosarekawaii.vandalism.integration.hud.impl.InfoHUDElement;
import de.nekosarekawaii.vandalism.integration.hud.impl.ModuleListHUDElement;
import de.nekosarekawaii.vandalism.integration.hud.impl.WatermarkHUDElement;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

public class HUDManager extends Storage<HUDElement> implements Render2DListener, ScreenListener, KeyboardInputListener, MinecraftWrapper {

    public WatermarkHUDElement watermarkHUDElement;
    public ModuleListHUDElement moduleListHUDElement;
    public InfoHUDElement infoHUDElement;

    public HUDManager(final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        Vandalism.getInstance().getEventSystem().subscribe(Render2DEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(ScreenEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
        configManager.add(new HUDConfig(this));
        clientMenuManager.add(new HUDClientMenuWindow(this));
    }

    @Override
    public void init() {
        this.add(
                this.watermarkHUDElement = new WatermarkHUDElement(),
                this.infoHUDElement = new InfoHUDElement(),
                this.moduleListHUDElement = new ModuleListHUDElement()
        );
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        for (final HUDElement hudElement : this.getList()) {
            if (!hudElement.isActive()) {
                continue;
            }
            hudElement.onRender(context, delta);
        }
    }

    @Override
    public void onResizeScreen(final ScreenEvent event) {
        for (final HUDElement hudElement : this.getList()) {
            hudElement.calculateAlignment();
            hudElement.calculatePosition();
        }
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        for (final HUDElement hudElement : this.getList()) {
            hudElement.onKeyInput(window, key, scanCode, action, modifiers);
        }
    }

}