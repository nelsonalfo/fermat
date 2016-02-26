package com.bitdubai.reference_wallet.crypto_broker_wallet.fragments.closed_negotiation_details;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ClauseType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.MoneyType;
import com.bitdubai.fermat_cbp_api.all_definition.enums.NegotiationType;
import com.bitdubai.fermat_cbp_api.all_definition.identity.ActorIdentity;
import com.bitdubai.fermat_cbp_api.all_definition.negotiation.NegotiationLocations;
import com.bitdubai.fermat_cbp_api.layer.network_service.negotiation_transmission.exceptions.CantSendNegotiationToCryptoCustomerException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.ClauseInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.interfaces.CustomerBrokerNegotiationInformation;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.CryptoBrokerWalletManager;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.CryptoBrokerWalletModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.adapters.ClosedNegotiationDetailsAdapter;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.dialogs.ClauseDateTimeDialog;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.dialogs.TextValueDialog;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.holders.negotiation_details.clauseViewHolder.ClauseViewHolder;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.holders.negotiation_details.clauseViewHolder.ExpirationTimeViewHolder;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.holders.negotiation_details.clauseViewHolder.FooterViewHolder;
import com.bitdubai.reference_wallet.crypto_broker_wallet.common.models.NegotiationWrapper;
import com.bitdubai.reference_wallet.crypto_broker_wallet.fragments.common.SimpleListDialogFragment;
import com.bitdubai.reference_wallet.crypto_broker_wallet.session.CryptoBrokerWalletSession;
import com.bitdubai.reference_wallet.crypto_broker_wallet.util.CommonLogger;
import com.bitdubai.reference_wallet.crypto_broker_wallet.util.MathUtils;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lozadaa on 22/02/16.
 */

public class ClosedNegotiationDetailsFragment  extends AbstractFermatFragment<CryptoBrokerWalletSession, ResourceProviderManager> {

    private static final String TAG = "ClosedNegotiationDetails";
    private static final NumberFormat numberFormat = DecimalFormat.getInstance();

    // DATA
    private NegotiationWrapper negotiationWrapper;

    // Fermat Managers
    private CryptoBrokerWalletManager walletManager;
    private ErrorManager errorManager;
    private ClosedNegotiationDetailsAdapter adapter;

    public static ClosedNegotiationDetailsFragment newInstance() {
        return new ClosedNegotiationDetailsFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            CryptoBrokerWalletModuleManager moduleManager = appSession.getModuleManager();
            walletManager = moduleManager.getCryptoBrokerWallet(appSession.getAppPublicKey());
            errorManager = appSession.getErrorManager();

            CustomerBrokerNegotiationInformation negotiationInfo = appSession.getNegotiationData();
            negotiationWrapper = new NegotiationWrapper(negotiationInfo, appSession);

        } catch (Exception ex) {
            CommonLogger.exception(TAG, ex.getMessage(), ex);
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET,
                        UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, ex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        configureToolbar();

        final View rootView = inflater.inflate(R.layout.cbw_fragment_closed_negotiation_details_activity, container, false);
        final Button btn_confirm = (Button) rootView.findViewById(R.id.cbw_confirm_button);
        final ImageView customerImage = (ImageView) rootView.findViewById(R.id.cbw_customer_image);
        final FermatTextView customerName = (FermatTextView) rootView.findViewById(R.id.cbw_customer_name);
        final FermatTextView buyingDetails = (FermatTextView) rootView.findViewById(R.id.cbw_buying_summary);
        final FermatTextView exchangeRateSummary = (FermatTextView) rootView.findViewById(R.id.cbw_selling_exchange_rate);
        final FermatTextView lastUpdateDate = (FermatTextView) rootView.findViewById(R.id.cbw_last_update_date);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cbw_closed_negotiation_details_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        btn_confirm.setVisibility(View.GONE);

        final CustomerBrokerNegotiationInformation negotiationInfo = negotiationWrapper.getNegotiationInfo();
        final ActorIdentity customer = negotiationInfo.getCustomer();
        final Map<ClauseType, ClauseInformation> clauses = negotiationInfo.getClauses();

        final String merchandise = clauses.get(ClauseType.CUSTOMER_CURRENCY).getValue();
        final String exchangeAmount = clauses.get(ClauseType.EXCHANGE_RATE).getValue();
        final String paymentCurrency = clauses.get(ClauseType.BROKER_CURRENCY).getValue();
        final String amount = clauses.get(ClauseType.CUSTOMER_CURRENCY_QUANTITY).getValue();

        //Negotiation Summary
        customerImage.setImageDrawable(getImgDrawable(customer.getProfileImage()));
        customerName.setText(customer.getAlias());
        lastUpdateDate.setText(DateFormat.format("dd MMM yyyy", negotiationInfo.getLastNegotiationUpdateDate()));
        exchangeRateSummary.setText(getResources().getString(R.string.cbw_exchange_rate_summary, merchandise, exchangeAmount, paymentCurrency));
        buyingDetails.setText(getResources().getString(R.string.cbw_buying_details, amount, merchandise));

        adapter = new ClosedNegotiationDetailsAdapter(getActivity(), negotiationWrapper);
        adapter.setMarketRateList(appSession.getActualExchangeRates());


        // TODO colocar el precio de referencia, es decir la cotizacion

        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cbw_closed_negotiation_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cbw_action_cancel_negotiation) {
            TextValueDialog dialog = new TextValueDialog(getActivity(), appSession, appResourcesProviderManager);
            dialog.configure(R.string.cbw_cancel_negotiation, R.string.cbw_reason);
            dialog.setTextFreeInputType(true);
            dialog.setAcceptBtnListener(new TextValueDialog.OnClickAcceptListener() {
                @Override
                public void onClick(String editTextValue) {
                    try {
                        walletManager.cancelNegotiation(negotiationWrapper.getNegotiationInfo(), editTextValue);
                        changeActivity(Activities.CBP_CRYPTO_BROKER_WALLET_HOME, appSession.getAppPublicKey());

                    } catch (FermatException e) {
                        Toast.makeText(getActivity(), "Oopss, an error ocurred", Toast.LENGTH_SHORT).show();
                        if (errorManager != null)
                            errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET,
                                    UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                        else
                            Log.e(TAG, e.getMessage(), e);
                    }
                }
            });
            dialog.show();
            return true;
        }
        return false;
    }




    private void configureToolbar() {
        Toolbar toolbar = getToolbar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            toolbar.setBackground(getResources().getDrawable(R.drawable.cbw_action_bar_gradient_colors, null));
        else
            toolbar.setBackground(getResources().getDrawable(R.drawable.cbw_action_bar_gradient_colors));
    }

    private Drawable getImgDrawable(byte[] customerImg) {
        Resources res = getResources();

        if (customerImg != null && customerImg.length > 0)
            return ImagesUtils.getRoundedBitmap(res, customerImg);

        return ImagesUtils.getRoundedBitmap(res, R.drawable.person);
    }

}
