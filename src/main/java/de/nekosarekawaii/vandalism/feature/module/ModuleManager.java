/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.event.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.player.HealthUpdateListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.config.ModuleConfig;
import de.nekosarekawaii.vandalism.feature.module.gui.ModulesClientWindow;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.consolespammer.ConsoleSpammerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.disabler.DisablerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.godmode.GodModeModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.servercrasher.ServerCrasherModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.notebot.NoteBotModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.translator.TranslatorModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.elytraflight.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.JesusModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.phase.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.step.StepModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.teleport.TeleportModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.impl.render.*;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.storage.NamedStorage;
import lombok.Getter;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Getter
public class ModuleManager extends NamedStorage<Module> implements
        KeyboardInputListener, MouseInputListener, ShutdownProcessListener,
        DisconnectListener, MinecraftWrapper, WorldListener,
        PlayerUpdateListener, HealthUpdateListener {

    private final ConfigManager configManager;

    private ModPacketBlockerModule modPacketBlockerModule;
    private ExploitFixerModule exploitFixerModule;
    private TrueSightModule trueSightModule;
    private BetterTabListModule betterTabListModule;
    private FastPlaceModule fastPlaceModule;
    private IllegalInteractionModule illegalInteractionModule;
    private ESPModule espModule;
    private KillAuraModule killAuraModule;
    private TickBaseModule tickBaseModule;
    private FullBrightModule fullBrightModule;
    private VehicleControlModule vehicleControlModule;
    private ConsoleSpammerModule consoleSpammerModule;
    private FastBreakModule fastBreakModule;
    private ZoomModule zoomModule;
    private GhostHandModule ghostHandModule;
    private NoFriendsModule noFriendsModule;
    private SoundBlockerModule soundBlockerModule;
    private ResourcePackSpooferModule resourcePackSpooferModule;
    private StepModule stepModule;
    private RiptideBoosterModule riptideBoosterModule;
    private NoChatReportsModule noChatReportsModule;
    private AntiBotsModule antiBotsModule;
    private LagRangeModule lagRangeModule;
    private AutoSoupModule autoSoupModule;
    private AutoSprintModule autoSprintModule;
    private ShowClickEventsModule showClickEventsModule;
    private NoSlowModule noSlowModule;
    private HenklerSprenklerModule henklerSprenklerModule;
    private FlightModule flightModule;
    private HAProxySpooferModule haProxySpooferModule;

    public ModuleManager(final DietrichEvents2 eventSystem, final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        this.configManager = configManager;
        clientWindowManager.add(new ModulesClientWindow());
        eventSystem.subscribe(
                this,
                KeyboardInputEvent.ID, MouseEvent.ID,
                ShutdownProcessEvent.ID, DisconnectEvent.ID,
                WorldLoadEvent.ID, PlayerUpdateEvent.ID,
                HealthUpdateEvent.ID
        );
    }

    @Override
    public void init() {
        if (FabricBootstrap.IS_DEV_ENVIRONMENT) {
            this.add(new TestModule());
        }
        this.add(
                this.killAuraModule = new KillAuraModule(),
                this.consoleSpammerModule = new ConsoleSpammerModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.tickBaseModule = new TickBaseModule(),
                this.fastBreakModule = new FastBreakModule(),
                this.fastPlaceModule = new FastPlaceModule(),
                this.illegalInteractionModule = new IllegalInteractionModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.vehicleControlModule = new VehicleControlModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.trueSightModule = new TrueSightModule(),
                this.espModule = new ESPModule(),
                this.fullBrightModule = new FullBrightModule(),
                this.zoomModule = new ZoomModule(),
                this.ghostHandModule = new GhostHandModule(),
                this.noFriendsModule = new NoFriendsModule(),
                this.soundBlockerModule = new SoundBlockerModule(),
                this.resourcePackSpooferModule = new ResourcePackSpooferModule(),
                this.stepModule = new StepModule(),
                this.riptideBoosterModule = new RiptideBoosterModule(),
                this.noChatReportsModule = new NoChatReportsModule(),
                this.antiBotsModule = new AntiBotsModule(),
                this.lagRangeModule = new LagRangeModule(),
                this.autoSoupModule = new AutoSoupModule(),
                this.autoSprintModule = new AutoSprintModule(),
                this.showClickEventsModule = new ShowClickEventsModule(),
                this.noSlowModule = new NoSlowModule(),
                this.flightModule = new FlightModule(),
                this.haProxySpooferModule = new HAProxySpooferModule(),
                this.henklerSprenklerModule = new HenklerSprenklerModule(),
                new FakeLagModule(),
                new AutoClickerModule(),
                new AutoRodModule(),
                new AutoShieldModule(),
                new BackTrackModule(),
                new BowSpammerModule(),
                new TeleportHitModule(),
                new TimerRangeModule(),
                new WTapModule(),
                new DisablerModule(),
                new GodModeModule(),
                new ServerCrasherModule(),
                new BungeeCordSpooferModule(),
                new CraftCarryModule(),
                new EcholocationModule(),
                new SignExploitsModule(),
                new VehicleOneHitModule(),
                new AutoArmorModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                new BlockBreakerModule(),
                new ChatReactionModule(),
                new ChestStealerModule(),
                new FakeGameModeModule(),
                new FlagDetectorModule(),
                new HandFuckerModule(),
                new InteractionSpammerModule(),
                //new ItemStackLoggerModule(), TODO
                new JoinLeaveNotifierModule(),
                new MessageEncryptorModule(),
                new MiddleClickFriendsModule(),
                new NoteBotModule(),
                new PacketManagerModule(),
                new AirJumpModule(),
                new BlinkModule(),
                new ElytraFlightModule(),
                new JesusModule(),
                new NoFallModule(),
                new PhaseModule(),
                new SpeedModule(),
                new VelocityModule(),
                new BlockNormalizerModule(),
                new FOVFuckerModule(),
                new LongJumpModule(),
                new PushVelocityModule(),
                new ScaffoldModule(),
                new StrafeModule(),
                new TeleportModule(),
                new TimerModule(),
                new AmbienceModule(),
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                new ProtectorModule(),
                new GameModeNotifierModule(),
                new SmartVClipModule(),
                new CheatDetectorModule(),
                new FreeCamModule(),
                new AutoToolModule(),
                new AntiFireballModule(),
                new InventoryMoveModule(),
                new TranslatorModule(),
                new KaboomFuckerModule(),
                new ProtocolIdChangerModule(),
                new NoObfuscatedTextModule(),
                new HenkelPortModule(),
                new DerpModule(),
                new TriggerBotModule()
        );
        this.configManager.add(new ModuleConfig(this));
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        this.handleInput(action, key);
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (event.type == MouseInputListener.Type.BUTTON) {
            this.handleInput(event.action, event.button);
        }
    }

    private void handleInput(final int action, final int code) {
        // Cancel if the key is unknown to prevent the script from being executed multiple times.
        if (action == GLFW.GLFW_REPEAT || code == GLFW.GLFW_KEY_UNKNOWN) {
            return;
        }

        for (final Module module : this.getList()) {
            if (action == GLFW.GLFW_PRESS && module.getKeyBind().isPressed(code)) {
                module.toggle();
            } else if (action == GLFW.GLFW_RELEASE && module.isDeactivateOnRelease() && module.getKeyBind().isReleased(code)) {
                module.deactivate();
            }
        }
    }

    @Override
    public void onShutdownProcess() {
        this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnShutdown()).forEach(Module::deactivate);
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnQuit()).forEach(Module::deactivate);
    }

    @Override
    public void onPreWorldLoad() {
        this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnWorldLoad()).forEach(Module::deactivate);
    }

    @Override
    public void onHealthUpdate(final HealthUpdateEvent event) {
        if (event.health <= 0.0F) {
            this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnDeath()).forEach(Module::deactivate);
        }
    }

    public List<Module> getByCategory(final Feature.Category category) {
        return this.getList().stream().filter(module -> module.getCategory() == category).toList();
    }

}