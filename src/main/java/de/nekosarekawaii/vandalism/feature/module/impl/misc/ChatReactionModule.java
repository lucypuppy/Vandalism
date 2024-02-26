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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.ButtonValue;
import de.nekosarekawaii.vandalism.event.normal.player.ChatReceiveListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.script.parse.ScriptVariable;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Util;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class ChatReactionModule extends AbstractModule implements ChatReceiveListener {

    private final Map<String[], String[]> contentMap = new HashMap<>();

    private final File contentFile = new File(Vandalism.getInstance().getRunDirectory(), "chat_reaction.txt");

    private final ButtonValue openFileButton = new ButtonValue(this, "Open File", "Opens the chat reaction file.", buttonValue -> {
        try {
            Util.getOperatingSystem().open(this.contentFile);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to open chat reaction file!", e);
        }
    });

    public ChatReactionModule() {
        super(
                "Chat Reaction",
                "If activated the client will react to certain words in the chat and will answer with a certain message.",
                Category.MISC
        );
        this.setup();
    }

    @Override
    public void onActivate() {
        this.setup();
        Vandalism.getInstance().getEventSystem().subscribe(ChatReceiveEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(ChatReceiveEvent.ID, this);
        this.contentMap.clear();
    }

    private void setup() {
        if (!this.contentFile.exists()) {
            try {
                final PrintWriter printWriter = new PrintWriter(this.contentFile);
                printWriter.println("'%mod_name%', 'best client' > '%mod_name% is the best client!', 'I love %mod_name%!'");
                printWriter.println("'cool', 'nice', 'awesome' > '%target% nice', '%target% cool', '%target% awesome'");
                printWriter.close();
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to create chat reaction file!", e);
            }
        }
        if (this.contentMap.isEmpty()) {
            try {
                final Scanner scanner = new Scanner(this.contentFile);
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    if (line.contains(">")) {
                        final String[] parts = line.split(">");
                        String triggersString = parts[0].trim();
                        String responsesString = parts[1].trim();
                        triggersString = triggersString.substring(1, triggersString.length() - 1);
                        responsesString = responsesString.substring(1, responsesString.length() - 1);
                        final String[] triggers = triggersString.split("', '");
                        final String[] responses = responsesString.split("', '");
                        for (int i = 0; i < triggers.length; i++) {
                            triggers[i] = triggers[i].trim();
                        }
                        for (int i = 0; i < responses.length; i++) {
                            responses[i] = responses[i].trim();
                        }
                        this.contentMap.put(triggers, responses);
                    }
                }
                scanner.close();
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to load chat reaction file!", e);
            }
        }
    }

    @Override
    public void onChatReceive(final ChatReceiveEvent event) {
        final String message = event.text.getString();
        if (
                StringUtils.contains(message, "<" + this.mc.session.getUsername() + ">") ||
                        StringUtils.contains(message, this.mc.session.getUsername() + ":") ||
                        StringUtils.contains(message, this.mc.session.getUsername() + "]") ||
                        StringUtils.contains(message, "] " + this.mc.session.getUsername())
        ) {
            return;
        }
        final AtomicReference<String> targetName = new AtomicReference<>("%target%");
        for (final PlayerListEntry playerListEntry : this.mc.getNetworkHandler().getPlayerList()) {
            final String playerName = playerListEntry.getProfile().getName();
            if (!this.mc.session.getUsername().equalsIgnoreCase(playerName) && StringUtils.contains(message, playerName)) {
                targetName.set(playerName);
                break;
            }
        }
        final String target = targetName.get();
        if (target.equals("%target%")) {
            return;
        }
        this.setup();
        this.contentMap.forEach((words, answers) -> {
            for (final String word : words) {
                if (StringUtils.contains(ScriptVariable.applyReplacements(message), word)) {
                    String answer = answers.length == 1 ? answers[0] : answers[RandomUtils.randomInt(0, answers.length)];
                    answer = ScriptVariable.applyReplacements(answer);
                    if (answer.contains("%target%")) {
                        answer = answer.replace("%target%", target);
                    }
                    this.mc.getNetworkHandler().sendChatMessage(answer);
                    break;
                }
            }
        });
    }

}
