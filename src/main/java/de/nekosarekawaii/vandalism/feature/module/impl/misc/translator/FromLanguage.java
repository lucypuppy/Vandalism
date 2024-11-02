/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.module.impl.misc.translator;

import de.nekosarekawaii.vandalism.util.interfaces.IName;
import lombok.Getter;

public enum FromLanguage implements IName {

    AUTO_DETECT("Detect Language", "auto"),
    AFRIKAANS("Afrikaans", "af"),
    ARABIC("Arabic", "ar"),
    CZECH("Czech", "cs"),
    CHINESE_SIMPLIFIED("Chinese (simplified)", "zh-CN"),
    CHINESE_TRADITIONAL("Chinese (traditional)", "zh-TW"),
    DANISH("Danish", "da"),
    DUTCH("Dutch", "nl"),
    ENGLISH("English", "en"),
    FINNISH("Finnish", "fi"),
    FRENCH("French", "fr"),
    GERMAN("Deutsch", "de"),
    GREEK("Greek", "el"),
    HINDI("Hindi", "hi"),
    ITALIAN("Italian", "it"),
    JAPANESE("Japanese", "ja"),
    KOREAN("Korean", "ko"),
    NORWEGIAN("Norwegian", "no"),
    POLISH("Polish", "pl"),
    PORTUGUESE("Portugese", "pt"),
    RUSSIAN("Russian", "ru"),
    SPANISH("Spanish", "es"),
    SWAHILI("Swahili", "sw"),
    SWEDISH("Swedish", "sv"),
    TURKISH("Turkish", "tr");

    private final String name;

    @Getter
    private final String value;

    FromLanguage(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

}