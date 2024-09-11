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

package de.nekosarekawaii.vandalism.util;

import com.google.gson.Gson;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.integration.ViaFabricPlusAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.Session;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.Uuids;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerUtil implements MinecraftWrapper {

    private static final Gson GSON = new Gson();
    private static final String IP_API_URL = "https://api.incolumitas.com/?q=";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final MSTimer LAST_SERVER_INFO_FETCH_TIMER = new MSTimer();
    private static String LAST_SERVER_ADDRESS = "";
    private static IPAddressInfo LAST_SERVER_ADDRESS_INFO = null;

    /**
     * Attach additional tooltip data.
     *
     * @param tooltip    The tooltip to attach to.
     * @param serverInfo The server info to attach.
     * @return The tooltip with additional data attached.
     */
    public static List<OrderedText> attachAdditionalTooltipData(final List<OrderedText> tooltip, final ServerInfo serverInfo) {
        if (serverInfo == null) return tooltip;
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue()) {
            if (enhancedServerListSettings.morePingTooltipServerInformation.getValue()) {
                tooltip.clear();
                tooltip.add(Text.literal("Server Info").asOrderedText());
                tooltip.add(Text.literal("Ping: " + Math.max(serverInfo.ping, 0) + " ms").asOrderedText());
                tooltip.add(Text.literal("Version: " + fixVersionName(serverInfo.version.getString(), false)).asOrderedText());
                final int protocol = serverInfo.protocolVersion;
                final ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(protocol);
                final String protocolName = protocolVersion.getName();
                tooltip.add(Text.literal("Protocol: " + protocolName + (!protocolName.contains("(") ? " (" + protocol + ")" : "")).asOrderedText());
                final String address = serverInfo.address;
                String addressWithoutPort = address;
                if (address.contains(":")) {
                    addressWithoutPort = addressWithoutPort.split(":")[0];
                }
                if (addressWithoutPort.equals("0") || addressWithoutPort.equals("localhost") || addressWithoutPort.equals("127.0.0.1") || addressWithoutPort.equals("0.0.0.0")) {
                    return tooltip;
                }
                if (LAST_SERVER_ADDRESS.equals(address) && LAST_SERVER_ADDRESS_INFO != null) {
                    final IPAddressInfo.Location location = LAST_SERVER_ADDRESS_INFO.getLocation();
                    if (location != null) {
                        tooltip.add(Text.literal("Country: " + location.getCountry() + " (" + location.getCountryCode() + ")").asOrderedText());
                    }
                    final IPAddressInfo.Company company = LAST_SERVER_ADDRESS_INFO.getCompany();
                    if (company != null) {
                        tooltip.add(Text.literal("Company: " + company.getName()).asOrderedText());
                    }
                    final IPAddressInfo.ASN asn = LAST_SERVER_ADDRESS_INFO.getAsn();
                    if (asn != null) {
                        tooltip.add(Text.literal("Domain: " + asn.getDomain()).asOrderedText());
                        tooltip.add(Text.literal("Organization: " + asn.getOrg()).asOrderedText());
                        tooltip.add(Text.literal("Description: " + asn.getDescr()).asOrderedText());
                        tooltip.add(Text.literal("ASN: " + asn.getAsn()).asOrderedText());
                    }
                } else {
                    tooltip.add(Text.literal("Error: Failed to get IP information, API is probably down.").asOrderedText());
                }
                if (LAST_SERVER_INFO_FETCH_TIMER.hasReached(2000, true)) {
                    if (!LAST_SERVER_ADDRESS.equals(address)) {
                        LAST_SERVER_ADDRESS = serverInfo.address;
                        EXECUTOR_SERVICE.submit(() -> {
                            final Pair<String, Integer> serverAddress = resolveServerAddress(address);
                            final String resolvedAddress = serverAddress.getLeft();
                            try {
                                LAST_SERVER_ADDRESS_INFO = GSON.fromJson(HttpClient.newHttpClient().send(
                                        HttpRequest.newBuilder().uri(URI.create(IP_API_URL + resolvedAddress))
                                                .headers("Content-Type", "application/json")
                                                .GET()
                                                .build(),
                                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                                ).body(), IPAddressInfo.class);
                            } catch (final Exception e) {
                                Vandalism.getInstance().getLogger().error("Failed to get ip information from: {}", resolvedAddress, e);
                            }
                        });
                    }
                }
            }
        }
        return tooltip;
    }

    /**
     * Fixes the version name.
     *
     * @param input      The input to fix.
     * @param formatting Whether to remove format from the input.
     * @return The fixed version name.
     */
    public static String fixVersionName(String input, final boolean formatting) {
        input = input.replaceAll(" +", " ");
        if (input.contains(" ")) {
            final String[] data = input.split(" ");
            if (data.length > 1) {
                final String name = data[0];
                String versionRange = input.substring(name.length() + 1);
                if (versionRange.contains(", ")) {
                    final String[] versions = versionRange.split(", ");
                    if (versions.length > 1) {
                        versionRange = versions[0] + "-" + versions[versions.length - 1];
                    }
                }
                return name + " " + versionRange;
            }
        }
        if (formatting) {
            input = Formatting.strip(input);
        }
        return input;
    }

    /**
     * Fixes the address.
     * @param input The input to fix.
     * @return The fixed address.
     */
    public static String fixAddress(String input) {
        input = input.replaceAll("[^a-zA-Z0-9.:\\-_] ", "");
        input = input.replace(",", ".");
        input = input.replace(" ", "");
        return input;
    }

    /**
     * Fixes the description aka. MOTD.
     *
     * @param input The input to fix.
     * @return The fixed description.
     */
    public static String fixDescription(String input) {
        String description = Formatting.strip(input);
        if (description != null && !description.isEmpty()) {
            final String colorCodePrefix = String.valueOf(Formatting.FORMATTING_CODE_PREFIX);
            if (description.contains(colorCodePrefix)) {
                description = description.replace(colorCodePrefix, "");
            }
            if (description.contains("    ")) {
                description = description.replace("    ", " ");
            }
            final StringBuilder filteredDescription = new StringBuilder();
            final List<Character> allowedCharacters = List.of(
                    ' ', '.', '_', '-', ',', ':', ';', '(', ')', '[', ']', '{', '}', '<', '>', '/',
                    '\\', '|', '?', '!', '@', '#', '$', '%', '^', '&', '*', '+', '=', '~', '`', '\'', '"'
            );
            for (final char c : description.toCharArray()) {
                if (Character.isLetterOrDigit(c) || allowedCharacters.contains(c)) {
                    filteredDescription.append(c);
                } else {
                    filteredDescription.append(c != '\n' && c != '\t' ? '?' : ' ');
                }
            }
            description = filteredDescription.toString();
            if (!description.isEmpty()) {
                final int maxLength = 200;
                if (description.length() > maxLength) {
                    description = description.substring(0, maxLength);
                    description += "...";
                }
            }
        }
        return description;
    }

    /**
     * Check if the player is anonymous.
     *
     * @param name The name of the player.
     * @param uuid The UUID of the player.
     * @return Whether the player is anonymous.
     */
    public static boolean isAnonymous(final String name, final String uuid) {
        return name.equals("Anonymous Player") || uuid.equals("00000000-0000-0000-0000-000000000000");
    }

    /**
     * Check if the player is the current player.
     *
     * @param name The name of the player.
     * @param uuid The UUID of the player.
     * @return Whether the player is the current player.
     */
    public static boolean isSelf(final String name, final String uuid) {
        final Session currentSession = MinecraftClient.getInstance().session;
        final String currentName = currentSession.getUsername();
        final UUID currentUuid = currentSession.getUuidOrNull();
        boolean isSameUUID = currentUuid != null && currentUuid.toString().equals(uuid);
        if (!isSameUUID) {
            isSameUUID = Uuids.getOfflinePlayerUuid(currentName).toString().equals(uuid);
        }
        return currentName.equals(name) && isSameUUID;
    }

    private static ServerInfo LAST_SERVER_INFO = null;

    /**
     * Check if the last server exists.
     *
     * @return Whether the last server exists.
     */
    public static boolean lastServerExists() {
        return LAST_SERVER_INFO != null;
    }

    /**
     * Get the last server info.
     *
     * @return The last server info.
     */
    public static ServerInfo getLastServerInfo() {
        return LAST_SERVER_INFO;
    }

    /**
     * Set the last server info.
     *
     * @param serverInfo The server info to set.
     */
    public static void setLastServerInfo(final ServerInfo serverInfo) {
        LAST_SERVER_INFO = serverInfo;
    }

    /**
     * Connect to the last server.
     */
    public static void connectToLastServer() {
        if (LAST_SERVER_INFO == null) return;
        connect(LAST_SERVER_INFO);
    }

    /**
     * Connect to a server.
     *
     * @param address The address to connect to.
     */
    public static void connect(final String address) {
        connect(new ServerInfo("", address, ServerInfo.ServerType.OTHER));
    }

    /**
     * Connect to a server.
     *
     * @param serverInfo The server info to connect to.
     */
    public static void connect(final ServerInfo serverInfo) {
        if (serverInfo == null) return;
        ConnectScreen.connect(
                new MultiplayerScreen(new TitleScreen()),
                mc,
                ServerAddress.parse(serverInfo.address),
                serverInfo,
                false,
                null // Null is correct, else it'll say "transferring to server" - Lucy
        );
    }

    /**
     * Autistic fix for in-game with revert on disconnect.
     *
     * @param address            The address to connect to.
     * @param protocolVersion    The protocol version to connect with.
     * @param revertOnDisconnect Whether to revert on disconnect.
     */
    public static void connectWithVFPFix(final String address, final ProtocolVersion protocolVersion, final boolean revertOnDisconnect) {
        ViaFabricPlusAccess.setPreviousVersion(null);
        ProtocolTranslator.setTargetVersion(protocolVersion, revertOnDisconnect);
        connect(address);
    }

    /**
     * Disconnect from the server.
     */
    public static void disconnect() {
        disconnect("Disconnected from the server.");
    }

    /**
     * Disconnect from the server.
     *
     * @param reason The reason to disconnect.
     */
    public static void disconnect(final String reason) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler != null) {
            networkHandler.getConnection().disconnect(
                    Text.literal(reason)
            );
        }
    }

    /**
     * Split the server address.
     *
     * @param address The address to split.
     * @return The split server address as a pair (address, port).
     */
    public static Pair<String, Integer> splitServerAddress(String address) {
        if (address.contains(":")) {
            final String[] data = address.split(":");
            if (data.length == 2) {
                try {
                    return new Pair<>(data[0], Integer.parseInt(data[1]));
                } catch (final Exception ignored) {
                }
            }
        }
        return new Pair<>(address, 25565);
    }

    /**
     * Resolve the server address.
     *
     * @param hostname The hostname to resolve.
     * @return The resolved server address.
     */
    public static Pair<String, Integer> resolveServerAddress(final String hostname) {
        try {
            final net.lenni0451.mcping.ServerAddress serverAddress = net.lenni0451.mcping.ServerAddress.parse(hostname, 25565);
            serverAddress.resolve();
            String address = serverAddress.getSocketAddress().toString();
            if (address.contains("./")) {
                address = address.replace("./", "/");
            }
            if (address.contains("/")) {
                address = address.replace("/", " ");
            }
            if (address.contains(" ")) {
                address = address.split(" ")[1];
            }
            if (address.contains(":")) {
                address = address.split(":")[0];
            }
            return new Pair<>(address, serverAddress.getPort());
        } catch (final Exception ignored) {
            final String[] split = hostname.split(":");
            if (split.length == 2) {
                return new Pair<>(split[0], Integer.parseInt(split[1]));
            }
            return new Pair<>(hostname, 25565);
        }
    }

}
