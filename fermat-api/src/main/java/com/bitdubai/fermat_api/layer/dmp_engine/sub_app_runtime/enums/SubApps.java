package com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums;

import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;

/**
 * Created by Matias Furszyfer on 2/14/15.
 */
public enum SubApps {

    CWP_WALLET_MANAGER("CWM"),
    CWP_WALLET_RUNTIME("CWR"),
    CWP_WALLET_STORE ("CWS"),
    CWP_WALLET_FACTORY ("CWF"),
    CWP_DEVELOPER_APP ("CDA"),
    CWP_WALLET_PUBLISHER("CWP"),
    CWP_INTRA_USER("CIU"),
    CWP_SHELL("CS"),
    CBP_CRYPTO_BROKER_IDENTITY("CBPCBI"),
    CBP_CRYPTO_BROKER_COMMUNITY("CBPCBC"),
    CBP_CRYPTO_CUSTOMER_IDENTITY("CBPCCI"),
    CBP_CRYPTO_CUSTOMER_COMMUNITY("CBPCCC"),
    CCP_CRYPTO_CUSTOMER_IDENTITY("CCPCCI"),
    CBP_CUSTOMERS("CBPC"),
    DAP_ASSETS_FACTORY("DAPAF"),;


    private final String code;

    SubApps(String code) {
        this.code = code;
    }

    public String getCode() { return this.code ; }

    public static SubApps getByCode(String code) throws InvalidParameterException {

        switch (code) {
            case "CWM": return SubApps.CWP_WALLET_MANAGER;
            case "CWR": return SubApps.CWP_WALLET_RUNTIME;
            case "CWS": return SubApps.CWP_WALLET_STORE;
            case "CWF": return SubApps.CWP_WALLET_FACTORY;
            case "CDA": return SubApps.CWP_DEVELOPER_APP;
            case "CWP": return SubApps.CWP_WALLET_PUBLISHER;
            case "CIU": return SubApps.CWP_INTRA_USER;
            case "CBPC": return CBP_CUSTOMERS;
            //Modified by Manuel Perez on 05/08/2015
            case "CS": return SubApps.CWP_SHELL;
            case "DAPAF": return  DAP_ASSETS_FACTORY;
            default:
                throw new InvalidParameterException(InvalidParameterException.DEFAULT_MESSAGE, null, "Code Received: " + code, "This Code Is Not Valid for the SubApps enum");

        }

        /**
         * Return by default.
         */
        //return null;
    }
}
