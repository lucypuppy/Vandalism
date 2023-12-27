package de.nekosarekawaii.vandalism;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.account.AccountManager;
import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.game.MinecraftBoostrapListener;
import de.nekosarekawaii.vandalism.base.event.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.feature.command.CommandManager;
import de.nekosarekawaii.vandalism.feature.creativetab.CreativeTabManager;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.feature.script.ScriptManager;
import de.nekosarekawaii.vandalism.integration.hud.HUDManager;
import de.nekosarekawaii.vandalism.integration.rotation.RotationListener;
import de.nekosarekawaii.vandalism.integration.serverlist.ServerListManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO | Verschlxfene
 *  - Re-add DebugModule as hud element
 *  - Apply checkstyle.xml to all classes
 *  - Delete MixinParticleManager
 *  - Replace MixinServerResourcePackProvider
 *  - Delete CustomRPConfirmScreen
 *  - Add Proxy manager
 *  - Add JiJ and JiS for building/exporting the mod
 * <p>
 * TODO | Everyone else
 *  - Fix tick bug (cps is not accurate) -> see BoxMuellerClicker#onUpdate
 *  - Rewrite EnhancedServerList
 *  - Add anti vanish via. the player list hud
 *  - Fix the entity layer rendering from the true sight module
 *  - Fix offsets for the new "teleport" method in the fov fucker module
 *  - Fix spaces in the text rendering when using the "deutsch macher" module
 *  - Protector Module:
 *      - Add protection for custom rank prefixes
 *      - Add protection for skins
 *      - Add protection for coords
 *      - Maybe use a chat event instead of a text draw event
 *  - Make the width and height customizable or use calculations in the modules im window for the tabs
 *  - Fix ImGui#begin in every im window (remove No Collapse Flag and move the code out of the if)
 *  - Fix module tabs display (no stacking) when the mod starts the first time
 *  - Fix calculations for the custom hud
 *  - Fix ClientMenuScreen#close because it could break the entire game
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
        this.clientMenuManager.init();

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

        for (VandalismAddonLauncher entrypoint : FabricLoader.getInstance().getEntrypoints(VandalismAddonLauncher.getEntrypointName(), VandalismAddonLauncher.class)) {
            entrypoint.onLaunch(this);
        }

        // We have to load the config files after all systems have been initialized
        this.configManager.init();

        this.logger.info("");
        this.logger.info("Done!");
        this.logger.info("");

        mc.getWindow().setTitle(FabricBootstrap.WINDOW_TITLE);
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
