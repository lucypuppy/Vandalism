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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.game.ServerUtil;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

    @Shadow
    @Final
    private static Text SEND_FEEDBACK_TEXT;

    @Shadow
    @Final
    private static Text REPORT_BUGS_TEXT;

    protected MixinGameMenuScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "createUrlButton", at = @At(value = "HEAD"), cancellable = true)
    private void addMoreButtons(final Text text, final String url, final CallbackInfoReturnable<ButtonWidget> cir) {
        if (!Vandalism.getInstance().getClientSettings().getMenuSettings().replaceGameMenuScreenButtons.getValue()) {
            return;
        }
        if (text == SEND_FEEDBACK_TEXT) {
            cir.setReturnValue(ButtonWidget.builder(Text.translatable("menu.multiplayer"), b -> this.client.setScreen(new MultiplayerScreen(this))).width(98).build());
        } else if (text == REPORT_BUGS_TEXT && !this.client.isInSingleplayer()) {
            final ButtonWidget button = ButtonWidget.builder(Text.literal("Reconnect"), b -> ServerUtil.connectToLastServer()).width(98).build();
            cir.setReturnValue(button);
        }
    }

}
