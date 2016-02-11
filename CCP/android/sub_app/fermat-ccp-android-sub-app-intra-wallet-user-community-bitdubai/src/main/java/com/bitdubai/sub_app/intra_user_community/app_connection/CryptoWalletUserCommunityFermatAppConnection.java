package com.bitdubai.sub_app.intra_user_community.app_connection;

import android.app.Activity;

import com.bitdubai.fermat_android_api.engine.FermatFragmentFactory;
import com.bitdubai.fermat_android_api.engine.FooterViewPainter;
import com.bitdubai.fermat_android_api.engine.HeaderViewPainter;
import com.bitdubai.fermat_android_api.engine.NavigationViewPainter;
import com.bitdubai.fermat_android_api.engine.NotificationPainter;
import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.AbstractFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Developers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.interfaces.IntraUserInformation;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.interfaces.IntraUserModuleManager;
import com.bitdubai.sub_app.intra_user_community.fragmentFactory.IntraUserFragmentFactory;
import com.bitdubai.sub_app.intra_user_community.navigation_drawer.IntraUserCommunityNavigationViewPainter;
import com.bitdubai.sub_app.intra_user_community.session.IntraUserSubAppSession;
import com.bitdubai.fermat_ccp_api.layer.actor.intra_user.interfaces.IntraWalletUserActor;
/**
 * Created by Matias Furszyfer on 2015.12.09..
 */
public class CryptoWalletUserCommunityFermatAppConnection extends AppConnections{

   private IntraUserSubAppSession fullyLoadedSession;
    private IntraUserModuleManager moduleManager;

    public CryptoWalletUserCommunityFermatAppConnection(Activity activity,IntraUserSubAppSession fullyLoadedSession) {
        super(activity);
        this.fullyLoadedSession = fullyLoadedSession;
        moduleManager = fullyLoadedSession.getModuleManager();
    }

    @Override
    public FermatFragmentFactory getFragmentFactory() {
        return new IntraUserFragmentFactory();
    }

    @Override
    public PluginVersionReference getPluginVersionReference() {
        return  new PluginVersionReference(
                Platforms.CRYPTO_CURRENCY_PLATFORM,
                Layers.SUB_APP_MODULE,
                Plugins.INTRA_WALLET_USER,
                Developers.BITDUBAI,
                new Version()
        );
    }

    @Override
    public AbstractFermatSession getSession() {
        return new IntraUserSubAppSession();
    }

    @Override
    public NavigationViewPainter getNavigationViewPainter() {
        return new IntraUserCommunityNavigationViewPainter(getActivity(),getActiveIdentity());
    }

    @Override
    public HeaderViewPainter getHeaderViewPainter() {
        return null;
    }

    @Override
    public FooterViewPainter getFooterViewPainter() {
        return null;
    }

    @Override
    public NotificationPainter getNotificationPainter(String code){

        NotificationPainter notification = null;

        String[] params = code.split("|");
        String notificationType = params[0];
        String senderActorPublicKey = params[1];

        switch (notificationType){
            case "CONNECTION_REQUEST":
            try
            {
                //find last notification by sender actor public key
                IntraWalletUserActor senderActor= moduleManager.getLastNotification(senderActorPublicKey);
                notification = new UserCommunityNotificationPainter("Nuevo pedido de conexión","Se recibió un pedido de conexion de " + senderActor.getName(),"","");
                break;
            }
            catch(Exception e)
            {

            }

        }

        return notification;
    }
}
