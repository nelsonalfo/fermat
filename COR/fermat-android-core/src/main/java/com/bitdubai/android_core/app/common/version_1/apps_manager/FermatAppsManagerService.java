package com.bitdubai.android_core.app.common.version_1.apps_manager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bitdubai.android_core.app.FermatApplication;
import com.bitdubai.android_core.app.common.version_1.communication.client_system_broker.exceptions.CantCreateProxyException;
import com.bitdubai.android_core.app.common.version_1.connection_manager.FermatAppConnectionManager;
import com.bitdubai.android_core.app.common.version_1.recents.RecentApp;
import com.bitdubai.android_core.app.common.version_1.sessions.FermatSessionManager;
import com.bitdubai.android_core.app.common.version_1.util.system.FermatSystemUtils;
import com.bitdubai.fermat_android_api.engine.FermatRecentApp;
import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.ComboType2FermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatSession;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.FermatAppType;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatStructure;
import com.bitdubai.fermat_api.layer.all_definition.runtime.FermatApp;
import com.bitdubai.fermat_api.layer.dmp_module.AppManager;
import com.bitdubai.fermat_api.layer.engine.runtime.RuntimeManager;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.bitdubai.android_core.app.common.version_1.util.system.FermatSystemUtils.getWalletRuntimeManager;

/**
 * Created by Matias Furszyfer on 2016.02.26..
 */
//TODO: esta clase es la cual se encargará de manejar la creación de una aplicación fermat, manejo de sesiones, ubicacion en la stack (para el back button o para ver la lista de apps activas),
// obtener conexiones, etc
//TODO: falta agregar el tema de cargar el AppsConfig cuando se incia la app por primera vez

public class FermatAppsManagerService extends Service implements com.bitdubai.fermat_android_api.engine.FermatAppsManager {

    private static final String TAG = "AppsManagerService";

    //    private Map<String,RecentApp> recentsAppsStack;
    private RecentsStack recents;
    private FermatSessionManager fermatSessionManager;
    private HashMap<String, FermatAppType> appsInstalledInDevice = new HashMap<>();
    // Binder given to clients
    private final IBinder localBinder = new AppManagerLocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class AppManagerLocalBinder extends Binder {
        public FermatAppsManagerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FermatAppsManagerService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        this.recentsAppsStack = new HashMap<>();
        this.fermatSessionManager = new FermatSessionManager();
        this.recents = new RecentsStack();
        init();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String key = intent.getStringExtra(AppManagerKeys.AUTENTIFICATION_CLIENT_KEY);
        return localBinder;
    }

    public void init() {
        AppsConfiguration appsConfiguration = new AppsConfiguration(this);
        //appsInstalledInDevice = appsConfiguration.readAppsCoreInstalled();
        //if(appsInstalledInDevice.isEmpty()){
//        appsInstalledInDevice = appsConfiguration.updateAppsCoreInstalled();
//        if(!appsInstalledInDevice.containsKey("main_desktop")){
//            Log.e(TAG,"Not contains desktop");
//        }
        //}
        try {
            for (FermatAppType fermatAppType : FermatAppType.values()) {
                RuntimeManager runtimeManager = selectRuntimeManager(fermatAppType);
                if (runtimeManager != null)
                    for (String key : runtimeManager.getListOfAppsPublicKey()) {
                        appsInstalledInDevice.put(key, fermatAppType);
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FermatStructure lastAppStructure() {
        return selectRuntimeManager(findLastElement().getFermatApp().getAppType()).getLastApp();
    }

    @Override
    public FermatSession lastAppSession() {
        return fermatSessionManager.getAppsSession(findLastElement().getPublicKey());
    }

    private RecentApp findLastElement() {
//        return (RecentApp) CollectionUtils.find(recentsAppsStack.values(), new Predicate() {
//            @Override
//            public boolean evaluate(Object o) {
//                int pos = ((RecentApp) o).getTaskStackPosition()+1;
//                return ((RecentApp) o).getTaskStackPosition()+1 == recentsAppsStack.size();
//            }
//        });
        return recents.peek();
    }


    @Override
    public List<FermatRecentApp> getRecentsAppsStack() {
//        ArrayList list = new ArrayList(recentsAppsStack.values());
//        Collections.sort(list,new RecentAppComparator());
        FermatRecentApp[] apps = new FermatRecentApp[recents.size()];
        recents.copyInto(apps);
        return Arrays.asList(apps);
    }


    @Override
    public boolean isAppOpen(String appPublicKey) {
        return recents.containsKey(appPublicKey);
    }

    @Override
    public FermatSession getAppsSession(String appPublicKey, boolean isForSubSession) {
        return getAppSession(appPublicKey, isForSubSession);
    }

    public FermatSession getAppsSession(String appPublicKey) {
        return getAppSession(appPublicKey, false);
    }

    private FermatSession getAppSession(String appPublicKey, boolean isForSubSession) {
        try {
            if (fermatSessionManager.isSessionOpen(appPublicKey) && !isForSubSession) {
//                orderStackWithThisPkLast(appPublicKey);
                recents.reOrder(appPublicKey);
                return fermatSessionManager.getAppsSession(appPublicKey);
            } else {
//                FermatAppType fermatAppType = appsInstalledInDevice.get(appPublicKey);
//                if(fermatAppType==null) Log.e(TAG,"App with publicKey: "+appPublicKey+ " is not loaded in memory");
                return openApp(
                        getApp(appPublicKey),
                        FermatAppConnectionManager.getFermatAppConnection(
                                appPublicKey, FermatApplication.getInstance().getApplicationContext()
                        ),
                        isForSubSession
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void orderStackWithThisPkLast(String publicKey) {
        recents.reOrder(publicKey);
    }

    @Override
    public FermatSession openApp(FermatApp fermatApp, AppConnections fermatAppConnection) throws CantOpenSessionException {
        return openFermatApp(fermatApp, fermatAppConnection, false);
    }

    public FermatSession openApp(FermatApp fermatApp, AppConnections fermatAppConnection, boolean isForSubSession) throws CantOpenSessionException {
        return openFermatApp(fermatApp, fermatAppConnection, isForSubSession);
    }

    private FermatSession openFermatApp(FermatApp fermatApp, AppConnections fermatAppConnection, boolean isForSubSession) throws CantOpenSessionException {
        if (fermatApp != null) {
            if (!isForSubSession) {
                if (recents.containsKey(fermatApp.getAppPublicKey())) {
                    recents.reOrder(fermatApp.getAppPublicKey());
                } else {
                    recents.push(new RecentApp(fermatApp.getAppPublicKey(), fermatApp, 0));
                }
            }
            return openSession(fermatApp, fermatAppConnection, isForSubSession);
        }
        return null;
    }

    private FermatSession openSession(FermatApp fermatApp, AppConnections fermatAppConnection, boolean isForSubSession) throws CantOpenSessionException {
        FermatSession referenceAppFermatSession = null;
        if (fermatSessionManager.isSessionOpen(fermatApp.getAppPublicKey())) {
            referenceAppFermatSession = fermatSessionManager.getAppsSession(fermatApp.getAppPublicKey());
        } else {
            PluginVersionReference[] pluginVersionReferences = null;
            if (fermatAppConnection != null)
                pluginVersionReferences = fermatAppConnection.getPluginVersionReference();
            else Log.e(TAG, new StringBuilder().append("AppConnection null, App publicKey: ").append(fermatApp.getAppPublicKey()).toString());
            FermatStructure fermatStructure = getAppStructure(fermatApp.getAppPublicKey());
            switch (fermatStructure.getAppStructureType()) {
                case REFERENCE:
                    try {
                        if (pluginVersionReferences == null)
                            throw new CantOpenSessionException("", new StringBuilder().append("PluginVersionReference null in app: ").append(fermatApp.getAppPublicKey()).toString());
                        if (pluginVersionReferences.length != 1)
                            throw new CantOpenSessionException("", new StringBuilder().append("ReferenceApp can't have more or less than one module to assign, check your AppConnections getPluginVersionReference method, App publicKey: ").append(fermatApp.getAppPublicKey()).toString());
                        ModuleManager moduleManager = FermatApplication.getInstance().getServicesHelpers().getClientSideBrokerServiceAIDL().getModuleManager(fermatAppConnection.getPluginVersionReference()[0]);
                        referenceAppFermatSession = fermatSessionManager.openAppSession(fermatApp, FermatSystemUtils.getErrorManager(), moduleManager, isForSubSession);
                    } catch (CantCreateProxyException e) {
                        e.printStackTrace();
                    }
                    break;
                case COMBO_TYPE_1:
                    try {
                        ModuleManager[] moduleManager = FermatApplication.getInstance().getServicesHelpers().getClientSideBrokerServiceAIDL().getModuleManager(fermatAppConnection.getPluginVersionReference());
                        referenceAppFermatSession = fermatSessionManager.openAppSession(fermatApp, FermatSystemUtils.getErrorManager(), moduleManager, isForSubSession);
                    } catch (CantCreateProxyException e) {
                        e.printStackTrace();
                    }
                    break;
                case COMBO_TYPE_2:
                    try {
                        //ModuleManager[] moduleManager = FermatApplication.getInstance().getServicesHelpers().getClientSideBrokerServiceAIDL().getModuleManager(fermatAppConnection.getPluginVersionReference());
                        //getApp()
                        ComboType2FermatSession comboType2FermatSession = fermatSessionManager.openComboAppType2Session(fermatApp, FermatSystemUtils.getErrorManager(), isForSubSession);
                        for (String key : fermatStructure.getAppsKeyConsumed()) {
                            FermatSession fermatSession = getAppsSession(key, true);
                            comboType2FermatSession.addSession(key, fermatSession);
                        }
                        referenceAppFermatSession = comboType2FermatSession;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case COMBO_TYPE_3:

                case NICHE:
                    break;
                default:
                    throw new CantOpenSessionException("", "AppStructure desconocida");
            }
        }
        if (fermatAppConnection != null)
            fermatAppConnection.setFullyLoadedSession(referenceAppFermatSession);
        return referenceAppFermatSession;
    }


    /**
     * aca no solo la obtengo si no que la tengo que poner arriba del stack de apps
     *
     * @param publicKey
     * @return
     */
    @Override
    public FermatApp getApp(String publicKey, FermatAppType fermatAppType) throws Exception {
        FermatApp fermatApp = null;
        if (recents.containsKey(publicKey)) {
            fermatApp = recents.getApp(publicKey).getFermatApp();
        } else {
            fermatApp = selectAppManager(fermatAppType).getApp(publicKey);
            //openApp(fermatApp,FermatAppConnectionManager.getFermatAppConnection(fermatApp.getAppPublicKey(),FermatApplication.getInstance().getApplicationContext()));
        }
        orderStackWithThisPkLast(publicKey);
        return fermatApp;
    }

    @Override
    public FermatApp getApp(String appPublicKey) throws Exception {
        FermatApp fermatApp = null;
        if (recents.containsKey(appPublicKey)) {
            fermatApp = recents.getApp(appPublicKey).getFermatApp();
        } else {
            fermatApp = selectAppManager(appsInstalledInDevice.get(appPublicKey)).getApp(appPublicKey);
        }
        return fermatApp;
    }

    @Override
    public FermatStructure getAppStructure(String appPublicKey, FermatAppType appType) {
        return selectRuntimeManager(appType).getAppByPublicKey(appPublicKey);
    }

    @Override
    public FermatStructure getAppStructure(String appPublicKey) {
        try {
            if (appPublicKey.equals("main_desktop")) {
                return selectRuntimeManager(FermatAppType.DESKTOP).getAppByPublicKey(appPublicKey);
            } else {
                return selectRuntimeManager(FermatAppType.WALLET).getAppByPublicKey(appPublicKey);
            }
        } catch (Exception e) {
            Log.e(TAG, new StringBuilder().append("App instaled in device null: ").append(appPublicKey).toString());
            Log.e(TAG, "If the public key of the app is fine, try removing data and restart app. filesystem problem..");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public FermatStructure getLastAppStructure() {
        RecentApp recentApp = findLastElement();
        return selectRuntimeManager(recentApp.getFermatApp().getAppType()).getAppByPublicKey(recentApp.getPublicKey());
    }


    @Override
    public void clearRuntime() {
        try {
            if (getWalletRuntimeManager().getLastWallet() != null)
                getWalletRuntimeManager().getLastWallet().clear();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Search app in every app manager in fermat (module)
     *
     * @param fermatAppType
     * @return
     */
    public AppManager selectAppManager(FermatAppType fermatAppType) {
        AppManager appManager = null;
        switch (fermatAppType) {
            case WALLET:
                appManager = FermatSystemUtils.getWalletManager();
                break;
            case SUB_APP:
                appManager = FermatSystemUtils.getSubAppManager();
                break;
            case DESKTOP:
                appManager = FermatSystemUtils.getDesktopManager();
                break;
        }
        return appManager;
    }

    /**
     * Search runtime manager in fermat
     *
     * @param fermatAppType
     * @return
     */
    public RuntimeManager selectRuntimeManager(FermatAppType fermatAppType) {
        RuntimeManager runtimeManager = null;
        //Este swith debe ser cambiado por una petición al core pasandole el FermatAppType
        switch (fermatAppType) {
            case WALLET:
            case SUB_APP:
                runtimeManager = FermatSystemUtils.getWalletRuntimeManager();
                break;
//                runtimeManager = FermatSystemUtils.getSubAppRuntimeMiddleware();
//                break;
            case DESKTOP:
                runtimeManager = FermatSystemUtils.getDesktopRuntimeManager();

                break;
            case P2P_APP:
                runtimeManager = FermatSystemUtils.getP2PApssRuntimeManager();
                break;
        }
        return runtimeManager;
    }


}
