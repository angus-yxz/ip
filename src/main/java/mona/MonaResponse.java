package mona;

import java.util.Objects;

/**
 * Stores response text together with its visual meaning.
 *
 * @param text the message shown to the user.
 * @param type the visual category of the message.
 * @param isExit whether the application should close after this response is shown.
 */
public record MonaResponse(String text, ResponseType type, boolean isExit) {
    /**
     * Creates a response with non-null text and type values.
     */
    public MonaResponse {
        Objects.requireNonNull(text, "Response text must not be null");
        Objects.requireNonNull(type, "Response type must not be null");
    }
}
