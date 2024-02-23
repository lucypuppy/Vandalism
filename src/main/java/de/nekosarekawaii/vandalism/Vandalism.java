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
import de.nekosarekawaii.vandalism.feature.hud.HUDManager;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.feature.script.ScriptManager;
import de.nekosarekawaii.vandalism.integration.friends.FriendsManager;
import de.nekosarekawaii.vandalism.integration.rotation.RotationListener;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerListManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO: NekosAreKawaii <br>
 *  - Fix true sight when holding an barrier block in hand
 *  - Add Version Range to the creative tabs
 *  - Change behavior of the echolocation module to use it as a chunk load detector
 *  - Protector Module:
 *      - Add protection for custom rank prefixes
 *      - Add protection for skins
 *      - Add protection for coords
 *      - Add protection for ip addresses
 *      - Add protection for account manager
 *  - Fix spotify menu control buttons
 *  - Use <a href="https://github.com/EvilCodeZ/JNI4J/tree/main">JNI4J</a> to improve the packet manager logging for fields
 *  - Fix vanilla title texts being displayed after leaving a server
 *  - Rewrite EnhancedServerList
 *  - Fix offsets for the new "teleport" method in the fov fucker module
 *  - Fix module tabs display (no stacking) when the client starts the first time
 *  - Use hashmap storage when it has been added to rclasses instead of a default storage because that would improve the performance
 *      - (Search for FriendManager#getList())
 * <br><br>
 * TODO: Verschlxfene <br>
 *  - Delete MixinParticleManager
 *  - Add Proxy manager
 *  - Rewrite GCD fix to be accurate
 *  - Implement uncharge speed into tickbase
 *  - Add support for multiple presses on tick in KeyBindSetting
 *  - Delete TickBaseModule#worldTicker and replace with worldTick()
 *  - Add working defensive and counter mode to Tickbase
 *  - Fix Game server pinger (ensure pings are on different threads)
 *  - Fix calculations for the HUD:
 *      - Fix HUDElement#calculatePosition
 *      - Fix module list out of screen rendering when the alignment is for example at the bottom of the screen
 *  - Add lag engine to fix countless BackTrack issues
 *  - Ensure MCConstants class file is used
 *  - Rework parts of NoSlow
 *  - Fix bukkit fly not working at all
 *  - Rewrite commands to use generic expressions (like #SpawnEgg, #Book)
 *  - Add anti vanish via. the player list hud
 *  - Delete ModuleManager class
 * <br><br>
 * TODO: FooFieOwO <br>
 *  - Fix SprintModule to be compatible with the rotation listener and also the backwards direction (ask Verschlxfene)
 *  - KillAura#onPrePlayerUpdate | Frame event (entity renderer set angles) -> rotate / Mouse event -> attack
 *  - Add more stuff to fake lag (Ideas off clumsy)
 *  - Fix backwards sprint not working
 *  - Add hunger to noslow
 * <br><br>
 * TODO: simon <br>
 *  - Add a better Autoblock to Killaura with FooFieOwO
 */
public class Vandalism implements MinecraftBoostrapListener, ShutdownProcessListener {

    private static final Vandalism instance = new Vandalism();

    private final DietrichEvents2 eventSystem = new DietrichEvents2(36 /* This value has to be incremented for every new event */, Throwable::printStackTrace);
    private final Logger logger = LoggerFactory.getLogger(FabricBootstrap.MOD_NAME);

    //Base handlers
    private File runDirectory;

    private ConfigManager configManager;
    private ClientMenuManager clientMenuManager;
    private ClientSettings clientSettings;
    private AccountManager accountManager;

    //Integration
    private RotationListener rotationListener;
    private ServerListManager serverListManager;
    private FriendsManager friendsManager;

    //Features
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ScriptManager scriptManager;
    private CreativeTabManager creativeTabManager;
    private HUDManager hudManager;

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

        //Base handlers
        FabricBootstrap.MOD_LOGO = new Identifier(FabricBootstrap.MOD_ID, "textures/logo.png");
        FabricBootstrap.MOD_ICON = new Identifier(FabricBootstrap.MOD_ID, "textures/icon/icon_1024x1024.png");

        this.runDirectory = new File(this.runDirectory, FabricBootstrap.MOD_ID);
        this.runDirectory.mkdirs();

        this.configManager = new ConfigManager();

        this.clientMenuManager = new ClientMenuManager(this.configManager, this.runDirectory);

        this.clientSettings = new ClientSettings(this.configManager, this.clientMenuManager);
        this.accountManager = new AccountManager(this.configManager, this.clientMenuManager);
        this.accountManager.init();

        //Integration
        this.rotationListener = new RotationListener();

        this.serverListManager = new ServerListManager(this.runDirectory);
        this.serverListManager.loadConfig();

        this.friendsManager = new FriendsManager(this.configManager);

        //Features
        this.moduleManager = new ModuleManager(this.eventSystem, this.configManager, this.clientMenuManager);
        this.moduleManager.init();

        this.commandManager = new CommandManager();
        this.commandManager.init();

        this.scriptManager = new ScriptManager(this.configManager, this.clientMenuManager, this.runDirectory);
        this.scriptManager.init();

        this.creativeTabManager = new CreativeTabManager();
        this.creativeTabManager.init();

        this.hudManager = new HUDManager(this.configManager, this.clientMenuManager);
        this.hudManager.init();

        // Cause of the menu category button order this needs to be called
        // after every default menu has been added and before the addons are loaded
        this.clientMenuManager.init();

        VandalismAddonLauncher.call(addon -> addon.onLaunch(this));

        //We have to load the config files after all systems have been initialized
        this.configManager.init();

        this.logger.info("");
        this.logger.info("Done!");
        this.logger.info("");

        mc.getWindow().setTitle(FabricBootstrap.WINDOW_TITLE);

        VandalismAddonLauncher.call(addon -> addon.onLateLaunch(this));

        FabricBootstrap.INITIALIZED = true;
    }

    @Override
    public void onShutdownProcess() {
        FabricBootstrap.SHUTTING_DOWN = true;
        Vandalism.getInstance().getLogger().info("Shutting down...");
        this.configManager.save();
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

    public FriendsManager getFriendsManager() {
        return friendsManager;
    }

}
