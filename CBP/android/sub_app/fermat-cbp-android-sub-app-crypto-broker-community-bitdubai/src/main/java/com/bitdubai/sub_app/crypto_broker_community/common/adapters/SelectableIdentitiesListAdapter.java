package com.bitdubai.sub_app.crypto_broker_community.common.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_android_api.ui.holders.FermatViewHolder;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_community.interfaces.CryptoBrokerCommunitySelectableIdentity;
import com.bitdubai.sub_app.crypto_broker_community.R;

import java.util.List;

/**
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 12/01/2015.
 *
 * @author lnacosta
 * @version 1.0.0
 */
public class SelectableIdentitiesListAdapter
        extends FermatAdapter<CryptoBrokerCommunitySelectableIdentity, SelectableIdentitiesListAdapter.AppSelectableIdentityHolder> {

    public SelectableIdentitiesListAdapter(Context context, List<CryptoBrokerCommunitySelectableIdentity> dataSet) {
        super(context, dataSet);
    }

    @Override
    protected AppSelectableIdentityHolder createHolder(View itemView, int type) {
        return new AppSelectableIdentityHolder(itemView);
    }

    @Override
    protected int getCardViewResource() {
        return R.layout.cbc_dialog_selectable_identity_list_item;
    }

    @Override
    protected void bindHolder(AppSelectableIdentityHolder holder, CryptoBrokerCommunitySelectableIdentity data, int position) {

        if (data.getActorType()== Actors.CBP_CRYPTO_CUSTOMER) {

            if (data.getPublicKey() != null) {
                holder.friendName.setText(data.getAlias());
                if (data.getImage() != null) {
                    Bitmap bitmap;
                    if (data.getImage().length > 0) {
                        bitmap = BitmapFactory.decodeByteArray(data.getImage(), 0, data.getImage().length);
                    } else {
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true);
                    holder.friendAvatar.setImageDrawable(ImagesUtils.getRoundedBitmap(context.getResources(), bitmap));
                } else {
                    Bitmap bitmap;
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true);
                    holder.friendAvatar.setImageDrawable(ImagesUtils.getRoundedBitmap(context.getResources(), bitmap));
                }

            } else {
                Bitmap bitmap;
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_image);
                bitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true);
                holder.friendAvatar.setImageDrawable(ImagesUtils.getRoundedBitmap(context.getResources(), bitmap));
            }
        }
    }

    public int getSize() {
        if (dataSet != null)
            return dataSet.size();
        return 0;
    }

    public class AppSelectableIdentityHolder extends FermatViewHolder {

        public ImageView friendAvatar;
        public FermatTextView friendName;

        /**
         * Constructor
         *
         * @param itemView cast ui elements
         */
        public AppSelectableIdentityHolder(View itemView) {
            super(itemView);

            friendName = (FermatTextView) itemView.findViewById(R.id.cbc_selectable_identity_username);
            friendAvatar = (ImageView) itemView.findViewById(R.id.cbc_selectable_identity_user_avatar);
        }
    }

}
