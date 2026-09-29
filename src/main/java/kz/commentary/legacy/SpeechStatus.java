package kz.commentary.legacy;
public final class SpeechStatus {
    public static final int OK = 0;
    public static final int ENGINE_BUSY = 5;
    public static final int UNSUPPORTED_LANG = 7;
    public static final int TEXT_TOO_LONG = 9;

    private final int code;

    public SpeechStatus(int code) { this.code = code; }

    public int code() { return code; }
    public boolean isOk() { return code == OK; }
}
