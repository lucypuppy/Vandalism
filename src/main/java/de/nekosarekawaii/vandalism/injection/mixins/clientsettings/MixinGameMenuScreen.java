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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerPingerWidget;
import de.nekosarekawaii.vandalism.util.server.ServerUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

    @Shadow
    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    protected MixinGameMenuScreen(final Text ignored) {
        super(ignored);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private Element removeTitleText(final GameMenuScreen instance, final Element element) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.serverPingerWidget.getValue()) {
            ServerPingerWidget.ping(this.client.getCurrentServerEntry());
            return element;
        }
        return instance.addDrawableChild((TextWidget) element);
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void drawServerPingerWidget(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.serverPingerWidget.getValue()) {
            ServerPingerWidget.draw(this.client.getCurrentServerEntry(), context, mouseX, mouseY, delta, 10);
        }
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addMoreButtons(final CallbackInfo ci) {
        if (!Vandalism.getInstance().getClientSettings().getMenuSettings().addMoreButtonsToGameMenuScreen.getValue()) {
            return;
        }
        final ButtonWidget.Builder multiplayerButton = ButtonWidget.builder(Text.translatable("menu.multiplayer"), b -> this.client.setScreen(new MultiplayerScreen(this))).width(98);
        this.addDrawableChild(multiplayerButton.position(4, this.height - 24).build());
        if (!this.client.isIntegratedServerRunning()) {
            final ButtonWidget.Builder reconnectButton = ButtonWidget.builder(Text.literal("Reconnect"), b -> ServerUtil.connect(this.client.getCurrentServerEntry())).width(98);
            this.addDrawableChild(reconnectButton.position(this.width - 102, this.height - 24).build());
        }
    }

}
