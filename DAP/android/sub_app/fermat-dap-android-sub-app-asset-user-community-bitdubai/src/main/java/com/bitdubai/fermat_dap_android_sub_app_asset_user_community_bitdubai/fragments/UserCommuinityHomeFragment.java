package com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.ui.Views.PresentationDialog;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatListItemListeners;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantPersistSettingsException;
import com.bitdubai.fermat_api.layer.all_definition.settings.structure.SettingsManager;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.exceptions.CantGetActiveLoginIdentityException;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.R;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.adapters.UserCommunityAdapter;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.interfaces.AdapterChangeListener;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.models.Actor;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.popup.ConnectDialog;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.sessions.AssetUserCommunitySubAppSession;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.sessions.SessionConstantsAssetUserCommunity;
import com.bitdubai.fermat_dap_api.layer.all_definition.DAPConstants;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityAssetUserException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.AssetUserActorRecord;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_module.wallet_asset_user.AssetUserSettings;
import com.bitdubai.fermat_dap_api.layer.dap_sub_app_module.asset_user_community.interfaces.AssetUserCommunitySubAppModuleManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * UserCommuinityHomeFragment
 */
public class UserCommuinityHomeFragment extends AbstractFermatFragment
        implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener,
        FermatListItemListeners<Actor> {

    public static final String USER_SELECTED = "user";
    private static AssetUserCommunitySubAppModuleManager manager;
    private int userNotificationsCount = 0;

    ErrorManager errorManager;

    // recycler
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private UserCommunityAdapter adapter;
    private View rootView;
    private LinearLayout emptyView;

    private List<Actor> actors;
    private Actor actor;
    private int MAX = 1;
    private int offset = 0;
    private Menu menu;

    SettingsManager<AssetUserSettings> settingsManager;

    /**
     * Flags
     */
    private boolean isRefreshing = false;

    public static UserCommuinityHomeFragment newInstance() {
        return new UserCommuinityHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            manager = ((AssetUserCommunitySubAppSession) appSession).getModuleManager();
            actor = (Actor) appSession.getData(USER_SELECTED);

            errorManager = appSession.getErrorManager();
            settingsManager = appSession.getModuleManager().getSettingsManager();

            userNotificationsCount = manager.getWaitingYourConnectionActorAssetUserCount();
            new FetchCountTask().execute();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.home_fragment, container, false);
//        initViews(rootView);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.gridView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserCommunityAdapter(getActivity());
        adapter.setAdapterChangeListener(new AdapterChangeListener<Actor>() {
            @Override
            public void onDataSetChanged(List<Actor> dataSet) {
                actors = dataSet;


                boolean someSelected = false;
                for (Actor actor : actors) {
                    if (actor.selected) {
                        someSelected = true;
                        break;
                    }
                }

                if (someSelected) {
                    menu.getItem(2).setVisible(true);
                }
                else
                {
                    menu.getItem(2).setVisible(false);
                }

            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setFermatListEventListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLUE);

        rootView.setBackgroundColor(Color.parseColor("#000b12"));
        emptyView = (LinearLayout) rootView.findViewById(R.id.empty_view);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();

        //Initialize settings
        settingsManager = appSession.getModuleManager().getSettingsManager();
        AssetUserSettings settings = null;
        try {
            settings = settingsManager.loadAndGetSettings(appSession.getAppPublicKey());
        } catch (Exception e) {
            settings = null;
        }
        if (settings == null) {
            settings = new AssetUserSettings();
            settings.setIsContactsHelpEnabled(true);
            settings.setIsPresentationHelpEnabled(true);

            try {
                settingsManager.persistSettings(appSession.getAppPublicKey(), settings);
            } catch (CantPersistSettingsException e) {
                e.printStackTrace();
            }
        }

//        final AssetUserSettings assetUserSettingsTemp = settings;
//
//
//        Handler handlerTimer = new Handler();
//        handlerTimer.postDelayed(new Runnable() {
//            public void run() {
//                if (assetUserSettingsTemp.isPresentationHelpEnabled()) {
//                    setUpPresentation(false);
//                }
//            }
//        }, 500);

        return rootView;
    }

    private void setUpPresentation(boolean checkButton) {
//        try {
        PresentationDialog presentationDialog = new PresentationDialog.Builder(getActivity(), appSession)
                .setBannerRes(R.drawable.banner_asset_user_community)
                .setIconRes(R.drawable.asset_user_comunity)
                .setVIewColor(R.color.dap_community_user_view_color)
                .setTitleTextColor(R.color.dap_community_user_view_color)
                .setSubTitle(R.string.dap_user_community_welcome_subTitle)
                .setBody(R.string.dap_user_community_welcome_body)
                .setTemplateType(PresentationDialog.TemplateType.TYPE_PRESENTATION_WITHOUT_IDENTITIES)
                .setIsCheckEnabled(checkButton)
                .build();

//            presentationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    Object o = appSession.getData(SessionConstantsAssetIssuer.PRESENTATION_IDENTITY_CREATED);
//                    if (o != null) {
//                        if ((Boolean) (o)) {
//                            //invalidate();
//                            appSession.removeData(SessionConstantsAssetIssuer.PRESENTATION_IDENTITY_CREATED);
//                        }
//                    }
//                    try {
//                        IdentityAssetIssuer identityAssetIssuer = moduleManager.getActiveAssetIssuerIdentity();
//                        if (identityAssetIssuer == null) {
//                            getActivity().onBackPressed();
//                        } else {
//                            invalidate();
//                        }
//                    } catch (CantGetIdentityAssetIssuerException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        presentationDialog.show();
//        } catch (CantGetIdentityAssetIssuerException e) {
//            e.printStackTrace();
//        }
    }

//    protected void initViews(View layout) {
//
//        // fab action button create
//        ActionButton create = (ActionButton) layout.findViewById(R.id.create);
//        create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////            if (item.getItemId() == R.id.action_connect) {
//                final ProgressDialog dialog = new ProgressDialog(getActivity());
//                dialog.setMessage("Connecting please wait...");
//                dialog.setCancelable(false);
//                dialog.show();
//                FermatWorker worker = new FermatWorker() {
//                    @Override
//                    protected Object doInBackground() throws Exception {
//                        List<ActorAssetUser> toConnect = new ArrayList<>();
//                        for (Actor actor : actors) {
//                            if (actor.selected)
//                                toConnect.add(actor);
//                        }
//                        //// TODO: 28/10/15 get Actor asset User
//                        manager.connectToActorAssetUser(null, toConnect);
//                        return true;
//                    }
//                };
//                worker.setContext(getActivity());
//                worker.setCallBack(new FermatWorkerCallBack() {
//                    @Override
//                    public void onPostExecute(Object... result) {
//                        dialog.dismiss();
//                        if (swipeRefreshLayout != null)
//                            swipeRefreshLayout.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    onRefresh();
//                                }
//                            });
//                    }
//
//                    @Override
//                    public void onErrorOccurred(Exception ex) {
//                        dialog.dismiss();
//                        Toast.makeText(getActivity(), String.format("We have detected an error. Make sure you have created an Asset Issuer or Asset User identities using the corresponding Identity application."), Toast.LENGTH_LONG).show();
//                        ex.printStackTrace();
//                    }
//                });
//                worker.execute();
////                return true;
//                /* create new asset factory project */
////                selectedAsset = null;
////                changeActivity(Activities.DAP_ASSET_EDITOR_ACTIVITY.getCode(), appSession.getAppPublicKey(), getAssetForEdit());
//            }
//        });
//        create.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_jump_from_down));
//        create.setVisibility(View.VISIBLE);
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
    }

//    private void configureToolbar() {
//        Toolbar toolbar = getPaintActivtyFeactures().getToolbar();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            toolbar.setBackground(getResources().getDrawable(R.drawable.dap_action_bar_gradient_colors, null));
//        else
//            toolbar.setBackground(getResources().getDrawable(R.drawable.dap_action_bar_gradient_colors));
//
//        toolbar.setTitleTextColor(Color.WHITE);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
//        inflater.inflate(R.menu.dap_community_user_home_menu, menu);
        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_CONNECT, 0, "Connect").setIcon(R.drawable.ic_sub_menu_connect)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(1, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_SELECT_ALL, 0, "Select All")//.setIcon(R.drawable.dap_community_user_help_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(2, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_DESELECT_ALL, 0, "Deselect All")//.setIcon(R.drawable.dap_community_user_help_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(3, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_PRESENTATION, 0, "Help").setIcon(R.drawable.dap_community_user_help_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.getItem(2).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_SELECT_ALL){

            for (Actor actorIssuer : actors)
            {
                actorIssuer.selected = true;
            }
            adapter.changeDataSet(actors);
            menu.getItem(2).setVisible(true);

        }

        if(id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_DESELECT_ALL){

            for (Actor actorIssuer : actors)
            {
                actorIssuer.selected = false;
            }
            adapter.changeDataSet(actors);
            menu.getItem(2).setVisible(false);
        }



        if (id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_CONNECT) {
            List<ActorAssetUser> actorsSelected = new ArrayList<>();
            for (Actor actor : actors) {
                if (actor.selected)
                    actorsSelected.add(actor);
            }
            if(actorsSelected.size() > 0) {


                ConnectDialog connectDialog;

                connectDialog = new ConnectDialog(getActivity(), (AssetUserCommunitySubAppSession) appSession, null)
                {
                    @Override
                    public void onClick(View v) {
                        int i = v.getId();
                        if (i == R.id.positive_button) {

                            final ProgressDialog dialog = new ProgressDialog(getActivity());
                            dialog.setMessage("Connecting please wait...");
                            dialog.setCancelable(false);
                            dialog.show();
                            FermatWorker worker = new FermatWorker() {
                                @Override
                                protected Object doInBackground() throws Exception {
                                    List<ActorAssetUser> toConnect = new ArrayList<>();
                                    for (Actor actor : actors) {
                                        if (actor.selected)
                                            toConnect.add(actor);
                                    }
                                    // TODO: 28/10/15 get Actor asset Redeem Point
                                    manager.askActorAssetUserForConnection(toConnect);

                                    Intent broadcast = new Intent(SessionConstantsAssetUserCommunity.LOCAL_BROADCAST_CHANNEL);
                                    broadcast.putExtra(SessionConstantsAssetUserCommunity.BROADCAST_CONNECTED_UPDATE, true);
                                    sendLocalBroadcast(broadcast);

//                                    manager.connectToActorAssetUser(null, toConnect);
                                    return true;
                                }
                            };
                            worker.setContext(getActivity());
                            worker.setCallBack(new FermatWorkerCallBack() {
                                @Override
                                public void onPostExecute(Object... result) {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "Connection request sent", Toast.LENGTH_SHORT).show();
                                    if (swipeRefreshLayout != null)
                                        swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onRefresh();
                                            }
                                        });
                                }

                                @Override
                                public void onErrorOccurred(Exception ex) {
                                    dialog.dismiss();
//                                Toast.makeText(getActivity(), String.format("An exception has been thrown: %s", ex.getMessage()), Toast.LENGTH_LONG).show();
                                    Toast.makeText(getActivity(), "Asset Issuer or Asset User Identities must be created before using this app.", Toast.LENGTH_LONG).show();
//                                ex.printStackTrace();
                                }
                            });
                            worker.execute();


                            dismiss();
                        } else if (i == R.id.negative_button) {
                            dismiss();
                        }
                    }
                };
                connectDialog.setTitle("Connection Request");
                connectDialog.setDescription("Do you want to send to ");
                connectDialog.setUsername((actorsSelected.size() > 1) ? "" + actorsSelected.size() +
                        " Users" : actorsSelected.get(0).getName());
                connectDialog.setSecondDescription("a connection request");
                connectDialog.show();
                return true;
            }else {
                Toast.makeText(getActivity(), "No User selected to connect.", Toast.LENGTH_LONG).show();
                return false;
            }


        /*int id = item.getItemId();

//        if (item.getItemId() == R.id.action_connect) {
        if (id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_CONNECT) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Connecting please wait...");
            dialog.setCancelable(false);
            dialog.show();
            FermatWorker worker = new FermatWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    List<ActorAssetUser> toConnect = new ArrayList<>();
                    for (Actor actor : actors) {
                        if (actor.selected)
                            toConnect.add(actor);
                    }
                    //// TODO: 28/10/15 get Actor asset User
                    manager.connectToActorAssetUser(null, toConnect);
                    return true;
                }
            };
            worker.setContext(getActivity());
            worker.setCallBack(new FermatWorkerCallBack() {
                @Override
                public void onPostExecute(Object... result) {
                    dialog.dismiss();
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                onRefresh();
                            }
                        });
                }

                @Override
                public void onErrorOccurred(Exception ex) {
                    dialog.dismiss();
//                    Toast.makeText(getActivity(), String.format("An exception has been thrown: %s", ex.getMessage()), Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), " Asset Issuer or Asset User Identities must be created before using this app.", Toast.LENGTH_LONG).show();
//                    ex.printStackTrace();
                }
            });
            worker.execute();
            return true;*/
        }

        try {
            if (id == SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_PRESENTATION) {
                setUpPresentation(settingsManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
                return true;
            }
        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getActivity(), "Asset User system error",
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationsBadge(int count) {
        userNotificationsCount = count;
        getActivity().invalidateOptionsMenu();
    }

    public void showEmpty(boolean show, View emptyView) {
        Animation anim = AnimationUtils.loadAnimation(getActivity(),
                show ? android.R.anim.fade_in : android.R.anim.fade_out);
        if (show &&
                (emptyView.getVisibility() == View.GONE || emptyView.getVisibility() == View.INVISIBLE)) {
            emptyView.setAnimation(anim);
            emptyView.setVisibility(View.VISIBLE);
            if (adapter != null)
                adapter.changeDataSet(null);
        } else if (!show && emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setAnimation(anim);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void setUpScreen(LayoutInflater layoutInflater) throws CantGetIdentityAssetUserException {

    }

    /*
Sample AsyncTask to fetch the notifications count
*/
    class FetchCountTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // example count. This is where you'd
            // query your data store for the actual count.
            return userNotificationsCount;
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(true);
            FermatWorker worker = new FermatWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    return getMoreData();
                }
            };
            worker.setContext(getActivity());
            worker.setCallBack(new FermatWorkerCallBack() {
                @SuppressWarnings("unchecked")
                @Override
                public void onPostExecute(Object... result) {
                    isRefreshing = false;
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (result != null &&
                            result.length > 0) {
                        if (getActivity() != null && adapter != null) {
                            actors = (ArrayList<Actor>) result[0];
                            adapter.changeDataSet(actors);
                            if (actors.isEmpty()) {
                                showEmpty(true, emptyView);
                            } else {
                                showEmpty(false, emptyView);
                            }
                        }
                    } else
                        showEmpty(true, emptyView);
                }

                @Override
                public void onErrorOccurred(Exception ex) {
                    isRefreshing = false;
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            });
            worker.execute();
        }
    }

    private synchronized List<Actor> getMoreData() throws Exception {
        List<Actor> dataSet = new ArrayList<>();
        List<AssetUserActorRecord> result = null;
        if (manager == null)
            throw new NullPointerException("AssetUserCommunitySubAppModuleManager is null");
        result = manager.getAllActorAssetUserRegistered();
        if (result != null && result.size() > 0) {
            for (AssetUserActorRecord record : result) {
                dataSet.add((new Actor(record)));
            }
        }
        return dataSet;
    }

    @Override
    public void onItemClickListener(Actor data, int position) {
        appSession.setData(USER_SELECTED, data);
        changeActivity(Activities.DAP_SUB_APP_ASSET_USER_COMMUNITY_CONNECTION_OTHER_PROFILE.getCode(), appSession.getAppPublicKey());
    }

    @Override
    public void onUpdateViewOnUIThread(String code) {
        switch (code) {
            case DAPConstants.DAP_UPDATE_VIEW_ANDROID:
                onRefresh();
                break;
            default:
                super.onUpdateViewOnUIThread(code);
        }
    }

    @Override
    public void onLongItemClickListener(Actor data, int position) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
