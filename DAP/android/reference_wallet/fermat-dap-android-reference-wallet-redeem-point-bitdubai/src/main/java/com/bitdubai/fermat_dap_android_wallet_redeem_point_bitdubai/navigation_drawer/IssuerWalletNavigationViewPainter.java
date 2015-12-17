package com.bitdubai.fermat_dap_android_wallet_redeem_point_bitdubai.navigation_drawer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bitdubai.fermat_android_api.engine.NavigationViewPainter;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_dap_android_wallet_redeem_point_bitdubai.R;

/**
 * Created by frank on 12/9/15.
 */
public class IssuerWalletNavigationViewPainter implements NavigationViewPainter {
    private Activity activity;
    private final ActorIdentity actorIdentity;

    public IssuerWalletNavigationViewPainter(Activity activity, ActorIdentity actorIdentity) {
        this.activity = activity;
        this.actorIdentity = actorIdentity;
    }

    @Override
    public View addNavigationViewHeader() {
        return null;
    }

    @Override
    public FermatAdapter addNavigationViewAdapter() {
        try {
            return new IssuerWalletNavigationViewAdapter(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ViewGroup addNavigationViewBodyContainer(LayoutInflater layoutInflater, ViewGroup base) {
        return (RelativeLayout) layoutInflater.inflate(R.layout.dap_wallet_asset_redeem_point_navigation_view_bottom, base, true);
    }

    @Override
    public Bitmap addBodyBackground() {
        Bitmap drawable = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = true;
            options.inSampleSize = 5;
            drawable = BitmapFactory.decodeResource(
                    activity.getResources(), R.color.fab_material_white);
        }catch (OutOfMemoryError error){
            error.printStackTrace();
        }
        return drawable;
    }

    @Override
    public int addBodyBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public RecyclerView.ItemDecoration addItemDecoration() {
        return null;
    }

    @Override
    public boolean hasBodyBackground() {
        return true;
    }
}
