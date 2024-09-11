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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerList;
import de.nekosarekawaii.vandalism.integration.serverlist.gui.ConfigScreen;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen {

    @Shadow
    protected abstract void refresh();

    @Shadow
    private net.minecraft.client.option.ServerList serverList;

    @Shadow
    protected MultiplayerServerListWidget serverListWidget;

    @Shadow
    public abstract void select(final MultiplayerServerListWidget.Entry entry);

    @Unique
    private static int vandalism$SELECTED_ENTRY_INDEX = -1;

    @Unique
    private static net.minecraft.client.option.ServerList vandalism$SERVER_LIST;

    @Unique
    private static double vandalism$SCROLL_AMOUNT;

    @Unique
    private static int vandalism$LAST_SERVER_LIST_SIZE;

    protected MixinMultiplayerScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "refresh", at = @At("HEAD"))
    private void resetServerListAndSelectedEntry(final CallbackInfo ci) {
        vandalism$SERVER_LIST = null;
        vandalism$SELECTED_ENTRY_INDEX = -1;
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;setServers(Lnet/minecraft/client/option/ServerList;)V"))
    private void cacheServerList(final MultiplayerServerListWidget instance, net.minecraft.client.option.ServerList serverList) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue() && enhancedServerListSettings.cacheServerList.getValue()) {
            if (vandalism$SERVER_LIST != null) {
                serverList = vandalism$SERVER_LIST;
            } else {
                vandalism$SERVER_LIST = serverList;
            }
        } else {
            vandalism$SERVER_LIST = null;
        }

        this.serverList = serverList;
        instance.setServers(serverList);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addServerListsButtonAndSetScrollingSetScreenInit(final CallbackInfo ci) {
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Server Lists"), button -> {
                if (this.client != null) {
                    this.client.setScreen(new ConfigScreen((MultiplayerScreen) (Object) this));
                }
            }).dimensions(4, 4, 100, 20).build());
            if (enhancedServerListSettings.saveScrollingAmount.getValue()) {
                if (vandalism$SCROLL_AMOUNT > 0 && vandalism$SCROLL_AMOUNT != this.serverListWidget.getScrollAmount()) {
                    this.serverListWidget.setScrollAmount(vandalism$SCROLL_AMOUNT);
                }
            }
            if (enhancedServerListSettings.saveSelectedEntry.getValue()) {
                final List<MultiplayerServerListWidget.Entry> children = this.serverListWidget.children();
                if (children != null && vandalism$SELECTED_ENTRY_INDEX >= 0 && vandalism$SELECTED_ENTRY_INDEX < children.size()) {
                    this.select(children.get(vandalism$SELECTED_ENTRY_INDEX));
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void enhancedServerListSyncServerListSaveScrollingAndSetSelectedEntry(final CallbackInfo ci) {
        if (Vandalism.getInstance().getServerListManager().hasBeenChanged()) {
            this.refresh();
        }
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue()) {
            if (enhancedServerListSettings.saveScrollingAmount.getValue()) {
                if (this.serverList.size() < vandalism$LAST_SERVER_LIST_SIZE) {
                    vandalism$LAST_SERVER_LIST_SIZE = this.serverList.size();
                    this.serverListWidget.setScrollAmount(Math.max(0, this.serverListWidget.getScrollAmount() - this.serverListWidget.itemHeight));
                } else if (this.serverList.size() > vandalism$LAST_SERVER_LIST_SIZE) {
                    final double currentScrollAmount = this.serverListWidget.getScrollAmount();
                    vandalism$LAST_SERVER_LIST_SIZE = this.serverList.size();
                    if (currentScrollAmount != 0) {
                        this.serverListWidget.setScrollAmount(currentScrollAmount + this.serverListWidget.itemHeight);
                    }
                }
                vandalism$SCROLL_AMOUNT = this.serverListWidget.getScrollAmount();
            }
            if (enhancedServerListSettings.saveSelectedEntry.getValue()) {
                boolean found = false;
                MultiplayerServerListWidget.Entry serverEntry = this.serverListWidget.getSelectedOrNull();
                if (serverEntry != null) {
                    for (int i = 0; i < this.serverListWidget.children().size(); i++) {
                        final MultiplayerServerListWidget.Entry entry = this.serverListWidget.children().get(i);
                        if (serverEntry == entry) {
                            vandalism$SELECTED_ENTRY_INDEX = i;
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    vandalism$SELECTED_ENTRY_INDEX = -1;
                }
            }
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"))
    private void enhancedServerListModifyTitle(final DrawContext instance, final TextRenderer textRenderer, final Text text, final int centerX, final int y, final int color) {
        if (Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings().enhancedServerList.getValue()) {
            final ServerList selectedServerList = Vandalism.getInstance().getServerListManager().getSelectedServerList();
            final MutableText title = Text.literal(selectedServerList.isDefault() ? ServerList.DEFAULT_SERVER_LIST_NAME : selectedServerList.getName());
            title.append(" (" + selectedServerList.getSize() + ") | ");
            title.append(text);
            instance.drawCenteredTextWithShadow(textRenderer, title, centerX, y, color);
        } else {
            instance.drawCenteredTextWithShadow(textRenderer, text, centerX, y, color);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void addMoreKeys(final int keyCode, final int scanCode, final int modifiers, final CallbackInfoReturnable<Boolean> cir) {
        final EnhancedServerListSettings settings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (!settings.enhancedServerList.getValue()) return;
        if (keyCode == settings.pasteServerKey.getValue()) {
            String clipboard = this.client.keyboard.getClipboard();
            if (clipboard == null || clipboard.isBlank()) return;
            final ServerInfo serverInfo = new ServerInfo(
                    "Copied from Clipboard",
                    ServerUtil.fixAddress(clipboard),
                    ServerInfo.ServerType.OTHER
            );
            this.serverList.add(serverInfo, false);
            this.serverList.saveFile();

            vandalism$SERVER_LIST = this.serverList;
            this.serverListWidget.setServers(this.serverList);
        } else if (keyCode == settings.copyServerKey.getValue()) {
            if (this.serverListWidget.getSelectedOrNull() instanceof final MultiplayerServerListWidget.ServerEntry selectedServerEntry) {
                this.client.keyboard.setClipboard(selectedServerEntry.getServer().address);
            }
        } else if (keyCode == settings.deleteServerKey.getValue()) {
            if (this.serverListWidget.getSelectedOrNull() instanceof final MultiplayerServerListWidget.ServerEntry selectedServerEntry) {
                final int index = this.serverListWidget.children().indexOf(selectedServerEntry);

                this.serverList.remove(selectedServerEntry.getServer());
                this.serverList.saveFile();

                vandalism$SERVER_LIST = this.serverList;
                this.serverListWidget.setServers(this.serverList);

                // checking if the size is greater than 1 because of the scanning entry
                if (this.serverListWidget.children().size() > 1) {
                    // size() - 2 because of scanning entry
                    final MultiplayerServerListWidget.Entry entry = this.serverListWidget.children().get(Math.min(index, this.serverListWidget.children().size() - 2));
                    if (entry instanceof MultiplayerServerListWidget.ServerEntry serverEntry) {
                        this.select(serverEntry);
                    }
                }
            }
        }
    }

}
