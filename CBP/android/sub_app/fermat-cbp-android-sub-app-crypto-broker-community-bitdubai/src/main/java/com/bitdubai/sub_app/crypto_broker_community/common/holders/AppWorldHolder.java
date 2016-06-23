package com.bitdubai.sub_app.crypto_broker_community.common.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitdubai.fermat_android_api.ui.holders.FermatViewHolder;
import com.bitdubai.sub_app.crypto_broker_community.R;

/**
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 16/12/2015.
 *
 * @author lnacosta
 * @version 1.0.0
 */
public class AppWorldHolder extends FermatViewHolder {

    public ImageView thumbnail;
    public TextView name;
    public ImageView connectionState;


    /**
     * Constructor
     *
     * @param itemView cast elements in layout
     * @param type
     */
    public AppWorldHolder(View itemView, int type) {
        super(itemView, type);
        //connectionState = (ImageView) itemView.findViewById(R.id.cbc_connection_state);
        thumbnail = (ImageView) itemView.findViewById(R.id.profile_image);
        name = (TextView) itemView.findViewById(R.id.community_name);
    }
}