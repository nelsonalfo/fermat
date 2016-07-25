package com.bitdubai.fermat_api.layer.osa_android.broadcaster;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;

import java.io.Serializable;


/**
 * Created by Matias Furszfer on 2016.02.02
 * Updated by Nelson Ramirez on 2016.03.04
 */
public interface Broadcaster extends FermatManager, Serializable {

    String PROGRESS_BAR = "progressBar";
    String PROGRESS_BAR_TEXT = "progressBarText";
    String PUBLISH_ID = "publishId";
    String NOTIFICATION_TYPE = "notificationType";

    /**
     * Let you fire a broadcast to update a fragment in your wallet or sub-app
     *
     * @param broadcasterType the broadcast type you want to fire. Can be {@link BroadcasterType#UPDATE_VIEW}
     * @param code            the message is going to be send by the broadcaster, this code let you deal with the broadcast the way yo want
     * @Deprecated: use publish(BroadcasterType broadcasterType, String appCode, FermatBundle bundle);
     */
    @Deprecated
    void publish(BroadcasterType broadcasterType, String code);

    /**
     * Let you fire a broadcast to update a fragment or show a notification in your wallet or sub-app
     *
     * @param broadcasterType    the broadcast type you want to fire. Can be {@link BroadcasterType#UPDATE_VIEW}
     *                           if you want to update a fragment or {@link BroadcasterType#NOTIFICATION_SERVICE} if you want to show a notification
     * @param appPublicKeyToOpen the publicKey of the wallet or subapp is going show the notification or <code>null</code> if you want to update a view
     * @param code               the message is going to be send by the broadcaster, this code let you deal with the broadcast the way yo want
     * @Deprecated: use publish(BroadcasterType broadcasterType, String appPublicKeyToOpen, FermatBundle bundle);
     */
    @Deprecated
    void publish(BroadcasterType broadcasterType, String appPublicKeyToOpen, String code);

    /**
     * Let you fire a broadcast to update a fragment or show a notification in your wallet or sub-app
     *
     * @param broadcasterType    the broadcast type you want to fire. Can be {@link BroadcasterType#UPDATE_VIEW}
     *                           if you want to update a fragment or {@link BroadcasterType#NOTIFICATION_SERVICE} if you want to show a notification
     * @param appPublicKeyToOpen the publicKey of the wallet or subapp is going show the notification or <code>null</code> if you want to update a view
     * @param bundle             the message is going to be send by the broadcaster, this bundle let you deal with the broadcast the way you want
     */
    void publish(BroadcasterType broadcasterType, String appPublicKeyToOpen, FermatBundle bundle);

    void publish(BroadcasterType broadcasterType, FermatBundle bundle);

    /**
     * Let you fire a broadcast to update a fragment or show a notification in your wallet or sub-app
     *
     * @param broadcasterType      the broadcast type you want to fire. Can be {@link BroadcasterType#UPDATE_VIEW}
     *                             if you want to update a fragment or {@link BroadcasterType#NOTIFICATION_SERVICE} if you want to show a notification
     * @param bundle               the message is going to be send by the broadcaster, this bundle let you deal with the broadcast the way you want
     * @param channelReceiversCode the broadcast is for everyone who are listening in this channel code.
     * @return int the id of the notification
     */
    int publish(BroadcasterType broadcasterType, FermatBundle bundle, String channelReceiversCode);


}
