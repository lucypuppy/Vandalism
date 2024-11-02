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

package de.nekosarekawaii.vandalism.integration.serverlist.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerList;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerListManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private final Screen parent;

    private final ServerList initialServerList;

    private ButtonWidget cleanBtn;
    private ButtonWidget clearBtn;
    private ButtonWidget deleteBtn;

    public ConfigScreen(final Screen parent) {
        super(Text.literal("Config"));
        this.parent = parent;
        this.initialServerList = Vandalism.getInstance().getServerListManager().getSelectedServerList();
    }

    @Override
    protected void init() {
        this.addDrawableChild(new SlotList(this.client, this.width, this.height, 32, 64, this.textRenderer.fontHeight + 4));
        final int buttonWidth = 60;
        this.deleteBtn = this.addDrawableChild(ButtonWidget.builder(Text.literal("Delete"), button -> {
            final ServerListManager serverListManager = Vandalism.getInstance().getServerListManager();
            final ServerList selectedServerList = serverListManager.getSelectedServerList();
            if (this.client != null && !selectedServerList.isDefault()) {
                this.client.setScreen(new ConfirmScreen((confirmed) -> {
                    if (confirmed) {
                        serverListManager.remove(selectedServerList.getName());
                    }
                    this.client.setScreen(new ConfigScreen(this.parent));
                },
                        Text.literal("Are you sure you want to delete this server list?"),
                        Text.literal("'" + selectedServerList.getName() + " (" + selectedServerList.getSize() + ")' " + "will be lost forever! (A long time!)"),
                        ScreenTexts.YES, ScreenTexts.NO
                ));
            }
        }).width(buttonWidth).build());
        final GridWidget gridWidget = new GridWidget();
        final GridWidget.Adder adder = gridWidget.createAdder(1);
        final AxisGridWidget axisGrid = adder.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL));
        axisGrid.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"), button -> MinecraftClient.getInstance().setScreen(new AddServerListScreen(this.parent))).width(buttonWidth).build()));
        this.cleanBtn = axisGrid.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Clean"), button -> {
            if (this.parent instanceof final MultiplayerScreen multiplayerScreen) {
                final net.minecraft.client.option.ServerList serverList = multiplayerScreen.getServerList();
                final List<String> addresses = new ArrayList<>();
                for (final ServerInfo server : serverList.servers) {
                    if (addresses.contains(server.address)) {
                        server.ping = 0L;
                    } else {
                        addresses.add(server.address);
                    }
                }
                serverList.servers.removeIf(server -> server.ping <= 0L);
                serverList.saveFile();
            }
        }).width(buttonWidth).build()));
        this.clearBtn = axisGrid.add(this.addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), button -> {
            final net.minecraft.client.option.ServerList serverList = new net.minecraft.client.option.ServerList(MinecraftClient.getInstance());
            serverList.servers.clear();
            serverList.hiddenServers.clear();
            serverList.saveFile();
        }).width(buttonWidth).build()));
        axisGrid.add(this.deleteBtn);
        axisGrid.add(this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> close()).width(buttonWidth).build()));
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height - 64, this.width, 64);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    public void tick() {
        final ServerList selectedServerList = Vandalism.getInstance().getServerListManager().getSelectedServerList();
        this.cleanBtn.active = selectedServerList.getName().equals(this.initialServerList.getName()) && selectedServerList.getSize() > 0;
        this.clearBtn.active = selectedServerList.getSize() > 0;
        this.deleteBtn.active = !selectedServerList.isDefault();
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    private static class SlotList extends AlwaysSelectedEntryListWidget<ListSlot> {

        public SlotList(final MinecraftClient minecraftClient, final int width, final int height, final int top, final int bottom, final int entryHeight) {
            super(minecraftClient, width, height - top - bottom, top, entryHeight);
            for (final ServerList serverList : Vandalism.getInstance().getServerListManager().getServerLists()) {
                this.addEntry(new ListSlot(serverList));
            }
        }

    }

    private static class ListSlot extends AlwaysSelectedEntryListWidget.Entry<ListSlot> {

        private final ServerList serverList;

        public ListSlot(final ServerList serverList) {
            this.serverList = serverList;
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.serverList.getName());
        }

        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            Vandalism.getInstance().getServerListManager().setSelectedServerList(this.serverList.getName());
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            final String name = this.serverList.getName();
            final boolean isSelected = name.equals(Vandalism.getInstance().getServerListManager().getSelectedServerList().getName());
            final int rectX = x - 1, rectY = y - 1, rectX2 = x + entryWidth - 3, rectY2 = y + entryHeight + 1;
            context.fill(rectX, rectY, rectX2, rectY2, isSelected ? Color.GRAY.getRGB() : Color.DARK_GRAY.getRGB());
            context.drawCenteredTextWithShadow(textRenderer, (this.serverList.isDefault() ? ServerList.DEFAULT_SERVER_LIST_NAME : name) + " (" + this.serverList.getSize() + ")", x + entryWidth / 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
        }

    }

}
