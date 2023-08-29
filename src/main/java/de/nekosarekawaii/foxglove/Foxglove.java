package de.nekosarekawaii.foxglove;

import de.nekosarekawaii.foxglove.config.ConfigManager;
import de.nekosarekawaii.foxglove.creativetab.CreativeTabRegistry;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandRegistry;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleRegistry;
import de.nekosarekawaii.foxglove.gui.imgui.ImGuiHandler;
import de.nekosarekawaii.foxglove.util.NativeInputHook;
import de.nekosarekawaii.foxglove.util.minecraft.FormattingUtils;
import de.nekosarekawaii.foxglove.util.rotation.RotationListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.util.Collection;

public class Foxglove {

    private final static Foxglove instance = new Foxglove();

    public static Foxglove getInstance() {
        return instance;
    }

    private final String name, lowerCaseName, version, windowTitle, authorsString;
    private final Collection<String> authors;
    private final Text clientNameText;

    private final Logger logger;

    private final File dir;

    private final boolean firstStart;

    private final CreativeTabRegistry creativeTabRegistry;

    private ModuleRegistry moduleRegistry;

    private CommandRegistry commandRegistry;

    private ConfigManager configManager;

    private ImGuiHandler imGuiHandler;

    private NativeInputHook nativeInputHook;

    private RotationListener rotationListener;

    public Foxglove() {
        this.name = "Foxglove";
        this.lowerCaseName = this.name.toLowerCase();

        final var modContainer = FabricLoader.getInstance().getModContainer(this.lowerCaseName).get().getMetadata();
        final String ver = modContainer.getVersion().getFriendlyString();
        this.version = ver.equals("${version}") ? "1337" : ver;
        this.authors = modContainer.getAuthors().stream().map(Person::getName).toList();
        this.authorsString = String.join(", ", this.authors);
        this.clientNameText = FormattingUtils.interpolateTextColor(this.name, Color.MAGENTA, Color.PINK);

        this.logger = LoggerFactory.getLogger(this.name);
        this.dir = new File(MinecraftClient.getInstance().runDirectory, this.lowerCaseName);
        this.firstStart = !this.dir.exists(); //TODO: Make better first start check.
        if (this.firstStart) {
            if (!this.dir.mkdirs()) {
                this.logger.error("Failed to create Mod directory!");
                System.exit(-1);
            }
        }

        this.windowTitle = String.format(
                "%s made by %s",
                this.name,
                this.authorsString
        );
        this.creativeTabRegistry = new CreativeTabRegistry();
    }

    public void start() {
        this.logger.info("Starting...");
        this.logger.info("Version: {}", this.version);
        this.logger.info("Made by {}", String.join(", ", this.authors));

        this.logger.info("Loading Features...");
        this.rotationListener = new RotationListener();
        this.moduleRegistry = new ModuleRegistry();
        this.commandRegistry = new CommandRegistry();
        this.logger.info("Features loaded.");

        this.logger.info("Loading ImGui...");
        this.imGuiHandler = new ImGuiHandler(this.dir);

        this.logger.info("Registering native input hook...");
        this.nativeInputHook = new NativeInputHook();

        this.logger.info("Loading configs...");
        this.configManager = new ConfigManager();
        this.configManager.load();

        FabricBridge.modInitialized = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        this.logger.info("Done!");
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

    public Collection<String> getAuthors() {
        return this.authors;
    }

    public String getAuthorsAsString() {
        return this.authorsString;
    }

    public Text getClientNameText() {
        return clientNameText;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public File getDir() {
        return this.dir;
    }

    public boolean isFirstStart() {
        return this.firstStart;
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

    public String getWindowTitle() {
        return this.windowTitle;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public ImGuiHandler getImGuiHandler() {
        return this.imGuiHandler;
    }

    public NativeInputHook getNativeInputHook() {
        return this.nativeInputHook;
    }

    public RotationListener getRotationListener() {
        return rotationListener;
    }

}
