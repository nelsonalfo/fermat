package org.fermat.fermat_dap_plugin.layer.sub_app_module.asset_user_identity.developer.version_1.structure;

import com.bitdubai.fermat_api.layer.modules.ModuleManagerImpl;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;

import com.bitdubai.fermat_api.layer.all_definition.enums.GeoFrequency;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantCreateNewIdentityAssetUserException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantGetAssetUserIdentitiesException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantListAssetUsersException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.exceptions.CantUpdateIdentityAssetUserException;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.interfaces.IdentityAssetUser;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_user.interfaces.IdentityAssetUserManager;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.asset_user_identity.UserIdentitySettings;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.asset_user_identity.interfaces.AssetUserIdentityModuleManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by nerio on 14/5/2016.
 */
public class AssetUserIdentitySubAppModuleManager extends ModuleManagerImpl<UserIdentitySettings> implements AssetUserIdentityModuleManager {

    private final PluginFileSystem pluginFileSystem;
    private final UUID pluginId;
    private IdentityAssetUserManager identityAssetUserManager;

    public AssetUserIdentitySubAppModuleManager(IdentityAssetUserManager identityAssetUserManager, PluginFileSystem pluginFileSystem, UUID pluginId) {
        super(pluginFileSystem, pluginId);

        this.identityAssetUserManager = identityAssetUserManager;
        this.pluginFileSystem = pluginFileSystem;
        this.pluginId = pluginId;
    }

    @Override
    public List<IdentityAssetUser> getIdentityAssetUsersFromCurrentDeviceUser() throws CantListAssetUsersException {
        return identityAssetUserManager.getIdentityAssetUsersFromCurrentDeviceUser();
    }

    @Override
    public IdentityAssetUser getIdentityAssetUser() throws CantGetAssetUserIdentitiesException {
        return identityAssetUserManager.getIdentityAssetUser();
    }

    @Override
    public IdentityAssetUser createNewIdentityAssetUser(String alias, byte[] profileImage, int accuracy, GeoFrequency frequency) throws CantCreateNewIdentityAssetUserException {
        return identityAssetUserManager.createNewIdentityAssetUser(alias, profileImage, accuracy, frequency);
    }

    @Override
    public void updateIdentityAssetUser(String identityPublicKey, String identityAlias, byte[] profileImage, int accuracy, GeoFrequency frequency) throws CantUpdateIdentityAssetUserException {
        identityAssetUserManager.updateIdentityAssetUser(identityPublicKey, identityAlias, profileImage, accuracy, frequency);
    }

    @Override
    public boolean hasAssetUserIdentity() throws CantListAssetUsersException {
        return identityAssetUserManager.hasAssetUserIdentity();
    }

    @Override
    public ActiveActorIdentityInformation getSelectedActorIdentity() throws CantGetSelectedActorIdentityException {
        try {
            List<IdentityAssetUser> identities = identityAssetUserManager.getIdentityAssetUsersFromCurrentDeviceUser();
            return (identities == null || identities.isEmpty()) ? null : identities.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void createIdentity(String name, String phrase, byte[] profile_img) throws Exception {
        identityAssetUserManager.createNewIdentityAssetUser(name,
                profile_img,
                identityAssetUserManager.getAccuracyDataDefault(),
                identityAssetUserManager.getFrequencyDataDefault());
    }

    @Override
    public void setAppPublicKey(String publicKey) {

    }

    @Override
    public int[] getMenuNotifications() {
        return new int[0];
    }

    @Override
    public int getAccuracyDataDefault() {
        return identityAssetUserManager.getAccuracyDataDefault();
    }

    @Override
    public GeoFrequency getFrequencyDataDefault() {
        return identityAssetUserManager.getFrequencyDataDefault();
    }
}
