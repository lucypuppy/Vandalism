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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import de.nekosarekawaii.vandalism.clientwindow.template.StateClientWindow;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import net.minecraft.client.gui.DrawContext;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class FritzBoxClientWindow extends StateClientWindow {

    private static final String[] URLS = new String[]{"igd", ""};
    private static final byte[] BODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:ForceTermination xmlns:u=\"urn:schemas-upnp-org:service:WANIPConnection:1\" /></s:Body></s:Envelope>".getBytes(StandardCharsets.UTF_8);

    private boolean reconnecting = false;

    public FritzBoxClientWindow() {
        super("Fritz Box", Category.MISC, 500f, 150f);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.onRender(context, mouseX, mouseY, delta);
        if (!this.reconnecting) {
            if (ImUtils.subButton("Reconnect")) {
                new Thread(() -> {
                    try (final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
                        this.setState("Trying to reconnect...");
                        for (final String url : URLS) {
                            final HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(String.format("http://fritz.box:49000/%supnp/control/WANIPConn1", url)))
                                    .timeout(Duration.ofSeconds(10))
                                    .header("Content-Type", "text/xml; charset=utf-8")
                                    .header("SOAPACTION", "\"urn:schemas-upnp-org:service:WANIPConnection:1#ForceTermination\"")
                                    .POST(HttpRequest.BodyPublishers.ofByteArray(BODY))
                                    .build();
                            client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                            this.setState("Reconnecting...");
                            this.reconnecting = true;
                            while (true) {
                                try {
                                    Thread.sleep(5000);
                                } catch (final InterruptedException ignored) {
                                }
                                try (final Socket socket = new Socket()) {
                                    socket.connect(new InetSocketAddress("1.1.1.1", 80), 2000);
                                    this.setState("Successfully reconnected.");
                                    this.reconnecting = false;
                                    break;
                                } catch (final Exception ignored) {
                                }
                            }
                        }
                    } catch (final Exception e) {
                        this.setState("Failed to reconnect: " + e.getMessage());
                        this.reconnecting = false;
                    }
                    this.delayedResetState(5000);
                }).start();
            }
        }
    }

}

