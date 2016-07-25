package org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.structure;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_pip_api.layer.user.device_user.exceptions.CantGetLoggedInDeviceUserException;
import com.bitdubai.fermat_pip_api.layer.user.device_user.interfaces.DeviceUser;
import com.bitdubai.fermat_pip_api.layer.user.device_user.interfaces.DeviceUserManager;

import com.bitdubai.fermat_api.layer.all_definition.enums.GeoFrequency;
import org.fermat.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantCreateAssetUserActorException;
import org.fermat.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserManager;
import org.fermat.fermat_dap_api.layer.dap_actor_network_service.asset_user.exceptions.CantRegisterActorAssetUserException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantCreateNewIdentityAssetUserException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantGetAssetUserIdentitiesException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantListAssetUsersException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantUpdateIdentityAssetUserException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.interfaces.IdentityAssetUser;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.interfaces.IdentityAssetUserManager;
import org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.AssetUserIdentityPluginRoot;
import org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.database.AssetUserIdentityDao;
import org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantInitializeAssetUserIdentityDatabaseException;
import org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by franklin on 02/11/15.
 */
public class IdentityAssetUserManagerImpl implements IdentityAssetUserManager {

    private final ActorAssetUserManager actorAssetUserManager;
    UUID pluginId;
    LogManager logManager;
    PluginDatabaseSystem pluginDatabaseSystem;
    PluginFileSystem pluginFileSystem;
    private DeviceUserManager deviceUserManager;

    AssetUserIdentityPluginRoot assetUserIdentityPluginRoot;


    /**
     * Constructor
     *
     * @param logManager
     * @param pluginDatabaseSystem
     * @param pluginFileSystem
     */

    public IdentityAssetUserManagerImpl(LogManager logManager,
                                        PluginDatabaseSystem pluginDatabaseSystem,
                                        PluginFileSystem pluginFileSystem,
                                        UUID pluginId,
                                        DeviceUserManager deviceUserManager,
                                        ActorAssetUserManager actorAssetUserManager,
                                        AssetUserIdentityPluginRoot assetUserIdentityPluginRoot) {
        this.logManager = logManager;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
        this.deviceUserManager = deviceUserManager;
        this.actorAssetUserManager = actorAssetUserManager;
        this.assetUserIdentityPluginRoot = assetUserIdentityPluginRoot;

    }

    private AssetUserIdentityDao getAssetUserIdentityDao() throws CantInitializeAssetUserIdentityDatabaseException {
        return new AssetUserIdentityDao(this.pluginDatabaseSystem, this.pluginFileSystem, this.pluginId);
    }

    public List<IdentityAssetUser> getIdentityAssetUsersFromCurrentDeviceUser() throws CantListAssetUsersException {

        try {

            List<IdentityAssetUser> assetUserList = new ArrayList<IdentityAssetUser>();

            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();
            assetUserList = getAssetUserIdentityDao().getIdentityAssetUsersFromCurrentDeviceUser(loggedUser);

            return assetUserList;

        } catch (CantGetLoggedInDeviceUserException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListAssetUsersException("CAN'T GET ASSET USER IDENTITIES", e, "Error get logged user device", "");
        } catch (org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListAssetUsersException("CAN'T GET ASSET USER  IDENTITIES", e, "", "");
        } catch (Exception e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListAssetUsersException("CAN'T GET ASSET USER IDENTITIES", FermatException.wrapException(e), "", "");
        }
    }

    public IdentityAssetUser createNewIdentityAssetUser(String alias, byte[] profileImage, int accuracy, GeoFrequency frequency) throws CantCreateNewIdentityAssetUserException {
        try {
            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();

            ECCKeyPair keyPair = new ECCKeyPair();
            String publicKey = keyPair.getPublicKey();
            String privateKey = keyPair.getPrivateKey();

            getAssetUserIdentityDao().createNewUser(alias, publicKey, privateKey, loggedUser, profileImage, accuracy, frequency);

            IdentityAssetUser identityAssetUser = new IdentityAssetUsermpl(alias, publicKey, privateKey, profileImage, accuracy, frequency);

            registerIdentities();

            return identityAssetUser;
        } catch (CantGetLoggedInDeviceUserException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateNewIdentityAssetUserException("CAN'T CREATE NEW ASSET USER IDENTITY", e, "Error getting current logged in device user", "");
        } catch (Exception e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateNewIdentityAssetUserException("CAN'T CREATE NEW ASSET USER IDENTITY", FermatException.wrapException(e), "", "");
        }
    }

    public void updateIdentityAssetUser(String identityPublicKey, String identityAlias, byte[] profileImage, int accuracy, GeoFrequency frequency) throws CantUpdateIdentityAssetUserException {
        try {
            getAssetUserIdentityDao().updateIdentityAssetUser(identityPublicKey, identityAlias, profileImage, accuracy, frequency);

            registerIdentities();
        } catch (CantInitializeAssetUserIdentityDatabaseException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            e.printStackTrace();
        } catch (org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasAssetUserIdentity() throws CantListAssetUsersException {
        try {
            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();
            return getAssetUserIdentityDao().getIdentityAssetUsersFromCurrentDeviceUser(loggedUser).size() > 0;
        } catch (CantGetLoggedInDeviceUserException e) {
            throw new CantListAssetUsersException("CAN'T GET IF ASSET ISSUER IDENTITIES  EXISTS", e, "Error get logged user device", "");
        } catch (CantListAssetUserIdentitiesException e) {
            throw new CantListAssetUsersException("CAN'T GET IF ASSET ISSUER IDENTITIES EXISTS", e, "", "");
        } catch (Exception e) {
            throw new CantListAssetUsersException("CAN'T GET ASSET ISSUER ISSUER IDENTITY EXISTS", FermatException.wrapException(e), "", "");
        }
    }

    public IdentityAssetUser getIdentityAssetUser() throws CantGetAssetUserIdentitiesException {
        IdentityAssetUser identityAssetUser = null;
        try {
            identityAssetUser = getAssetUserIdentityDao().getIdentityAssetUser();
        } catch (CantInitializeAssetUserIdentityDatabaseException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            e.printStackTrace();
        }
        return identityAssetUser;
    }

    public boolean hasIntraUserIdentity() throws CantListAssetUsersException {
        try {

            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();
            return getAssetUserIdentityDao().getIdentityAssetUsersFromCurrentDeviceUser(loggedUser).size() > 0;
        } catch (CantGetLoggedInDeviceUserException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListAssetUsersException("CAN'T GET IF ASSET USER IDENTITIES  EXISTS", e, "Error get logged user device", "");
        } catch (org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListAssetUsersException("CAN'T GET IF ASSET USER IDENTITIES EXISTS", e, "", "");
        } catch (Exception e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListAssetUsersException("CAN'T GET ASSET USER USER IDENTITY EXISTS", FermatException.wrapException(e), "", "");
        }
    }

    @Override
    public int getAccuracyDataDefault() {
        return 0;
    }

    @Override
    public GeoFrequency getFrequencyDataDefault() {
        return GeoFrequency.NORMAL;
    }

    public void registerIdentities() throws CantListAssetUserIdentitiesException {
        try {
            List<IdentityAssetUser> identityAssetUsers = getAssetUserIdentityDao().getIdentityAssetUsersFromCurrentDeviceUser(deviceUserManager.getLoggedInDeviceUser());
            if (identityAssetUsers.size() > 0) {
                for (IdentityAssetUser identityAssetUser : identityAssetUsers) {
                    actorAssetUserManager.createActorAssetUserFactory(identityAssetUser.getPublicKey(), identityAssetUser.getAlias(), identityAssetUser.getImage());
                }
            }
        } catch (CantGetLoggedInDeviceUserException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException("CAN'T GET IF ASSET USER IDENTITIES  EXISTS", e, "Cant Get Logged InDevice User", "");
        } catch (org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException("CAN'T GET IF ASSET USER IDENTITIES  EXISTS", e, "Cant List Asset User Identities", "");
        } catch (CantCreateAssetUserActorException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException("CAN'T GET IF ASSET USER IDENTITIES  EXISTS", e, "Cant Create Actor Asset User", "");
        } catch (CantInitializeAssetUserIdentityDatabaseException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new org.fermat.fermat_dap_plugin.layer.identity.asset.user.developer.version_1.exceptions.CantListAssetUserIdentitiesException("CAN'T GET IF ASSET USER IDENTITIES  EXISTS", e, "Cant Initialize Asset User Identity Database", "");
        }
    }

    public void registerIdentitiesANS() throws CantRegisterActorAssetUserException {
        try {
            actorAssetUserManager.registerActorInActorNetworkService();
        } catch (CantRegisterActorAssetUserException e) {
            assetUserIdentityPluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            e.printStackTrace();
        }
    }
}
