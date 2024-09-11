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

package de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist;

import de.nekosarekawaii.vandalism.clientwindow.template.widgets.datalist.dataentry.DataEntry;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;

import java.util.concurrent.CopyOnWriteArrayList;

@Deprecated
public interface DataListWidget {

    boolean filterDataEntry(final DataEntry dataEntry);

    boolean shouldHighlightDataEntry(final DataEntry dataEntry);

    float[] getDataEntryHighlightColor(final DataEntry dataEntry);

    void onDataEntryClick(final DataEntry dataEntry);

    void renderDataEntryContextMenu(final String id, final int index, final DataEntry dataEntry);

    default boolean hasContextMenu(final DataEntry dataEntry) {
        return true;
    }

    default float getDefaultContextMenuWidth() {
        return 200f;
    }

    default void renderDataList(final String id, final float height, final CopyOnWriteArrayList<? extends DataEntry> dataList) {
        this.renderDataList(id, height, 100f, dataList);
    }

    default void renderDataList(final String id, final float height, final float entryHeight, final CopyOnWriteArrayList<? extends DataEntry> dataList) {
        this.renderDataList(id, height, ImGui.getColumnWidth(), entryHeight, dataList);
    }

    default void renderDataList(final String id, final float height, final float entryWidth, final float entryHeight, final CopyOnWriteArrayList<? extends DataEntry> dataList) {
        ImGui.beginChild(id, ImGui.getColumnWidth(), height, true, ImGuiWindowFlags.HorizontalScrollbar);
        for (int i = 0; i < dataList.size(); i++) {
            final DataEntry dataEntry = dataList.get(i);
            if (this.filterDataEntry(dataEntry)) {
                continue;
            }
            final float[] color = this.getDataEntryHighlightColor(dataEntry);
            final boolean shouldHighlight = this.shouldHighlightDataEntry(dataEntry) && color != null && color.length == 4;
            if (shouldHighlight) {
                ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
            }
            if (ImGui.button(id + "dataEntry" + i, entryWidth, ImUtils.modulateDimension(entryHeight))) {
                this.onDataEntryClick(dataEntry);
            }
            if (shouldHighlight) {
                ImGui.popStyleColor(3);
            }
            if (this.hasContextMenu(dataEntry)) {
                ImGui.setNextWindowSizeConstraints(this.getDefaultContextMenuWidth(), 50f, 1000000f, 1000000f);
                if (ImGui.beginPopupContextItem(id + "contextMenu" + i, ImGuiPopupFlags.MouseButtonRight)) {
                    this.renderDataEntryContextMenu(id, i, dataEntry);
                    ImGui.endPopup();
                }
            }
            ImGui.sameLine(10);
            ImGui.text(dataEntry.getData());
        }
        ImGui.endChild();
    }

}
