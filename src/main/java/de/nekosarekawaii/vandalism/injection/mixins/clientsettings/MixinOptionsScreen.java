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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen extends Screen {

    @Shadow
    private CyclingButtonWidget<Difficulty> difficultyButton;

    protected MixinOptionsScreen(final Text ignored) {
        super(ignored);
    }

    @Unique
    private static Text vandalism$addEaZy(final Text text) {
        return Text.literal(text.getString().replace("Easy", "EaZy"));
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void changeDifficultyButtonText(final CallbackInfo ci) {
        if (this.difficultyButton != null) {
            this.difficultyButton.setMessage(vandalism$addEaZy(this.difficultyButton.getMessage()));
        }
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void addRunDirButton(final CallbackInfo ci) {
        if (Vandalism.getInstance().getClientSettings().getMenuSettings().runDirectoryButton.getValue()) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("\uD83D\uDCC1"), button -> {
                Util.getOperatingSystem().open(FabricBootstrap.RUN_ARGS.directories.runDir);
            }).position(4, 4).width(20).build());
        }
    }

    @Inject(method = "method_39487", at = @At("HEAD"))
    private static void changeDifficultyButtonText2(final MinecraftClient minecraftClient, final CyclingButtonWidget button, final Difficulty difficulty, final CallbackInfo ci) {
        if (difficulty == Difficulty.EASY) {
            button.setMessage(vandalism$addEaZy(button.getMessage()));
        }
    }

}
