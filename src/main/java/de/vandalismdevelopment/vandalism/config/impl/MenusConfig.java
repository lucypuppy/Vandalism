package de.vandalismdevelopment.vandalism.config.impl;

import com.google.gson.JsonObject;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.ValueableConfig;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenu;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenuCategory;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenuCategoryRegistry;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MenusConfig extends ValueableConfig {

    public MenusConfig(final File dir) {
        super(dir, "menus");
    }

    @Override
    public JsonObject save() throws IOException {
        final JsonObject menusObject = new JsonObject();
        final ImGuiMenuCategoryRegistry imGuiMenuCategoryRegistry = Vandalism.getInstance().getImGuiHandler().getImGuiMenuCategoryRegistry();
        if (imGuiMenuCategoryRegistry == null) return menusObject;
        final List<ImGuiMenuCategory> imGuiMenuCategories = imGuiMenuCategoryRegistry.getImGuiMenuCategories();
        if (imGuiMenuCategories == null || imGuiMenuCategories.isEmpty()) return menusObject;
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                menusObject.addProperty(imGuiMenu.getName(), imGuiMenu.getState());
                Vandalism.getInstance().getLogger().info("ImGui Menu " + imGuiMenu.getName() + " has been saved.");
            }
        }
        return menusObject;
    }

    @Override
    public void load(final JsonObject jsonObject) throws IOException {
        final ImGuiMenuCategoryRegistry imGuiMenuCategoryRegistry = Vandalism.getInstance().getImGuiHandler().getImGuiMenuCategoryRegistry();
        if (imGuiMenuCategoryRegistry == null) return;
        final List<ImGuiMenuCategory> imGuiMenuCategories = imGuiMenuCategoryRegistry.getImGuiMenuCategories();
        if (imGuiMenuCategories == null || imGuiMenuCategories.isEmpty() || jsonObject == null) return;
        for (final ImGuiMenuCategory imGuiMenuCategory : imGuiMenuCategories) {
            for (final ImGuiMenu imGuiMenu : imGuiMenuCategory.getImGuiMenus()) {
                if (!jsonObject.has(imGuiMenu.getName())) continue;
                imGuiMenu.setState(jsonObject.get(imGuiMenu.getName()).getAsBoolean());
            }
        }
    }

}
