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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.util.render.ServerPingerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectConnectScreen.class)
public abstract class MixinDirectConnectScreen extends Screen {

    @Shadow private TextFieldWidget addressField;

    protected MixinDirectConnectScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void initServerPingerWidget(final CallbackInfo ci) {
        final String address = this.addressField.getText();
        if (address.isEmpty()) return;
        ServerPingerWidget.ping(new ServerInfo(address, address, ServerInfo.ServerType.OTHER));
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void drawServerPingerWidget(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        final String address = this.addressField.getText();
        if (address.isEmpty()) return;
        ServerPingerWidget.draw(new ServerInfo(address, address, ServerInfo.ServerType.OTHER), context, mouseX, mouseY, delta, 10);
    }

}
