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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import de.nekosarekawaii.vandalism.feature.module.Module;
import imgui.ImGui;

public class AntiCaptureModule extends Module {

    public AntiCaptureModule() {
        super("Anti Capture", "Prevents the game from being captured.", Category.MISC);
    }

    @Override
    protected void onActivate() {
        this.setWindowDisplayAffinity(true);
    }

    @Override
    protected void onDeactivate() {
        this.setWindowDisplayAffinity(false);
    }

    private void setWindowDisplayAffinity(final boolean hide) {
        if (Platform.isWindows()) {
            User32.INSTANCE.SetWindowDisplayAffinity(new WinDef.HWND(new Pointer(ImGui.getMainViewport().getPlatformHandleRaw())), new WinDef.DWORD(hide ? 0x11 : 0));
        }
    }

    public interface User32 extends Library {

        User32 INSTANCE = Native.load("user32", User32.class);

        void SetWindowDisplayAffinity(final WinDef.HWND hWnd, final WinDef.DWORD dwAffinity);

    }

}
