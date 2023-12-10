package de.vandalismdevelopment.vandalism.feature.module;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.event.InputListener;
import de.vandalismdevelopment.vandalism.feature.module.impl.combat.BowSpammerModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.development.PacketLoggerModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.development.TestModule;
import de.vandalismdevelopment.vandalism.feature.module.impl.exploit.*;
import de.vandalismdevelopment.vandalism.feature.module.impl.misc.*;
import de.vandalismdevelopment.vandalism.feature.module.impl.movement.*;
import de.vandalismdevelopment.vandalism.feature.module.impl.render.*;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import org.lwjgl.glfw.GLFW;

public class ModuleManager extends Storage<AbstractModule> implements InputListener, MinecraftWrapper {

    private BetterTabListModule betterTabListModule;
    private ESPModule espModule;
    private ExploitFixerModule exploitFixerModule;
    private FastUseModule fastUseModule;
    private IllegalBlockPlaceModule illegalBlockPlaceModule;
    private MessageEncryptorModule messageEncryptorModule;
    private ModPacketBlockerModule modPacketBlockerModule;
    private TrueSightModule trueSightModule;
    private VisualThrottleModule visualThrottleModule;

    public ModuleManager() {
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
    }

    @Override
    public void init() {
        this.add(
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
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS || this.mc.player == null || this.mc.currentScreen != null) {
            return;
        }
        for (final AbstractModule module : Vandalism.getInstance().getModuleRegistry().getList()) {
            if (module.getKeyBind().getKeyCode() == key) {
                module.toggle();
            }
        }
    }

    public BetterTabListModule getBetterTabListModule() {
        return betterTabListModule;
    }

    public ESPModule getEspModule() {
        return espModule;
    }

    public ExploitFixerModule getExploitFixerModule() {
        return exploitFixerModule;
    }

    public FastUseModule getFastUseModule() {
        return fastUseModule;
    }

    public IllegalBlockPlaceModule getIllegalBlockPlaceModule() {
        return illegalBlockPlaceModule;
    }

    public MessageEncryptorModule getMessageEncryptorModule() {
        return messageEncryptorModule;
    }

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return modPacketBlockerModule;
    }

    public TrueSightModule getTrueSightModule() {
        return trueSightModule;
    }

    public VisualThrottleModule getVisualThrottleModule() {
        return visualThrottleModule;
    }

}
