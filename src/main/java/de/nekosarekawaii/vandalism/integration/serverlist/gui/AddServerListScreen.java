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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AddServerListScreen extends Screen {

    private static final Predicate<String> PATTERN = Pattern.compile(
            "^[a-zA-Z0-9\\s]*$"
    ).asPredicate();

    private final Screen parent;
    private TextFieldWidget nameField;
    private ButtonWidget addBtn;

    public AddServerListScreen(final Screen parent) {
        super(Text.of("Add Server List"));
        this.parent = parent;
    }

    @Override
    public void tick() {
        super.tick();
        this.addBtn.active =
                this.nameField.getText().length() >= 3 &&
                        Vandalism.getInstance()
                                .getServerListManager()
                                .get(
                                        this.nameField.getText()
                                ) == null;
    }

    @Override
    protected void init() {
        super.init();
        this.nameField = this.addSelectableChild(
                new TextFieldWidget(
                        this.textRenderer,
                        this.width / 2 - 100,
                        116,
                        200,
                        20,
                        Text.of("Name")
                )
        );
        this.nameField.setMaxLength(50);
        this.nameField.setTextPredicate(PATTERN);
        this.addBtn = this.addDrawableChild(ButtonWidget.builder(Text.of("Add Server List"), (button) -> {
            if (Vandalism.getInstance().getServerListManager().add(this.nameField.getText())) {
                if (this.client != null) {
                    this.client.setScreen(new ConfigScreen(this.parent));
                }
            }
        }).dimensions(
                this.width / 2 - 100,
                this.height / 4 + 96 + 12,
                200,
                20
        ).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> close())
                .dimensions(
                        this.width / 2 - 100,
                        this.height / 4 + 120 + 12,
                        200,
                        20
                ).build()
        );

        this.setInitialFocus(this.nameField);
    }

    @Override
    public void close() {
        if (this.client == null) {
            return;
        }
        this.client.setScreen(new ConfigScreen(this.parent));
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                20,
                -1
        );
        context.drawTextWithShadow(this.textRenderer,
                Text.of("Server List Name"),
                this.width / 2 - 100,
                100,
                -1
        );
        this.nameField.render(context, mouseX, mouseY, delta);
    }

}
