package me.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.util.string.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.List;

//TODO: Finish this ~ NekosAreKawaii
public class BugScraperMenu {

    private final static String
            url = "https://bugs.mojang.com/projects/MC/issues/",
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/116.0",
            searchResultStart =
                    "window.WRM=window.WRM||{};window.WRM._unparsedData=window.WRM._unparsedData||{};window.WRM._unparsedErrors=window.WRM._unparsedErrors||{};\n" +
                            "WRM._unparsedData[\"com.atlassian.jira.jira-issue-navigator-components:search-results\"]=\"",
            scriptTag = "script",
            searchResultEnd = "WRM._unparsedData";

    private final static ObjectArrayList<Bug> currentData = new ObjectArrayList<>();

    private static State currentState = State.WAITING_INPUT;

    public static void render() {
        if (ImGui.begin("Bug Scraper", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize)) {
            ImGui.setWindowSize(750, 750);
            ImGui.text("State: " + currentState.getMessage() + (currentState == State.PARSE_ERROR ? " (" + currentState.getId() + ")" : ""));
            if (ImGui.button("Scrape##bugscraper")) {
                new Thread(() -> {
                    currentState = State.SENDING_REQUEST;
                    try {
                        final Document document = Jsoup.connect(url).userAgent(userAgent).followRedirects(true).get();
                        currentState = State.PARSING_DATA;
                        final Elements scripts = document.select(scriptTag);
                        for (final Element script : scripts) {
                            final String scriptContent = script.html();
                            if (scriptContent.startsWith(searchResultStart)) {
                                try {
                                    final JsonElement jsonElement = JsonParser.parseString(getJson(scriptContent));
                                    if (jsonElement.isJsonObject()) {
                                        final JsonObject jsonObject = jsonElement.getAsJsonObject();
                                        if (jsonObject.has("response")) {
                                            final JsonObject response = jsonObject.getAsJsonObject("response");
                                            if (response.has("issueTable")) {
                                                final JsonObject issueTable = response.getAsJsonObject("issueTable");
                                                if (issueTable.has("table")) {
                                                    final JsonArray table = issueTable.getAsJsonArray("table");
                                                    boolean empty = true;
                                                    for (final JsonElement entry : table) {
                                                        if (entry.isJsonObject()) {
                                                            final JsonObject entryJsonObject = entry.getAsJsonObject();
                                                            if (entryJsonObject.has("type")) {
                                                                final JsonObject type = entryJsonObject.getAsJsonObject("type");
                                                                if (type.has("name")) {
                                                                    final String name = type.get("name").getAsString();
                                                                    if (name.equals("Bug")) {
                                                                        if (entryJsonObject.has("key")) {
                                                                            if (entryJsonObject.has("status")) {
                                                                                if (entryJsonObject.has("summary")) {
                                                                                    empty = false;
                                                                                    currentData.add(new Bug(
                                                                                            entryJsonObject.get("key").getAsString(),
                                                                                            entryJsonObject.get("status").getAsString(),
                                                                                            entryJsonObject.get("summary").getAsString()
                                                                                    ));
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (!empty) currentState = State.SUCCESS;
                                                    else currentState = State.PARSE_ERROR.setId(6);
                                                } else currentState = State.PARSE_ERROR.setId(5);
                                            } else currentState = State.PARSE_ERROR.setId(4);
                                        } else currentState = State.PARSE_ERROR.setId(3);
                                    } else currentState = State.PARSE_ERROR.setId(2);
                                } catch (final Exception e) {
                                    currentState = State.PARSE_ERROR.setId(1);
                                    Foxglove.getInstance().getLogger().error("Bug Scraper parsing error:", e);
                                }
                                break;
                            }
                        }
                    } catch (final Exception e) {
                        currentState = State.FAILED_FETCH;
                        Foxglove.getInstance().getLogger().error(currentState.getMessage(), e);
                    }
                }).start();
            }
            if (!currentData.isEmpty()) {
                if (ImGui.button("Clear##bugscraper")) {
                    currentData.clear();
                    currentState = State.WAITING_INPUT;
                }
                ImGui.text("Bugs: " + currentData.size());
                if (ImGui.beginListBox("##bugscraper", 730, 620)) {
                    for (final Bug bug : currentData) {
                        final String key = bug.key();
                        if (ImGui.beginListBox("##bugscraper" + key, 705, 50)) {
                            ImGui.text("[" + bug.status() + "] > " + key);
                            ImGui.sameLine();
                            if (ImGui.button("Open##bugscraper" + key)) {
                                final String link = url + key;
                                try {
                                    Util.getOperatingSystem().open(new URI(link));
                                } catch (final Exception e) {
                                    Foxglove.getInstance().getLogger().error("Failed to open bug link: " + link, e);
                                }
                            }
                            //TODO: Recode this.
                            final List<StringVisitable> versionNameLines = MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(
                                    bug.summary(),
                                    580, Style.EMPTY
                            );
                            for (final StringVisitable versionNameLine : versionNameLines) {
                                ImGui.text(versionNameLine.getString());
                            }
                            ImGui.endListBox();
                        }
                    }
                    ImGui.endListBox();
                }
            }
            ImGui.end();
        }
    }

    private static String getJson(final String scriptContent) {
        final String firstFix = StringUtils.replaceLast(
                scriptContent
                        .replace(searchResultStart, "")
                        .split(searchResultEnd)[0],
                "\";", ""
        );
        final StringBuilder sb = new StringBuilder();
        final char[] chars = firstFix.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (c == '\\') {
                final int nextCharIndex = i + 1;
                if (nextCharIndex < chars.length && chars[nextCharIndex] != '"') {
                    sb.append('#');
                }
                continue;
            }
            sb.append(c);
        }
        return sb.toString().replace("##\"", "\\\"").replace("#'", "'");
    }

    private enum State {

        WAITING_INPUT("Waiting for input..."),
        SENDING_REQUEST("Sending request..."),
        PARSING_DATA("Parsing data..."),
        SUCCESS("Success."),
        FAILED_FETCH("Failed, fetching error."),
        PARSE_ERROR("Failed, parsing error.");

        private final String message;

        private int id;

        State(final String message) {
            this.message = message;
            this.id = 1;
        }

        public String getMessage() {
            return this.message;
        }

        public int getId() {
            return this.id;
        }

        public State setId(final int id) {
            this.id = id;
            return this;
        }

    }

    private record Bug(String key, String status, String summary) {
    }

}
