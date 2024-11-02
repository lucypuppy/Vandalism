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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkinOptionsScreen.class)
public abstract class MixinSkinOptionsScreen extends Screen {

    protected MixinSkinOptionsScreen(final Text ignored) {
        super(ignored);
    }

    @Override
    protected void init() {
        super.init();
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().skinsDirectoryButton.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("\uD83D\uDCC1"), button -> {
                Util.getOperatingSystem().open(FabricBootstrap.RUN_ARGS.directories.assetDir.toPath().resolve("skins"));
            }).position(4, 4).width(20).build());
        }
    }

}
