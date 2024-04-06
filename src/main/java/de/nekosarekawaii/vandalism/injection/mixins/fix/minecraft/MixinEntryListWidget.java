/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntryListWidget.class)
public abstract class MixinEntryListWidget<E extends EntryListWidget.Entry<E>> extends ContainerWidget {

    // ignored
    public MixinEntryListWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    @Shadow @Final private List<?> children;

    @Shadow
    private boolean renderBackground;

    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    public abstract int getRowLeft();

    @Shadow
    public abstract int getRowRight();

    @Inject(method = "renderEntry", at = @At("HEAD"), cancellable = true)
    private void fixIOOBE(final DrawContext context, final int mouseX, final int mouseY, final float delta, final int index, final int x, final int y, final int entryWidth, final int entryHeight, final CallbackInfo ci) {
        if (index < 0 || index >= this.children.size()) {
            ci.cancel();
        }
    }

    // remove the previous render background calls lol
    @Redirect(
            method = "renderWidget",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderBackground:Z"))
    public boolean renderWidgetBackground(EntryListWidget<?> instance) {
        return false;
    }

    @Inject(method = "renderWidget",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderBackground:Z",
                    shift = At.Shift.AFTER,
                    ordinal = 0))
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.renderBackground) {
            context.fill(
                    getRowLeft() - 5,
                    this.getY(),
                    getRowRight() + 1,
                    this.getBottom(),
                    0x50000000
            );
        }
    }
}
