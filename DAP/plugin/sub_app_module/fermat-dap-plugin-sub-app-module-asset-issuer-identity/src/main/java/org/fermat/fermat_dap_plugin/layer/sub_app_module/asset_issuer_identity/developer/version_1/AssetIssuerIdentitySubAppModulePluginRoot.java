package org.fermat.fermat_dap_plugin.layer.sub_app_module.asset_issuer_identity.developer.version_1;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractModule;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantGetModuleManagerException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.LogManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.core.PluginInfo;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogLevel;

import org.fermat.fermat_dap_api.layer.dap_identity.asset_issuer.interfaces.IdentityAssetIssuerManager;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.asset_issuer_identity.IssuerIdentitySettings;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.asset_issuer_identity.interfaces.AssetIssuerIdentityModuleManager;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.redeem_point_community.exceptions.CantGetSupAppRedeemPointModuleException;
import org.fermat.fermat_dap_plugin.layer.sub_app_module.asset_issuer_identity.developer.version_1.structure.AssetIssuerIdentitySubAppModuleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by Nerio on 13/10/15.
 * Updated by lnacosta (laion.cj91@gmail.com) on 11/11/2015.
 */
@PluginInfo(difficulty = PluginInfo.Dificulty.LOW,
        maintainerMail = "nerioindriago@gmail.com",
        createdBy = "nindriago",
        layer = Layers.SUB_APP_MODULE,
        platform = Platforms.DIGITAL_ASSET_PLATFORM,
        plugin = Plugins.BITDUBAI_DAP_ASSET_ISSUER_IDENTITY)
public class AssetIssuerIdentitySubAppModulePluginRoot extends AbstractModule<IssuerIdentitySettings, ActiveActorIdentityInformation> implements
        LogManagerForDevelopers {

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.IDENTITY, plugin = Plugins.ASSET_ISSUER)
    private IdentityAssetIssuerManager identityAssetIssuerManager;

    AssetIssuerIdentityModuleManager assetIssuerIdentityModuleManager;

    private static Map<String, LogLevel> newLoggingLevel = new HashMap<String, LogLevel>();

    public AssetIssuerIdentitySubAppModulePluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public List<String> getClassesFullPath() {
        List<String> returnedClasses = new ArrayList<>();
        returnedClasses.add("AssetIssuerIdentitySubAppModulePluginRoot");
        returnedClasses.add("AssetIssuerIdentitySubAppModuleManager");

        /**
         * I return the values.
         */
        return returnedClasses;
    }

    @Override
    public void setLoggingLevelPerClass(Map<String, LogLevel> newLoggingLevel) {
        /**
         * I will check the current values and update the LogLevel in those which is different
         */

        for (Map.Entry<String, LogLevel> pluginPair : newLoggingLevel.entrySet()) {
            /**
             * if this path already exists in the Root.bewLoggingLevel I'll update the value, else, I will put as new
             */
            if (AssetIssuerIdentitySubAppModulePluginRoot.newLoggingLevel.containsKey(pluginPair.getKey())) {
                AssetIssuerIdentitySubAppModulePluginRoot.newLoggingLevel.remove(pluginPair.getKey());
                AssetIssuerIdentitySubAppModulePluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
            } else {
                AssetIssuerIdentitySubAppModulePluginRoot.newLoggingLevel.put(pluginPair.getKey(), pluginPair.getValue());
            }
        }
    }

    /**
     * Static method to get the logging level from any class under root.
     *
     * @param className
     * @return
     */
    public static LogLevel getLogLevelByClass(String className) {
        try {
            /**
             * sometimes the classname may be passed dinamically with an $moretext
             * I need to ignore whats after this.
             */
            String[] correctedClass = className.split(Pattern.quote("$"));
            return AssetIssuerIdentitySubAppModulePluginRoot.newLoggingLevel.get(correctedClass[0]);
        } catch (Exception e) {
            /**
             * If I couldn't get the correct loggin level, then I will set it to minimal.
             */
            return DEFAULT_LOG_LEVEL;
        }
    }

//    @Override
//    public void start(){
//        /**
//         * Init the plugin manager
//         */
//        System.out.println("******* Init Asset Issuer Sup App Module Identity ******");
////        assetIssuerIdentityModuleManager = new AssetIssuerIdentitySubAppModuleManager(identityAssetIssuerManager, pluginFileSystem, pluginId);
//
//        this.serviceStatus = ServiceStatus.STARTED;
//    }

    @Override
//    @moduleManagerInterfacea(moduleManager = AssetIssuerIdentitySubAppModuleManager.class)
    public AssetIssuerIdentityModuleManager getModuleManager() throws CantGetModuleManagerException {
        try {
//            logManager.log(AssetIssuerIdentitySubAppModulePluginRoot.getLogLevelByClass(this.getClass().getName()), "Asset Issuer Identity Sup AppModule instantiation started...", null, null);

            if (assetIssuerIdentityModuleManager == null) {
                assetIssuerIdentityModuleManager = new AssetIssuerIdentitySubAppModuleManager(identityAssetIssuerManager, pluginFileSystem, pluginId);

                this.serviceStatus = ServiceStatus.STARTED;
            }
//            logManager.log(AssetIssuerIdentitySubAppModulePluginRoot.getLogLevelByClass(this.getClass().getName()), "Asset Issuer Identity Sup AppModule instantiation finished successfully.", null, null);

            return assetIssuerIdentityModuleManager;

        } catch (final Exception e) {
            throw new CantGetModuleManagerException(CantGetSupAppRedeemPointModuleException.DEFAULT_MESSAGE, FermatException.wrapException(e).toString());
        }
    }
}

