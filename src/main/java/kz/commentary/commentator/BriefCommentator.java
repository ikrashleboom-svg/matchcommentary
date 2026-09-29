package kz.commentary.commentator;

import kz.commentary.model.EventType;
import kz.commentary.model.MatchEvent;
import kz.commentary.output.CommentaryOutput;
public class BriefCommentator extends MatchCommentator {
    public BriefCommentator(CommentaryOutput output) { super(output); }

    @Override
    protected boolean isWorthMentioning(MatchEvent e) {
        return e.type() == EventType.GOAL || e.type() == EventType.RED_CARD;
    }

    @Override
    protected String describe(MatchEvent e) {
        return e.type() == EventType.GOAL
                ? e.minute() + "' GOAL! " + e.player() + " scores for " + e.team() + "."
                : e.minute() + "' Red card for " + e.player() + " (" + e.team() + ").";
    }
}
