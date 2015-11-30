package com.bitdubai.fermat_cbp_core.layer.contract.customer_broker_sale;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPluginSubsystem;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantStartSubsystemException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.transaction_transmission.developer.bitdubai.DeveloperBitDubai;

/**
 * Created by Angel on 28/11/15.
 */
public class CustomerBrokerSalePluginSubsystem extends AbstractPluginSubsystem {

    public CustomerBrokerSalePluginSubsystem() {
        super(new PluginReference(Plugins.CONTRACT_SALE));
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
