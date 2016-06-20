package com.bitdubai.sub_app.crypto_customer_identity.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_customer_identity.interfaces.CryptoCustomerIdentityModuleManager;
import com.bitdubai.sub_app.crypto_customer_identity.R;
import com.edmodo.cropper.CropImageView;

/**
 * Developed by abicelis on 15/06/16.
 */
public class CryptoCustomerImageCropperFragment extends AbstractFermatFragment<ReferenceAppFermatSession<CryptoCustomerIdentityModuleManager>, ResourceProviderManager> implements View.OnClickListener {


    //Constants
    static final String ORIGINAL_IMAGE = "ORIGINAL_IMAGE";
    static final String CROPPED_IMAGE = "CROPPED_IMAGE";
    static final String BACK_ACTIVITY = "BACK_ACTIVITY";

    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 400;
    private static final int IMAGE_COMPRESSION_PERCENTAGE = 30;

    //UI
    CropImageView cropImageView;


    //DATA
    Enum<Activities> backActivity;
    Bitmap originalImage;
    Bitmap croppedImage;

    public CryptoCustomerImageCropperFragment() {}
    public static CryptoCustomerImageCropperFragment newInstance() {return new CryptoCustomerImageCropperFragment();}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Capture data from session, then clean it.
        originalImage = (Bitmap) appSession.getData(ORIGINAL_IMAGE);
        backActivity = (Enum<Activities>) appSession.getData(BACK_ACTIVITY);

        appSession.removeData(ORIGINAL_IMAGE);
        appSession.removeData(BACK_ACTIVITY);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.cci_fragment_crop_image, container, false);

        cropImageView = (CropImageView) layout.findViewById(R.id.cbi_crop_image_view);
        cropImageView.setImageBitmap(originalImage);
        cropImageView.setGuidelines(2);

        Button cropButton = (Button) layout.findViewById(R.id.cbi_crop_button);
        Button rotateButton = (Button) layout.findViewById(R.id.cbi_rotate_button);
        Button cancelButton = (Button) layout.findViewById(R.id.cbi_cancel_button);

        cropButton.setOnClickListener(this);
        rotateButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        return layout;
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.cbi_crop_button) {

            //Crop image
            croppedImage = cropImageView.getCroppedImage();

            if(croppedImage.getHeight() >= 80) {


                //Scale it to default size (IMAGE_WIDTH x IMAGE_HEIGHT)
                croppedImage = Bitmap.createScaledBitmap(croppedImage, IMAGE_WIDTH, IMAGE_HEIGHT, true);
                byte[] croppedImageByteArray2 = ImagesUtils.toByteArray(croppedImage);

                //Compress it
                byte[] croppedImageByteArray = ImagesUtils.toCompressedByteArray(croppedImage, IMAGE_COMPRESSION_PERCENTAGE);

                //Save it to session
                appSession.setData(CROPPED_IMAGE, croppedImageByteArray);

                goBackToCallerActivity();

            }
            else
                Toast.makeText(getActivity(), "Cropped image is too small", Toast.LENGTH_SHORT).show();

        }

        if (i == R.id.cbi_rotate_button)
            cropImageView.rotateImage(90);

        if( i == R.id.cbi_cancel_button)
            goBackToCallerActivity();


    }

    private void goBackToCallerActivity()
    {
        if(backActivity == Activities.CBP_SUB_APP_CRYPTO_CUSTOMER_IDENTITY_CREATE_IDENTITY)
            changeActivity(Activities.CBP_SUB_APP_CRYPTO_CUSTOMER_IDENTITY_CREATE_IDENTITY, appSession.getAppPublicKey());
        else if(backActivity == Activities.CBP_SUB_APP_CRYPTO_CUSTOMER_IDENTITY_EDIT_IDENTITY)
            changeActivity(Activities.CBP_SUB_APP_CRYPTO_CUSTOMER_IDENTITY_EDIT_IDENTITY, appSession.getAppPublicKey());
        else
            Toast.makeText(getActivity(), "Error! Wrong back activity!", Toast.LENGTH_SHORT).show();

    }
}