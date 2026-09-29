package kz.commentary.resolve;

import kz.commentary.output.CommentaryOutput;
import kz.commentary.output.OutputException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
public class OutputResolver {
    @FunctionalInterface
    public interface Factory { CommentaryOutput create(URI target) throws OutputException; }

    private final Map<String, Factory> factories = new HashMap<>();

    public OutputResolver register(String scheme, Factory factory) {
        factories.put(scheme.toLowerCase(), factory);
        return this;
    }

    public CommentaryOutput resolve(String target) throws OutputException {
        URI uri = URI.create(target);
        Factory f = factories.get(String.valueOf(uri.getScheme()).toLowerCase());
        if (f == null) throw new IllegalArgumentException("No output registered for target: " + target);
        return f.create(uri);
    }
}
