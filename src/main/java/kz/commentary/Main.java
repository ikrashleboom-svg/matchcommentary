package kz.commentary;

import kz.commentary.adapter.SpeechOutput;
import kz.commentary.commentator.*;
import kz.commentary.legacy.LegacySpeechEngine;
import kz.commentary.model.*;
import kz.commentary.output.*;
import kz.commentary.resolve.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
public class Main {
    public static void main(String[] args) throws Exception {
        String style = args.length > 0 ? args[0] : "accessible";
        String target = args.length > 1 ? args[1] : "speech://en";

        OutputResolver outputs = new OutputResolver()
                .register("console", u -> new ConsoleOutput(System.out))
                .register("srt", u -> new SubtitleFileOutput(Path.of(u.getPath())))
                .register("speech", u -> new SpeechOutput(new LegacySpeechEngine(), Locale.forLanguageTag(u.getHost())));
        CommentatorResolver styles = new CommentatorResolver()
                .register("brief", BriefCommentator::new)
                .register("detailed", DetailedCommentator::new)
                .register("accessible", AccessibleCommentator::new);

        MatchCommentator commentator = styles.create(style, outputs.resolve(target));

        List<MatchEvent> match = List.of(
                new MatchEvent(12, EventType.CORNER, "Kairat", null),
                new MatchEvent(23, EventType.GOAL, "Kairat", "Satpaev"),
                new MatchEvent(41, EventType.YELLOW_CARD, "Astana", "Tomasov"),
                new MatchEvent(67, EventType.GOAL, "Astana", "Kenzhebek"),
                new MatchEvent(88, EventType.RED_CARD, "Kairat", "Ospanov"));
        for (MatchEvent e : match) System.out.println(commentator.onEvent(e) + " <- " + e);
    }
}
