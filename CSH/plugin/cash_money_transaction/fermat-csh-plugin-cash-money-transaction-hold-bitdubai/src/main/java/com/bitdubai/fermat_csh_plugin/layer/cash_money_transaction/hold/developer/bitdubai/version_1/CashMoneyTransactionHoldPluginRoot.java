package com.bitdubai.fermat_csh_plugin.layer.cash_money_transaction.hold.developer.bitdubai.version_1;


import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_csh_api.all_definition.enums.CashTransactionStatus;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.exceptions.CantCreateHoldTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.exceptions.CantGetHoldTransactionException;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.interfaces.CashHoldTransaction;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.interfaces.CashHoldTransactionManager;
import com.bitdubai.fermat_csh_api.layer.csh_cash_money_transaction.hold.interfaces.CashHoldTransactionParameters;
import com.bitdubai.fermat_csh_plugin.layer.cash_money_transaction.hold.developer.bitdubai.version_1.database.HoldCashMoneyTransactionDeveloperDatabaseFactory;
import com.bitdubai.fermat_csh_plugin.layer.cash_money_transaction.hold.developer.bitdubai.version_1.exceptions.CantInitializeHoldCashMoneyTransactionDatabaseException;
import com.bitdubai.fermat_csh_plugin.layer.cash_money_transaction.hold.developer.bitdubai.version_1.structure.CashMoneyTransactionHoldManager;
import com.bitdubai.fermat_csh_plugin.layer.cash_money_transaction.hold.developer.bitdubai.version_1.structure.CashMoneyTransactionHoldProcessorAgent;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantDeliverDatabaseException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;


import java.util.List;
import java.util.UUID;


/**
 * Created by Alex on 11/17/2015
 */

public class CashMoneyTransactionHoldPluginRoot extends AbstractPlugin implements DatabaseManagerForDevelopers, CashHoldTransactionManager {


    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    private ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;


    private CashMoneyTransactionHoldProcessorAgent processorAgent;
    private CashMoneyTransactionHoldManager holdTransactionManager;

    /*
     * PluginRoot Constructor
     */
    public CashMoneyTransactionHoldPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }








    /*
     * CashHoldTransactionManager interface implementation
     */
    @Override
    public CashHoldTransaction createCashHoldTransaction(CashHoldTransactionParameters holdParameters) throws CantCreateHoldTransactionException {
        return holdTransactionManager.createCashHoldTransaction(holdParameters);
    }

    @Override
    public CashTransactionStatus getCashHoldTransactionStatus(UUID transactionId) throws CantGetHoldTransactionException {
        return holdTransactionManager.getCashHoldTransactionStatus(transactionId);
    }


    /*
     * Service interface implementation
     */
    @Override
    public void start() throws CantStartPluginException {
        try {
            holdTransactionManager = new CashMoneyTransactionHoldManager(pluginDatabaseSystem, pluginId, errorManager);
        } catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_MONEY_TRANSACTION_HOLD, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, FermatException.wrapException(e), null, null);
        }

        processorAgent = new CashMoneyTransactionHoldProcessorAgent(errorManager, holdTransactionManager);
        processorAgent.start();

        serviceStatus = ServiceStatus.STARTED;
    }

    @Override
    public void stop() {
        processorAgent.stop();
        this.serviceStatus = ServiceStatus.STOPPED;
    }





    /*
     * DatabaseManagerForDevelopers interface implementation
     */
    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        HoldCashMoneyTransactionDeveloperDatabaseFactory factory = new HoldCashMoneyTransactionDeveloperDatabaseFactory(pluginDatabaseSystem, pluginId);
        return factory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        HoldCashMoneyTransactionDeveloperDatabaseFactory factory = new HoldCashMoneyTransactionDeveloperDatabaseFactory(pluginDatabaseSystem, pluginId);
        return factory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        HoldCashMoneyTransactionDeveloperDatabaseFactory factory = new HoldCashMoneyTransactionDeveloperDatabaseFactory(pluginDatabaseSystem, pluginId);
        List<DeveloperDatabaseTableRecord> tableRecordList = null;
        try {
            factory.initializeDatabase();
            tableRecordList = factory.getDatabaseTableContent(developerObjectFactory, developerDatabaseTable);
        } catch(CantInitializeHoldCashMoneyTransactionDatabaseException cantInitializeException) {
            FermatException e = new CantDeliverDatabaseException("Database cannot be initialized", cantInitializeException, "CashMoneyTransactionHoldPluginRoot", "");
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_CSH_MONEY_TRANSACTION_HOLD, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,e);
        }
        return tableRecordList;
    }


}