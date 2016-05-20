package com.bitdubai.reference_wallet.bank_money_wallet.fragments.account_management;

import android.os.Build;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankAccountType;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.exceptions.CantEditAccountException;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankAccountNumber;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet_module.interfaces.BankMoneyWalletModuleManager;
import com.bitdubai.fermat_bnk_plugin.layer.wallet.bank_money.developer.bitdubai.version_1.structure.BankAccountNumberImpl;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.reference_wallet.bank_money_wallet.R;
import com.bitdubai.reference_wallet.bank_money_wallet.common.holders.AccountListViewHolder;
import com.bitdubai.reference_wallet.bank_money_wallet.session.BankMoneyWalletSession;
import com.bitdubai.reference_wallet.bank_money_wallet.util.ReferenceWalletConstants;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Created by Alejandro Bicelis on 12/05/16.
 */
public class EditAccountFragment extends AbstractFermatFragment implements Spinner.OnItemSelectedListener {

    List<BankAccountNumber> bankAccounts = new ArrayList<>();

    private BankMoneyWalletModuleManager moduleManager;
    private ErrorManager errorManager;

    EditText accountNumberText;
    EditText accountAliasText;

    BankAccountNumber oldData;
    private String newImageId;

    List<String> accountImages = new ArrayList<>();
    ArrayAdapter<String> imagesSpinnerAdapter;
    Spinner accountImageSpinner;

    public static EditAccountFragment newInstance() {
        return new EditAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            moduleManager = ((BankMoneyWalletSession) appSession).getModuleManager();
            bankAccounts = moduleManager.getBankingWallet().getAccounts();

            //Get old account data from session
            oldData = (BankAccountNumber) appSession.getData("account_data");

            errorManager = appSession.getErrorManager();
        } catch (Exception e) {
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.BNK_BANKING_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, e);
        }

        //Fill up accountImages
        accountImages.add("Cube");
        accountImages.add("Safe");
        accountImages.add("Money");
        accountImages.add("Money 2");
        accountImages.add("Coins");
        accountImages.add("Coins 2");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.bw_edit_account, container, false);

        accountNumberText = (EditText) layout.findViewById(R.id.account_number);
        accountAliasText = (EditText) layout.findViewById(R.id.account_alias);

        accountNumberText.setText(oldData.getAccount());
        accountAliasText.setText(oldData.getAlias());

        imagesSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accountImages);
        imagesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imagesSpinnerAdapter.notifyDataSetChanged();
        accountImageSpinner = (Spinner) layout.findViewById(R.id.bnk_edit_account_image_id_spinner);
        accountImageSpinner.setAdapter(imagesSpinnerAdapter);
        accountImageSpinner.setOnItemSelectedListener(this);
        accountImageSpinner.setSelection(accountImages.indexOf(oldData.getAccountImageId()));

        //Allow only numbers and dashes
        accountNumberText.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));

        configureToolbar();
        return layout;
    }

    private void configureToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getToolbar().setBackground(getResources().getDrawable(R.drawable.bw_header_gradient_background,null));
        else
            getToolbar().setBackground(getResources().getDrawable(R.drawable.bw_header_gradient_background));
        //getToolbar().setNavigationIcon(R.drawable.bw_back_icon_action_bar);
    }

    private boolean editAccount(){

        String newAccountNumber = accountNumberText.getText().toString().trim();
        String newAlias = accountAliasText.getText().toString().trim();

        //Check that account number is not blank
        if(newAccountNumber.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid account number", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Check that alias is not blank
        if(newAlias.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid account alias", Toast.LENGTH_SHORT).show();
            return false;
        }

        //If accountNumber changed, verify it is unique
        if(!oldData.getAccount().equals(newAccountNumber)){

            //Check that newAccountNumber is different than every account number saved into database
            for (BankAccountNumber savedAccount : bankAccounts) {
                if (savedAccount.getAccount().equals(newAccountNumber)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Account number already exists!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        //If something was indeed changed
        if(!oldData.getAccount().equals(newAccountNumber)
                || !oldData.getAlias().equals(newAlias)
                || !oldData.getAccountImageId().equals(newImageId)) {

            try{
                moduleManager.getBankingWallet().editAccount(oldData.getAccount(), newAlias, newAccountNumber, newImageId);

                //Update account data in session
                BankAccountNumber newData = new BankAccountNumberImpl(newAlias, newAccountNumber, oldData.getCurrencyType(), oldData.getAccountType(), oldData.getBankName(), newImageId);
                appSession.setData("account_data", newData);
                appSession.setData("account_image", AccountListViewHolder.getResource(newImageId));

                Toast.makeText(getActivity().getApplicationContext(), "Account Edited", Toast.LENGTH_SHORT).show();
            }catch(CantEditAccountException e){
                Toast.makeText(getActivity().getApplicationContext(), "There was a problem editing the account, try again later", Toast.LENGTH_SHORT).show();
                errorManager.reportUnexpectedWalletException(Wallets.BNK_BANKING_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Nothing was edited", Toast.LENGTH_SHORT).show();
        }
        return true;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(new Bundle());
        } catch (Exception e){
            errorManager.reportUnexpectedWalletException(Wallets.BNK_BANKING_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, e);
            makeText(getActivity(), "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==ReferenceWalletConstants.SAVE_ACTION){
            if(editAccount())
                changeActivity(Activities.BNK_BANK_MONEY_WALLET_ACCOUNT_DETAILS, appSession.getAppPublicKey());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, ReferenceWalletConstants.SAVE_ACTION, 0, "Save")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i = parent.getId();

        if(i == R.id.bnk_edit_account_image_id_spinner) {
            newImageId = accountImages.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}