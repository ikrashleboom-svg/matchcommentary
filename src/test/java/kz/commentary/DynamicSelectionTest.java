package kz.commentary;

import kz.commentary.adapter.SpeechOutput;
import kz.commentary.commentator.*;
import kz.commentary.legacy.LegacySpeechEngine;
import kz.commentary.model.*;
import kz.commentary.output.*;
import kz.commentary.resolve.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class DynamicSelectionTest {

    private OutputResolver outputs(Path file) {
        return new OutputResolver()
                .register("console", u -> new ConsoleOutput(new PrintStream(new ByteArrayOutputStream())))
                .register("srt", u -> new SubtitleFileOutput(file))
                .register("speech", u -> new SpeechOutput(new LegacySpeechEngine(), Locale.forLanguageTag(u.getHost())));
    }

    @Test
    void schemeOfTargetPicksOutput(@TempDir Path dir) throws Exception {
        OutputResolver r = outputs(dir.resolve("s.txt"));
        assertInstanceOf(ConsoleOutput.class, r.resolve("console:x"));
        assertInstanceOf(SubtitleFileOutput.class, r.resolve("srt:///tmp/x"));
        assertInstanceOf(SpeechOutput.class, r.resolve("speech://en"));
    }

    @Test
    void unknownSchemeIsRejected(@TempDir Path dir) {
        assertThrows(IllegalArgumentException.class, () -> outputs(dir.resolve("s")).resolve("radio://fm"));
    }

    @Test
    void newOutputCanBeAddedWithoutChangingExistingClasses(@TempDir Path dir) throws Exception {
        CommentaryOutput custom = (text, tone) -> { };
        assertSame(custom, outputs(dir.resolve("s")).register("custom", u -> custom).resolve("custom://a"));
    }

    @Test
    void everyStyleWorksOnEveryOutputEndToEnd(@TempDir Path dir) throws Exception {
        CommentatorResolver styles = new CommentatorResolver()
                .register("brief", BriefCommentator::new)
                .register("detailed", DetailedCommentator::new)
                .register("accessible", AccessibleCommentator::new);
        MatchEvent goal = new MatchEvent(10, EventType.GOAL, "A", "P");
        for (String target : new String[]{"console:x", "srt:///x", "speech://en"}) {
            for (String style : new String[]{"brief", "detailed", "accessible"}) {
                MatchCommentator c = styles.create(style, outputs(dir.resolve(style + ".txt")).resolve(target));
                assertEquals(Outcome.DELIVERED, c.onEvent(goal), style + " on " + target);
            }
        }
        assertTrue(Files.exists(dir.resolve("brief.txt")));
    }

    @Test
    void speechFailureSurfacesAsFailedOutcomeThroughTheWholeBridge() throws Exception {
        LegacySpeechEngine engine = new LegacySpeechEngine();
        engine.setDeviceMissing(true);
        MatchCommentator c = new BriefCommentator(new SpeechOutput(engine, Locale.ENGLISH));

        assertEquals(Outcome.FAILED, c.onEvent(new MatchEvent(1, EventType.GOAL, "A", "P")));
    }
}
