package com.bitdubai.reference_niche_wallet.fermat_wallet.common.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.bitdubai.android_fermat_ccp_wallet_fermat.R;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatButton;
import com.bitdubai.fermat_android_api.ui.dialogs.FermatDialog;
import com.bitdubai.fermat_api.layer.all_definition.enums.SubAppsPublicKeys;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.fermat_wallet.interfaces.FermatWallet;


/**
 * Created by mati on 2015.11.17..
 */

public class ConnectionWithCommunityDialog extends FermatDialog<ReferenceAppFermatSession<FermatWallet>,ResourceProviderManager> implements View.OnClickListener {


    private final Activity activity;
    private FermatButton search_contact_btn;
    private FermatButton cancel_btn;

    /**
     * Constructor using Session and Resources
     *
     * @param activity
     * @param fermatSession parent class of walletSession and SubAppSession
     * @param resources     parent class of WalletResources and SubAppResources
     */

    public ConnectionWithCommunityDialog(Activity activity, ReferenceAppFermatSession<FermatWallet> fermatSession, ResourceProviderManager resources) {

        super(activity, fermatSession, resources);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        search_contact_btn =(FermatButton) findViewById(R.id.search_contact_btn);
        cancel_btn = (FermatButton) findViewById(R.id.cancel_btn);

        search_contact_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fermat_wallet_connection_with_community_dialog;
    }

    @Override
    protected int setWindowFeature() {
        return Window.FEATURE_NO_TITLE;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.search_contact_btn){
            try {
                Object[] object = new Object[2];
                changeApp(SubAppsPublicKeys.CCP_COMMUNITY.getCode(), object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(id == R.id.cancel_btn){
            dismiss();
        }
    }
}
