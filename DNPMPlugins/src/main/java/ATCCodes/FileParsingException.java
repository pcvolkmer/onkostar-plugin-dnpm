package ATCCodes;

/**
 * Exception to be thrown if any file parsing error occurs
 *
 * @author Paul-Christian Volkmer
 * @since 0.1.0
 */
public class FileParsingException extends RuntimeException {

    public FileParsingException(final String msg) {
        super(msg);
    }
}