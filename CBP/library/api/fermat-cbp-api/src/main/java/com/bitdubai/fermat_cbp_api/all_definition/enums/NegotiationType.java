package com.bitdubai.fermat_cbp_api.all_definition.enums;

import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;

/**
 * Created by Yordin Alayn on 09.12.15.
 */
public enum NegotiationType implements FermatEnum{
    PURCHASE    ("PURC"),
    SALE        ("SALE")
    ;

    private String code;

    NegotiationType(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    public static NegotiationType getByCode(String code) throws InvalidParameterException {
        switch (code) {
            case "PURC": return NegotiationType.PURCHASE;
            case "SALE": return NegotiationType.SALE;
            default: throw new InvalidParameterException(InvalidParameterException.DEFAULT_MESSAGE, null, "Code Received: " + code, "This Code Is Not Valid for the NegotiationType enum");
        }
    }
}
