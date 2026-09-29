package kz.commentary;

import kz.commentary.adapter.SpeechOutput;
import kz.commentary.legacy.*;
import kz.commentary.output.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class SpeechOutputTest {
    @Mock LegacySpeechEngine engine;
    SpeechOutput adapter;

    @BeforeEach
    void setUp() throws Exception { adapter = new SpeechOutput(engine, Locale.ENGLISH); }

    private static SpeechStatus status(int code) { return new SpeechStatus(code); }

    @Test
    void emitCallsVendorWithSpeedFirstAndLanguageCode() throws Exception {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(SpeechStatus.OK));

        adapter.emit("Goal!", Tone.EXCITED);

        verify(engine).speak(210, LegacySpeechEngine.LANG_EN, "Goal!");
    }

    @Test
    void calmToneUsesSlowerSpeed() throws Exception {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(SpeechStatus.OK));
        adapter.emit("Corner", Tone.CALM);
        verify(engine).speak(eq(150), anyInt(), eq("Corner"));
    }

    @Test
    void russianLocaleMapsToRussianCode() throws Exception {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(0));
        new SpeechOutput(engine, Locale.forLanguageTag("ru")).emit("Гол", Tone.CALM);
        verify(engine).speak(anyInt(), eq(LegacySpeechEngine.LANG_RU), anyString());
    }

    @Test
    void unsupportedLocaleIsRejectedAtConstruction() {
        assertThrows(OutputRejectedException.class, () -> new SpeechOutput(engine, Locale.forLanguageTag("kk")));
    }

    @Test
    void longTextIsSplitIntoSeveralUtterancesWithinVendorLimit() throws Exception {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(0));
        String longText = "word ".repeat(60).trim();          // 299 chars

        adapter.emit(longText, Tone.CALM);

        verify(engine, atLeast(3)).speak(anyInt(), anyInt(),
                argThat(s -> s.length() <= LegacySpeechEngine.MAX_LENGTH));
    }

    // ---- status object -> exception ----
    @Test
    void busyStatusBecomesUnavailable() {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(SpeechStatus.ENGINE_BUSY));
        assertThrows(OutputUnavailableException.class, () -> adapter.emit("x", Tone.CALM));
    }

    @Test
    void unsupportedLanguageStatusBecomesRejected() {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(SpeechStatus.UNSUPPORTED_LANG));
        assertThrows(OutputRejectedException.class, () -> adapter.emit("x", Tone.CALM));
    }

    @Test
    void textTooLongStatusBecomesRejected() {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(SpeechStatus.TEXT_TOO_LONG));
        assertThrows(OutputRejectedException.class, () -> adapter.emit("x", Tone.CALM));
    }

    @Test
    void unknownStatusCodeIsNotSwallowed() {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(status(999));
        assertThrows(OutputUnavailableException.class, () -> adapter.emit("x", Tone.CALM));
    }

    @Test
    void nullStatusIsNotSwallowed() {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenReturn(null);
        assertThrows(OutputUnavailableException.class, () -> adapter.emit("x", Tone.CALM));
    }

    @Test
    void missingAudioDeviceBecomesUnavailableAndKeepsCause() {
        when(engine.speak(anyInt(), anyInt(), anyString())).thenThrow(new AudioDeviceException("none"));

        OutputException e = assertThrows(OutputUnavailableException.class, () -> adapter.emit("x", Tone.CALM));

        assertInstanceOf(AudioDeviceException.class, e.getCause());
    }
}
