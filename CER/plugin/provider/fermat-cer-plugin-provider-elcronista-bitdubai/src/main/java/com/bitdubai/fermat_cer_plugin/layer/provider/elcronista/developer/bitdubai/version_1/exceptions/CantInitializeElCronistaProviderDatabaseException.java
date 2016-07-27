package com.bitdubai.fermat_cer_plugin.layer.provider.elcronista.developer.bitdubai.version_1.exceptions;

import com.bitdubai.fermat_api.FermatException;

/**
 * The Class <code>package com.bitdubai.fermat_cer_plugin.layer.provider.elcronista.developer.bitdubai.version_1.exceptions.CantInitializeDolartodayProviderDatabaseException</code>
 * is thrown when an error occurs initializing database
 * <p/>
 * <p/>
 * Created by Alejandro Bicelis - (abicelis@gmail.com) on 08/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CantInitializeElCronistaProviderDatabaseException extends FermatException {

    public static final String DEFAULT_MESSAGE = "CAN'T INTIALIZE ELCRONISTA PROVIDER DATABASE EXCEPTION";

    public CantInitializeElCronistaProviderDatabaseException(final String message, final Exception cause, final String context, final String possibleReason) {
        super(message, cause, context, possibleReason);
    }

    public CantInitializeElCronistaProviderDatabaseException(final String message, final Exception cause) {
        this(message, cause, "", "");
    }

    public CantInitializeElCronistaProviderDatabaseException(final String message) {
        this(message, null);
    }

    public CantInitializeElCronistaProviderDatabaseException() {
        this(DEFAULT_MESSAGE);
    }
}