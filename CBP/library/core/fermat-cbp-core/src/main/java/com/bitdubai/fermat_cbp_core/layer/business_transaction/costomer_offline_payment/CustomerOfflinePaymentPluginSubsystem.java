package com.bitdubai.fermat_cbp_core.layer.business_transaction.costomer_offline_payment;

import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_core_api.layer.all_definition.system.exceptions.CantStartSubsystemException;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_offline_payment.developer.bitdubai.DeveloperBitDubai;
import com.bitdubai.fermat_core_api.layer.all_definition.system.abstract_classes.AbstractPluginSubsystem;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 12/12/15.
 */
public class CustomerOfflinePaymentPluginSubsystem extends AbstractPluginSubsystem {

    public CustomerOfflinePaymentPluginSubsystem() {
        super(new PluginReference(Plugins.CUSTOMER_OFFLINE_PAYMENT));
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
