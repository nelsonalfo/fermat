package com.bitdubai.fermat_cbp_core.layer.user_level_business_transaction.customer_broker_sale;

import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_cbp_plugin.layer.user_level_business_transaction.customer_broker_sale.developer.bitdubai.DeveloperBitDubai;
import com.bitdubai.fermat_core_api.layer.all_definition.system.abstract_classes.AbstractPluginSubsystem;
import com.bitdubai.fermat_core_api.layer.all_definition.system.exceptions.CantStartSubsystemException;

/**
 * Created by franklin on 15/12/15.
 */
public class CustomerBrokerSaleSubsystem extends AbstractPluginSubsystem {

    public CustomerBrokerSaleSubsystem() {
        super(new PluginReference(Plugins.CUSTOMER_BROKER_SALE));
    }

    @Override
    public void start() throws CantStartSubsystemException {
        try {
            registerDeveloper(new DeveloperBitDubai());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            throw new CantStartSubsystemException(e, null, null);
        }
    }
}
