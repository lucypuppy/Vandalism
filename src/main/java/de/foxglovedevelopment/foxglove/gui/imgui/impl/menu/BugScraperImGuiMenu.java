package de.foxglovedevelopment.foxglove.gui.imgui.impl.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.gui.imgui.ImGuiMenu;
import de.foxglovedevelopment.foxglove.util.EnumNameNormalizer;
import de.foxglovedevelopment.foxglove.util.StringUtils;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.util.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//TODO: Add the rest of searching and filtering.
public class BugScraperImGuiMenu extends ImGuiMenu {

    private final String url, userAgent, searchResultStart, scriptTag, searchResultEnd;

    private final List<Bug> currentData = new ArrayList<>();

    private State currentState = State.WAITING_INPUT;

    private Filter currentFilter = Filter.NONE;

    public BugScraperImGuiMenu() {
        super("Bug Scraper");
        this.url = "https://bugs.mojang.com/projects/MC/issues/";
        this.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/116.0";
        this.searchResultStart = "window.WRM=window.WRM||{};window.WRM._unparsedData=window.WRM._unparsedData||{};window.WRM._unparsedErrors=window.WRM._unparsedErrors||{};\nWRM._unparsedData[\"com.atlassian.jira.jira-issue-navigator-components:search-results\"]=\"";
        this.scriptTag = "script";
        this.searchResultEnd = "WRM._unparsedData";
    }

    @Override
    public void render() {
        if (ImGui.begin("Bug Scraper", ImGuiWindowFlags.NoCollapse)) {
            ImGui.text("State: " + this.currentState.getMessage());
            if (ImGui.beginCombo("Filter##bugscraper", this.currentFilter.normalName())) {
                for (final Filter filter : Filter.values()) {
                    if (ImGui.selectable(filter.normalName(), filter.normalName().equals(this.currentFilter.normalName()))) {
                        this.currentFilter = filter;
                    }
                }
                ImGui.endCombo();
            }
            if (ImGui.button("Scrape##bugscraper")) {
                this.currentData.clear();
                new Thread(() -> {
                    this.currentState = State.SENDING_REQUEST;
                    try {
                        final Document document = Jsoup.connect(this.url + (this.currentFilter != Filter.NONE ? "?filter=" + this.currentFilter.getValue() : "")).userAgent(this.userAgent).followRedirects(true).get();
                        this.currentState = State.PARSING_DATA;
                        final Elements scripts = document.select(this.scriptTag);
                        for (final Element script : scripts) {
                            final String scriptContent = script.html();
                            if (scriptContent.startsWith(this.searchResultStart)) {
                                try {
                                    final JsonElement jsonElement = JsonParser.parseString(getJson(scriptContent));
                                    final JsonObject
                                            jsonObject = jsonElement.getAsJsonObject(),
                                            response = jsonObject.getAsJsonObject("response"),
                                            issueTable = response.getAsJsonObject("issueTable");
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
                                                                    this.currentData.add(new Bug(
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
                                    if (!empty) this.currentState = State.SUCCESS;
                                    else this.currentState = State.PARSE_ERROR;
                                } catch (final Exception e) {
                                    this.currentState = State.PARSE_ERROR;
                                    Foxglove.getInstance().getLogger().error("Bug Scraper parsing error:", e);
                                }
                                break;
                            }
                        }
                    } catch (final Exception e) {
                        this.currentState = State.FAILED_FETCH;
                        Foxglove.getInstance().getLogger().error(this.currentState.getMessage(), e);
                    }
                }).start();
            }
            if (!this.currentData.isEmpty()) {
                if (ImGui.button("Clear##bugscraper")) {
                    this.currentData.clear();
                    this.currentState = State.WAITING_INPUT;
                }
                ImGui.text("Bugs: " + this.currentData.size());
                if (ImGui.beginListBox("##bugscraper", 730, 620)) {
                    for (final Bug bug : this.currentData) {
                        final String key = bug.key();
                        if (ImGui.beginListBox("##bugscraper" + key, 705, 50)) {
                            ImGui.text("[" + bug.status() + "] > " + key);
                            ImGui.sameLine();
                            if (ImGui.button("Open##bugscraper" + key)) {
                                final String link = this.url + key;
                                try {
                                    Util.getOperatingSystem().open(new URI(link));
                                } catch (final Exception e) {
                                    Foxglove.getInstance().getLogger().error("Failed to open bug link: " + link, e);
                                }
                            }
                            ImGui.pushTextWrapPos(ImGui.getCursorPos().x + 685);
                            ImGui.text(bug.summary());
                            ImGui.popTextWrapPos();
                            ImGui.endListBox();
                        }
                    }
                    ImGui.endListBox();
                }
            }
            ImGui.end();
        }
    }

    private String getJson(final String scriptContent) {
        final String firstFix = StringUtils.replaceLast(
                scriptContent
                        .replace(this.searchResultStart, "")
                        .split(this.searchResultEnd)[0],
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

        State(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

    }

    private record Bug(String key, String status, String summary) {
    }

    private enum Filter implements EnumNameNormalizer {

        NONE(""),
        ALL_ISSUES("allissues"),
        ALL_OPEN_ISSUES("allopenissues"),
        DONE_ISSUES("doneissues"),
        ADDED_RECENTLY("addedrecently"),
        RESOLVED_RECENTLY("resolvedrecently"),
        UPDATED_RECENTLY("updatedrecently");

        private final String normalName, value;

        Filter(final String value) {
            this.normalName = this.normalizeName(this.name());
            this.value = value;
        }

        @Override
        public String normalName() {
            return this.normalName;
        }

        public String getValue() {
            return this.value;
        }

    }

}
