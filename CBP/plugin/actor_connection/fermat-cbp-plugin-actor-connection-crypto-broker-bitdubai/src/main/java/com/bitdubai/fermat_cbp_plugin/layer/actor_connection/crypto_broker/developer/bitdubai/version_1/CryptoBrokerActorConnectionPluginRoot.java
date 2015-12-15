package com.bitdubai.fermat_cbp_plugin.layer.actor_connection.crypto_broker.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantInitializeActorConnectionDatabaseException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_broker.interfaces.CryptoBrokerManager;
import com.bitdubai.fermat_cbp_plugin.layer.actor_connection.crypto_broker.developer.bitdubai.version_1.database.CryptoBrokerActorConnectionDao;
import com.bitdubai.fermat_cbp_plugin.layer.actor_connection.crypto_broker.developer.bitdubai.version_1.structure.ActorConnectionManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_network_service.crypto_broker.developer.bitdubai.version_1.CryptoBrokerActorConnectionPluginRoot</code>
 * bla bla bla.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 20/11/2015.
 */
public class CryptoBrokerActorConnectionPluginRoot extends AbstractPlugin {


    @NeededAddonReference (platform = Platforms.PLUG_INS_PLATFORM     , layer = Layers.PLATFORM_SERVICE, addon  = Addons .ERROR_MANAGER         )
    private ErrorManager errorManager;

    @NeededAddonReference (platform = Platforms.PLUG_INS_PLATFORM     , layer = Layers.PLATFORM_SERVICE, addon  = Addons .EVENT_MANAGER         )
    private EventManager eventManager;

    @NeededAddonReference (platform = Platforms.OPERATIVE_SYSTEM_API  , layer = Layers.SYSTEM          , addon  = Addons .PLUGIN_FILE_SYSTEM    )
    protected PluginFileSystem pluginFileSystem        ;

    @NeededAddonReference (platform = Platforms.OPERATIVE_SYSTEM_API  , layer = Layers.SYSTEM          , addon  = Addons .PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededPluginReference(platform = Platforms.CRYPTO_BROKER_PLATFORM, layer = Layers.ACTOR_NETWORK_SERVICE, plugin =  Plugins.CRYPTO_BROKER)
    private CryptoBrokerManager cryptoBrokerManagerNetworkService;


    public CryptoBrokerActorConnectionPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    private ActorConnectionManager fermatManager;

    @Override
    public FermatManager getManager() {
        return fermatManager;
    }

    @Override
    public void start() throws CantStartPluginException {

        try {

            final CryptoBrokerActorConnectionDao dao = new CryptoBrokerActorConnectionDao(
                    pluginDatabaseSystem,
                    pluginId
            );

            dao.initializeDatabase();

            fermatManager = new ActorConnectionManager(
                    cryptoBrokerManagerNetworkService,
                    dao,
                    errorManager,
                    this.getPluginVersionReference()
            );

            super.start();

        } catch (final CantInitializeActorConnectionDatabaseException cantInitializeActorConnectionDatabaseException) {

            errorManager.reportUnexpectedPluginException(
                    getPluginVersionReference()                           ,
                    UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN,
                    cantInitializeActorConnectionDatabaseException
            );

            throw new CantStartPluginException(
                    cantInitializeActorConnectionDatabaseException,
                    "Crypto Broker Actor Connection.",
                    "Problem initializing database of the plug-in."
            );
        }
    }

}
