package kz.commentary.output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
public class SubtitleFileOutput implements CommentaryOutput {
    private final Path file;
    private int counter = 0;

    public SubtitleFileOutput(Path file) { this.file = file; }

    @Override
    public void emit(String text, Tone tone) throws OutputException {
        String block = (++counter) + "\n[" + tone + "] " + text + "\n\n";
        try {
            Files.writeString(file, block, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new OutputUnavailableException("Cannot write subtitles to " + file, e);
        }
    }
}
