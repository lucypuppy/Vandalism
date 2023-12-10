package de.vandalismdevelopment.vandalism.feature.impl.module;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.InputListener;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.combat.BowSpammerModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.PacketLoggerModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.TestModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.*;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import org.lwjgl.glfw.GLFW;

public class ModuleRegistry implements InputListener, MinecraftWrapper {

    private final BetterTabListModule betterTabListModule;
    private final ESPModule espModule;
    private final ExploitFixerModule exploitFixerModule;
    private final FastUseModule fastUseModule;
    private final IllegalBlockPlaceModule illegalBlockPlaceModule;
    private final MessageEncryptorModule messageEncryptorModule;
    private final ModPacketBlockerModule modPacketBlockerModule;
    private final TrueSightModule trueSightModule;
    private final VisualThrottleModule visualThrottleModule;

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

    private final FeatureList<Module> modules;

    public ModuleRegistry() {
        this.modules = new FeatureList<>();
        this.registerModules(
                new BowSpammerModule(),
                new PacketLoggerModule(),
                new TestModule(),
                new BungeeCordSpooferModule(),
                new ConsoleSpammerModule(),
                new CraftCarryModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                new GodModeModule(),
                new JoinLeaveModule(),
                new ServerCrasherModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                this.fastUseModule = new FastUseModule(),
                this.illegalBlockPlaceModule = new IllegalBlockPlaceModule(),
                new InteractionSpammerModule(),
                new ItemStackLoggerModule(),
                this.messageEncryptorModule = new MessageEncryptorModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                new BlockDensityModule(),
                new ElytraFlightModule(),
                new FlightModule(),
                new FOVFuckerModule(),
                new NoFallModule(),
                new PhaseModule(),
                new PushVelocityModule(),
                new SpeedModule(),
                new TimerModule(),
                new SprintModule(),
                new StepModule(),
                new VelocityModule(),
                new AmbienceModule(),
                this.betterTabListModule = new BetterTabListModule(),
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                this.espModule = new ESPModule(),
                new ProtectorModule(),
                this.trueSightModule = new TrueSightModule(),
                this.visualThrottleModule = new VisualThrottleModule(),
                new VehicleOneHitModule(),
                new LongJumpModule()
        );
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
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
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || this.mc.player == null || this.mc.currentScreen != null) {
            return;
        }
        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
            if (module.getKeyBind().getKeyCode() == key) {
                module.toggle();
            }
        }
    }

}
