package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.ChatReceiveListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.script.parse.ScriptVariable;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TrashTalkModule extends AbstractModule implements ChatReceiveListener {

    private final Map<String[], String[]> contentMap = new HashMap<>();

    private final File contentFile = new File(Vandalism.getInstance().getRunDirectory(), "trash_talk.txt");

    public TrashTalkModule() {
        super(
                "Trash Talk",
                "If activated the mod will react to certain words in the chat and will answer with a funny message.",
                Category.MISC
        );
    }

    @Override
    public void onEnable() {
        this.setup();
        DietrichEvents2.global().subscribe(ChatReceiveEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(ChatReceiveEvent.ID, this);
        this.contentMap.clear();
    }

    private void setup() {
        if (!this.contentFile.exists()) {
            try {
                final PrintWriter printWriter = new PrintWriter(this.contentFile);
                printWriter.println("'%mod_name%', 'best client' > '%mod_name% is the best client!', 'I love %mod_name%!'");
                printWriter.println("'cool', 'nice', 'awesome' > 'Looser', 'Cringe'");
                printWriter.close();
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to create trash talk file!", e);
            }
        }
        if (this.contentMap.isEmpty()) {
            try {
                final Scanner scanner = new Scanner(this.contentFile);
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    if (line.contains(">")) {
                        final String[] parts = line.split(">");
                        String triggersString = parts[0].trim();
                        String responsesString = parts[1].trim();
                        triggersString = triggersString.substring(1, triggersString.length() - 1);
                        responsesString = responsesString.substring(1, responsesString.length() - 1);
                        final String[] triggers = triggersString.split("', '");
                        final String[] responses = responsesString.split("', '");
                        for (int i = 0; i < triggers.length; i++) {
                            triggers[i] = triggers[i].trim();
                        }
                        for (int i = 0; i < responses.length; i++) {
                            responses[i] = responses[i].trim();
                        }
                        this.contentMap.put(triggers, responses);
                    }
                }
                scanner.close();
            } catch (Exception e) {
                Vandalism.getInstance().getLogger().error("Failed to load trash talk file!", e);
            }
        }
    }

    @Override
    public void onChatReceive(final ChatReceiveEvent event) {
        final String message = event.text.getString();
        if (StringUtils.contains(message, this.mc.session.getUsername())) {
            return;
        }
        this.setup();
        this.contentMap.forEach((words, answers) -> {
            for (final String word : words) {
                if (StringUtils.contains(ScriptVariable.applyReplacements(message), word)) {
                    String answer;
                    if (answers.length == 1) {
                        answer = answers[0];
                    } else {
                        answer = answers[RandomUtils.randomInt(0, answers.length)];
                    }
                    this.mc.getNetworkHandler().sendChatMessage(ScriptVariable.applyReplacements(answer));
                    break;
                }
            }
        });
    }

}
