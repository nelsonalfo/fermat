package org.fermat.fermat_dap_android_sub_app_asset_user_community.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.ui.Views.PresentationDialog;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.R;

import org.fermat.fermat_dap_android_sub_app_asset_user_community.sessions.SessionConstantsAssetUserCommunity;
import org.fermat.fermat_dap_api.layer.dap_module.wallet_asset_user.AssetUserSettings;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.asset_user_community.interfaces.AssetUserCommunitySubAppModuleManager;

import static android.widget.Toast.makeText;

/**
 * Jinmy Bohorquez 02/26/2016
 */
public class UserCommunitySettingsNotificationsFragment extends AbstractFermatFragment<ReferenceAppFermatSession<AssetUserCommunitySubAppModuleManager>, ResourceProviderManager> {

    private View rootView;
    private Spinner spinner;
    private Switch notificationSwitch;

    private AssetUserCommunitySubAppModuleManager moduleManager;
    AssetUserSettings settings = null;

    //    SettingsManager<AssetUserSettings> settingsManager;
    private ErrorManager errorManager;

    public static UserCommunitySettingsNotificationsFragment newInstance() {
        return new UserCommunitySettingsNotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        moduleManager = appSession.getModuleManager();
        errorManager = appSession.getErrorManager();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            rootView = inflater.inflate(R.layout.dap_user_community_settings_notifications, container, false);
            setUpUi();
            //configureToolbar();
            return rootView;
        } catch (Exception e) {
            makeText(getActivity(), R.string.dap_user_community_opps_system_error, Toast.LENGTH_SHORT).show();
            errorManager.reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.CRASH, e);
        }

        return null;
    }

    public void setUpUi() {
        notificationSwitch = (Switch) rootView.findViewById(R.id.switch_notification);
    }

    @Override
    public void onOptionMenuPrepared(Menu menu){
        super.onOptionMenuPrepared(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_SETTINGS_NOTIFICATION) {
                setUpSettings(moduleManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
                return true;
            }

        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getActivity(), R.string.dap_user_community_system_error,
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpSettings(boolean checkButton) {
        try {
            PresentationDialog presentationDialog = new PresentationDialog.Builder(getActivity(), appSession)
                    .setBannerRes(R.drawable.banner_asset_user_community)
                    .setIconRes(R.drawable.asset_user_comunity)
                    .setVIewColor(R.color.dap_community_user_view_color)
                    .setTitleTextColor(R.color.dap_community_user_view_color)
                    .setSubTitle(R.string.dap_user_community_settings_notification_subTitle)
                    .setBody(R.string.dap_user_community_settings_notification_body)
                    .setTemplateType(PresentationDialog.TemplateType.TYPE_PRESENTATION_WITHOUT_IDENTITIES)
                    .setIsCheckEnabled(checkButton)
                    .build();

            presentationDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    private void configureToolbar() {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            FermatDrawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = getResources().getDrawable(R.drawable.dap_wallet_asset_user_action_bar_gradient_colors, null);
                toolbar.setElevation(0);
            } else {
                drawable = getResources().getDrawable(R.drawable.dap_wallet_asset_user_action_bar_gradient_colors);
            }
            toolbar.setBackground(drawable);
        }
    }*/

}
