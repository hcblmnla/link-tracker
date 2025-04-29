package backend.academy.base.client;

public class RetryExhaustedException extends RuntimeException {
    public RetryExhaustedException(final String spec, final long reties) {
        super("Failed after " + reties + " attempts with spec: " + spec);
    }
}
