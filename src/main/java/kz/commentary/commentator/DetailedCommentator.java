package kz.commentary.commentator;

import kz.commentary.model.MatchEvent;
import kz.commentary.output.CommentaryOutput;
public class DetailedCommentator extends MatchCommentator {
    public DetailedCommentator(CommentaryOutput output) { super(output); }

    @Override protected boolean isWorthMentioning(MatchEvent e) { return true; }

    @Override
    protected String describe(MatchEvent e) {
        String who = e.player() + " of " + e.team();
        return e.minute() + "' " + switch (e.type()) {
            case GOAL -> "Goal! " + who + " puts the ball in the net.";
            case YELLOW_CARD -> "The referee shows a yellow card to " + who + ".";
            case RED_CARD -> "Red card! " + who + " is sent off.";
            case SHOT -> who + " takes a shot.";
            case CORNER -> "Corner kick for " + e.team() + ".";
            case SUBSTITUTION -> e.player() + " comes on for " + e.team() + ".";
        };
    }
}
