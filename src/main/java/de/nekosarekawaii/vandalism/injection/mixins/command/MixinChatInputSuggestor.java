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

package de.nekosarekawaii.vandalism.injection.mixins.command;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.feature.command.CommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinChatInputSuggestor {

    @Shadow
    private ParseResults<CommandSource> parse;

    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    boolean completingSuggestions;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    private ChatInputSuggestor.SuggestionWindow window;

    @Shadow
    protected abstract void showCommandSuggestions();

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void suggestClientCommands(final CallbackInfo ci, final String string, final StringReader reader) {
        final CommandManager commandManager = Vandalism.getInstance().getCommandManager();
        final String prefix = Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue();
        final int length = prefix.length();
        if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor()) && MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
            reader.setCursor(reader.getCursor() + length);
            if (this.parse == null)
                this.parse = commandManager.getCommandDispatcher().parse(reader, Command.COMMAND_SOURCE);
            final int cursor = this.textField.getCursor();
            if (cursor >= length && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandManager.getCommandDispatcher().getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) this.showCommandSuggestions();
                });
            }
            ci.cancel();
        }
    }

}
