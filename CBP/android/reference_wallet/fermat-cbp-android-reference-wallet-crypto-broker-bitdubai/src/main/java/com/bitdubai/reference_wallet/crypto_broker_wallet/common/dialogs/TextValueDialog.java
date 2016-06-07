package com.bitdubai.reference_wallet.crypto_broker_wallet.common.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;

import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatButton;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatEditText;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.dialogs.FermatDialog;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;
import com.bitdubai.reference_wallet.crypto_broker_wallet.session.CryptoBrokerWalletSession;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;


/**
 * Created by nelson on 14/01/16.
 */
public class TextValueDialog extends FermatDialog<CryptoBrokerWalletSession, ResourceProviderManager>
        implements View.OnClickListener {

    private FermatEditText editTextView;
    private int titleStringResource;
    private int hintStringResource;
    private String editTextValue;

    private OnClickAcceptListener acceptBtnListener;
    private boolean setTextFree;

    //TEXT COUNT
    private boolean activeTextCount = false;
    private int maxLenghtTextCount = 100;
    FermatTextView textCount;

    //TEXT COUNT
    private final TextWatcher textWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {textCount.setText(String.valueOf(maxLenghtTextCount - s.length()));}
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    public interface OnClickAcceptListener {
        void onClick(String editTextValue);
    }


    public TextValueDialog(Activity activity, CryptoBrokerWalletSession fermatSession, ResourceProviderManager resources) {
        super(activity, fermatSession, resources);

        hintStringResource = R.string.hint;
        titleStringResource = R.string.title;
        editTextValue = null;
    }

    public void configure(int titleRes, int hintRes) {
        titleStringResource = titleRes;
        hintStringResource = hintRes;
    }

    public void setAcceptBtnListener(OnClickAcceptListener acceptBtnListener) {
        this.acceptBtnListener = acceptBtnListener;
    }

    public void setTextFreeInputType(boolean setTextFree) {
        this.setTextFree = setTextFree;

        if (editTextView != null)
            if (setTextFree)
                editTextView.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE);
            else
                editTextView.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
    }

    public void setEditTextValue(String editTextValue) {
        this.editTextValue = editTextValue;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cbw_text_dialog_cancel_btn) {
            dismiss();

        } else if (view.getId() == R.id.cbw_text_dialog_accept_btn) {
            if (acceptBtnListener != null)
                acceptBtnListener.onClick(editTextView.getText().toString());
            dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FermatTextView titleTextView = (FermatTextView) findViewById(R.id.cbw_text_dialog_title);
        titleTextView.setText(titleStringResource);
        FermatButton acceptBtn = (FermatButton) findViewById(R.id.cbw_text_dialog_accept_btn);
        acceptBtn.setOnClickListener(this);
        FermatButton cancelBtn = (FermatButton) findViewById(R.id.cbw_text_dialog_cancel_btn);
        cancelBtn.setOnClickListener(this);
        editTextView = (FermatEditText) findViewById(R.id.cbw_text_dialog_edit_text);
        editTextView.setHint(hintStringResource);

        //TEXT COUNT
        if(activeTextCount) {
            textCount = (FermatTextView) findViewById(R.id.cbw_text_dialog_edit_text_count);
            editTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLenghtTextCount)});
            editTextView.addTextChangedListener(textWatcher);
            textCount.setText(String.valueOf(maxLenghtTextCount));
            textCount.setVisibility(View.VISIBLE);
        }

        if (editTextValue != null)
            editTextView.setText(editTextValue);
        if (setTextFree)
            editTextView.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE);
        else
            editTextView.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.cbw_clause_text_dialog;
    }

    @Override
    protected int setWindowFeature() {
        return Window.FEATURE_NO_TITLE;
    }

    //TEXT COUNT
    public void setTextCount(int maxLenghtText){
        this.maxLenghtTextCount = maxLenghtText;
        this.activeTextCount = true;
    }
}
