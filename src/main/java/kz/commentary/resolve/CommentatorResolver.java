package kz.commentary.resolve;

import kz.commentary.commentator.MatchCommentator;
import kz.commentary.output.CommentaryOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
public class CommentatorResolver {
    private final Map<String, Function<CommentaryOutput, MatchCommentator>> factories = new HashMap<>();

    public CommentatorResolver register(String style, Function<CommentaryOutput, MatchCommentator> factory) {
        factories.put(style.toLowerCase(), factory);
        return this;
    }

    public MatchCommentator create(String style, CommentaryOutput output) {
        Function<CommentaryOutput, MatchCommentator> f = factories.get(style.toLowerCase());
        if (f == null) throw new IllegalArgumentException("Unknown commentator style: " + style);
        return f.apply(output);
    }
}
