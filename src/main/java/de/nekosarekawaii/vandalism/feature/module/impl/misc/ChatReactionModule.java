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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import com.google.gson.*;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.LongValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.ButtonValue;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.Placeholders;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatReactionModule extends Module implements IncomingPacketListener, PlayerUpdateListener, DisconnectListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final LongValue minDelay = new LongValue(
            this,
            "Min Delay",
            "The minimum delay in milliseconds.",
            2000L,
            0L,
            10000L
    );

    private final LongValue maxDelay = new LongValue(
            this,
            "Max Delay",
            "The maximum delay in milliseconds.",
            2500L,
            0L,
            10000L
    );

    private final ButtonValue openFileButton = new ButtonValue(this, "Open File", "Opens the chat reaction file.", buttonValue -> {
        try {
            Util.getOperatingSystem().open(this.contentFile);
        } catch (Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to open chat reaction file!", e);
        }
    });

    private final Map<String[], String[]> contentMap = new HashMap<>();
    private final File contentFile = new File(Vandalism.getInstance().getRunDirectory(), "chat-reaction.json");
    private final Map<String, Long> queuedMessages = new ConcurrentHashMap<>();

    public ChatReactionModule() {
        super(
                "Chat Reaction",
                "If activated the client will react to certain words in the chat and will answer with a certain message.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        this.reset();
        this.reloadContent();
        Vandalism.getInstance().getEventSystem().subscribe(this, IncomingPacketEvent.ID, PlayerUpdateEvent.ID, DisconnectEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, IncomingPacketEvent.ID, PlayerUpdateEvent.ID, DisconnectEvent.ID);
        this.reset();
    }

    private void reset() {
        this.contentMap.clear();
        this.queuedMessages.clear();
    }

    private void reloadContent() {
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
            } catch (final Exception e) {
                ChatUtil.errorChatMessage("Failed to create chat reaction file: " + e.getMessage());
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
            } catch (final Exception e) {
                ChatUtil.errorChatMessage("Failed to load chat reaction file: " + e.getMessage());
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler != null) {
            for (final Map.Entry<String, Long> entry : this.queuedMessages.entrySet()) {
                if (System.currentTimeMillis() - entry.getValue() >= RandomUtils.randomLong(this.minDelay.getValue(), this.maxDelay.getValue())) {
                    final String message = entry.getKey();
                    this.queuedMessages.remove(message);
                    final String self = mc.getGameProfile().getName();
                    final String targetPlaceholder = "%target%";
                    String target = targetPlaceholder;
                    final PlayerListEntry playerListEntry = WorldUtil.getPlayerFromTab(message);
                    if (playerListEntry != null) {
                        target = playerListEntry.getProfile().getName();
                    }
                    if (StringUtils.contains(target, self)) {
                        target = targetPlaceholder;
                    }
                    final String targetPlayer = target;
                    this.contentMap.forEach((triggers, responses) -> {
                        for (final String trigger : triggers) {
                            if (StringUtils.contains(Placeholders.applyReplacements(message), trigger)) {
                                String answer = responses.length == 1 ? responses[0] : responses[RandomUtils.randomIndex(responses.length)];
                                answer = Placeholders.applyReplacements(answer);
                                if (answer.contains(targetPlaceholder) && !targetPlayer.equals(targetPlaceholder)) {
                                    answer = answer.replace(targetPlaceholder, targetPlayer);
                                }
                                if (answer.startsWith("/")) {
                                    networkHandler.sendChatCommand(answer.substring(1));
                                } else {
                                    networkHandler.sendChatMessage(answer);
                                }
                                break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        String message = "";
        final Packet<?> packet = event.packet;
        if (packet instanceof final GameMessageS2CPacket gameMessageS2CPacket) {
            if (!gameMessageS2CPacket.overlay()) {
                message = gameMessageS2CPacket.content().getString();
            }
        } else if (packet instanceof final ChatMessageS2CPacket chatMessageS2CPacket) {
            final PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(chatMessageS2CPacket.sender());
            if (playerListEntry != null) {
                message = "<" + playerListEntry.getProfile().getName() + "> " + chatMessageS2CPacket.body().content();
            }
        }
        if (message.startsWith(ChatUtil.getChatPrefix().getString())) {
            return;
        }
        message = Formatting.strip(message);
        if (message.replace(" ", "").isEmpty()) {
            return;
        }
        this.queuedMessages.put(message, System.currentTimeMillis());
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        this.reset();
    }

}
