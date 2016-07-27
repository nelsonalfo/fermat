package com.bitdubai.fermat_cht_core.layer.network_service.chat;

import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_cht_plugin.layer.network_service.chat.developer.bitdubai.DeveloperBitDubai;
import com.bitdubai.fermat_core_api.layer.all_definition.system.abstract_classes.AbstractPluginSubsystem;
import com.bitdubai.fermat_core_api.layer.all_definition.system.exceptions.CantStartSubsystemException;

/**
 * Created by Gabriel Araujo (gabe_512@hotmail.com) on 05/01/2016.
 */
public class ChatNetworkServicePluginSubsystem extends AbstractPluginSubsystem {

    public ChatNetworkServicePluginSubsystem() {
        super(new PluginReference(Plugins.CHAT_NETWORK_SERVICE));
    }

    @Override
    public void start() throws CantStartSubsystemException {
        try {
            registerDeveloper(new DeveloperBitDubai());
        } catch (Exception e) {
            System.err.println(new StringBuilder().append("Exception: ").append(e.getMessage()).toString());
            throw new CantStartSubsystemException(e, null, null);
        }
    }
}
