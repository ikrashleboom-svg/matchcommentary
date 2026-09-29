package kz.commentary;

import kz.commentary.commentator.*;
import kz.commentary.model.*;
import kz.commentary.output.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CommentatorDelegationTest {
    @Mock CommentaryOutput output;

    static final MatchEvent GOAL = new MatchEvent(23, EventType.GOAL, "Kairat", "Satpaev");
    static final MatchEvent CORNER = new MatchEvent(12, EventType.CORNER, "Kairat", null);

    @Test
    void briefCommentatorDeliversGoalWithExcitedTone() throws Exception {
        Outcome o = new BriefCommentator(output).onEvent(GOAL);

        verify(output).emit(contains("Satpaev"), eq(Tone.EXCITED));
        assertEquals(Outcome.DELIVERED, o);
    }

    @Test
    void briefCommentatorSkipsCornerWithoutTouchingOutput() {
        Outcome o = new BriefCommentator(output).onEvent(CORNER);

        verifyNoInteractions(output);
        assertEquals(Outcome.SKIPPED, o);
    }

    @Test
    void detailedCommentatorDeliversCornerWithCalmTone() throws Exception {
        new DetailedCommentator(output).onEvent(CORNER);

        verify(output).emit(contains("Corner kick"), eq(Tone.CALM));
    }

    @Test
    void accessibleCommentatorAnnouncesRunningScore() throws Exception {
        MatchCommentator c = new AccessibleCommentator(output);
        c.onEvent(GOAL);
        c.onEvent(new MatchEvent(67, EventType.GOAL, "Astana", "Kenzhebek"));

        verify(output).emit(contains("Score: Kairat 1, Astana 1."), any());
    }

    @Test
    void outputFailureBecomesFailedOutcomeNotACrash() throws Exception {
        doThrow(new OutputUnavailableException("down")).when(output).emit(anyString(), any());

        assertEquals(Outcome.FAILED, new DetailedCommentator(output).onEvent(GOAL));
    }
}
