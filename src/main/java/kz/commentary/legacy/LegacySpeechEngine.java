package kz.commentary.legacy;
public class LegacySpeechEngine {
    public static final int LANG_EN = 1;
    public static final int LANG_RU = 2;
    public static final int MAX_LENGTH = 120;

    private boolean busy = false;
    private boolean deviceMissing = false;

    public SpeechStatus speak(int wordsPerMinute, int langCode, String utterance) {
        if (deviceMissing) throw new AudioDeviceException("No audio device");
        if (busy) return new SpeechStatus(SpeechStatus.ENGINE_BUSY);
        if (langCode != LANG_EN && langCode != LANG_RU) return new SpeechStatus(SpeechStatus.UNSUPPORTED_LANG);
        if (utterance.length() > MAX_LENGTH) return new SpeechStatus(SpeechStatus.TEXT_TOO_LONG);
        System.out.println("[TTS " + wordsPerMinute + "wpm lang" + langCode + "] " + utterance);
        return new SpeechStatus(SpeechStatus.OK);
    }
    public void setBusy(boolean busy) { this.busy = busy; }
    public void setDeviceMissing(boolean missing) { this.deviceMissing = missing; }
}
