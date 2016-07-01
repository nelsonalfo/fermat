package com.bitdubai.sub_app.chat_community.common.popups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatButton;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.dialogs.FermatDialog;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ActorConnectionRequestNotFoundException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.CantAcceptChatRequestException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.exceptions.ChatActorConnectionDenialFailedException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunityInformation;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunitySelectableIdentity;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunitySubAppModuleManager;
import com.bitdubai.fermat_pip_api.layer.network_service.subapp_resources.SubAppResourcesProviderManager;
import com.bitdubai.sub_app.chat_community.R;
import com.bitdubai.sub_app.chat_community.session.SessionConstants;

/**
 * SearchAliasDialog
 *
 * @author Jose Cardozo josejcb (josejcb89@gmail.com) on 28/06/16.
 * @version 1.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class SearchAliasDialog
        extends FermatDialog<ReferenceAppFermatSession<ChatActorCommunitySubAppModuleManager>, SubAppResourcesProviderManager>
        implements View.OnClickListener {

    /**
     * UI components
     */
    private final ChatActorCommunityInformation chatUserInformation;
    private final ChatActorCommunitySelectableIdentity identity            ;
    private AdapterCallbackAlias mAdapterCallback;
    private FermatTextView title      ;
    private FermatTextView description;
    private FermatTextView userName   ;
    private FermatButton   positiveBtn;
    private FermatButton   negativeBtn;
    private String   alias;

    public SearchAliasDialog(final Context activity              ,
                             final ReferenceAppFermatSession<ChatActorCommunitySubAppModuleManager> chatUserSubAppSession,
                             final SubAppResourcesProviderManager subAppResources       ,
                             final ChatActorCommunityInformation  chatUserInformation  ,
                             final ChatActorCommunitySelectableIdentity identity   ,
                             final String alias   ,
                             AdapterCallbackAlias mAdapterCallback           ) {

        super(activity, chatUserSubAppSession, subAppResources);

        this.chatUserInformation = chatUserInformation;
        this.identity            = identity;
        this.mAdapterCallback            = mAdapterCallback;
        this.alias            = alias;
    }

    public static interface AdapterCallbackAlias {
        void onMethodCallbackAlias(String alias);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title       = (FermatTextView) findViewById(R.id.title          );
        description = (FermatTextView) findViewById(R.id.description    );
        userName    = (FermatTextView) findViewById(R.id.user_name      );
        positiveBtn = (FermatButton)   findViewById(R.id.positive_button);
        negativeBtn = (FermatButton)   findViewById(R.id.negative_button);

        positiveBtn.setOnClickListener(this);
        negativeBtn.setOnClickListener(this);

        title.setText("Search a Peer");
        description.setText("Do you want to search an specific peer in the Community?");
        userName.setText("");

    }

    @Override
    protected int setLayoutId() {
        return R.layout.cht_comm_dialog_builder;
    }

    @Override
    protected int setWindowFeature() {
        return Window.FEATURE_NO_TITLE;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();

        if (i == R.id.positive_button) {

            try {
                mAdapterCallback.onMethodCallbackAlias(alias);
                dismiss();
            } catch (Exception e) {
                super.getErrorManager().reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.UNSTABLE, e);
                //super.toastDefaultError();
            }
            dismiss();

        } else if (i == R.id.negative_button) {
            dismiss();
        }
    }
}
