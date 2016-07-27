package com.bitdubai.fermat_cht_api.all_definition.exceptions;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 04/09/15.
 */
public class CantSetObjectException extends CHTException {

    static final String DEFAULT_MESSAGE = "There was an error setting an object.";

    public CantSetObjectException(Exception cause, String context, String possibleReason) {
        super(DEFAULT_MESSAGE, cause, context, possibleReason);
    }

    public CantSetObjectException(final String message) {
        this(null, DEFAULT_MESSAGE, message);
    }

}
