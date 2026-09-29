package kz.commentary.adapter;

import kz.commentary.legacy.AudioDeviceException;
import kz.commentary.legacy.LegacySpeechEngine;
import kz.commentary.legacy.SpeechStatus;
import kz.commentary.output.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class SpeechOutput implements CommentaryOutput {
    static final int CALM_WPM = 150;
    static final int EXCITED_WPM = 210;

    private final LegacySpeechEngine engine;
    private final int langCode;

    /** @throws OutputRejectedException if the language has no vendor code */
    public SpeechOutput(LegacySpeechEngine engine, Locale locale) throws OutputRejectedException {
        this.engine = engine;
        this.langCode = switch (locale.getLanguage()) {
            case "en" -> LegacySpeechEngine.LANG_EN;
            case "ru" -> LegacySpeechEngine.LANG_RU;
            default -> throw new OutputRejectedException("Unsupported language: " + locale.getLanguage());
        };
    }

    @Override
    public void emit(String text, Tone tone) throws OutputException {
        int wpm = tone == Tone.EXCITED ? EXCITED_WPM : CALM_WPM;
        for (String part : split(text)) {
            SpeechStatus status;
            try {
                status = engine.speak(wpm, langCode, part);
            } catch (AudioDeviceException e) {
                throw new OutputUnavailableException("No audio device", e);
            }
            translate(status);
        }
    }

    private static void translate(SpeechStatus status) throws OutputException {
        if (status == null) throw new OutputUnavailableException("Speech engine gave no status");
        switch (status.code()) {
            case SpeechStatus.OK -> { }
            case SpeechStatus.ENGINE_BUSY -> throw new OutputUnavailableException("Speech engine busy");
            case SpeechStatus.UNSUPPORTED_LANG -> throw new OutputRejectedException("Language not supported by speech engine");
            case SpeechStatus.TEXT_TOO_LONG -> throw new OutputRejectedException("Text too long for speech engine");
            default -> throw new OutputUnavailableException("Speech engine error " + status.code());
        }
    }

    static List<String> split(String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        for (String word : text.split(" ")) {
            if (cur.length() > 0 && cur.length() + 1 + word.length() > LegacySpeechEngine.MAX_LENGTH) {
                parts.add(cur.toString());
                cur.setLength(0);
            }
            if (cur.length() > 0) cur.append(' ');
            cur.append(word);
        }
        if (cur.length() > 0) parts.add(cur.toString());
        return parts;
    }
}
