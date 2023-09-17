package de.foxglovedevelopment.foxglove.gui.imgui;

import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.gui.imgui.impl.menu.*;

import java.util.ArrayList;
import java.util.List;

public class ImGuiMenuRegistry {

    private final List<ImGuiMenu> imGuiMenus;

    public ImGuiMenuRegistry() {
        this.imGuiMenus = new ArrayList<>();
        this.registerImGuiMenus(
                new DemoImGuiMenu(),
                new ConfigImGuiMenu(),
                //TODO: Finish this -> new MacrosImGuiMenu(),
                new AltManagerImGuiMenu(),
                new NameHistoryImGuiMenu(),
                new ServerPingerImGuiMenu(),
                new ServerAddressResolverImGuiMenu(),
                new BugScraperImGuiMenu()
        );
    }

    private void registerImGuiMenus(final ImGuiMenu... imGuiMenus) {
        for (final ImGuiMenu imGuiMenu : imGuiMenus) {
            if (!this.imGuiMenus.contains(imGuiMenu)) {
                this.imGuiMenus.add(imGuiMenu);
                Foxglove.getInstance().getLogger().info("ImGui Menu '" + imGuiMenu + "' has been registered.");
            } else {
                Foxglove.getInstance().getLogger().error("Duplicated ImGui Menu found: " + imGuiMenu);
            }
        }
        final int imGuiMenuListSize = this.imGuiMenus.size();
        if (imGuiMenuListSize < 1) Foxglove.getInstance().getLogger().info("No ImGui Menus found!");
        else Foxglove.getInstance().getLogger().info("Registered " + imGuiMenuListSize + " ImGui Menu/s.");
    }

    public List<ImGuiMenu> getImGuiMenus() {
        return this.imGuiMenus;
    }

}
