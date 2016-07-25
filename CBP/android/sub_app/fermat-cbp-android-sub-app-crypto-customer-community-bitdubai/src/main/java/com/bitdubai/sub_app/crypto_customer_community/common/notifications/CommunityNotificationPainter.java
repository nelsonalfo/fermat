package com.bitdubai.sub_app.crypto_customer_community.common.notifications;

import android.widget.RemoteViews;

import com.bitdubai.fermat_android_api.engine.NotificationPainter;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;


/**
 * This class contains the basic functionality of the crypto customer community notification painter.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 29/02/2016.
 *
 * @author lnacosta
 * @version 1.0.0
 */
public class CommunityNotificationPainter extends NotificationPainter {

    private String title;
    private String textBody;
    private String image;
    private int icon;

    public CommunityNotificationPainter(final String title,
                                        final String textBody,
                                        final String image,
                                        final int icon) {

        this.title = title;
        this.textBody = textBody;
        this.image = image;
        this.icon = icon;

    }

    @Override
    public RemoteViews getNotificationView(String code) {
        return null;
    }

    @Override
    public String getNotificationTitle() {
        return this.title;
    }

    @Override
    public String getNotificationImageText() {
        return this.image;
    }

    @Override
    public String getNotificationTextBody() {
        return this.textBody;
    }

    @Override
    public int getIcon() {
        return icon;
    }

    @Override
    public String getActivityCodeResult() {
        return Activities.CBP_SUB_APP_CRYPTO_CUSTOMER_COMMUNITY_CONNECTION_NOTIFICATIONS.getCode();
    }

    @Override
    public boolean showNotification() {
        return true;
    }

}
