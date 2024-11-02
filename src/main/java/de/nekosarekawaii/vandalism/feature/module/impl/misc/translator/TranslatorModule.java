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

package de.nekosarekawaii.vandalism.feature.module.impl.misc.translator;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.ChatReceiveListener;
import de.nekosarekawaii.vandalism.event.player.ChatSendListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.injection.access.IClientPlayNetworkHandler;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslatorModule extends Module implements ChatSendListener, ChatReceiveListener {

    private final ValueGroup incomingGroup = new ValueGroup(
            this,
            "Incoming",
            "Incoming chat message settings."
    );

    private final BooleanValue translateIncoming = new BooleanValue(
            this.incomingGroup,
            "Translate Incoming",
            "Translates incoming chat messages.",
            true
    );

    private final EnumModeValue<FromLanguage> incomingFromLanguage = new EnumModeValue<>(
            this.incomingGroup,
            "From Language",
            "The language to translate from.",
            FromLanguage.AUTO_DETECT,
            FromLanguage.values()
    ).visibleCondition(this.translateIncoming::getValue);

    private final EnumModeValue<ToLanguage> incomingToLanguage = new EnumModeValue<>(
            this.incomingGroup,
            "To Language",
            "The language to translate to.",
            ToLanguage.ENGLISH,
            ToLanguage.values()
    ).visibleCondition(this.translateIncoming::getValue);

    private final ValueGroup outgoingGroup = new ValueGroup(
            this,
            "Outgoing",
            "Outgoing chat message settings."
    );

    private final BooleanValue translateOutgoing = new BooleanValue(
            this.outgoingGroup,
            "Translate Outgoing",
            "Translates outgoing chat messages.",
            false
    );

    private final EnumModeValue<FromLanguage> outgoingFromLanguage = new EnumModeValue<>(
            this.outgoingGroup,
            "From Language",
            "The language to translate from.",
            FromLanguage.AUTO_DETECT,
            FromLanguage.values()
    ).visibleCondition(this.translateOutgoing::getValue);

    private final EnumModeValue<ToLanguage> outgoingToLanguage = new EnumModeValue<>(
            this.outgoingGroup,
            "To Language",
            "The language to translate to.",
            ToLanguage.ENGLISH,
            ToLanguage.values()
    ).visibleCondition(this.translateOutgoing::getValue);

    private static final MutableText TRANSLATION_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("T").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(new Color(44, 217, 131).getRGB()))))
            .append("]");

    private ExecutorService executorService;

    public TranslatorModule() {
        super(
                "Translator",
                "Translates incoming and outgoing chat messages.",
                Category.MISC
        );
    }

    private void reset() {
        try {
            this.executorService.shutdownNow();
        } catch (final Exception ignored) {
        }
        this.executorService = null;
    }

    @Override
    protected void onActivate() {
        this.reset();
        this.executorService = Executors.newFixedThreadPool(10);
        Vandalism.getInstance().getEventSystem().subscribe(this, ChatSendEvent.ID, ChatReceiveEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, ChatSendEvent.ID, ChatReceiveEvent.ID);
        this.reset();
    }

    @Override
    public void onChatReceive(final ChatReceiveEvent event) {
        if (!this.translateIncoming.getValue()) {
            return;
        }
        final Text text = event.text;
        if (text.getString().startsWith(ChatUtil.getChatPrefix().getString())) {
            return;
        }
        if (text.getString().endsWith(TRANSLATION_PREFIX.getString())) {
            return;
        }
        event.cancel();
        this.translateIncoming(text);
    }

    private void translateIncoming(final Text text) {
        this.executorService.execute(() -> {
            final String message = Formatting.strip(text.getString());
            final String translated = GoogleTranslate.translate(message, this.incomingFromLanguage.getValue(), this.incomingToLanguage.getValue()).orElse(message);
            final MutableText mutableText = text.copy();
            mutableText.append(TRANSLATION_PREFIX.copy().setStyle(
                    TRANSLATION_PREFIX.getStyle().withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.literal(translated)
                    )))
            );
            mc.inGameHud.getChatHud().addMessage(mutableText);
        });
    }

    @Override
    public void onChatSend(final ChatSendEvent event) {
        if (!this.translateOutgoing.getValue()) {
            return;
        }
        final String message = event.message;
        if (message.startsWith("/") || message.startsWith(Vandalism.getInstance().getClientSettings().getChatSettings().commandPrefix.getValue())) {
            return;
        }
        event.cancel();
        this.translateOutgoing(message);
    }

    private void translateOutgoing(final String message) {
        this.executorService.execute(() -> {
            final String translated = GoogleTranslate.translate(message, this.outgoingFromLanguage.getValue(), this.outgoingToLanguage.getValue()).orElse(message);
            ((IClientPlayNetworkHandler) mc.getNetworkHandler()).vandalism$sendChatMessage(translated);
        });
    }

}
