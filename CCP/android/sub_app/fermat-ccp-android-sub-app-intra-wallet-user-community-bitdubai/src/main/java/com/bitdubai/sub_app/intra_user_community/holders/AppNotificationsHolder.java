package com.bitdubai.sub_app.intra_user_community.holders;

import android.view.View;

import com.bitdubai.fermat_android_api.layer.definition.wallet.FermatRoundedImageView;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.holders.FermatViewHolder;
import com.bitdubai.sub_app.intra_user_community.R;

/**
 * Created by josemanueldsds on 29/11/15.
 */
public class AppNotificationsHolder extends FermatViewHolder {

    public FermatRoundedImageView userAvatar;
    public FermatTextView userName;

    /**
     * Constructor
     *
     * @param itemView
     */
    public AppNotificationsHolder(View itemView) {
        super(itemView);

        userName = (FermatTextView)itemView.findViewById(R.id.userName);
        userAvatar = (FermatRoundedImageView)itemView.findViewById(R.id.imageView_avatar);

    }
}
