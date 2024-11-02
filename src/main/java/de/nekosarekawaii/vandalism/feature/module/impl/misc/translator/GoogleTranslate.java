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

import de.nekosarekawaii.vandalism.Vandalism;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleTranslate {

    private static final String GOOGLE_TRANSLATE_URL = "https://translate.google.com/m?hl=en&sl=%s&tl=%s&ie=UTF-8&prev=_m&q=%s";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();

    private static final Pattern TRANSLATION_PATTERN = Pattern.compile(
            "class=\"result-container\">([^<]*)<\\/div>",
            Pattern.MULTILINE
    );

    public static Optional<String> translate(final String text, final FromLanguage langFrom, final ToLanguage langTo) {
        final String html = getHTML(text, langFrom, langTo);
        if (html == null) return Optional.empty();
        final String translated = parseHTML(html);
        if (text.equalsIgnoreCase(translated)) {
            return Optional.empty();
        }
        return Optional.ofNullable(translated);
    }

    private static String getHTML(final String text, final FromLanguage langFrom, final ToLanguage langTo) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            final URI uri = createURI(text, langFrom, langTo);
            final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            final HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                Vandalism.getInstance().getLogger().error("Failed to fetch translation from Google Translate. Status code: {}", response.statusCode());
                return null;
            }
            return response.body();
        } catch (final Exception e) {
            Vandalism.getInstance().getLogger().error("Failed to fetch translation from Google Translate.", e);
            return null;
        }
    }

    private static URI createURI(final String text, final FromLanguage langFrom, final ToLanguage langTo) {
        try {
            final String encodedText = URLEncoder.encode(text.trim(), StandardCharsets.UTF_8);
            final String urlString = String.format(
                    GOOGLE_TRANSLATE_URL,
                    langFrom.getValue(), langTo.getValue(), encodedText
            );
            return URI.create(urlString);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException("Invalid URL format.", e);
        }
    }

    private static String parseHTML(final String html) {
        final Matcher matcher = TRANSLATION_PATTERN.matcher(html);
        if (matcher.find()) {
            final String match = matcher.group(1);
            if (match == null || match.isEmpty()) {
                return null;
            }
            return StringEscapeUtils.unescapeHtml4(match);
        }
        return null;
    }

}