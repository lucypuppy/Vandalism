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

package de.nekosarekawaii.vandalism.clientwindow.template.widgets.field;

import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.util.Pair;

public interface IPFieldWidget extends MinecraftWrapper {

    ImGuiInputTextCallback IP_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData callbackData) {
            final int eventCharInt = callbackData.getEventChar();
            if (eventCharInt == 0) return;
            final char eventChar = (char) eventCharInt;
            if (!Character.isLetterOrDigit(eventChar) && eventChar != '.' && eventChar != ':' && eventChar != '-') {
                callbackData.setEventChar((char) 0);
            }
        }

    };

    default ImString createImIP() {
        return new ImString(253);
    }

    ImString getImIP();

    default boolean isValidIP() {
        final String ip = this.getImIP().get();
        return !ip.isBlank() &&
                ip.length() >= 4 &&
                ip.contains(".") &&
                ip.indexOf(".") < ip.length() - 2;
    }

    default void onDataSplit(final String[] data, final boolean resolved) {
        if (data.length > 0) {
            this.getImIP().set(data[0]);
        }
    }

    default boolean shouldResolve() {
        return true;
    }

    default void renderField(final String id) {
        ImGui.text("IP");
        ImGui.setNextItemWidth(-1);
        ImGui.inputText(
                id + "IP", this.getImIP(),
                ImGuiInputTextFlags.CallbackCharFilter,
                IP_FILTER
        );
        if (this.getImIP().isEmpty() && ServerUtil.lastServerExists()) {
            if (ImUtils.subButton("Use " + (mc.player != null ? "Current" : "Last") + " Server" + id + "useLastOrCurrentServer")) {
                this.getImIP().set(ServerUtil.getLastServerInfo().address);
            }
        }
        if (this.isValidIP()) {
            String ip = this.getImIP().get();
            boolean resolved = false;
            if (this.shouldResolve() && StringUtils.containsLetter(ip)) {
                if (ImUtils.subButton("Resolve IP" + id + "resolve")) {
                    final Pair<String, Integer> serverAddress = ServerUtil.resolveServerAddress(ip);
                    ip = serverAddress.getLeft() + ":" + serverAddress.getRight();
                    resolved = true;
                }
            }
            if (ip.contains(":")) {
                this.onDataSplit(ip.split(":"), resolved);
            }
        }
    }

}
