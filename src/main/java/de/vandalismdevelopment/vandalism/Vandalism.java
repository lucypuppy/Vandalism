package de.vandalismdevelopment.vandalism;

import de.vandalismdevelopment.vandalism.config.ConfigManager;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.command.CommandRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptRegistry;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiHandler;
import de.vandalismdevelopment.vandalism.util.rotation.RotationListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class Vandalism {

    private final static Vandalism INSTANCE = new Vandalism();

    public static Vandalism getInstance() {
        return INSTANCE;
    }

    private final String id, name, version, windowTitle, author;

    private final Logger logger;

    private File dir;

    private CreativeTabRegistry creativeTabRegistry;

    private ImGuiHandler imGuiHandler;

    private ScriptRegistry scriptRegistry;

    private ModuleRegistry moduleRegistry;

    private CommandRegistry commandRegistry;

    private ConfigManager configManager;

    private RotationListener rotationListener;

    public Vandalism() {
        this.id = "vandalism";
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(this.id);
        final ModMetadata modMetadata = modContainer.get().getMetadata();
        this.name = modMetadata.getName();
        this.author = modMetadata.getAuthors().stream().findFirst().get().getName();
        this.version = modMetadata.getVersion().getFriendlyString();
        this.logger = LoggerFactory.getLogger(this.name);
        this.windowTitle = String.format(
                "%s v%s made by %s",
                this.name,
                this.version,
                this.author
        );
    }

    public void start(final MinecraftClient mc) {
        mc.getWindow().setTitle(String.format("Starting %s ...", this.windowTitle));
        this.logger.info("Starting {} ...", this.windowTitle);
        this.dir = new File(mc.runDirectory, this.id);
        this.dir.mkdirs();
        this.creativeTabRegistry = new CreativeTabRegistry();
        this.imGuiHandler = new ImGuiHandler(this.dir);
        this.configManager = new ConfigManager(this.dir);
        this.scriptRegistry = new ScriptRegistry(this.dir);
        this.rotationListener = new RotationListener();
        this.moduleRegistry = new ModuleRegistry();
        this.commandRegistry = new CommandRegistry();
        this.configManager.load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        this.logger.info("Done!");
        mc.getWindow().setTitle(this.windowTitle);
    }

    private void stop() {
        this.logger.info("Stopping...");
        this.configManager.save();
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

    public CreativeTabRegistry getCreativeTabRegistry() {
        return this.creativeTabRegistry;
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

}
