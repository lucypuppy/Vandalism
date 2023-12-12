package de.vandalismdevelopment.vandalism.integration.hud;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.base.config.ConfigManager;
import de.vandalismdevelopment.vandalism.base.event.render.CameraClipRaytraceListener;
import de.vandalismdevelopment.vandalism.base.event.game.ScreenListener;
import de.vandalismdevelopment.vandalism.gui.ImGuiManager;
import de.vandalismdevelopment.vandalism.integration.hud.config.HUDConfig;
import de.vandalismdevelopment.vandalism.integration.hud.gui.HUDImWindow;
import de.vandalismdevelopment.vandalism.integration.hud.impl.DebugElement;
import de.vandalismdevelopment.vandalism.integration.hud.impl.InfoHUDElement;
import de.vandalismdevelopment.vandalism.integration.hud.impl.ModuleListHUDElement;
import de.vandalismdevelopment.vandalism.integration.hud.impl.WatermarkHUDElement;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

public class HUDManager extends Storage<HUDElement> implements CameraClipRaytraceListener, ScreenListener, MinecraftWrapper {

    public ModuleListHUDElement moduleListHUDElement;

    public HUDManager(final ConfigManager configManager, final ImGuiManager imGuiManager) {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(ScreenEvent.ID, this);

        configManager.add(new HUDConfig(this));
        imGuiManager.add(new HUDImWindow(this));
    }

    @Override
    public void init() {
        this.add(
                new WatermarkHUDElement(),
                this.moduleListHUDElement = new ModuleListHUDElement(),
                new InfoHUDElement(),
                new DebugElement()
        );
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        for (final HUDElement hudElement : this.getList()) {
            if (!hudElement.isEnabled()) {
                continue;
            }
            hudElement.onRender(context, delta);
        }
    }

    @Override
    public void onResizeScreen(final ScreenEvent event) {
        for (final HUDElement hudElement : this.getList()) {
            hudElement.calculateAlignment();
            hudElement.calculatePosition();
        }
    }
}