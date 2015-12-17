package com.bitdubai.fermat_cbp_api.all_definition.enums;

import java.security.InvalidParameterException;

/**
 * Created by nelson on 12/12/15.
 */
public enum NegotiationStepStatus {

    ACCEPTED("ACCEPT"),
    CHANGED("CHANGE"),
    CONFIRM("CONFIR");

    private final String code;

    NegotiationStepStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static NegotiationStepStatus getByCode(final String code) throws InvalidParameterException {
        switch (code) {
            case "ACCEPT":
                return ACCEPTED;
            case "CHANGE":
                return CHANGED;
            case "CONFIR":
                return CONFIRM;
            default:
                throw new InvalidParameterException("Code Received: " + code +
                        " - This Code Is Not Valid for the ClauseType enum");
        }
    }
}
