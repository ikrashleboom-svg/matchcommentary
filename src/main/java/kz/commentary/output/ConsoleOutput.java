package kz.commentary.output;

import java.io.PrintStream;
public class ConsoleOutput implements CommentaryOutput {
    private final PrintStream out;

    public ConsoleOutput(PrintStream out) { this.out = out; }

    @Override
    public void emit(String text, Tone tone) {
        out.println((tone == Tone.EXCITED ? "!! " : "   ") + text);
    }
}
