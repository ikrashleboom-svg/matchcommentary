package kz.commentary.commentator;

import kz.commentary.model.MatchEvent;
import kz.commentary.output.CommentaryOutput;

public class AccessibleCommentator extends DetailedCommentator {
    public AccessibleCommentator(CommentaryOutput output) { super(output); }

    @Override
    protected String describe(MatchEvent e) {
        String base = super.describe(e).replaceFirst("^\\d+' ", "");
        String scoreText = score().isEmpty() ? "No goals yet." : "Score: " + scoreLine() + ".";
        return "Minute " + e.minute() + ". " + base + " " + scoreText;
    }
}
