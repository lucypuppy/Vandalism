/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonscripts.parse;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.addonscripts.base.Script;
import de.nekosarekawaii.vandalism.addonscripts.parse.command.ScriptCommand;
import de.nekosarekawaii.vandalism.addonscripts.parse.info.ScriptInfo;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import net.minecraft.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScriptParser {

    public static final String SCRIPT_FILE_EXTENSION = "." + FabricBootstrap.MOD_ID + "-script";

    public static final String INFO_CHAR = "@", CODE_CHAR = "!", VARIABLE_CHAR = "%";

    public static Pair<ScriptCommand, Pair<Integer, String>> parseCodeFromScriptLine(final String scriptName, String line, final int lineNumber, final boolean advancedErrors) throws RuntimeException {
        if (line.startsWith(CODE_CHAR)) {
            if (line.startsWith(CODE_CHAR + CODE_CHAR)) line = line.replaceFirst(CODE_CHAR + CODE_CHAR, CODE_CHAR);
            for (final ScriptCommand scriptCommand : ScriptCommand.values()) {
                final String commandStart = CODE_CHAR + scriptCommand.name().toLowerCase();
                if (line.split("( )+")[0].equals(commandStart)) {
                    String code;
                    final String command = line.substring(commandStart.length() - 1).trim();
                    final String[] commandCodePair = command.split(" ", 2);
                    if (commandCodePair.length >= 2) {
                        code = commandCodePair[1];
                    } else if (commandCodePair.length == 1) {
                        code = "";
                    } else {
                        throw new RuntimeException("Invalid script command '" + line + "' " + (advancedErrors ? "in script '" + scriptName + "' at line " + lineNumber : ""));
                    }
                    try {
                        scriptCommand.test(scriptName, lineNumber, code);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return new Pair<>(scriptCommand, new Pair<>(lineNumber, code));
                }
            }
        }
        return null;
    }

    public static List<Pair<ScriptCommand, Pair<Integer, String>>> parseCodeFromScriptFile(final File file) throws RuntimeException {
        final String name = StringUtils.replaceLast(file.getName(), SCRIPT_FILE_EXTENSION, "");
        int lineNumber = 0;
        try {
            final Scanner scanner = new Scanner(file);
            final List<Pair<ScriptCommand, Pair<Integer, String>>> code = new ArrayList<>();
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                lineNumber++;
                if (!line.startsWith(CODE_CHAR)) continue;
                final Pair<ScriptCommand, Pair<Integer, String>> command = parseCodeFromScriptLine(name, line, lineNumber, true);
                if (command == null) {
                    throw new RuntimeException("Failed to parse script code line '" + line + "' in script '" + name + "' at line " + lineNumber);
                }
                code.add(command);
            }
            scanner.close();
            return code;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse script code line in script '" + name + "' at line " + lineNumber + " due to an exception: " + e.getMessage());
        }
    }

    public static Object parseInfoFromScriptLine(final String scriptName, String line, final int lineNumber, final boolean advancedErrors) throws RuntimeException {
        if (line.startsWith(INFO_CHAR)) {
            if (line.startsWith(INFO_CHAR + INFO_CHAR)) line = line.replaceFirst(INFO_CHAR + INFO_CHAR, INFO_CHAR);
            for (final ScriptInfo info : ScriptInfo.values()) {
                if (line.split("( )+")[0].equals(INFO_CHAR + info.getTag())) {
                    final String[] split = line.substring(info.getTag().length() - 1).trim().split(" ", 2);
                    if (split.length != 2) {
                        throw new RuntimeException("Invalid script info '" + line + "' " + (advancedErrors ? "in script '" + scriptName + "' at line " + lineNumber : ""));
                    }
                    try {
                        return info.parseValue(split[1]);
                    } catch (Exception exception) {
                        throw new RuntimeException("Failed to parse script info '" + line + "' " + (advancedErrors ? "in script '" + scriptName + "' at line " + lineNumber : "") + " due to an exception: " + exception.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public static Script parseScriptObjectFromFile(final File file) throws RuntimeException {
        final String name = StringUtils.replaceLast(file.getName(), SCRIPT_FILE_EXTENSION, "");
        int lineNumber = 0;
        String version = ScriptInfo.VERSION.getDefaultValue(), author = ScriptInfo.AUTHOR.getDefaultValue(), description = ScriptInfo.DESCRIPTION.getDefaultValue();
        Feature.Category category = ScriptInfo.CATEGORY.getDefaultValue();
        boolean experimental = ScriptInfo.EXPERIMENTAL.getDefaultValue();
        try {
            final Scanner scanner = new Scanner(file);
            for (final ScriptInfo scriptInfo : ScriptInfo.values()) {
                if (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    final Object value = parseInfoFromScriptLine(name, line, 1, true);
                    if (value != null) {
                        switch (scriptInfo) {
                            case VERSION -> version = (String) value;
                            case AUTHOR -> author = (String) value;
                            case DESCRIPTION -> description = (String) value;
                            case CATEGORY -> category = (Feature.Category) value;
                            case EXPERIMENTAL -> experimental = (Boolean) value;
                            default -> {
                            }
                        }
                    } else {
                        throw new RuntimeException("Failed to parse script info '" + line + "' in script '" + name + "' at line " + lineNumber);
                    }
                }
                lineNumber++;
            }
            scanner.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse script info at line " + lineNumber + " due to an exception: " + e.getMessage());
        }
        final Script script = new Script(name, description, category, file, version, author);
        if (experimental) script.markExperimental();
        return script;
    }

}
