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

package de.nekosarekawaii.vandalism.injection.mixins.fix.imnbt;

import imgui.ImGui;
import net.lenni0451.imnbt.ui.windows.MainWindow;
import net.lenni0451.mcstructs.nbt.INbtTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(value = MainWindow.class, remap = false)
public abstract class MixinMainWindow {

    @Shadow @Nullable public abstract INbtTag getOpenTab();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Limgui/ImGui;menuItem(Ljava/lang/String;)Z"))
    private boolean changeRenderMenuItems(String name) {
        switch (name) {
            case "About" -> {
                return false;
            }
            case "Close" -> {
                name = "Close Tab";
                if (this.getOpenTab() == null) {
                    return ImGui.menuItem(name, null, false, false);
                }
            }
            case "Exit" -> {
                name = "Close";
            }
        }
        return ImGui.menuItem(name);
    }

    @Unique
    private static final ExecutorService vandalism$EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/lenni0451/imnbt/ui/windows/MainWindow;chooseFile()V"))
    private void chooseFileInANewThread(final MainWindow instance) {
        vandalism$EXECUTOR_SERVICE.submit(instance::chooseFile);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/lenni0451/imnbt/ui/windows/MainWindow;saveFile()V"))
    private void saveFileInANewThread(final MainWindow instance) {
        vandalism$EXECUTOR_SERVICE.submit(instance::saveFile);
    }

}
