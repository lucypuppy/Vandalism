package de.vandalismdevelopment.vandalism;

import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import de.vandalismdevelopment.vandalism.base.account.AccountManager;
import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.config.ConfigManager;
import de.vandalismdevelopment.vandalism.gui_v2.ImGuiManager;
import de.vandalismdevelopment.vandalism.integration.serverlist.ServerListManager;
import de.vandalismdevelopment.vandalism.base.event.game.MinecraftBoostrapListener;
import de.vandalismdevelopment.vandalism.base.event.game.ShutdownProcessListener;
import de.vandalismdevelopment.vandalism.feature.command.CommandManager;
import de.vandalismdevelopment.vandalism.feature.module.ModuleManager;
import de.vandalismdevelopment.vandalism.feature.script.ScriptManager;
import de.vandalismdevelopment.vandalism.integration.hud.HUDManager;
import de.vandalismdevelopment.vandalism.integration.rotation.RotationListener;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO
 *  - Delete GlfwKeyName and replace with InputUtil
 *  - Clean event system package to prevent overusing listener classes
 *  - Apply checkstyle.xml to all classes
 *  - Fix tick bug (cps is not accurate) -> see BoxMuellerClicker#update
 *  - Fix module tabs display (no stacking) when the mod starts the first time.
 *  - Replace MixinServerResourcePackProvider
 *  - MixinClientConnection is broken (recall method using boolean)
 *  - Delete MixinClientPlayerEntity & MixinClientWorld
 *  - Delete events which only have one usage
 *  - Fix MixinMinecraftClient screen event
 *  - Take a look into command system and deduplicate code
 *  - Delete CustomRPConfirmScreen
 *  - Delete MixinGameRenderer view bobbing -> use proper MixinExtras instead of copy-pasting game code
 *  - Rewrite EnhancedServerList
 *  - Delete MixinIdentifier as it breaks game code
 *  - Delete MixinParticleManager
 *  - Delete MixinSodiumWorldRenderer, Sodium has merged this fix into their codebase
 *  - Update AuthLib array instead of MixinTextureUrlChecker
 *  - Readd DebugModule as ImWindow
 *  - Fix forceSort by creating a module toggle event
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

    // Features
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ScriptManager scriptManager;

    // Integration
    private RotationListener rotationListener;
    private ServerListManager serverListManager;
    private HUDManager hudManager;

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
        this.logger.info("");
        this.logger.info(spacer);

        for (final String line : ASCII_ART) {
            this.logger.info(line);
        }
        this.logger.info(FabricBootstrap.WINDOW_TITLE.replaceFirst(FabricBootstrap.MOD_NAME, " ".repeat(15)));

        this.logger.info(spacer);
        this.logger.info("");
    }

    @Override
    public void onBootstrapGame(MinecraftClient mc) {
        printStartup();
        this.logger.info("Salvete amicus, et vale! Quid agis?");
        mc.getWindow().setTitle(String.format("Starting %s...", FabricBootstrap.WINDOW_TITLE));

        // Base handlers
        this.runDirectory = new File(runDirectory, FabricBootstrap.MOD_ID);
        this.runDirectory.mkdirs();

        this.configManager = new ConfigManager();
        this.imGuiManager = new ImGuiManager(this.runDirectory);
        this.clientSettings = new ClientSettings(this.configManager, this.imGuiManager);
        this.accountManager = new AccountManager(this.configManager);
        this.accountManager.init();
        
        // Features
        this.moduleManager = new ModuleManager(configManager);
        this.commandManager = new CommandManager();
        this.scriptManager = new ScriptManager(configManager, this.runDirectory);

        // Integration
        this.rotationListener = new RotationListener();
        this.serverListManager = new ServerListManager(this.runDirectory);
        this.serverListManager.loadConfig();
        this.hudManager = new HUDManager(this.configManager, this.imGuiManager);
        this.hudManager.init();

        // We have to load the config files after all systems have been initialized
        this.configManager.init();
        mc.getWindow().setTitle(FabricBootstrap.WINDOW_TITLE);
    }

    @Override
    public void onShutdownProcess() {
        this.configManager.save();
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
