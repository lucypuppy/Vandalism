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

package de.nekosarekawaii.vandalism.clientwindow.base;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.clientwindow.impl.AboutClientWindow;
import de.nekosarekawaii.vandalism.clientwindow.impl.GlobalSearchClientWindow;
import de.nekosarekawaii.vandalism.feature.hud.gui.HUDClientWindow;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.imgui.ImLoader;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClientWindowScreen extends Screen {

    private static final String[] URLS = new String[]{"igd", ""};
    private static final byte[] BODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:ForceTermination xmlns:u=\"urn:schemas-upnp-org:service:WANIPConnection:1\" /></s:Body></s:Envelope>".getBytes(StandardCharsets.UTF_8);

    private final ClientWindowManager clientWindowManager;
    private final Screen prevScreen;

    public ClientWindowScreen(final ClientWindowManager clientWindowManager, final Screen prevScreen) {
        super(Text.literal("Client Window"));
        this.clientWindowManager = clientWindowManager;
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        super.init();

        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.init();
        }
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        ImLoader.draw(() -> {
            final HUDClientWindow hudImWindow = this.clientWindowManager.getByClass(HUDClientWindow.class);
            if (hudImWindow.isActive()) {
                hudImWindow.render(context, mouseX, mouseY, delta);
            } else {
                if (ImGui.beginMainMenuBar()) {
                    if (ImGui.button("Global Search")) {
                        this.clientWindowManager.getByClass(GlobalSearchClientWindow.class).toggle();
                    }
                    for (final ClientWindow.Category category : this.clientWindowManager.getCategories()) {
                        if (category == null) continue;
                        if (ImGui.beginMenu(category.getName())) {
                            for (final ClientWindow clientWindow : this.clientWindowManager.getByCategory(category)) {
                                if (ImGui.checkbox(clientWindow.getName(), clientWindow.isActive())) {
                                    clientWindow.toggle();
                                }
                            }
                            if (category == ClientWindow.Category.CONFIG) {
                                ImGui.separator();
                                if (ImUtils.subButton("Save Configs")) {
                                    Vandalism.getInstance().getConfigManager().save();
                                }
                            } else if (category == ClientWindow.Category.SERVER) {
                                if (ServerUtil.lastServerExists() && this.client.getCurrentServerEntry() == null) {
                                    ImGui.separator();
                                    if (ImUtils.subButton("Connect to last server")) {
                                        ServerUtil.connectToLastServer();
                                    }
                                }
                            }
                            ImGui.endMenu();
                        }
                    }
                    if (ImGui.button("About")) {
                        this.clientWindowManager.getByClass(AboutClientWindow.class).toggle();
                    }
                    if (ImGui.button("Fritz!box reconnect")) {
                        try {
                            reconnect();
                        } catch (IOException e) {
                            System.out.println("[Fritz!box] failed to reconnect.");
                            e.printStackTrace();
                        }
                    }
                    ImGui.endMainMenuBar();
                }
                for (final ClientWindow window : this.clientWindowManager.getList()) {
                    if (window.isActive()) {
                        window.render(context, mouseX, mouseY, delta);
                    }
                }
                if (ImGui.beginMainMenuBar()) {
                    if (ImGui.button("Close")) {
                        this.close();
                    }
                    ImGui.endMainMenuBar();
                }
            }
        });
    }

    @Override
    public void tick() {
        for (final ClientWindow clientWindow : this.clientWindowManager.getList()) {
            clientWindow.tick();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, false);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            window.mouseClicked(mouseX, mouseY, button, true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            if (!window.keyPressed(keyCode, scanCode, modifiers, false)) {
                return false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        for (final ClientWindow window : this.clientWindowManager.getList()) {
            if (!window.isActive()) {
                continue;
            }
            if (!window.keyPressed(keyCode, scanCode, modifiers, true)) {
                return false;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        ImLoader.forceUpdateMouse();
        if (this.prevScreen == null) {
            this.client.mouse.lockCursor();
            if (this.client.player == null) {
                return;
            }
        }
        this.client.setScreen(this.prevScreen);
    }

    private static void reconnect() throws IOException {
        System.out.println("[Fritz!box] trying to reconnect...");
        for (String url : URLS) {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://fritz.box:49000/" + url + "upnp/control/WANIPConn1").openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
            urlConnection.addRequestProperty("Connection", "close");
            urlConnection.addRequestProperty("Content-Length", String.valueOf(BODY.length));
            urlConnection.addRequestProperty("HOST", "fritz.box:49000");
            urlConnection.addRequestProperty("SOAPACTION", "\"urn:schemas-upnp-org:service:WANIPConnection:1#ForceTermination\"");
            urlConnection.getOutputStream().write(BODY);
            if (urlConnection.getResponseCode() == 200) {
                System.out.println("[Fritz!box] reconnecting...");
                break;
            }
        }
    }

}
