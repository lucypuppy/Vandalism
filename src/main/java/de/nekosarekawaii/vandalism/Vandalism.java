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

package de.nekosarekawaii.vandalism;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.VandalismAddonLauncher;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.game.MinecraftBoostrapListener;
import de.nekosarekawaii.vandalism.event.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.feature.command.CommandManager;
import de.nekosarekawaii.vandalism.feature.hud.HUDManager;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.integration.friends.FriendsManager;
import de.nekosarekawaii.vandalism.integration.rotation.RotationManager;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerListManager;
import de.nekosarekawaii.vandalism.util.game.NameGenerationUtil;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO: NekosAreKawaii <br>
 *  - Change behavior of the echolocation module to use it as a chunk load detector
 *  - Protector Module:
 *      - Add protection for custom rank prefixes
 *      - Add protection for skins
 *      - Add protection for coords
 *      - Add protection for ip addresses
 *      - Add protection for account manager
 *  - Fix spotify menu control buttons
 *  - Add anti vanish via. the player list hud
 *  - Add Proxy manager
 *  - Restructure addon system/loader
 *  - Networking optimizations and fixes
 * <br><br>
 * TODO: FooFieOwO <br>
 *  - Rewrite GCD fix to be accurate
 *  - Implement uncharge speed into tickbase
 *  - Add working defensive and counter mode to Tickbase
 *  - Add lag engine to fix countless BackTrack issues
 *  - Improve bukkit fly not working at all
 *  - Fix SprintModule to be compatible with the rotation listener and also the backwards direction
 *  - KillAura#onPrePlayerUpdate | Frame event (entity renderer set angles) -> rotate / Mouse event -> attack
 *  - Add more stuff to fake lag (Ideas off clumsy)
 *  - Fix autoblock blocking without sword
 *  - Rework windmouse algorythm
 *  - Technically this should be legit but i need more investigation -> AutoGUICloseModule#onIncomingPacket
 *  - Maybe add faster ray traces -> RotationBuilder#build
 *  - Make this customizable -> InventoryUtil#getHotbarSlotForItem
 *  - Add enchants, Durability etc. -> InventoryUtil#isItemBetter
 *  - Check if the item in the chest is better otherwise ignore it -> ChestStealerModule#onPrePlayerUpdate
 *  - Check if the item is useful in any way -> ChestStealerModule#onPrePlayerUpdate
 * <br><br>
 * TODO: simon <br>
 *  - Add a better Autoblock to Killaura with FooFieOwO
 *  - Add double clicks to KillAura when recoding
 *  - Add prioritize criticals to KillAura when recoding
 *  - Add jumping with prediction if in range to LagRange
 * <br><br>
 * TODO: EvilCodeZ <br>
 *  - Add Bold -> SimpleFont#SimpleGlyphRenderer#renderGlyph
 *  - Also handle MINECRAFT_LINES and MINECRAFT_LINE_STRIP -> PersistentMeshProducer#addPass at "} else if (pass.getPrimitiveType() == PrimitiveType.QUADS) {"
 *  - Finish AtlasFontRenderer#substringText x2
 */
@Getter
public class Vandalism implements MinecraftBoostrapListener, ShutdownProcessListener {

    @Getter
    private static final Vandalism instance = new Vandalism();

    private final DietrichEvents2 eventSystem = new DietrichEvents2(48 /* This value has to be incremented for every new event */, Throwable::printStackTrace);
    private final Logger logger = LoggerFactory.getLogger(FabricBootstrap.MOD_NAME);

    // Base handlers
    private File runDirectory;
    private double startTime;
    private boolean isFirstTime;

    private ConfigManager configManager;
    private ClientWindowManager clientWindowManager;
    private ClientSettings clientSettings;
    private AccountManager accountManager;

    // Integration
    private RotationManager rotationManager;
    private ServerListManager serverListManager;
    private FriendsManager friendsManager;
    private HUDManager hudManager;

    // Features
    private ModuleManager moduleManager;
    private CommandManager commandManager;

    @Setter
    private RunArgs runArgs; // TODO Is it okay to have this here?

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
        this.logger.info("Version: {}", FabricBootstrap.MOD_VERSION);
        this.logger.info("Made by {} ;3", FabricBootstrap.MOD_AUTHORS);
        this.logger.info("");
        this.logger.info("Starting...");
        this.logger.info("");
    }

    @Override
    public void onBootstrapGame(final MinecraftClient mc) {
        this.printStartup();
        mc.getWindow().setTitle(String.format("Starting %s...", FabricBootstrap.WINDOW_TITLE));

        // Base handlers
        FabricBootstrap.MOD_LOGO = Identifier.of(FabricBootstrap.MOD_ID, "textures/logo.png");
        FabricBootstrap.MOD_ICON = Identifier.of(FabricBootstrap.MOD_ID, "textures/icon/icon_1024x1024.png");

        this.runDirectory = new File(this.runDirectory, FabricBootstrap.MOD_ID);

        this.isFirstTime = !this.runDirectory.exists();

        this.runDirectory.mkdirs();

        this.configManager = new ConfigManager();

        this.clientWindowManager = new ClientWindowManager(this.configManager, this.runDirectory);

        this.clientSettings = new ClientSettings(this.configManager, this.clientWindowManager);

        this.accountManager = new AccountManager(this.configManager, this.clientWindowManager);
        this.accountManager.init();

        // Integration
        this.rotationManager = new RotationManager();

        this.serverListManager = new ServerListManager(this.runDirectory);
        this.serverListManager.loadConfig();

        this.friendsManager = new FriendsManager(this.configManager, this.clientWindowManager);

        Shaders.loadAll();

        if (System.getProperty("vandalism.no_connections") == null) {
            NameGenerationUtil.loadUsernameParts();
        }

        this.hudManager = new HUDManager(this.configManager, this.clientWindowManager, this.runDirectory);
        this.hudManager.init();

        // Features
        this.moduleManager = new ModuleManager(this.eventSystem, this.configManager, this.clientWindowManager);
        this.moduleManager.init();

        this.commandManager = new CommandManager();
        this.commandManager.init();

        // After system init since this manager has many cross-usages
        this.clientWindowManager.init();

        VandalismAddonLauncher.call(addon -> addon.onLaunch(this));

        // We have to load the config files after all systems have been initialized
        this.configManager.init();

        // After setting load
        this.clientWindowManager.load(this.clientSettings);

        mc.getWindow().setTitle(FabricBootstrap.WINDOW_TITLE);

        VandalismAddonLauncher.call(addon -> addon.onLateLaunch(this));

        this.startTime = GLFW.glfwGetTime();
        FabricBootstrap.INITIALIZED = true;

        this.logger.info("");
        this.logger.info("Done!");
        this.logger.info("");
    }

    @Override
    public void onShutdownProcess() {
        FabricBootstrap.SHUTTING_DOWN = true;
        Vandalism.getInstance().getLogger().info("Shutting down...");
        this.configManager.save();
    }

}
