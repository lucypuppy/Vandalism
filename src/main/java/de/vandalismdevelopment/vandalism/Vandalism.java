package de.vandalismdevelopment.vandalism;

import de.vandalismdevelopment.vandalism.config.ConfigManager;
import de.vandalismdevelopment.vandalism.creativetab.CreativeTabRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.command.CommandRegistry;
import de.vandalismdevelopment.vandalism.feature.impl.module.ModuleRegistry;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiHandler;
import de.vandalismdevelopment.vandalism.util.rotation.RotationListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.main.Main;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Vandalism {

    private final static Vandalism INSTANCE = new Vandalism();

    public static Vandalism getInstance() {
        return INSTANCE;
    }

    private final String name, lowerCaseName, version, windowTitle, author;

    private final static String FALLBACK_VERSION = "1337";

    private final Logger logger;

    private File dir;

    private CreativeTabRegistry creativeTabRegistry;

    private ModuleRegistry moduleRegistry;

    private CommandRegistry commandRegistry;

    private ConfigManager configManager;

    private ImGuiHandler imGuiHandler;

    private RotationListener rotationListener;

    public Vandalism() {
        this.name = "Vandalism";
        this.lowerCaseName = this.name.toLowerCase();
        this.author = "Vandalism Development";
        final String modVersionString;
        final Optional<ModContainer> modContainer = FabricLoader.
                getInstance().
                getModContainer(this.lowerCaseName);
        if (modContainer.isPresent()) {
            modVersionString = modContainer.
                    get().
                    getMetadata().
                    getVersion().
                    getFriendlyString();
        } else modVersionString = FALLBACK_VERSION;
        this.version = modVersionString.equals("${version}") ? FALLBACK_VERSION : modVersionString;
        this.logger = LoggerFactory.getLogger(this.name);
        this.windowTitle = String.format(
                "%s made by %s",
                this.name,
                this.author
        );
    }

    private void setIcon(final Window window) throws IOException, RuntimeException {
        final int platform = GLFW.glfwGetPlatform();
        switch (platform) {
            case GLFW.GLFW_PLATFORM_WIN32:
            case GLFW.GLFW_PLATFORM_X11: {
                final int[] iconSizes = new int[]{16, 32, 48, 128, 256};
                final List<InputStream> inputStreams = new ArrayList<>();
                final String iconTexturesPath = "assets/" + this.lowerCaseName + "/textures/icon/";
                for (final int iconSize : iconSizes) {
                    final InputStream inputStream =
                            Main.class
                                    .getClassLoader()
                                    .getResourceAsStream(iconTexturesPath + "icon_" + iconSize + "x" + iconSize + ".png");
                    if (inputStream != null) inputStreams.add(inputStream);
                }
                if (inputStreams.isEmpty()) {
                    throw new RuntimeException("Failed to find icons.");
                }
                final List<ByteBuffer> byteBuffers = new ArrayList<>(inputStreams.size());
                try {
                    final MemoryStack memoryStack = MemoryStack.stackPush();
                    try {
                        final GLFWImage.Buffer buffer = GLFWImage.malloc(inputStreams.size(), memoryStack);
                        for (int i = 0; i < inputStreams.size(); ++i) {
                            final NativeImage nativeImage = NativeImage.read(inputStreams.get(i));
                            try {
                                final ByteBuffer byteBuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);
                                byteBuffers.add(byteBuffer);
                                byteBuffer.asIntBuffer().put(nativeImage.copyPixelsRgba());
                                buffer.position(i);
                                buffer.width(nativeImage.getWidth());
                                buffer.height(nativeImage.getHeight());
                                buffer.pixels(byteBuffer);
                            } catch (final Throwable throwable) {
                                try {
                                    nativeImage.close();
                                } catch (final Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                                throw throwable;
                            }
                            nativeImage.close();
                        }
                        GLFW.glfwSetWindowIcon(window.getHandle(), buffer.position(0));
                    } catch (final Throwable throwable) {
                        try {
                            memoryStack.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                        throw throwable;
                    }
                    memoryStack.close();
                    break;
                } finally {
                    byteBuffers.forEach(MemoryUtil::memFree);
                }
            }
            default: {
                this.logger.warn("Not setting icon for unrecognized platform: {}", platform);
            }
        }

    }

    public void start(final MinecraftClient mc) {
        mc.getWindow().setTitle(this.windowTitle + " | Starting...");
        try {
            this.setIcon(mc.getWindow());
        } catch (final IOException | RuntimeException exception) {
            this.logger.error("Couldn't set icon.", exception);
        }
        this.logger.info("Starting...");
        this.logger.info("Version: {}", this.version);
        this.logger.info("Made by {}", this.author);
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

    public String getAuthor() {
        return this.author;
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
