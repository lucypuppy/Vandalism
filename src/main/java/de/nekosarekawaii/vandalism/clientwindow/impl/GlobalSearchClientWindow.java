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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.DataListWidget;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.impl.ListDataEntry;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.feature.module.Module;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Pair;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GlobalSearchClientWindow extends ClientWindow implements DataListWidget {

    private final ImString searchQuery = new ImString(200);
    private final CopyOnWriteArrayList<ListDataEntry> searchEntries = new CopyOnWriteArrayList<>();

    public GlobalSearchClientWindow() {
        super("Global Search", null, 600f, 500f);
        for (final Value<?> value : Vandalism.getInstance().getClientSettings().getValues()) {
            if (value instanceof final ValueGroup settingsGroup) {
                for (final Value<?> setting : settingsGroup.getValues()) {
                    final CopyOnWriteArrayList<Pair<String, String>> settingsList = new CopyOnWriteArrayList<>();
                    settingsList.add(new Pair<>("Type", "Client Setting"));
                    settingsList.add(new Pair<>("Category", settingsGroup.getName()));
                    settingsList.add(new Pair<>("Name", setting.getName()));
                    if (setting.getDescription() != null) {
                        settingsList.add(new Pair<>("Description", setting.getDescription()));
                    }
                    this.searchEntries.add(new ListDataEntry(settingsList));
                }
            }
        }
        for (final Module module : Vandalism.getInstance().getModuleManager().getList()) {
            final CopyOnWriteArrayList<Pair<String, String>> modulesList = new CopyOnWriteArrayList<>();
            modulesList.add(new Pair<>("Type", "Module"));
            modulesList.add(new Pair<>("Category", module.getCategory().getName()));
            modulesList.add(new Pair<>("Name", module.getName()));
            if (module.getDescription() != null) {
                modulesList.add(new Pair<>("Description", module.getDescription()));
            }
            this.searchEntries.add(new ListDataEntry(modulesList));
            for (int i = 1; i < module.getValues().size(); i++) {
                final Value<?> value = module.getValues().get(i);
                final CopyOnWriteArrayList<Pair<String, String>> valuesList = new CopyOnWriteArrayList<>();
                valuesList.add(new Pair<>("Type", "Module Value"));
                valuesList.add(new Pair<>("Module", value.getParent().getName()));
                valuesList.add(new Pair<>("Name", value.getName()));
                if (value.getDescription() != null) {
                    valuesList.add(new Pair<>("Description", value.getDescription()));
                }
                this.searchEntries.add(new ListDataEntry(valuesList));
            }
        }
        for (final Command command : Vandalism.getInstance().getCommandManager().getList()) {
            final CopyOnWriteArrayList<Pair<String, String>> commandsList = new CopyOnWriteArrayList<>();
            commandsList.add(new Pair<>("Type", "Command"));
            commandsList.add(new Pair<>("Category", command.getCategory().getName()));
            commandsList.add(new Pair<>("Name", command.getName()));
            String description = command.getDescription();
            if (description != null) {
                if (description.contains("\n")) {
                    description = description.split("\n")[0];
                }
                commandsList.add(new Pair<>("Description", description));
            }
            this.searchEntries.add(new ListDataEntry(commandsList));
        }
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(id + "searchQuery", this.searchQuery);
        ImGui.separator();
        this.renderDataList(id, -1, 70, this.searchEntries);
    }

    @Override
    public boolean filterDataEntry(final DataEntry dataEntry) {
        return !dataEntry.getData().toLowerCase().contains(this.searchQuery.get().toLowerCase());
    }

    @Override
    public boolean shouldHighlightDataEntry(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (listDataEntry.getFirst().getRight().equals("Module")) {
                final Module module = Vandalism.getInstance().getModuleManager().getByName(listDataEntry.getThird().getRight());
                if (module != null) {
                    return module.isActive();
                }
            }
        }
        return false;
    }

    @Override
    public float[] getDataEntryHighlightColor(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (listDataEntry.getFirst().getRight().equals("Module")) {
                final Module module = Vandalism.getInstance().getModuleManager().getByName(listDataEntry.getThird().getRight());
                if (module != null && module.isActive()) {
                    final Color color = Vandalism.getInstance().getClientSettings().getMenuSettings().activatedModuleColor.getColor();
                    return new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f};
                }
            }
        }
        return new float[0];
    }

    @Override
    public void onDataEntryClick(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            if (listDataEntry.getFirst().getRight().equals("Module")) {
                final Module module = Vandalism.getInstance().getModuleManager().getByName(listDataEntry.getThird().getRight());
                if (module != null) {
                    module.toggle();
                }
            }
        }
    }

    @Override
    public void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String type = listDataEntry.getFirst().getRight();
            ImGui.text(type + " / " + listDataEntry.getSecond().getRight() + " / " + listDataEntry.getThird().getRight());
            switch (type) {
                case "Module" -> {
                    ImGui.separator();
                    final Module module = Vandalism.getInstance().getModuleManager().getByName(listDataEntry.getThird().getRight());
                    if (module != null) {
                        module.renderValues();
                    }
                }
                case "Module Value" -> {
                    final String parentName = listDataEntry.getSecond().getRight();
                    final Module module = Vandalism.getInstance().getModuleManager().getByName(parentName);
                    if (module != null) {
                        module.renderValues(listDataEntry.getThird().getRight());
                    }
                }
                case "Client Setting" -> {
                    ImGui.separator();
                    for (final Value<?> value : Vandalism.getInstance().getClientSettings().getValues()) {
                        if (value instanceof final ValueGroup settingsGroup) {
                            for (final Value<?> setting : settingsGroup.getValues()) {
                                if (listDataEntry.getSecond().getRight().equals(settingsGroup.getName())) {
                                    if (listDataEntry.getThird().getRight().equals(setting.getName())) {
                                        setting.render();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public boolean hasContextMenu(final DataEntry dataEntry) {
        if (dataEntry instanceof final ListDataEntry listDataEntry) {
            final String type = listDataEntry.getFirst().getRight();
            return type.equals("Module") || type.equals("Module Value") || type.equals("Client Setting");
        }
        return true;
    }

    @Override
    public float getDefaultContextMenuWidth() {
        return 300f;
    }

}
