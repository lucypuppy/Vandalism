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

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.feature.command.arguments.ConfigArgumentType;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.command.CommandSource;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigCommand extends Command {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Pattern INVALID_CONFIG_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9_.-]");

    public static final File CONFIGS_DIR = new File(Vandalism.getInstance().getRunDirectory(), "configs");

    public ConfigCommand() {
        super("Lets you load, save or delete configs.", Category.MISC, "config", "configs");
        if (!CONFIGS_DIR.exists()) {
            if (!CONFIGS_DIR.mkdirs()) {
                Vandalism.getInstance().getLogger().error("Failed to create configs directory.");
            }
        }
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("save").then(argument("config-name", StringArgumentType.string()).executes(context -> {
            final String name = StringArgumentType.getString(context, "config-name");
            try {
                if (INVALID_CONFIG_NAME_PATTERN.matcher(name).find()) {
                    ChatUtil.errorChatMessage("Invalid config name.");
                    return SINGLE_SUCCESS;
                }
                ChatUtil.infoChatMessage("Saving config " + name + "...");
                final File file = new File(CONFIGS_DIR, name + ".json");
                try {
                    file.delete();
                    file.createNewFile();
                    final ModuleManager moduleManager = Vandalism.getInstance().getModuleManager();
                    final JsonObject modulesJsonObject = new JsonObject();
                    for (final Module module : moduleManager.getList()) {
                        if (module.getCategory() == Category.RENDER) {
                            continue;
                        }
                        final List<Value<?>> values = new ArrayList<>();
                        for (final Value<?> value : module.getValues()) {
                            if (module.getDefaultValues().contains(value)) {
                                continue;
                            }
                            if (value instanceof KeyBindValue) {
                                continue;
                            }
                            values.add(value);
                        }
                        final JsonObject valuesJsonObject = new JsonObject();
                        ConfigWithValues.saveValues(valuesJsonObject, values);
                        final JsonObject jsonObject = new JsonObject();
                        jsonObject.add("values", valuesJsonObject);
                        jsonObject.addProperty("active", module.isActive());
                        modulesJsonObject.add(module.getName(), jsonObject);
                    }
                    try (final FileWriter fw = new FileWriter(file)) {
                        fw.write(GSON.toJson(modulesJsonObject));
                        fw.flush();
                        ChatUtil.infoChatMessage("Config " + name + " has been saved.");
                    } catch (final Exception e) {
                        ChatUtil.errorChatMessage("Failed to save config: " + name);
                        Vandalism.getInstance().getLogger().error("Failed to save config: {}", name, e);
                    }
                } catch (final IOException e) {
                    ChatUtil.errorChatMessage("Failed to save config: " + name);
                    Vandalism.getInstance().getLogger().error("Failed to save config: {}", name, e);
                }
            } catch (final Exception e) {
                ChatUtil.errorChatMessage("Failed to save config: " + name);
                Vandalism.getInstance().getLogger().error("Failed to save config: {}", name, e);
            }
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("load").then(argument("config-name", ConfigArgumentType.create(CONFIGS_DIR)).executes(context -> {
            this.loadConfig(ConfigArgumentType.get(context), true);
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("load").then(argument("config-name", ConfigArgumentType.create(CONFIGS_DIR)).then(argument("activate-modules", BoolArgumentType.bool()).executes(context -> {
            this.loadConfig(ConfigArgumentType.get(context), BoolArgumentType.getBool(context, "activate-modules"));
            return SINGLE_SUCCESS;
        }))));
        builder.then(literal("delete").then(argument("config-name", ConfigArgumentType.create(CONFIGS_DIR)).executes(context -> {
            try {
                final File file = ConfigArgumentType.get(context);
                final String name = StringUtils.replaceLast(file.getName(), ".json", "");
                ChatUtil.infoChatMessage("Deleting config " + name + "...");
                if (file.delete()) {
                    ChatUtil.infoChatMessage("Config " + name + " has been deleted.");
                } else {
                    ChatUtil.errorChatMessage("Failed to delete config " + name + ".");
                }
            } catch (final Exception e) {
                ChatUtil.errorChatMessage("Failed to delete config.");
                Vandalism.getInstance().getLogger().error("Failed to delete config.", e);
            }
            return SINGLE_SUCCESS;
        })));
    }

    private void loadConfig(final File file, final boolean activateModules) {
        final String name = StringUtils.replaceLast(file.getName(), ".json", "");
        ChatUtil.infoChatMessage("Loading config " + name + "...");
        if (file.exists()) {
            try (final FileReader fr = new FileReader(file)) {
                final ModuleManager moduleManager = Vandalism.getInstance().getModuleManager();
                final JsonObject jsonObject = GSON.fromJson(fr, JsonObject.class);
                for (final Module module : moduleManager.getList()) {
                    if (module.getCategory() == Category.RENDER) {
                        continue;
                    }
                    final String moduleName = module.getName();
                    if (!jsonObject.has(moduleName)) {
                        for (final Value<?> value : module.getValues()) value.resetValue();
                        continue;
                    }
                    final JsonObject moduleJsonObject = jsonObject.getAsJsonObject(moduleName);
                    if (activateModules) {
                        if (moduleJsonObject != null && moduleJsonObject.has("active")) {
                            if (moduleJsonObject.get("active").getAsBoolean()) {
                                module.activate();
                            } else {
                                module.deactivate();
                            }
                        }
                    }
                    if (moduleJsonObject != null && moduleJsonObject.has("values")) {
                        ConfigWithValues.loadValues(moduleJsonObject.getAsJsonObject("values"), module.getValues());
                    }
                }
                ChatUtil.infoChatMessage("Config " + name + " has been loaded.");
            } catch (final Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to load config {}", file.getName(), e);
            }
        } else {
            ChatUtil.errorChatMessage("Config " + name + " does not exist.");
        }
    }

}
