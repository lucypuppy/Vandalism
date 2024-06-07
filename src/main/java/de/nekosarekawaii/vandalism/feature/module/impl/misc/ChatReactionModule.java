/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

import com.google.gson.*;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.ButtonValue;
import de.nekosarekawaii.vandalism.event.player.ChatReceiveListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.Placeholders;
import de.nekosarekawaii.vandalism.util.common.RandomUtils;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ChatReactionModule extends AbstractModule implements ChatReceiveListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String[], String[]> contentMap = new HashMap<>();

    private final File contentFile = new File(Vandalism.getInstance().getRunDirectory(), "chat-reaction.json");

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
            try (final FileWriter fw = new FileWriter(this.contentFile)) {
                final JsonObject jsonObject = new JsonObject();
                final JsonArray reactions = new JsonArray();

                final JsonObject reaction1 = new JsonObject();
                final JsonArray reaction1Triggers = new JsonArray();
                reaction1Triggers.add("%mod_name%");
                reaction1Triggers.add("best client");
                final JsonArray reaction1Responses = new JsonArray();
                reaction1Responses.add("%mod_name% is the best client!");
                reaction1Responses.add("I love %mod_name%!");
                reaction1.add("triggers", reaction1Triggers);
                reaction1.add("responses", reaction1Responses);
                reactions.add(reaction1);

                final JsonObject reaction2 = new JsonObject();
                final JsonArray reaction2Triggers = new JsonArray();
                reaction2Triggers.add("cool");
                reaction2Triggers.add("nice");
                reaction2Triggers.add("awesome");
                final JsonArray reaction2Responses = new JsonArray();
                reaction2Responses.add("%target% nice");
                reaction2Responses.add("%target% cool");
                reaction2Responses.add("%target% awesome");
                reaction2.add("triggers", reaction2Triggers);
                reaction2.add("responses", reaction2Responses);
                reactions.add(reaction2);

                jsonObject.add("reactions", reactions);

                fw.write(GSON.toJson(jsonObject));
                fw.flush();
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to create chat reaction file!", e);
            }
        }
        if (this.contentMap.isEmpty()) {
            try (final FileReader fr = new FileReader(this.contentFile)) {
                final JsonObject jsonObject = GSON.fromJson(fr, JsonObject.class);
                if (jsonObject.has("reactions")) {
                    final JsonArray reactions = jsonObject.getAsJsonArray("reactions");
                    for (final JsonElement reactionElement : reactions) {
                        final JsonObject reaction = reactionElement.getAsJsonObject();
                        if (!reaction.has("triggers") || !reaction.has("responses")) {
                            continue;
                        }
                        final JsonArray triggers = reaction.getAsJsonArray("triggers");
                        final JsonArray responses = reaction.getAsJsonArray("responses");
                        final String[] triggersArray = new String[triggers.size()];
                        final String[] responsesArray = new String[responses.size()];
                        for (int i = 0; i < triggers.size(); i++) {
                            triggersArray[i] = triggers.get(i).getAsString();
                        }
                        for (int i = 0; i < responses.size(); i++) {
                            responsesArray[i] = responses.get(i).getAsString();
                        }
                        this.contentMap.put(triggersArray, responsesArray);
                    }
                }
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
        this.setup();
        this.contentMap.forEach((triggers, responses) -> {
            for (final String trigger : triggers) {
                if (StringUtils.contains(Placeholders.applyReplacements(message), trigger)) {
                    String answer = responses.length == 1 ? responses[0] : responses[RandomUtils.randomInt(0, responses.length)];
                    answer = Placeholders.applyReplacements(answer);
                    final String target = targetName.get();
                    if (answer.contains("%target%") && !target.equals("%target%")) {
                        answer = answer.replace("%target%", target);
                    }
                    if (answer.startsWith("/")) {
                        this.mc.getNetworkHandler().sendChatCommand(answer.substring(1));
                    }
                    else {
                        this.mc.getNetworkHandler().sendChatMessage(answer);
                    }
                    break;
                }
            }
        });
    }

}
