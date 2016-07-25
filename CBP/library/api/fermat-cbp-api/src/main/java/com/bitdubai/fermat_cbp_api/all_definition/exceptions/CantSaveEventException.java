package com.bitdubai.fermat_cbp_api.all_definition.exceptions;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 30/11/15.
 */
public class CantSaveEventException extends CBPException {

    /**
     * Represent the default message
     */
    public static final String DEFAULT_MESSAGE = "CANNOT SET AN OBJECT";

    /**
     * Constructor with parameters
     *
     * @param message
     * @param cause
     * @param context
     * @param possibleReason
     */
    public CantSaveEventException(final String message, final Exception cause, final String context, final String possibleReason) {
        super(message, cause, context, possibleReason);
    }

    /**
     * Constructor with parameters
     *
     * @param cause
     * @param context
     * @param possibleReason
     */
    public CantSaveEventException(Exception cause, String context, String possibleReason) {
        super(DEFAULT_MESSAGE, cause, context, possibleReason);
    }

    /**
     * Constructor with parameters
     *
     * @param message
     * @param cause
     */
    public CantSaveEventException(final String message, final Exception cause) {
        this(message, cause, "", "");
    }

    /**
     * Constructor with parameter
     *
     * @param message
     */
    public CantSaveEventException(final String message) {
        this(message, null);
    }

    /**
     * Constructor with parameter
     *
     * @param exception
     */
    public CantSaveEventException(final Exception exception) {
        this(exception.getMessage());
        setStackTrace(exception.getStackTrace());
    }

    /**
     * Constructor
     */
    public CantSaveEventException() {
        this(DEFAULT_MESSAGE);
    }
}
