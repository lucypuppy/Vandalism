package me.nekosarekawaii.foxglove;

import me.nekosarekawaii.foxglove.config.ConfigManager;
import me.nekosarekawaii.foxglove.creativetab.CreativeTabRegistry;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandRegistry;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleRegistry;
import me.nekosarekawaii.foxglove.gui.imgui.ImGuiHandler;
import me.nekosarekawaii.foxglove.util.CustomServerList;
import me.nekosarekawaii.foxglove.util.minecraft.FormattingUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Collection;

public class Foxglove {

    private final static Foxglove instance = new Foxglove();

    public static Foxglove getInstance() {
        return instance;
    }

    private final String name, lowerCaseName, version, windowTitle;
    private final Collection<String> authors;
    private final Text clientNameText;

    private final Color color1, color2;

    private final Logger logger;

    private final File dir;

    private final boolean firstStart, jvmDebugMode;

    private final CreativeTabRegistry creativeTabRegistry;

    private ModuleRegistry moduleRegistry;

    private CommandRegistry commandRegistry;

    private ConfigManager configManager;

    private ImGuiHandler imGuiHandler;

    private CustomServerList selectedServerList = null; //null means default serverlist;

    public Foxglove() {
        this.name = "Foxglove";
        this.lowerCaseName = this.name.toLowerCase();

        final var modContainer = FabricLoader.getInstance().getModContainer(this.lowerCaseName).get().getMetadata();
        this.version = modContainer.getVersion().getFriendlyString();
        this.authors = modContainer.getAuthors().stream().map(Person::getName).toList();
        this.color1 = Color.MAGENTA;
        this.color2 = Color.PINK;
        this.clientNameText = FormattingUtils.interpolateTextColor(this.name, this.color1, this.color2);

        this.logger = LoggerFactory.getLogger(this.name);
        this.dir = new File(MinecraftClient.getInstance().runDirectory, this.lowerCaseName);
        this.firstStart = !this.dir.exists(); //TODO: Make better first start check.
        if (this.firstStart) {
            if (!this.dir.mkdirs()) {
                this.logger.error("Failed to create Mod directory!");
                System.exit(-1);
            }
        }
        this.jvmDebugMode = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("jdwp");
        this.windowTitle = String.format(
                "%s %s made by %s%s",
                this.name,
                this.version,
                String.join(", ", this.authors),
                this.jvmDebugMode ? " - JVM Debug Mode" : ""
        );
        this.creativeTabRegistry = new CreativeTabRegistry();
    }

    public void start() {
        this.logger.info("Starting...");
        this.logger.info("Version: {}", this.version);
        this.logger.info("Made by {}", String.join(", ", this.authors));

        this.logger.info("Loading Features...");
        this.moduleRegistry = new ModuleRegistry();
        this.commandRegistry = new CommandRegistry();
        this.logger.info("Features loaded.");

        this.logger.info("Loading ImGui...");
        this.imGuiHandler = new ImGuiHandler(this.dir);
        this.logger.info("ImGui loaded.");

        this.configManager = new ConfigManager();
        this.configManager.load();

        FabricBridge.modInitialized = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
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
        return authors;
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

    public boolean isJvmDebugMode() {
        return this.jvmDebugMode;
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

    public CustomServerList getSelectedServerList() {
        return selectedServerList;
    }

    public void setSelectedServerList(final CustomServerList selectedServerList) {
        this.selectedServerList = selectedServerList;
    }

}
