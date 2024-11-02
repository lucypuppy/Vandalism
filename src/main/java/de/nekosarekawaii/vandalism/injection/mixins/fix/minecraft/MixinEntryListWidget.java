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

package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraft;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntryListWidget.class)
public abstract class MixinEntryListWidget<E extends EntryListWidget.Entry<E>> extends ContainerWidget {

    public MixinEntryListWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    @Shadow @Final private List<?> children;

    @Inject(method = "renderEntry", at = @At("HEAD"), cancellable = true)
    private void fixIOOBE(final DrawContext context, final int mouseX, final int mouseY, final float delta, final int index, final int x, final int y, final int entryWidth, final int entryHeight, final CallbackInfo ci) {
        if (index < 0 || index >= this.children.size()) {
            ci.cancel();
        }
    }

}
