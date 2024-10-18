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

package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraft;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AddServerScreen.class)
public abstract class MixinAddServerScreen extends Screen {

    @Shadow
    protected abstract void addAndClose();

    @Shadow
    private TextFieldWidget addressField;

    protected MixinAddServerScreen(final Text ignored) {
        super(ignored);
    }

    @Redirect(method = "updateAddButton", at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z"))
    private boolean allowEmptyServerNames(final String instance) {
        return false;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (!this.addressField.getText().isEmpty() && keyCode == GLFW.GLFW_KEY_ENTER) {
            this.addAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
