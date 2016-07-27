package com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.identity;

import com.bitdubai.fermat_api.layer.all_definition.enums.GeoFrequency;
import com.bitdubai.fermat_api.layer.modules.ModuleSettingsImpl;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;
import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_api.layer.osa_android.location_system.exceptions.CantGetDeviceLocationException;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantCreateNewChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantGetChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantListChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantUpdateChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.identity.interfaces.ChatIdentity;

import java.io.Serializable;
import java.util.List;

/**
 * FERMAT-ORG
 * Developed by Lozadaa on 30/03/16.
 */

public interface ChatIdentityModuleManager extends ModuleManager, ModuleSettingsImpl<ChatIdentityPreferenceSettings>, Serializable {
    /**
     * The method <code>getIdentityAssetUsersFromCurrentDeviceUser</code> will give us a list of all the intra wallet users associated to the actual Device User logged in
     *
     * @return the list of Chat users associated to the current logged in Device User.
     * @throws CantListChatIdentityException if something goes wrong.
     */
    List<ChatIdentity> getIdentityChatUsersFromCurrentDeviceUser() throws CantListChatIdentityException;

    /**
     * The method <code>getIdentityAssetIssuer</code> will give Identity Asset Issuer associated
     *
     * @return Identity Chat associated.
     * @throws CantGetChatIdentityException if something goes wrong.
     */
    ChatIdentity getIdentityChatUser() throws CantGetChatIdentityException;

    /**
     * The method <code>createNewIdentityChat</code> creates a new intra wallet user Identity for the logged in Device User and returns the
     * associated public key
     *
     * @param alias        the alias that the user choose as intra user identity
     * @param profileImage the profile image to identify this identity
     * @return the intra user created
     * @throws CantCreateNewChatIdentityException if something goes wrong.
     */
    void createNewIdentityChat(String alias,
                               byte[] profileImage, String country, String state, String city, String connectionState, long accurancy, GeoFrequency frecuency) throws CantCreateNewChatIdentityException;

    /**
     * The method <code>updateIdentityChat</code> change a identity information data
     *
     * @param identityPublicKey
     * @param identityAlias
     * @param profileImage
     * @throws CantUpdateChatIdentityException
     */
    void updateIdentityChat(String identityPublicKey, String identityAlias, byte[] profileImage, String country, String state, String city, String connectionState, long accurancy, GeoFrequency frecuency) throws CantUpdateChatIdentityException;

    /**
     * The method <code>getLocation</code> get location coordinates of the user
     *
     * @throws CantGetDeviceLocationException
     */
    Location getLocation() throws CantGetDeviceLocationException;
}
