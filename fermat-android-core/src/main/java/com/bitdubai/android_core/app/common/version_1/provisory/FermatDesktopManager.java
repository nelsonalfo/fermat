package com.bitdubai.android_core.app.common.version_1.provisory;

import com.bitdubai.fermat_api.AppsStatus;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.FermatAppType;

/**
 * Created by mati on 2016.02.22..
 */
public class FermatDesktopManager implements DesktopManager{


    @Override
    public String getAppName() {
        return "desktop";
    }

    @Override
    public String getAppPublicKey() {
        return "main_desktop";
    }

    @Override
    public AppsStatus getAppStatus() {
        return AppsStatus.RELEASE;
    }

    @Override
    public FermatAppType getAppType() {
        return FermatAppType.DESKTOP;
    }
}
