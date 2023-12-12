package de.vandalismdevelopment.vandalism;

import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import de.vandalismdevelopment.vandalism.base.account.AccountManager;
import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.config.ConfigManager;
import de.vandalismdevelopment.vandalism.base.event.game.MinecraftBoostrapListener;
import de.vandalismdevelopment.vandalism.base.event.game.ShutdownProcessListener;
import de.vandalismdevelopment.vandalism.feature.command.CommandManager;
import de.vandalismdevelopment.vandalism.feature.module.ModuleManager;
import de.vandalismdevelopment.vandalism.feature.script.ScriptManager;
import de.vandalismdevelopment.vandalism.gui.ImGuiManager;
import de.vandalismdevelopment.vandalism.integration.hud.HUDManager;
import de.vandalismdevelopment.vandalism.integration.rotation.RotationListener;
import de.vandalismdevelopment.vandalism.integration.serverlist.ServerListManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO | Recode
 *  - Fix MixinMinecraftClient screen event
 *  - When changing the module mode of speed/flight/nofall for example from the default mode to another mode it removes
 *    the default mode from the list because of some reason
 *  - Fix game crash when binding modules to keys -> KeyNameArgumentType.java:22 throws NullPointerException
 *  - Make account manager multi threaded (to prevent the game from freezing)
 *  - Fix module list hud element not syncing module states by creating a module toggle event
 *  - Re-implement last session login for the account manager
 *  - Re-add DebugModule as hud element
 *  - Re-implement im window state saving
 *  - Delete MixinParticleManager
 *  - Update AuthLib array instead of MixinTextureUrlChecker
 *  - Fix mixin injection names for the fields (remove vandalism$ for every function)
 *  - Replace MixinServerResourcePackProvider
 *  - Delete MixinClientPlayerEntity & MixinClientWorld
 *  - Delete events which only have one usage
 *  - Delete CustomRPConfirmScreen
 * <p>
 * TODO | Common skill issues
 *  - Apply checkstyle.xml to all classes
 *  - Fix tick bug (cps is not accurate) -> see BoxMuellerClicker#update
 *  - Delete MixinGameRenderer view bobbing -> use proper MixinExtras instead of copy-pasting game code
 *  - Rewrite EnhancedServerList
 *  - Add anti vanish via. the player list hud
 *  - Add the dripping stone block to the block density module
 *  - Fix the entity layer rendering from the true sight module
 *  - Fix offsets for the new "teleport" method in the fov fucker module
 *  - Fix spaces in the text rendering when using the "deutsch macher" module
 *  - Protector Module:
 *      - Add protection for custom rank prefixes
 *      - Add protection for skins
 *      - Add protection for coords
 *      - Maybe use a chat event instead of a text draw event
 *  - Make NullPointerException crash fix for the particle tracking system from the visual throttle module
 *  - Add Proxy manager
 *  - Make the width and height customizable or use calculations in the modules im window for the tabs
 *  - Fix ImGui#begin in every im window (remove No Collapse Flag and move the code out of the if)
 *  - Fix module tabs display (no stacking) when the mod starts the first time
 *  - Fix calculations for the custom hud
 *  - Change skin texture to head texture in the account manager
 *  - Fix the sprint event (attacking is wrong when sprintEvent.force = true)
 */
public class Vandalism implements MinecraftBoostrapListener, ShutdownProcessListener {

    private static final Vandalism instance = new Vandalism();
    private final Logger logger = LoggerFactory.getLogger(FabricBootstrap.MOD_NAME);
    private File runDirectory;

    // Base handlers
    private ConfigManager configManager;
    private ImGuiManager imGuiManager;
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

    public void printStartup() {
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
        final String spacer = "=".repeat(ASCII_ART[0].length() + 15);
        logger.info("");
        logger.info(spacer);

        for (final String line : ASCII_ART) {
            logger.info(line);
        }
        logger.info(FabricBootstrap.WINDOW_TITLE.replaceFirst(FabricBootstrap.MOD_NAME, " ".repeat(15)));

        logger.info(spacer);
        logger.info("");
    }

    @Override
    public void onBootstrapGame(MinecraftClient mc) {
        printStartup();
        mc.getWindow().setTitle(String.format("Starting %s...", FabricBootstrap.WINDOW_TITLE));

        // Base handlers
        logger.info("Schmuse Katze hasst diesen Trick!");

        FabricBootstrap.MOD_ICON = new Identifier(FabricBootstrap.MOD_ID, "textures/logo.png");

        runDirectory = new File(runDirectory, FabricBootstrap.MOD_ID);
        runDirectory.mkdirs();

        configManager = new ConfigManager();

        imGuiManager = new ImGuiManager(runDirectory);
        imGuiManager.init();

        clientSettings = new ClientSettings(configManager, imGuiManager);
        accountManager = new AccountManager(configManager, imGuiManager);
        accountManager.init();

        // Integration
        rotationListener = new RotationListener();
        serverListManager = new ServerListManager(runDirectory);
        serverListManager.loadConfig();
        hudManager = new HUDManager(configManager, imGuiManager);
        hudManager.init();

        // Features
        moduleManager = new ModuleManager(configManager, imGuiManager);
        moduleManager.init();

        commandManager = new CommandManager();
        commandManager.init();

        scriptManager = new ScriptManager(configManager, imGuiManager, runDirectory);
        scriptManager.init();

        // We have to load the config files after all systems have been initialized
        configManager.init();
        mc.getWindow().setTitle(FabricBootstrap.WINDOW_TITLE);
    }

    @Override
    public void onShutdownProcess() {
        configManager.save();
    }

    public static Vandalism getInstance() {
        return instance;
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

    public ImGuiManager getImGuiManager() {
        return imGuiManager;
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
