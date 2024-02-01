/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.florianmichael.rclasses.pattern.storage.named.NamedStorage;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.event.normal.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.base.event.normal.network.DisconnectListener;
import de.nekosarekawaii.vandalism.base.event.normal.network.WorldListener;
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.gui.ModulesClientMenuWindow;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.*;
import de.nekosarekawaii.vandalism.feature.module.impl.development.TestModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.consolespammer.ConsoleSpammerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.godmode.GodModeModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.servercrasher.ServerCrasherModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.elytraflight.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.phase.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.impl.render.*;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

public class ModuleManager extends NamedStorage<AbstractModule> implements
        KeyboardInputListener, ShutdownProcessListener,
        DisconnectListener, MinecraftWrapper,
        WorldListener, PlayerUpdateListener
{

    private final ConfigManager configManager;

    private ModPacketBlockerModule modPacketBlockerModule;
    private ExploitFixerModule exploitFixerModule;
    private AutoBlockModule autoBlockModule;
    private TrueSightModule trueSightModule;
    private VisualThrottleModule visualThrottleModule;
    private BetterTabListModule betterTabListModule;
    private FastUseModule fastUseModule;
    private IllegalInteractionModule illegalInteractionModule;
    private ESPModule espModule;
    private KillAuraModule killAuraModule;

    public ModuleManager(final DietrichEvents2 eventSystem, final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        this.configManager = configManager;
        clientMenuManager.add(new ModulesClientMenuWindow());
        eventSystem.subscribe(
                this,
                KeyboardInputEvent.ID, ShutdownProcessEvent.ID,
                DisconnectEvent.ID, WorldLoadEvent.ID,
                PlayerUpdateEvent.ID
        );
    }

    @Override
    public void init() {
        this.add(
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.autoBlockModule = new AutoBlockModule(),
                this.trueSightModule = new TrueSightModule(),
                this.visualThrottleModule = new VisualThrottleModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.fastUseModule = new FastUseModule(),
                this.illegalInteractionModule = new IllegalInteractionModule(),
                this.espModule = new ESPModule(),
                this.killAuraModule = new KillAuraModule(this.autoBlockModule),
                new UseItemSlowdownModule(this.autoBlockModule),
                new BackTrackModule(this.killAuraModule),
                new TestModule(),
                new PacketManagerModule(),
                new ServerCrasherModule(),
                new BungeeCordSpooferModule(),
                new GodModeModule(),
                new BowSpammerModule(),
                new ConsoleSpammerModule(),
                new JoinLeaveModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                new InteractionSpammerModule(),
                new ItemStackLoggerModule(),
                new MessageEncryptorModule(),
                new BlockNormalizerModule(),
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
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                new ProtectorModule(),
                new VehicleOneHitModule(),
                new LongJumpModule(),
                new ChatReactionModule(),
                new VehicleFlightModule(),
                new EcholocationModule(),
                new AutoClickerModule(),
                new SprintTapModule(),
                new CraftCarryModule()
        );
        this.configManager.add(new ConfigWithValues("modules", getList()));
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        //Cancel if the key is unknown to prevent the module from being toggled multiple times.
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || this.mc.player == null || this.mc.currentScreen != null) {
            return;
        }
        for (final AbstractModule module : this.getList()) {
            if (module.getKeyBind().getValue() == key) {
                module.toggle();
            }
        }
    }

    @Override
    public void onShutdownProcess() {
        for (final AbstractModule module : this.getList()) {
            if (module.isActive() && module.isDeactivateOnShutdown()) {
                module.deactivate();
            }
        }
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        //There is a thing called pinging a server
        if (this.mc.getNetworkHandler() != null && Objects.equals(clientConnection, this.mc.getNetworkHandler().getConnection())) {
            for (final AbstractModule module : this.getList()) {
                if (module.isActive() && module.isDeactivateOnQuit()) {
                    module.deactivate();
                }
            }
        }
    }

    @Override
    public void onPreWorldLoad() {
        for (final AbstractModule module : this.getList()) {
            if (module.isActive() && module.isDeactivateOnWorldLoad()) {
                module.deactivate();
            }
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.mc.player.isDead()) return;
        for (final AbstractModule module : this.getList()) {
            if (module.isActive() && module.isDeactivateOnDeath()) {
                module.deactivate();
            }
        }
    }

    public List<AbstractModule> getByCategory(final Feature.Category category) {
        return this.getList().stream().filter(abstractModule -> abstractModule.getCategory() == category).toList();
    }

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return modPacketBlockerModule;
    }

    public ExploitFixerModule getExploitFixerModule() {
        return exploitFixerModule;
    }

    public AutoBlockModule getAutoBlockModule() {
        return autoBlockModule;
    }

    public TrueSightModule getTrueSightModule() {
        return trueSightModule;
    }

    public VisualThrottleModule getVisualThrottleModule() {
        return visualThrottleModule;
    }

    public BetterTabListModule getBetterTabListModule() {
        return betterTabListModule;
    }

    public IllegalInteractionModule getIllegalInteractionModule() {
        return illegalInteractionModule;
    }

    public FastUseModule getFastUseModule() {
        return fastUseModule;
    }

    public ESPModule getEspModule() {
        return espModule;
    }

    public KillAuraModule getKillAuraModule() {
        return killAuraModule;
    }

}
