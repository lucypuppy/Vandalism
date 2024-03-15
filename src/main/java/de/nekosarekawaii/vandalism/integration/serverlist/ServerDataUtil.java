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

package de.nekosarekawaii.vandalism.integration.serverlist;

import com.google.gson.Gson;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.rclasses.math.timer.MSTimer;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.EnhancedServerListSettings;
import de.nekosarekawaii.vandalism.util.game.ServerConnectionUtil;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerDataUtil {

    private static final Gson GSON = new Gson();

    private static final String IP_API_URL = "https://api.incolumitas.com/?q=";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private static final MSTimer LAST_SERVER_INFO_FETCH_TIMER = new MSTimer();

    private static String LAST_SERVER_ADDRESS = "";

    private static IPAddressInfo LAST_SERVER_ADDRESS_INFO = null;

    public static List<Text> attachAdditionalTooltipData(final List<Text> tooltip, final ServerInfo serverInfo) {
        if (serverInfo == null) return tooltip;
        GeneralSettings.global().showAdvertisedServerVersion.setValue(false);
        final EnhancedServerListSettings enhancedServerListSettings = Vandalism.getInstance().getClientSettings().getEnhancedServerListSettings();
        if (enhancedServerListSettings.enhancedServerList.getValue()) {
            if (enhancedServerListSettings.morePingTooltipServerInformation.getValue()) {
                tooltip.clear();
                tooltip.add(Text.literal("Server Info"));
                tooltip.add(Text.literal("Ping: " + Math.max(serverInfo.ping, 0) + " ms"));
                tooltip.add(Text.literal("Version: " + ServerDataUtil.fixVersionName(serverInfo.version.getString())));
                final int protocol = serverInfo.protocolVersion;
                final ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(protocol);
                final String protocolName = protocolVersion.getName();
                tooltip.add(Text.literal("Protocol: " + protocolName + (!protocolName.contains("(") ? " (" + protocol + ")" : "")));
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
                        tooltip.add(Text.literal("Country: " + location.getCountry() + " (" + location.getCountryCode() + ")"));
                    }
                    final IPAddressInfo.Company company = LAST_SERVER_ADDRESS_INFO.getCompany();
                    if (company != null) {
                        tooltip.add(Text.literal("Company: " + company.getName()));
                    }
                    final IPAddressInfo.ASN asn = LAST_SERVER_ADDRESS_INFO.getAsn();
                    if (asn != null) {
                        tooltip.add(Text.literal("Domain: " + asn.getDomain()));
                        tooltip.add(Text.literal("Organization: " + asn.getOrg()));
                        tooltip.add(Text.literal("Description: " + asn.getDescr()));
                        tooltip.add(Text.literal("ASN: " + asn.getAsn()));
                    }
                }
                else {
                    tooltip.add(Text.literal("Error: Failed to get IP information, API is probably down."));
                }
                if (LAST_SERVER_INFO_FETCH_TIMER.hasReached(2000, true)) {
                    if (!LAST_SERVER_ADDRESS.equals(address)) {
                        LAST_SERVER_ADDRESS = serverInfo.address;
                        EXECUTOR_SERVICE.submit(() -> {
                            final Pair<String, Integer> serverAddress = ServerConnectionUtil.resolveServerAddress(address);
                            final String resolvedAddress = serverAddress.getLeft();
                            try {
                                LAST_SERVER_ADDRESS_INFO = GSON.fromJson(HttpClient.newHttpClient().send(
                                        HttpRequest.newBuilder().uri(URI.create(IP_API_URL + resolvedAddress))
                                                .headers("Content-Type", "application/json")
                                                .GET()
                                                .build(),
                                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                                ).body(), IPAddressInfo.class);
                            } catch (Exception e) {
                                Vandalism.getInstance().getLogger().error("Failed to get ip information from: " + resolvedAddress, e);
                            }
                        });
                    }
                }
            }
        }
        return tooltip;
    }

    public static String fixVersionName(String input) {
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
        return input;
    }

    public static String fixAddress(String input) {
        input = input.replaceAll("[^a-zA-Z0-9.:\\-_] ", "");
        input = input.replace(",", ".");
        input = input.replace(" ", "");
        return input;
    }

}
