package kz.commentary.output;
public interface CommentaryOutput {
    void emit(String text, Tone tone) throws OutputException;
}
