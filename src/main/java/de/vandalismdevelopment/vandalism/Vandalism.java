package de.vandalismdevelopment.vandalism;

import de.vandalismdevelopment.vandalism.config.ConfigManager;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.command.CommandRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptRegistry;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiHandler;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation.RotationListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Vandalism {

    private final static Vandalism INSTANCE = new Vandalism();

    private final String id, name, version, windowTitle, author;
    private final Logger logger;

    private File dir;
    private ImGuiHandler imGuiHandler;
    private ScriptRegistry scriptRegistry;
    private ModuleRegistry moduleRegistry;
    private CommandRegistry commandRegistry;
    private ConfigManager configManager;
    private RotationListener rotationListener;
    private Identifier logo;

    public Vandalism() {
        final ModMetadata data = FabricLoader.getInstance().getModContainer(this.id = "vandalism").get().getMetadata();
        this.name = data.getName();
        this.author = String.join(", ", data.getAuthors().stream().map(Person::getName).toArray(String[]::new));
        this.version = data.getVersion().getFriendlyString();
        this.logger = LoggerFactory.getLogger(this.name);
        this.windowTitle = String.format("%s v%s made by %s", this.name, this.version, this.author);
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
        this.logger.info(this.windowTitle.replaceFirst(this.name, " ".repeat(25)));
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
        this.configManager.load();
        this.logo = new Identifier(this.id, "textures/logo.png");
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        window.setTitle(this.windowTitle);
        this.logger.info("Done!");
        this.logger.info("");
    }

    private void stop() {
        this.logger.info("Stopping...");
        this.configManager.save();
        this.logger.info("");
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

    public String getAuthor() {
        return this.author;
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

    public Identifier getLogo() {
        return this.logo;
    }

    public static Vandalism getInstance() {
        return INSTANCE;
    }

}
