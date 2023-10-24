package de.vandalismdevelopment.vandalism.gui.imgui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.*;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script.ScriptsImGuiMenu;

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
                new ScriptsImGuiMenu(),
                new AccountManagerImGuiMenu(),
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
                Vandalism.getInstance().getLogger().info("ImGui Menu '" + imGuiMenu + "' has been registered.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated ImGui Menu found: " + imGuiMenu);
            }
        }
        final int imGuiMenuListSize = this.imGuiMenus.size();
        if (imGuiMenuListSize < 1) Vandalism.getInstance().getLogger().info("No ImGui Menus found!");
        else Vandalism.getInstance().getLogger().info("Registered " + imGuiMenuListSize + " ImGui Menu/s.");
    }

    public List<ImGuiMenu> getImGuiMenus() {
        return this.imGuiMenus;
    }

    public <M extends ImGuiMenu> M getImGuiMenuByClass(final Class<M> clazz) {
        for (final ImGuiMenu imGuiMenu : this.imGuiMenus) {
            if (imGuiMenu.getClass().equals(clazz)) {
                return (M) imGuiMenu;
            }
        }
        return null;
    }

}
