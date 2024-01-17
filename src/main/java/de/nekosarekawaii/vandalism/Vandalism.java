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

package de.nekosarekawaii.vandalism;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.normal.game.MinecraftBoostrapListener;
import de.nekosarekawaii.vandalism.base.event.normal.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.feature.command.CommandManager;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.feature.script.ScriptManager;
import de.nekosarekawaii.vandalism.integration.hud.HUDManager;
import de.nekosarekawaii.vandalism.integration.rotation.RotationListener;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerListManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO: NekosAreKawaii <br>
 *  - Fix an config load issue by adding #has from Gson in all the values
 *  - Remove duplicated code by creating a new template for modules which uses packets or something like that
 *  - Add Access Violation crash item to CrashItemCreativeTab
 *  - Add 1.20.2 / maybe 1.20.3 instant crash crash item to CrashItemCreativeTab
 *  - Replaced target selection with actual entity selection (create something like an registry multi selection value)
 * <br><br>
 * TODO: Verschlxfene <br>
 *  - Apply checkstyle.xml to all classes
 *  - Delete MixinParticleManager
 *  - Add Proxy manager
 *  - Add JiJ and JiS for building/exporting the mod
 * <br><br>
 * TODO: FooFieOwO <br>
 *  - Fix module list out of screen rendering when the alignment is at the bottom of the screen
 *  - Fix Rotation raytrace when block normalizer is enabled (Collision shape event)
 *  - Fix NoClipMode from PhaseModule
 *  - Fix SprintModule to be compatible with the rotation listener and also the backwards direction (ask Verschlxfene)
 *  - Fix calculations for the HUD aka. HUDElement#calculatePosition
 *  - Fix tick bug (cps is not accurate) -> see BoxMuellerClicker#onUpdate
 *  - Fix this by something like an auto detection (for example you could make module modes features):
 *    "...visibleCondition(() -> this.getParent().mode.getValue().equals(this))"
 *  - Make a better calculation for the rotate speed RotationListener#rotationDistribution
 *  - Check if anything has changed in 1.20.2 regarding hit box position offsetting RotationUtil#getVisibleHitBoxSides
 *  - KillAura#onPrePlayerUpdate | Frame event (entity renderer set angles) -> rotate / Mouse event -> attack
 * <br><br>
 * TODO: Snow <br>
 *  - Implement ServerDiscoveryClientMenuWindow#renderServerPopup "Add to server list" feature
 * <br><br>
 * TODO: Everyone <br>
 *  - Rewrite EnhancedServerList
 *  - Fix the entity layer rendering from the true sight module
 *  - Fix offsets for the new "teleport" method in the fov fucker module
 *  - Fix spaces in the text rendering when using the "deutsch macher" module
 *  - Protector Module:
 *      - Add protection for custom rank prefixes
 *      - Add protection for skins
 *      - Add protection for coords
 *      - Add protection for ip addresses
 *  - Make the width and height customizable or use calculations in the modules im window for the tabs
 *  - Fix ImGui#begin in every im window (remove No Collapse Flag and move the code out of the if)
 *  - Fix module tabs display (no stacking) when the mod starts the first time
 *  - Fix ClientMenuScreen#close because it could break the entire game
 *  - Add anti vanish via. the player list hud
 */
public class Vandalism implements MinecraftBoostrapListener, ShutdownProcessListener {

    private static final Vandalism instance = new Vandalism();

    private final DietrichEvents2 eventSystem = new DietrichEvents2(33 /* This value has to be incremented for every new event */, Throwable::printStackTrace);
    private final Logger logger = LoggerFactory.getLogger(FabricBootstrap.MOD_NAME);

    // Base handlers
    private File runDirectory;

    private ConfigManager configManager;
    private ClientMenuManager clientMenuManager;
    private ClientSettings clientSettings;
    private AccountManager accountManager;

    // Integration
    private RotationListener rotationListener;
    private ServerListManager serverListManager;
    private HUDManager hudManager;

    // Features
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ScriptManager scriptManager;
    private CreativeTabManager creativeTabManager;

    public void printStartup() {
        this.logger.info("");
        final String[] ASCII_ART = {
                " ██▒   █▓ ▄▄▄       ███▄    █ ▓█████▄  ▄▄▄       ██▓     ██▓  ██████  ███▄ ▄███▓",
                "▓██░   █▒▒████▄     ██ ▀█   █ ▒██▀ ██▌▒████▄    ▓██▒    ▓██▒▒██    ▒ ▓██▒▀█▀ ██▒",
                " ▓██  █▒░▒██  ▀█▄  ▓██  ▀█ ██▒░██   █▌▒██  ▀█▄  ▒██░    ▒██▒░ ▓██▄   ▓██    ▓██░",
                "  ▒██ █░░░██▄▄▄▄██ ▓██▒  ▐▌██▒░▓█▄   ▌░██▄▄▄▄██ ▒██░    ░██░  ▒   ██▒▒██    ▒██ ",
                "   ▒▀█░   ▓█   ▓██▒▒██░   ▓██░░▒████▓  ▓█   ▓██▒░██████▒░██░▒██████▒▒▒██▒   ░██▒",
                "   ░ ▐░   ▒▒   ▓▒█░░ ▒░   ▒ ▒  ▒▒▓  ▒  ▒▒   ▓▒█░░ ▒░▓  ░░▓  ▒ ▒▓▒ ▒ ░░ ▒░   ░  ░",
                "   ░ ░░    ▒   ▒▒ ░░ ░░   ░ ▒░ ░ ▒  ▒   ▒   ▒▒ ░░ ░ ▒  ░ ▒ ░░ ░▒  ░ ░░  ░      ░",
                "     ░░    ░   ▒      ░   ░ ░  ░ ░  ░   ░   ▒     ░ ░    ▒ ░░  ░  ░  ░      ░   ",
                "      ░        ░  ░         ░    ░          ░  ░    ░  ░ ░        ░         ░   ",
                "     ░                         ░                                                "
        };
        for (final String line : ASCII_ART) {
            this.logger.info(line);
        }
        this.logger.info("");
        this.logger.info("Version: " + FabricBootstrap.MOD_VERSION);
        this.logger.info("Made by " + FabricBootstrap.MOD_AUTHORS + " with <3");
        this.logger.info("");
        this.logger.info("Starting...");
        this.logger.info("");
    }

    @Override
    public void onBootstrapGame(final MinecraftClient mc) {
        this.printStartup();
        mc.getWindow().setTitle(String.format("Starting %s...", FabricBootstrap.WINDOW_TITLE));

        // Base handlers
        FabricBootstrap.MOD_ICON = new Identifier(FabricBootstrap.MOD_ID, "textures/logo.png");

        this.runDirectory = new File(this.runDirectory, FabricBootstrap.MOD_ID);
        this.runDirectory.mkdirs();

        this.configManager = new ConfigManager();

        this.clientMenuManager = new ClientMenuManager(this.configManager, this.runDirectory);

        this.clientSettings = new ClientSettings(this.configManager, this.clientMenuManager);
        this.accountManager = new AccountManager(this.configManager, this.clientMenuManager);
        this.accountManager.init();

        // Integration
        this.rotationListener = new RotationListener();

        this.serverListManager = new ServerListManager(this.runDirectory);
        this.serverListManager.loadConfig();

        this.hudManager = new HUDManager(this.configManager, this.clientMenuManager);
        this.hudManager.init();

        // Features
        this.moduleManager = new ModuleManager(this.configManager, this.clientMenuManager);
        this.moduleManager.init();

        this.commandManager = new CommandManager();
        this.commandManager.init();

        this.scriptManager = new ScriptManager(this.configManager, this.clientMenuManager, this.runDirectory);
        this.scriptManager.init();

        this.creativeTabManager = new CreativeTabManager();
        this.creativeTabManager.init();

        /*
                Cause of the menu category button order this needs to be called
                after every default menu has been added and before the addons are loaded
         */
        this.clientMenuManager.init();

        VandalismAddonLauncher.call(addon -> addon.onLaunch(this));

        // We have to load the config files after all systems have been initialized
        this.configManager.init();

        this.logger.info("");
        this.logger.info("Done!");
        this.logger.info("");

        mc.getWindow().setTitle(FabricBootstrap.WINDOW_TITLE);

        VandalismAddonLauncher.call(addon -> addon.onLateLaunch(this));
    }

    @Override
    public void onShutdownProcess() {
        configManager.save();
    }

    public static Vandalism getInstance() {
        return instance;
    }

    public DietrichEvents2 getEventSystem() {
        return eventSystem;
    }

    public Logger getLogger() {
        return logger;
    }

    public File getRunDirectory() {
        return runDirectory;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ClientMenuManager getClientMenuManager() {
        return clientMenuManager;
    }

    public ClientSettings getClientSettings() {
        return clientSettings;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public CreativeTabManager getCreativeTabManager() {
        return creativeTabManager;
    }

    public RotationListener getRotationListener() {
        return rotationListener;
    }

    public ServerListManager getServerListManager() {
        return serverListManager;
    }

    public HUDManager getHudManager() {
        return hudManager;
    }

}
