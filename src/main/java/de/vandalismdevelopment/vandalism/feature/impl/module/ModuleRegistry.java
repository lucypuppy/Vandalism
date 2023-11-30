package de.vandalismdevelopment.vandalism.feature.impl.module;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.combat.BowSpammerModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.DebugModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.PacketLoggerModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.TestModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.*;
import de.vandalismdevelopment.vandalism.util.interfaces.MinecraftWrapper;
import org.lwjgl.glfw.GLFW;

public class ModuleRegistry implements MinecraftWrapper, KeyboardListener {

    private AmbienceModule ambienceModule;
    private AntiFOVModule antiFOVModule;
    private AutoFishModule autoFishModule;
    private AutoRespawnModule autoRespawnModule;
    private BetterTabListModule betterTabListModule;
    private BetterTooltipsModule betterTooltipsModule;
    private BlockDensityModule blockDensityModule;
    private BowSpammerModule bowSpammerModule;
    private BungeeCordSpooferModule bungeeCordSpooferModule;
    private CameraNoClipModule cameraNoClipModule;
    private CraftCarryModule craftCarryModule;
    private ConsoleSpammerModule consoleSpammerModule;
    private DebugModule debugModule;
    private DeutschMacherModule deutschMacherModule;
    private ESPModule espModule;
    private ElytraFlightModule elytraFlightModule;
    private ExploitFixerModule exploitFixerModule;
    private FastUseModule fastUseModule;
    private FlightModule flightModule;
    private GodModeModule godModeModule;
    private HUDModule hudModule;
    private IllegalBlockPlaceModule illegalBlockPlaceModule;
    private InteractionSpammerModule interactionSpammerModule;
    private ItemStackLoggerModule itemStackLoggerModule;
    private JoinLeaveModule joinLeaveModule;
    private MessageEncryptorModule messageEncryptorModule;
    private ModPacketBlockerModule modPacketBlockerModule;
    private NoFallModule noFallModule;
    private NoteBlockPlayerModule noteBlockPlayerModule;
    private PacketLoggerModule packetLoggerModule;
    private PhaseModule phaseModule;
    private PushVelocityModule pushVelocityModule;
    private ProtectorModule protectorModule;
    private ServerCrasherModule serverCrasherModule;
    private SpeedModule speedModule;
    private StepModule stepModule;
    private TestModule testModule;
    private TrueSightModule trueSightModule;
    private VelocityModule velocityModule;
    private VisualThrottleModule visualThrottleModule;

    public BetterTabListModule getBetterTabListModule() {
        return this.betterTabListModule;
    }

    public ESPModule getEspModule() {
        return this.espModule;
    }

    public ExploitFixerModule getExploitFixerModule() {
        return this.exploitFixerModule;
    }

    public FastUseModule getFastUseModule() {
        return this.fastUseModule;
    }

    public HUDModule getHudModule() {
        return this.hudModule;
    }

    public IllegalBlockPlaceModule getIllegalBlockPlaceModule() {
        return this.illegalBlockPlaceModule;
    }

    public MessageEncryptorModule getMessageEncryptorModule() {
        return this.messageEncryptorModule;
    }

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return this.modPacketBlockerModule;
    }

    public TrueSightModule getTrueSightModule() {
        return this.trueSightModule;
    }

    public VisualThrottleModule getVisualThrottleModule() {
        return this.visualThrottleModule;
    }

    private boolean done;

    private final FeatureList<Module> modules;

    public ModuleRegistry() {
        this.done = false;
        this.modules = new FeatureList<>();
        this.register();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        this.done = true;
    }

    private void register() {
        this.registerModules(
                this.ambienceModule = new AmbienceModule(),
                this.antiFOVModule = new AntiFOVModule(),
                this.autoFishModule = new AutoFishModule(),
                this.autoRespawnModule = new AutoRespawnModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.betterTooltipsModule = new BetterTooltipsModule(),
                this.blockDensityModule = new BlockDensityModule(),
                this.bowSpammerModule = new BowSpammerModule(),
                this.bungeeCordSpooferModule = new BungeeCordSpooferModule(),
                this.cameraNoClipModule = new CameraNoClipModule(),
                this.craftCarryModule = new CraftCarryModule(),
                this.consoleSpammerModule = new ConsoleSpammerModule(),
                this.debugModule = new DebugModule(),
                this.deutschMacherModule = new DeutschMacherModule(),
                this.espModule = new ESPModule(),
                this.elytraFlightModule = new ElytraFlightModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.fastUseModule = new FastUseModule(),
                this.flightModule = new FlightModule(),
                this.godModeModule = new GodModeModule(),
                this.hudModule = new HUDModule(),
                this.illegalBlockPlaceModule = new IllegalBlockPlaceModule(),
                this.interactionSpammerModule = new InteractionSpammerModule(),
                this.itemStackLoggerModule = new ItemStackLoggerModule(),
                this.joinLeaveModule = new JoinLeaveModule(),
                this.messageEncryptorModule = new MessageEncryptorModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.noFallModule = new NoFallModule(),
                this.noteBlockPlayerModule = new NoteBlockPlayerModule(),
                this.packetLoggerModule = new PacketLoggerModule(),
                this.phaseModule = new PhaseModule(),
                this.pushVelocityModule = new PushVelocityModule(),
                this.protectorModule = new ProtectorModule(),
                this.serverCrasherModule = new ServerCrasherModule(),
                this.speedModule = new SpeedModule(),
                this.stepModule = new StepModule(),
                this.testModule = new TestModule(),
                this.trueSightModule = new TrueSightModule(),
                this.velocityModule = new VelocityModule(),
                this.visualThrottleModule = new VisualThrottleModule()
        );
    }

    private void registerModules(final Module... modules) {
        Vandalism.getInstance().getLogger().info("Registering modules...");
        for (final Module module : modules) {
            if (!this.modules.contains(module)) {
                this.modules.add(module);
                Vandalism.getInstance().getLogger().info("Module '" + module.toString() + "' has been registered.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated module found: " + module.toString());
            }
        }
        final int moduleListSize = this.modules.size();
        if (moduleListSize < 1) Vandalism.getInstance().getLogger().info("No modules found!");
        else Vandalism.getInstance().getLogger().info("Registered " + moduleListSize + " modules.");
    }

    public FeatureList<Module> getModules() {
        return this.modules;
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || this.player() == null || this.currentScreen() != null) {
            return;
        }
        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
            if (module.getKeyBind().getKeyCode() == key) {
                module.toggle();
            }
        }
    }

    public boolean isDone() {
        return this.done;
    }

}
