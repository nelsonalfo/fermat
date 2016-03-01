package com.bitdubai.sub_app.intra_user_community.app_connection;

import android.app.Activity;

import com.bitdubai.fermat_android_api.engine.FermatFragmentFactory;
import com.bitdubai.fermat_android_api.engine.FooterViewPainter;
import com.bitdubai.fermat_android_api.engine.HeaderViewPainter;
import com.bitdubai.fermat_android_api.engine.NavigationViewPainter;
import com.bitdubai.fermat_android_api.engine.NotificationPainter;
import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.AbstractFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatSession;
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
public class CryptoWalletUserCommunityFermatAppConnection extends AppConnections<IntraUserSubAppSession>{

   private IntraUserSubAppSession intraUserSubAppSession;
    private IntraUserModuleManager moduleManager;

    public CryptoWalletUserCommunityFermatAppConnection(Activity activity) {
        super(activity);

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
        try
        {
            this.intraUserSubAppSession = (IntraUserSubAppSession)this.getSession();
            if(intraUserSubAppSession!=  null)
               moduleManager = intraUserSubAppSession.getModuleManager();
            return CryptoWalletUserCommunityBuildNotification.getNotification(moduleManager,code);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
