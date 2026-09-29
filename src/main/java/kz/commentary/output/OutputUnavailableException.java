package kz.commentary.output;
public class OutputUnavailableException extends OutputException {
    public OutputUnavailableException(String message) { super(message); }
    public OutputUnavailableException(String message, Throwable cause) { super(message, cause); }
}
