package com.bitdubai.sub_app.chat_community.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.util.BitmapWorkerTask;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.sub_app.chat_community.R;
import com.bitdubai.sub_app.chat_community.common.views.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;

/**
 * @author Created by mati on 2015.11.12..
 * @author Modified byJose Manuel De Sousa 08/12/2015
 */
public class FragmentsCommons {


    public static View setUpHeaderScreen(LayoutInflater inflater, Context activity, ActiveActorIdentityInformation identity) throws Exception {
        /**
         * Navigation view header
         */
        RelativeLayout relativeLayout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
        View view = inflater.inflate(R.layout.cht_comm_navigation_drawer_header_item, relativeLayout, true);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_profile);
        ImageView imageViewCircle = (ImageView) view.findViewById(R.id.image_view_circle);
        if (identity != null) {
            if (identity.getImage() != null) {
                if (identity.getImage().length > 0) {
                    // set circle bitmap
                    ByteArrayInputStream bytes = new ByteArrayInputStream(identity.getImage());
                    BitmapDrawable bmd = new BitmapDrawable(bytes);
                    imageView.setImageBitmap(Utils.getCircleBitmap(bmd.getBitmap()));
                    imageViewCircle.setVisibility(View.VISIBLE);
//                    BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView,activity.getResources(),0,false);
//                    bitmapWorkerTask.execute(identity.getImage());
                } else {
                    imageViewCircle.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.cht_comm_icon_user); //Picasso.with(activity).load(R.drawable.cht_comm_btn_conect_background);//profile_image).into(imageView);
                }
            } else {
                imageViewCircle.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.cht_comm_icon_user); //Picasso.with(activity).load(R.drawable.cht_comm_btn_conect_background);//profile_image).into(imageView);
            }
//            } else
//                Picasso.with(activity).load(R.drawable.cht_comm_btn_conect_background);//profile_image).into(imageView);
            FermatTextView fermatTextView = (FermatTextView) view.findViewById(R.id.txt_name);
            fermatTextView.setText(identity.getAlias());
        }
        return view;
    }
}
