/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.hud;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindowScreen;
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.feature.hud.config.HUDConfig;
import de.nekosarekawaii.vandalism.feature.hud.gui.HUDClientWindow;
import de.nekosarekawaii.vandalism.feature.hud.impl.InfoHUDElement;
import de.nekosarekawaii.vandalism.feature.hud.impl.ModuleListHUDElement;
import de.nekosarekawaii.vandalism.feature.hud.impl.WatermarkHUDElement;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.storage.Storage;
import net.minecraft.client.gui.DrawContext;

import java.io.File;

public class HUDManager extends Storage<HUDElement> implements Render2DListener, MinecraftWrapper {

    public WatermarkHUDElement watermarkHUDElement;
    public ModuleListHUDElement moduleListHUDElement;
    public InfoHUDElement infoHUDElement;

    private final File logoFolder;

    public HUDManager(final ConfigManager configManager, final ClientWindowManager clientWindowManager, final File runDirectory) {
        Vandalism.getInstance().getEventSystem().subscribe(this, Render2DEvent.ID);
        configManager.add(new HUDConfig(this));
        clientWindowManager.add(new HUDClientWindow(this));

        this.logoFolder = new File(runDirectory, "logos");
        this.logoFolder.mkdirs();
    }

    @Override
    public void init() {
        this.add(
                this.watermarkHUDElement = new WatermarkHUDElement(this.logoFolder),
                this.infoHUDElement = new InfoHUDElement(),
                this.moduleListHUDElement = new ModuleListHUDElement()
        );
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        final HUDClientWindow hudImWindow = Vandalism.getInstance().getClientWindowManager().getByClass(HUDClientWindow.class);
        if (mc.currentScreen instanceof ClientWindowScreen && hudImWindow.isActive()) {
            return;
        }
        for (final HUDElement hudElement : this.getList()) {
            if (!hudElement.isActive()) continue;
            hudElement.onRender(context, delta, true);
        }
    }

}