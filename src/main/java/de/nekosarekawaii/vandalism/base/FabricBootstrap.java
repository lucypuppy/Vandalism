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

package de.nekosarekawaii.vandalism.base;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.MinecraftBoostrapListener;
import de.nekosarekawaii.vandalism.event.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.util.SoundHooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.RunArgs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.File;

public class FabricBootstrap implements ClientModInitializer {

    public static String MOD_ID, MOD_NAME, MOD_AUTHORS, MOD_VERSION;
    public static Identifier MOD_LOGO, MOD_ICON; // Initialize these in the main class
    public static String WINDOW_TITLE;
    public static boolean IS_DEV_ENVIRONMENT;
    public static boolean INITIALIZED = false;
    public static boolean SHUTTING_DOWN = false;
    public static RunArgs RUN_ARGS;

    private void tryRenderDoc() {
        final Util.OperatingSystem os = Util.getOperatingSystem();
        if (os == Util.OperatingSystem.WINDOWS) {
            final File renderDoc = new File("C:\\Program Files\\RenderDoc\\renderdoc.dll");
            if (renderDoc.exists() && System.getProperty("vandalism.load.renderdoc", "false").equalsIgnoreCase("true")) {
                System.load(renderDoc.getAbsolutePath());
            }
        }
    }

    @Override
    public void onInitializeClient() {
        tryRenderDoc();
        FabricLoader.getInstance().getModContainer(MOD_ID = "vandalism").ifPresent(modContainer -> {
            FabricBootstrap.MOD_NAME = modContainer.getMetadata().getName();
            FabricBootstrap.MOD_AUTHORS = String.join(", ", modContainer.getMetadata().getAuthors().stream().map(Person::getName).toList());
            FabricBootstrap.MOD_VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
        });
        FabricBootstrap.WINDOW_TITLE = MOD_NAME;
        FabricBootstrap.IS_DEV_ENVIRONMENT = FabricLoader.getInstance().isDevelopmentEnvironment();
        Vandalism.getInstance().getEventSystem().subscribe(Vandalism.getInstance(),
                MinecraftBoostrapListener.MinecraftBootstrapEvent.ID,
                ShutdownProcessListener.ShutdownProcessEvent.ID
        );
        SoundHooks.register();
        VandalismAddonLauncher.call(addon -> addon.onPreLaunch(Vandalism.getInstance()));
    }

}
