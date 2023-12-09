package de.vandalismdevelopment.vandalism.integration.hud;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.RenderListener;
import de.vandalismdevelopment.vandalism.base.event.ScreenListener;
import de.vandalismdevelopment.vandalism.integration.hud.impl.DebugElement;
import de.vandalismdevelopment.vandalism.integration.hud.impl.InfoHUDElement;
import de.vandalismdevelopment.vandalism.integration.hud.impl.ModuleListHUDElement;
import de.vandalismdevelopment.vandalism.integration.hud.impl.WatermarkHUDElement;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HUDManager implements RenderListener, ScreenListener, MinecraftWrapper {

    private final List<HUDElement> hudElements;
    private final ModuleListHUDElement moduleListHUDElement;

    public HUDManager() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(ScreenEvent.ID, this);
        this.hudElements = new CopyOnWriteArrayList<>();
        this.hudElements.addAll(Arrays.asList(
                new WatermarkHUDElement(),
                this.moduleListHUDElement = new ModuleListHUDElement(),
                new InfoHUDElement(),
                new DebugElement()
        ));
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        for (final HUDElement hudElement : this.hudElements) {
            if (!hudElement.isEnabled()) continue;
            hudElement.onRender(context, delta);
        }
    }

    @Override
    public void onResizeScreen(final ScreenEvent event) {
        for (final HUDElement hudElement : this.hudElements) {
            hudElement.calculateAlignment();
            hudElement.calculatePosition();
        }
    }

    public List<HUDElement> getHudElements() {
        return this.hudElements;
    }

    public ModuleListHUDElement getModuleListHUDElement() {
        return this.moduleListHUDElement;
    }

}