package de.nekosarekawaii.foxglove;

import de.nekosarekawaii.foxglove.config.ConfigManager;
import de.nekosarekawaii.foxglove.creativetab.CreativeTabRegistry;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandRegistry;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleRegistry;
import de.nekosarekawaii.foxglove.gui.imgui.ImGuiHandler;
import de.nekosarekawaii.foxglove.util.rotation.RotationListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Foxglove {

    private final static Foxglove instance = new Foxglove();

    public static Foxglove getInstance() {
        return instance;
    }

    private final String name, lowerCaseName, version, windowTitle, authorsString;

    private final static String FALLBACK_VERSION = "1337", FALLBACK_AUTHOR = "NekosAreKawaii";

    private final Logger logger;

    private File dir;

    private CreativeTabRegistry creativeTabRegistry;

    private ModuleRegistry moduleRegistry;

    private CommandRegistry commandRegistry;

    private ConfigManager configManager;

    private ImGuiHandler imGuiHandler;

    private RotationListener rotationListener;

    public Foxglove() {
        this.name = "Foxglove";
        this.lowerCaseName = this.name.toLowerCase();
        final String modVersionString;
        final Collection<String> modAuthors;
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(this.lowerCaseName);
        if (modContainer.isPresent()) {
            final ModMetadata modMetadata = modContainer.get().getMetadata();
            modVersionString = modMetadata.getVersion().getFriendlyString();
            modAuthors = modMetadata.getAuthors().stream().map(Person::getName).toList();
        } else {
            modVersionString = FALLBACK_VERSION;
            modAuthors = List.of(FALLBACK_AUTHOR);
        }
        this.version = modVersionString.equals("${version}") ? FALLBACK_VERSION : modVersionString;
        this.authorsString = String.join(", ", modAuthors);
        this.logger = LoggerFactory.getLogger(this.name);
        this.windowTitle = String.format(
                "%s made by %s",
                this.name,
                this.authorsString
        );
    }

    public void start(final MinecraftClient mc) {
        mc.getWindow().setTitle(this.windowTitle + " | Starting...");
        this.logger.info("Starting...");
        this.logger.info("Version: {}", this.version);
        this.logger.info("Made by {}", this.authorsString);
        this.dir = new File(mc.runDirectory, this.lowerCaseName);
        this.dir.mkdirs();
        this.creativeTabRegistry = new CreativeTabRegistry();
        this.rotationListener = new RotationListener();
        this.moduleRegistry = new ModuleRegistry();
        this.commandRegistry = new CommandRegistry();
        this.imGuiHandler = new ImGuiHandler(this.dir);
        this.configManager = new ConfigManager();
        this.configManager.load();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        this.logger.info("Done!");
        mc.getWindow().setTitle(this.windowTitle);
    }

    private void stop() {
        this.logger.info("Stopping...");
        this.configManager.save();
    }

    public String getName() {
        return this.name;
    }

    public String getLowerCaseName() {
        return this.lowerCaseName;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthorsAsString() {
        return this.authorsString;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public File getDir() {
        return this.dir;
    }

    public ModuleRegistry getModuleRegistry() {
        return this.moduleRegistry;
    }

    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    public CreativeTabRegistry getCreativeTabRegistry() {
        return this.creativeTabRegistry;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ImGuiHandler getImGuiHandler() {
        return this.imGuiHandler;
    }

    public RotationListener getRotationListener() {
        return this.rotationListener;
    }

}
