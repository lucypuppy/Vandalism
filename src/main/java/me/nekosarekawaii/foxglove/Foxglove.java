package me.nekosarekawaii.foxglove;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.config.ConfigManager;
import me.nekosarekawaii.foxglove.creativetab.CreativeTabRegistry;
import me.nekosarekawaii.foxglove.event.*;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandRegistry;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleRegistry;
import me.nekosarekawaii.foxglove.gui.imgui.ImGUIMenu;
import me.nekosarekawaii.foxglove.gui.imgui.impl.MainMenu;
import me.nekosarekawaii.foxglove.util.imgui.ImGuiRenderer;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;

public class Foxglove implements MinecraftWrapper, ClientListener, KeyboardListener, TickListener, Render2DListener, ScreenListener {

    private final static Foxglove instance = new Foxglove();

    public static Foxglove getInstance() {
        return instance;
    }

    private final String name, lowerCaseName, version, author, windowTitle;

    private final Color color;
    private final int colorRGB;

    private final Logger logger;

    private final File dir;

    private final boolean firstStart, jvmDebugMode;

    private ModuleRegistry moduleRegistry;
    private CommandRegistry commandRegistry;
    private CreativeTabRegistry creativeTabRegistry;

    private ConfigManager configManager;

    public boolean blockKeyEvent;

    public ImGuiRenderer imGuiRenderer;

    private ImGUIMenu currentImGUIMenu;

    public Foxglove() {
        this.name = "Foxglove";
        this.lowerCaseName = this.name.toLowerCase();
        this.version = "1.0.0";
        this.author = "NekosAreKawaii";
        this.color = Color.MAGENTA;
        this.colorRGB = this.color.getRGB();
        this.logger = LoggerFactory.getLogger(this.name);
        this.dir = new File(mc().runDirectory, this.lowerCaseName);
        this.firstStart = !this.dir.exists();
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
                this.author,
                this.jvmDebugMode ? " - JVM Debug Mode" : ""
        );
        this.blockKeyEvent = false;
        this.creativeTabRegistry = new CreativeTabRegistry();
        DietrichEvents2.global().subscribe(ClientEvent.ID, this);
        DietrichEvents2.global().subscribe(OpenScreenEvent.ID, this);
    }

    private void start() {
        this.logger.info("Starting...");
        this.logger.info("Version: {}", this.version);
        this.logger.info("Made by {}", this.author);

        this.logger.info("Loading Features...");
        this.moduleRegistry = new ModuleRegistry();
        this.commandRegistry = new CommandRegistry();
        this.logger.info("Features loaded.");

        this.logger.info("Loading ImGUI Renderer...");
        this.imGuiRenderer = new ImGuiRenderer(this.dir);
        this.logger.info("ImGUI Renderer loaded.");

        this.configManager = new ConfigManager();
        this.configManager.load();

        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(TickEvent.ID, this);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private void stop() {
        this.logger.info("Stopping...");
        this.configManager.save();
    }

    @Override
    public void onStart() {
        this.start();
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        if (this.currentImGUIMenu != null && !this.currentImGUIMenu.keyPress(key, scanCode, modifiers)) {
            return;
        }
        if (this.configManager.getMainConfig().getMainMenuKeyCode() == key) {
            if (!(this.currentImGUIMenu instanceof MainMenu)) {
                this.setCurrentImGUIMenu(new MainMenu());
            }
            return;
        }
        if (this.blockKeyEvent || mc().currentScreen != null) return;
        final ClientPlayNetworkHandler playNetworkHandler = mc().getNetworkHandler();
        if (playNetworkHandler != null) {
            this.configManager.getMainConfig().getChatMacros().object2ObjectEntrySet().fastForEach(macro -> {
                if (macro.getValue() == key) {
                    if (macro.getKey().startsWith("/")) playNetworkHandler.sendChatCommand(macro.getKey());
                    else playNetworkHandler.sendChatMessage(macro.getKey());
                }
            });
        }
    }

    @Override
    public void onTick() {
        if (this.currentImGUIMenu != null) this.currentImGUIMenu.tick();
    }

    private void renderImGuiContext() {
        if (this.currentImGUIMenu != null) {
            this.imGuiRenderer.addRenderInterface(io -> this.currentImGUIMenu.render(io));
        }

        this.imGuiRenderer.render();
    }

    @Override
    public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (mc().currentScreen != null) {
            this.renderImGuiContext();
        }
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
        if (mc().currentScreen == null) {
            this.renderImGuiContext();
        }
    }

    @Override
    public void onOpenScreen(final OpenScreenEvent event) {
        if (event.screen instanceof TitleScreen) {
            event.cancel();
            mc().setScreen(new me.nekosarekawaii.foxglove.gui.screen.TitleScreen());
        }
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

    public String getAuthor() {
        return this.author;
    }

    public Color getColor() {
        return this.color;
    }

    public int getColorRGB() {
        return this.colorRGB;
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

    public ImGUIMenu getCurrentImGUIMenu() {
        return this.currentImGUIMenu;
    }

    public void setCurrentImGUIMenu(final ImGUIMenu currentImGUIMenu) {
        this.currentImGUIMenu = currentImGUIMenu;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

}
