package com.bitdubai.fermat_cbp_core.layer.actor.crypto_broker;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPluginSubsystem;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantStartSubsystemException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_cbp_plugin.layer.network_service.transaction_transmission.developer.bitdubai.DeveloperBitDubai;

/**
 * Created by Yordin Alayn on 22.11/15.
 */
public class CryptoBrokerPluginSubsystem extends AbstractPluginSubsystem {

    public CryptoBrokerPluginSubsystem() {
        super(new PluginReference(Plugins.CRYPTO_BROKER_ACTOR));
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
