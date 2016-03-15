package com.bitdubai.sub_app.crypto_broker_identity.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums.SubApps;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.ExposureLevel;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_identity.interfaces.CryptoBrokerIdentityInformation;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_identity.utils.CryptoBrokerIdentityInformationImpl;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedSubAppExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.sub_app.crypto_broker_identity.R;
import com.bitdubai.sub_app.crypto_broker_identity.util.EditIdentityWorker;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;

import static com.bitdubai.sub_app.crypto_broker_identity.session.CryptoBrokerIdentitySubAppSession.IDENTITY_INFO;
import static com.bitdubai.sub_app.crypto_broker_identity.util.CreateBrokerIdentityExecutor.INVALID_ENTRY_DATA;
import static com.bitdubai.sub_app.crypto_broker_identity.util.CreateBrokerIdentityExecutor.MISSING_IMAGE;
import static com.bitdubai.sub_app.crypto_broker_identity.util.CreateBrokerIdentityExecutor.SUCCESS;


/**
 * A simple {@link Fragment} subclass.
 */

public class EditCryptoBrokerIdentityFragment extends AbstractFermatFragment implements FermatWorkerCallBack {
    // Constants
    private static final String TAG = "EditBrokerIdentity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    // data
    private Bitmap cryptoBrokerBitmap;
    private boolean wantPublishIdentity;
    private String cryptoBrokerPublicKey;

    private boolean actualizable;

    // Managers
    private ErrorManager errorManager;

    private ImageView mBrokerImage;
    private ImageView sw;
    private EditText mBrokerName;

    private ExecutorService executor;
    private byte[] profileImage;

    private ImageView camara;
    private ImageView galeria;

    private Button botonU;

    public static EditCryptoBrokerIdentityFragment newInstance() {
        return new EditCryptoBrokerIdentityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_edit_crypto_broker_identity, container, false);
        initViews(rootLayout);
        return rootLayout;
    }

    /**
     * Inicializa las vistas de este Fragment
     *
     * @param layout el layout de este Fragment que contiene las vistas
     */
    private void initViews(View layout) {
        botonU = (Button) layout.findViewById(R.id.update_crypto_broker_button);

        actualizable = true;

        mBrokerName    = (EditText) layout.findViewById(R.id.crypto_broker_name);
        mBrokerImage   = (ImageView) layout.findViewById(R.id.crypto_broker_image);
        sw             = (ImageView) layout.findViewById(R.id.sw);
        camara         = (ImageView) layout.findViewById(R.id.camara);
        galeria        = (ImageView) layout.findViewById(R.id.galeria);

        final CryptoBrokerIdentityInformation identityInfo = (CryptoBrokerIdentityInformation) appSession.getData(IDENTITY_INFO);


        botonU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizable = false;
                editIdentityInfoInBackDevice("onClick");
            }
        });

        if (identityInfo != null) {

            cryptoBrokerPublicKey = identityInfo.getPublicKey();
            mBrokerName.setText(identityInfo.getAlias());
            mBrokerName.selectAll();
            mBrokerName.requestFocus();
            mBrokerName.performClick();
            wantPublishIdentity = identityInfo.isPublished();

            profileImage = identityInfo.getProfileImage();

            if(profileImage.length == 0){
                mBrokerImage.setImageResource(R.drawable.pic_space);
            }else{
                ByteArrayInputStream bytes = new ByteArrayInputStream(profileImage);
                BitmapDrawable bmd = new BitmapDrawable(bytes);
                mBrokerImage.setImageBitmap(bmd.getBitmap());
            }

        }

        mBrokerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (actualizable) {
                    actualizable = false;
                    //editIdentityInfoInBackDevice("onFocus");
                }
            }
        });

        if(wantPublishIdentity){
            sw.setImageResource(R.drawable.visible);
        }else{
            sw.setImageResource(R.drawable.notvisible);
        }

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageFromGallery();
            }
        });

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wantPublishIdentity) {
                    sw.setImageResource(R.drawable.notvisible);
                    wantPublishIdentity = false;
                } else {
                    sw.setImageResource(R.drawable.visible);
                    wantPublishIdentity = true;
                }
            }
        });

        ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ImageView pictureView = mBrokerImage;
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    cryptoBrokerBitmap = (Bitmap) extras.get("data");
                    break;
                case REQUEST_LOAD_IMAGE:
                    Uri selectedImage = data.getData();
                    try {
                        if (isAttached) {
                            ContentResolver contentResolver = getActivity().getContentResolver();
                            cryptoBrokerBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error loading image.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            if (pictureView != null && cryptoBrokerBitmap != null) {
                pictureView.setImageBitmap(cryptoBrokerBitmap);
            }
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void editIdentityInfoInBackDevice(String d) {
        String brokerNameText = mBrokerName.getText().toString();
        ExposureLevel ex;
        byte[] imgInBytes;
        if (wantPublishIdentity) {
            ex = ExposureLevel.PUBLISH;
        }else{
            ex = ExposureLevel.HIDE;
        }
        if (cryptoBrokerBitmap != null) {
            imgInBytes = ImagesUtils.toByteArray(cryptoBrokerBitmap);
        }else{
            imgInBytes = profileImage;
        }
        if(brokerNameText.trim().equals("")) {
            Toast.makeText(getActivity(), "Please enter a name or alias", Toast.LENGTH_LONG).show();
        }else{
            if(imgInBytes == null){
                Toast.makeText(getActivity(), "You must enter an image", Toast.LENGTH_LONG).show();
            }else{
                if(cryptoBrokerPublicKey != null) {
                    if(d.equalsIgnoreCase("onClick")){
                        changeActivity(Activities.CBP_SUB_APP_CRYPTO_BROKER_IDENTITY, appSession.getAppPublicKey());
                    }else{

                    }
                    Toast.makeText(getActivity(), "Crypto Broker Identity Updated.", Toast.LENGTH_LONG).show();
                    CryptoBrokerIdentityInformation identity = new CryptoBrokerIdentityInformationImpl(brokerNameText, cryptoBrokerPublicKey, imgInBytes, ex);
                    EditIdentityWorker EditIdentityWorker = new EditIdentityWorker(getActivity(), appSession, identity, this);
                    executor = EditIdentityWorker.execute();
                }
            }
        }
    }

    private void dispatchTakePictureIntent() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void loadImageFromGallery() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent loadImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(loadImageIntent, REQUEST_LOAD_IMAGE);
    }

    @Override
    public void onPostExecute(Object... result) {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (result.length > 0) {
            int resultCode = (int) result[0];
            switch (resultCode) {
                case SUCCESS:
                    //Toast.makeText(getActivity(), "Crypto Broker Identity Updated.", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @Override
    public void onErrorOccurred(Exception ex) {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        Toast.makeText(getActivity().getApplicationContext(), "Error trying to edit the identity.", Toast.LENGTH_SHORT).show();
        errorManager.reportUnexpectedSubAppException(SubApps.CBP_CRYPTO_BROKER_IDENTITY, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, ex);
    }
}