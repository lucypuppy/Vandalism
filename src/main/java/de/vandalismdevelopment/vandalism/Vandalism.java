package de.vandalismdevelopment.vandalism;

import de.vandalismdevelopment.vandalism.account_v2.AccountManager;
import de.vandalismdevelopment.vandalism.config.ConfigManager;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import de.vandalismdevelopment.vandalism.enhancedserverlist.ServerListManager;
import de.vandalismdevelopment.vandalism.feature.impl.command.CommandRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptRegistry;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiHandler;
import de.vandalismdevelopment.vandalism.gui.ingame.CustomHUDRenderer;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.RotationListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * TODO
 *  - Rewrite Config class file to support JsonElement type (e.g. JsonArray)
 *  - Reload signature verifier, realms reloader and gameProfileFuture (see AbstractAccount)
 *  - Delete GlfwKeyName and replace with InputUtil
 *  - Clean packages to sort classes by system reference instead of type reference
 *  - Get rid of ImGuiMenuCategoryRegistry
 *  - Clean value system
 *  - Clean event system package to prevent overusing listener classes
 *  - Delete EncryptionUtil
 *  - Delete MinecraftUtil and MinecraftWrapper
 *  - Apply checkstyle.xml to all classes
 *  - Rewrite Vandalism#start
 *  - Delete HUDElementAlignment
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
 *  - Add ImLoader, ImGuiExtensions and ImGuiFontHack
 *  - Add ImGui error handling redirectors (see ImGuiPlatformIO)
 */
public class Vandalism {

    private final static Vandalism INSTANCE = new Vandalism();

    private final String id, name, authors, version, windowTitle;
    private final Identifier logo;
    private final Logger logger;

    private File dir;
    private ImGuiHandler imGuiHandler;
    private ScriptRegistry scriptRegistry;
    private ModuleRegistry moduleRegistry;
    private CommandRegistry commandRegistry;
    private ConfigManager configManager;
    private RotationListener rotationListener;
    private CustomHUDRenderer customHUDRenderer;
    private ServerListManager serverListManager;
    private AccountManager accountManager;

    public Vandalism() {
        final ModMetadata data = FabricLoader.getInstance().getModContainer(this.id = "vandalism").get().getMetadata();
        this.name = data.getName();
        this.authors = String.join(", ", data.getAuthors().stream().map(Person::getName).toArray(String[]::new));
        this.version = data.getVersion().getFriendlyString();
        this.windowTitle = String.format("%s v%s made by %s", this.name, this.version, this.authors);
        this.logo = new Identifier(this.id, "textures/logo.png");
        this.logger = LoggerFactory.getLogger(this.name);
    }

    private final static String[] ASCII_ART = {
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

    private void printAsciiArtTrimLine() {
        this.logger.info("=".repeat(ASCII_ART[0].length() + 15));
    }

    public void start(final Window window, final File runDirectory) {
        this.logger.info("");
        this.printAsciiArtTrimLine();
        for (final String line : ASCII_ART) this.logger.info(line);
        this.logger.info(this.windowTitle.replaceFirst(this.name, " ".repeat(15)));
        this.printAsciiArtTrimLine();
        this.logger.info("");
        this.logger.info("Starting...");
        window.setTitle(String.format("Starting %s...", this.windowTitle));
        this.dir = new File(runDirectory, this.id);
        this.dir.mkdirs();
        CreativeTabRegistry.getInstance().register();
        this.imGuiHandler = new ImGuiHandler(window.getHandle(), this.dir);
        this.configManager = new ConfigManager(this.dir);
        this.scriptRegistry = new ScriptRegistry(this.dir);
        this.rotationListener = new RotationListener();
        this.moduleRegistry = new ModuleRegistry();
        this.commandRegistry = new CommandRegistry();
        this.serverListManager = new ServerListManager(this.dir);
        this.serverListManager.loadConfig();
        this.accountManager = new AccountManager();
        this.accountManager.init();
        this.customHUDRenderer = new CustomHUDRenderer();
        this.configManager.load();
        this.logger.info("Done!");
        this.logger.info("");
        window.setTitle(this.windowTitle);
    }

    public void stop() {
        this.logger.info("");
        this.logger.info("Saving...");
        this.configManager.save();
        this.logger.info("Done!");
        this.logger.info("");
    }

    public static Vandalism getInstance() {
        return INSTANCE;
    }

    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getVersion() {
        return this.version;
    }
    public String getAuthors() {
        return this.authors;
    }

    public Identifier getLogo() {
        return this.logo;
    }
    public Logger getLogger() {
        return this.logger;
    }
    public File getDir() {
        return this.dir;
    }
    public ImGuiHandler getImGuiHandler() {
        return this.imGuiHandler;
    }
    public ScriptRegistry getScriptRegistry() {
        return this.scriptRegistry;
    }
    public ModuleRegistry getModuleRegistry() {
        return this.moduleRegistry;
    }
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
    public RotationListener getRotationListener() {
        return this.rotationListener;
    }
    public CustomHUDRenderer getCustomHUDRenderer() {
        return this.customHUDRenderer;
    }
    public ServerListManager getServerListManager() {
        return this.serverListManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }
}
