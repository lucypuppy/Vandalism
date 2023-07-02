package me.nekosarekawaii.foxglove;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.nekosarekawaii.foxglove.config.ConfigManager;
import me.nekosarekawaii.foxglove.config.impl.MainConfig;
import me.nekosarekawaii.foxglove.event.ClientListener;
import me.nekosarekawaii.foxglove.event.KeyboardListener;
import me.nekosarekawaii.foxglove.event.RenderListener;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.feature.FeatureRegistry;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandHandler;
import me.nekosarekawaii.foxglove.gui.imgui.ImGUIMenu;
import me.nekosarekawaii.foxglove.gui.imgui.impl.MainMenu;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * The Foxglove class represents the main entry point for the Foxglove mod. It implements various event listeners
 * and handles the initialization and shutdown of the mod.
 */
public final class Foxglove implements MinecraftWrapper, ClientListener, KeyboardListener, TickListener, RenderListener {

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

	private FeatureRegistry features;
	private CommandHandler commandHandler;

	private ConfigManager configManager;

	public boolean blockKeyEvent;

	private ImGuiImplGl3 imGuiImplGl3;
	private ImGuiImplGlfw imGuiImplGlfw;
	private ImGUIMenu currentImGUIMenu;

	/**
	 * Constructs a new instance of the Foxglove mod.
	 */
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
		DietrichEvents2.global().subscribe(ClientEvent.ID, this);
	}

	/**
	 * Starts the Foxglove mod. Initializes various components and registers event listeners.
	 */
	private void start() {
		this.logger.info("Starting...");
		this.logger.info("Version: {}", this.version);
		this.logger.info("Made by {}", this.author);
		this.logger.info("Loading Features...");
		this.features = new FeatureRegistry();
		this.commandHandler = new CommandHandler();
		this.logger.info("Features loaded.");
		this.logger.info("Loading ImGUI Renderer...");
		this.imGuiImplGl3 = new ImGuiImplGl3();
		this.imGuiImplGlfw = new ImGuiImplGlfw();
		ImGui.createContext();
		ImPlot.createContext();
		final ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setConfigFlags(ImGuiConfigFlags.DockingEnable);
		imGuiIO.setFontGlobalScale(1f);
		imGuiIO.setIniFilename(this.dir.getName() + "/imgui.ini");
		this.imGuiImplGlfw.init(mc().getWindow().getHandle(), true);
		this.imGuiImplGl3.init();
		this.logger.info("ImGUI Renderer loaded.");
		this.configManager = new ConfigManager();
		this.configManager.load();
		DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
		DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
		DietrichEvents2.global().subscribe(TickEvent.ID, this);
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}

	/**
	 * Stops the Foxglove mod. Performs cleanup tasks and shuts down the mod.
	 */
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
		if (this.getConfig().getMainMenuKeyCode() == key) {
			if (!(this.currentImGUIMenu instanceof MainMenu)) {
				this.setCurrentImGUIMenu(new MainMenu());
			}
			return;
		}
		if (this.blockKeyEvent || mc().currentScreen != null) return;
		final ClientPlayNetworkHandler playNetworkHandler = mc().getNetworkHandler();
		if (playNetworkHandler != null) {
			Foxglove.getInstance().getConfig().getChatMacros().object2ObjectEntrySet().fastForEach(macro -> {
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

	/**
	 * Renders the current ImGui menu.
	 */
	private void renderCurrentImGUIMenu() {
		if (this.currentImGUIMenu == null) return;
		this.imGuiImplGlfw.newFrame();
		ImGui.newFrame();
		this.currentImGUIMenu.render(ImGui.getIO());
		ImGui.render();
		this.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			final long pointer = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(pointer);
		}
	}

	@Override
	public void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
		if (mc().currentScreen != null) {
			this.renderCurrentImGUIMenu();
		}
	}

	@Override
	public void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
		if (mc().currentScreen == null) {
			this.renderCurrentImGUIMenu();
		}
	}

	/**
	 * Returns the name of the Foxglove mod.
	 *
	 * @return the name of the mod
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the lowercase name of the Foxglove mod.
	 *
	 * @return the lowercase name of the mod
	 */
	public String getLowerCaseName() {
		return this.lowerCaseName;
	}

	/**
	 * Returns the version of the Foxglove mod.
	 *
	 * @return the version of the mod
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Returns the author of the Foxglove mod.
	 *
	 * @return the author of the mod
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Returns the color associated with the Foxglove mod.
	 *
	 * @return the color of the mod
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Returns the RGB value of the color associated with the Foxglove mod.
	 *
	 * @return the RGB value of the color
	 */
	public int getColorRGB() {
		return this.colorRGB;
	}

	/**
	 * Returns the logger used by the Foxglove mod.
	 *
	 * @return the logger instance
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Returns the directory where the Foxglove mod is installed.
	 *
	 * @return the mod directory
	 */
	public File getDir() {
		return this.dir;
	}

	/**
	 * Checks if it is the first start of the Foxglove mod.
	 *
	 * @return true if it is the first start, false otherwise
	 */
	public boolean isFirstStart() {
		return this.firstStart;
	}

	/**
	 * Checks if the Foxglove mod is running in JVM debug mode.
	 *
	 * @return true if running in JVM debug mode, false otherwise
	 */
	public boolean isJvmDebugMode() {
		return this.jvmDebugMode;
	}

	/**
	 * Returns the registry of features for the Foxglove mod.
	 *
	 * @return the feature registry
	 */
	public FeatureRegistry getFeatures() {
		return this.features;
	}

	/**
	 * Returns the command handler for the Foxglove mod.
	 *
	 * @return the command handler
	 */
	public CommandHandler getCommandHandler() {
		return this.commandHandler;
	}

	/**
	 * Returns the window title of the Foxglove mod.
	 *
	 * @return the window title
	 */
	public String getWindowTitle() {
		return this.windowTitle;
	}

	/**
	 * Returns the current ImGUIMenu being displayed.
	 *
	 * @return the current ImGUIMenu
	 */
	public ImGUIMenu getCurrentImGUIMenu() {
		return this.currentImGUIMenu;
	}

	/**
	 * Sets the current ImGUIMenu to be displayed.
	 *
	 * @param currentImGUIMenu the current ImGUIMenu
	 */
	public void setCurrentImGUIMenu(final ImGUIMenu currentImGUIMenu) {
		this.currentImGUIMenu = currentImGUIMenu;
	}

	/**
	 * Returns the config manager for the Foxglove mod.
	 *
	 * @return the config manager
	 */
	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	/**
	 * Returns the config for the Foxglove mod.
	 *
	 * @return the config
	 */
	public MainConfig getConfig() {
		return this.configManager.getMainConfig();
	}

}
