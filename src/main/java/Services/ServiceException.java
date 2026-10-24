package Services;

import java.util.logging.Level;
import java.util.logging.Logger;

/** Database failure exposed to application callers without leaking JDBC details to the UI. */
public final class ServiceException extends RuntimeException {
    private static final Logger LOGGER = Logger.getLogger(ServiceException.class.getName());

    public ServiceException(String userMessage) {
        super(userMessage);
    }

    public ServiceException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        LOGGER.log(Level.SEVERE, userMessage, cause);
    }

    public static ServiceException wrap(String userMessage, RuntimeException cause) {
        return cause instanceof ServiceException serviceException
                ? serviceException
                : new ServiceException(userMessage, cause);
    }
}
