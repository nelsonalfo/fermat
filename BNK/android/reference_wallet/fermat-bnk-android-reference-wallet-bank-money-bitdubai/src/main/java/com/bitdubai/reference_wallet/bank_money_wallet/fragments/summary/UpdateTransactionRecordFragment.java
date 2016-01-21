package com.bitdubai.reference_wallet.bank_money_wallet.fragments.summary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankMoneyTransactionRecord;
import com.bitdubai.reference_wallet.bank_money_wallet.R;

/**
 * Created by memo on 19/01/16.
 */
public class UpdateTransactionRecordFragment extends AbstractFermatFragment {

    private EditText transactionType;
    private EditText transactionAmount;
    private EditText transactionDate;
    private EditText transactionConcept;

    private BankMoneyTransactionRecord transactionRecord;
    public static UpdateTransactionRecordFragment newInstance(){
        return new UpdateTransactionRecordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionRecord = ( BankMoneyTransactionRecord )appSession.getData("transaction_data");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.bw_transaction_detail,container,false);

        return layout;
    }



}
