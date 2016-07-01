package org.fermat.fermat_dap_android_wallet_redeem_point.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.ui.Views.PresentationDialog;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantGetSettingsException;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantPersistSettingsException;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.SettingsNotFoundException;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_dap_android_wallet_redeem_point_bitdubai.R;
import com.mati.fermat_preference_settings.drawer.FermatPreferenceFragment;
import com.mati.fermat_preference_settings.drawer.interfaces.PreferenceSettingsItem;
import com.mati.fermat_preference_settings.drawer.models.PreferenceSettingsSwithItem;

import org.fermat.fermat_dap_api.layer.dap_module.wallet_asset_redeem_point.RedeemPointSettings;
import org.fermat.fermat_dap_api.layer.dap_module.wallet_asset_redeem_point.interfaces.AssetRedeemPointWalletSubAppModule;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;


/**
 * Created by Jinmy  on 02/02/16.
 */
public class SettingsNotificationsFragment extends FermatPreferenceFragment<ReferenceAppFermatSession<AssetRedeemPointWalletSubAppModule>, ResourceProviderManager> {

    private View rootView;
    private Spinner spinner;
    private Switch notificationSwitch;
    private Toolbar toolbar;
    private AssetRedeemPointWalletSubAppModule moduleManager;
    private ErrorManager errorManager;
    RedeemPointSettings settings = null;

    public static SettingsNotificationsFragment newInstance() {
        return new SettingsNotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {

            moduleManager = appSession.getModuleManager();
            errorManager = appSession.getErrorManager();

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } catch (Exception e) {
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.DAP_REDEEM_POINT_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, e);
            e.printStackTrace();
        }
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        try {
//            rootView = inflater.inflate(R.layout.dap_wallet_asset_uredeempoint_settings_notifications, container, false);
//            setUpUi();
//            configureToolbar();
//            return rootView;
//        } catch (Exception e) {
//            makeText(getActivity(), R.string.dap_redeem_point_wallet_opps_system_error, Toast.LENGTH_SHORT).show();
//            redeemPointSession.getErrorManager().reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.CRASH, e);
//        }
//
//        return null;
//    }

//    public void setUpUi() {
//        notificationSwitch = (Switch) rootView.findViewById(R.id.switch_notification);
//    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    protected List<PreferenceSettingsItem> setSettingsItems() {
        BlockchainNetworkType blockchainNetworkType = null;
        List<PreferenceSettingsItem> list = new ArrayList<>();

        try {
            settings = moduleManager.loadAndGetSettings(appSession.getAppPublicKey());

            list.add(new PreferenceSettingsSwithItem(1, "Enabled Notifications", settings.getNotificationEnabled()));
            list.add(new PreferenceSettingsSwithItem(2, "Enabled Ask Reception for Assets", settings.getAssetNotificationEnabled()));

//            if (settingsManager.getBlockchainNetworkType() != null) {
//                blockchainNetworkType = bitcoinWalletSettings.getBlockchainNetworkType();
//                switch (blockchainNetworkType) {
//                    case PRODUCTION:
//                        previousSelectedItem = "MainNet";
//                        break;
//                    case REG_TEST:
//                        previousSelectedItem = "RegTest";
//                        break;
//                    case TEST_NET:
//                        previousSelectedItem = "TestNet";
//                        break;
//                }
//            }
            final Bundle dataDialog = new Bundle();
//            dataDialog.putInt("items", R.array.items);
//            dataDialog.putString("positive_button_text", getResources().getString(R.string.ok_label));
//            dataDialog.putString("negative_button_text", getResources().getString(R.string.cancel_label));
//            dataDialog.putString("title", getResources().getString(R.string.title_label));
//            dataDialog.putString("mode", "single_option");
//            dataDialog.putString("previous_selected_item", previousSelectedItem);
//            list.add(new PreferenceSettingsOpenDialogText(5, "Select Network", dataDialog));
//            list.add(new PreferenceSettingsLinkText(9, "Send Error Report", "", 15, Color.GRAY));
//            list.add(new PreferenceSettingsLinkText(10, "Export Private key ", "", 15, Color.GRAY));
        } catch (CantGetSettingsException e) {
            e.printStackTrace();
        } catch (SettingsNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onSettingsTouched(PreferenceSettingsItem preferenceSettingsItem, int position) {
        try {
            try {
                settings = moduleManager.loadAndGetSettings(appSession.getAppPublicKey());
            } catch (CantGetSettingsException e) {
                e.printStackTrace();
            } catch (SettingsNotFoundException e) {
                e.printStackTrace();
            }
            settings.setIsPresentationHelpEnabled(false);

            try {
                moduleManager.persistSettings(appSession.getAppPublicKey(), settings);
            } catch (CantPersistSettingsException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSettingsTouched(String item, int position) {

    }

    @Override
    public void onSettingsChanged(PreferenceSettingsItem preferenceSettingsItem, int position, boolean isChecked) {
        try {
            try {
                settings = moduleManager.loadAndGetSettings(appSession.getAppPublicKey());
            } catch (CantGetSettingsException e) {
                e.printStackTrace();
            } catch (SettingsNotFoundException e) {
                e.printStackTrace();
            }

            if (preferenceSettingsItem.getId() == 1) {
                //enable notifications settings
                settings.setNotificationEnabled(isChecked);
            }
            if (preferenceSettingsItem.getId() == 2) {
                //enable notifications settings
                settings.setAssetNotificationEnabled(isChecked);
            }

            try {
                moduleManager.persistSettings(appSession.getAppPublicKey(), settings);
            } catch (CantPersistSettingsException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public int getBackgroundAlpha() {
        return 100;
    }

    @Override
    public void optionSelected(PreferenceSettingsItem preferenceSettingsItem, int position) {

    }

    @Override
    public void dialogOptionSelected(String item, int position) {
        // CustomDialogFragment customDialogFragment = (CustomDialogFragment) dialog;
        // previousSelectedItem = customDialogFragment.getPreviousSelectedItem();
        // Toast.makeText(this, "OK button pressed", Toast.LENGTH_SHORT).show();

        BlockchainNetworkType blockchainNetworkType;

//        switch (item) {
//
//            case "MainNet":
//                blockchainNetworkType = BlockchainNetworkType.PRODUCTION;
//                break;
//            case "TestNet":
//                blockchainNetworkType = BlockchainNetworkType.TEST_NET;
//                break;
//            case "RegTest":
//                blockchainNetworkType = BlockchainNetworkType.REG_TEST;
//                break;
//            default:
//                blockchainNetworkType = BlockchainNetworkType.getDefaultBlockchainNetworkType();
//                break;
//        }
//        System.out.println("NETWORK TYPE TO BE SAVED IS  " + blockchainNetworkType.getCode());
//        if (blockchainNetworkType == null) {
//            if (settings.getBlockchainNetworkType() != null) {
//                blockchainNetworkType = settings.getBlockchainNetworkType();
//            } else {
//                blockchainNetworkType = BlockchainNetworkType.getDefaultBlockchainNetworkType();
//            }
//        }
//        settings.setBlockchainNetworkType(blockchainNetworkType);
//        try {
//            settingsManager.persistSettings(appSession.getAppPublicKey(), settings);
//        } catch (CantPersistSettingsException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onOptionMenuPrepared(Menu menu){
        super.onOptionMenuPrepared(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            switch (id) {
                case 1://IC_ACTION_REDEEM_SETTINGS_NOTIFICATIONS
                    setUpSettingsNetwork(moduleManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
                    break;
            }

//            if (id == SessionConstantsRedeemPoint.IC_ACTION_REDEEM_SETTINGS_NOTIFICATIONS) {
//                setUpSettingsNetwork(moduleManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
//                return true;
//            }

        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getActivity(), R.string.dap_redeem_point_wallet_system_error,
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpSettingsNetwork(boolean checkButton) {
        try {
            PresentationDialog presentationDialog = new PresentationDialog.Builder(getActivity(), appSession)
                    .setBannerRes(R.drawable.banner_redeem_point_wallet)
                    .setIconRes(R.drawable.redeem_point)
                    .setVIewColor(R.color.dap_redeem_point_view_color)
                    .setTitleTextColor(R.color.dap_redeem_point_view_color)
                    .setSubTitle(R.string.dap_redeem_wallet_detail_subTitle)
                    .setBody(R.string.dap_redeem_wallet_detail_body)
                    .setTemplateType(PresentationDialog.TemplateType.TYPE_PRESENTATION_WITHOUT_IDENTITIES)
                    .setIsCheckEnabled(checkButton)
                    .build();

            presentationDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureToolbar() {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.redeem_card_titlebar));
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setBottom(Color.WHITE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.setStatusBarColor(getResources().getColor(R.color.redeem_card_titlebar));
            }
        }
    }
}
