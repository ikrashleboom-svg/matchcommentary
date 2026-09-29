package kz.commentary.commentator;

import kz.commentary.model.EventType;
import kz.commentary.model.MatchEvent;
import kz.commentary.output.CommentaryOutput;
import kz.commentary.output.OutputException;
import kz.commentary.output.Tone;

import java.util.LinkedHashMap;
import java.util.Map;
public abstract class MatchCommentator {
    protected final CommentaryOutput output;
    private final Map<String, Integer> score = new LinkedHashMap<>();

    protected MatchCommentator(CommentaryOutput output) {
        this.output = output;
    }

    public final Outcome onEvent(MatchEvent event) {
        if (event.type() == EventType.GOAL) score.merge(event.team(), 1, Integer::sum);
        if (!isWorthMentioning(event)) return Outcome.SKIPPED;
        try {
            output.emit(describe(event), toneFor(event));
            return Outcome.DELIVERED;
        } catch (OutputException e) {
            return Outcome.FAILED;
        }
    }

    protected Map<String, Integer> score() { return score; }

    protected String scoreLine() {
        StringBuilder sb = new StringBuilder();
        score.forEach((team, goals) -> sb.append(sb.length() == 0 ? "" : ", ").append(team).append(' ').append(goals));
        return sb.toString();
    }

    protected abstract boolean isWorthMentioning(MatchEvent e);

    protected abstract String describe(MatchEvent e);

    protected Tone toneFor(MatchEvent e) {
        return e.type() == EventType.GOAL || e.type() == EventType.RED_CARD ? Tone.EXCITED : Tone.CALM;
    }
}
