package org.fermat.fermat_dap_android_sub_app_asset_user_community.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.Log;
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
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.ui.Views.PresentationDialog;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatListItemListeners;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantPersistSettingsException;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_dap_android_sub_app_asset_user_community_bitdubai.R;

import org.fermat.fermat_dap_android_sub_app_asset_user_community.adapters.UserCommunityAdapter;
import org.fermat.fermat_dap_android_sub_app_asset_user_community.interfaces.AdapterChangeListener;
import org.fermat.fermat_dap_android_sub_app_asset_user_community.models.Actor;
import org.fermat.fermat_dap_android_sub_app_asset_user_community.popup.CancelDialog;
import org.fermat.fermat_dap_android_sub_app_asset_user_community.popup.ConnectDialog;
import org.fermat.fermat_dap_android_sub_app_asset_user_community.popup.DisconnectDialog;
import org.fermat.fermat_dap_android_sub_app_asset_user_community.sessions.SessionConstantsAssetUserCommunity;
import org.fermat.fermat_dap_api.layer.all_definition.DAPConstants;
import org.fermat.fermat_dap_api.layer.all_definition.enums.DAPConnectionState;
import org.fermat.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityAssetUserException;
import org.fermat.fermat_dap_api.layer.dap_actor.asset_user.AssetUserActorRecord;
import org.fermat.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import org.fermat.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import org.fermat.fermat_dap_api.layer.dap_module.wallet_asset_user.AssetUserSettings;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.asset_user_community.interfaces.AssetUserCommunitySubAppModuleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.widget.Toast.makeText;

/**
 * UserCommuinityHomeFragment
 */
public class UserCommuinityHomeFragment extends AbstractFermatFragment<ReferenceAppFermatSession<AssetUserCommunitySubAppModuleManager>, ResourceProviderManager>
        implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener,
        FermatListItemListeners<Actor> {

    protected final String TAG = "UserCommunityFragment";

    public static final String USER_SELECTED = "user";
    private static final String SEARCH = "user_community_search";
    private static AssetUserCommunitySubAppModuleManager moduleManager;
    AssetUserSettings settings = null;
    private int userNotificationsCount = 0;
    static ErrorManager errorManager;

    // recycler
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private UserCommunityAdapter adapter;
    private View rootView;
    private LinearLayout emptyView;

    private List<Actor> actors;
    private List<Actor> actorsConnecting;
    private List<ActorAssetUser> actorsToConnect;
    private Actor actor;
    private Menu menu;
    private android.support.v7.widget.Toolbar toolbar;

    private MenuItem menuItemConnect;
    private MenuItem menuItemDisconnect;
    private MenuItem menuItemSelect;
    private MenuItem menuItemUnselect;
    private MenuItem menuItemCancel;
    private int menuItemSize;
    private SearchView searchView;
    private ExecutorService _executor;

    /**
     * Flags
     */
    private boolean isRefreshing = false;
    private static final int MAX = 10;
    private int offset = 0;

    public static UserCommuinityHomeFragment newInstance() {
        return new UserCommuinityHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _executor = Executors.newFixedThreadPool(2);

            setHasOptionsMenu(true);

            actor = (Actor) appSession.getData(USER_SELECTED);

            moduleManager = appSession.getModuleManager();
            errorManager = appSession.getErrorManager();

            try {
                settings = appSession.getModuleManager().loadAndGetSettings(appSession.getAppPublicKey());
            } catch (Exception e) {
                settings = null;
            }

            if (appSession.getAppPublicKey() != null) //the identity not exist yet
            {
                if (settings == null) {
                    settings = new AssetUserSettings();
                    settings.setIsPresentationHelpEnabled(true);
                    appSession.getModuleManager().persistSettings(appSession.getAppPublicKey(), settings);
                }
            }

            userNotificationsCount = moduleManager.getWaitingYourConnectionActorAssetUserCount();
            new FetchCountTask().execute();
        } catch (Exception ex) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, ex);
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

//                actors = dataSet;
                for (int i = 0; i < actors.size(); i++) {
                    for (int j = 0; j < dataSet.size(); j++) {
                        if (dataSet.get(j).getActorPublicKey().equals(actors.get(i).getActorPublicKey())) {
                            actors.set(i, dataSet.get(j));
                        }
                    }
                }

                boolean someSelected = false;
                int selectedActors = 0;
                int cheackeableActors = 0;
                List<Actor> actorsSelected = new ArrayList<>();
                actorsConnecting = new ArrayList<>();
                actorsToConnect = new ArrayList<>();

                for (Actor actor : actors) {
                    if (actor.getCryptoAddress() == null) {
                        cheackeableActors++;
                    }

                    if (actor.selected) {

                        actorsSelected.add(actor);
                        if (actor.getDapConnectionState().equals(DAPConnectionState.CONNECTING)) {
                            actorsConnecting.add(actor);
                        }
                        if (!(actor.getDapConnectionState().equals(DAPConnectionState.CONNECTING))) {
                            actorsToConnect.add(actor);
                        }
                        someSelected = true;
                        selectedActors++;
                    }
                }

                try {
                    if (actorsConnecting.size() > 0) {
                        changeOptionMenuVisibility(menuItemCancel.getItemId(), true, true);
//                    menuItemCancel.setVisible(true);
                    } else {
                        changeOptionMenuVisibility(menuItemCancel.getItemId(), false, true);
//                    menuItemCancel.setVisible(false);
                    }

                    if (someSelected) {
                        if (actorsConnecting.size() == selectedActors) {
                            changeOptionMenuVisibility(menuItemConnect.getItemId(), false, true);
//                        menuItemConnect.setVisible(false);
                        } else if (actorsConnecting.size() == 0) {
                            changeOptionMenuVisibility(menuItemConnect.getItemId(), true, true);
//                        menuItemConnect.setVisible(true);
                        }
                        if (selectedActors > actorsConnecting.size()) {
                            changeOptionMenuVisibility(menuItemConnect.getItemId(), true, true);
//                        menuItemConnect.setVisible(true);
                        }
                        changeOptionMenuVisibility(menuItemUnselect.getItemId(), true, true);
//                    menuItemUnselect.setVisible(true);
                        if (selectedActors == cheackeableActors) {
                            changeOptionMenuVisibility(menuItemSelect.getItemId(), false, true);
//                        menuItemSelect.setVisible(false);
                        } else {
                            changeOptionMenuVisibility(menuItemSelect.getItemId(), true, true);
//                        menuItemSelect.setVisible(true);
                        }

//                  if(ableToDisconnect(actorsSelected)){
//                    changeOptionMenuVisibility(menuItemConnect.getItemId(), false);
//                    changeOptionMenuVisibility(menuItemDisconnect.getItemId(), true);
//                    menuItemConnect.setVisible(false);
//                    menuItemDisconnect.setVisible(true);
//                      /*TODO solucion temporal discutir*/
//                     /* if (cantSelected > 1){
//                          menuItemConnect.setVisible(false);
//                          menuItemDisconnect.setVisible(false);
//                      }*/
//                  }else if(!(ableToDisconnect(actorsSelected))&& selectedActors > 1 && !(ableToConnect(actorsSelected))){
//                    changeOptionMenuVisibility(menuItemConnect.getItemId(), false);
//                    changeOptionMenuVisibility(menuItemDisconnect.getItemId(), false);
//                      menuItemConnect.setVisible(false);
//                      menuItemDisconnect.setVisible(false);
//                  }

                    } else {
                        restartButtons();
//                        changeOptionMenuVisibility(menuItemUnselect.getItemId(), false, true);
//                        changeOptionMenuVisibility(menuItemSelect.getItemId(), true, true);
//                        changeOptionMenuVisibility(menuItemConnect.getItemId(), false, true);
//                        changeOptionMenuVisibility(menuItemDisconnect.getItemId(), false, true);
//                    menuItemUnselect.setVisible(false);
//                    menuItemSelect.setVisible(true);
//                    menuItemConnect.setVisible(true);
//                    menuItemDisconnect.setVisible(false);
                    }
                } catch (InvalidParameterException e) {
                    e.printStackTrace();
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
//        settingsManager = appSession.getModuleManager().getSettingsManager();
        try {
            settings = moduleManager.loadAndGetSettings(appSession.getAppPublicKey());
        } catch (Exception e) {
            settings = null;
        }
        if (settings == null) {
            settings = new AssetUserSettings();
            settings.setIsContactsHelpEnabled(true);
            settings.setNotificationEnabled(true);

            try {
                if (moduleManager != null) {
                    moduleManager.persistSettings(appSession.getAppPublicKey(), settings);
                    moduleManager.setAppPublicKey(appSession.getAppPublicKey());
                }
            } catch (CantPersistSettingsException e) {
                e.printStackTrace();
            }
        }

        final AssetUserSettings assetUserSettingsTemp = settings;

        Handler handlerTimer = new Handler();
        handlerTimer.postDelayed(new Runnable() {
            public void run() {
                if (assetUserSettingsTemp.isPresentationHelpEnabled()) {
                    setUpPresentation(false);
                }
            }
        }, 500);

        isRefreshing = true;
        _executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    getMoreData();
                    isRefreshing = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
////            if (item.getId() == R.id.action_connect) {
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
//                        moduleManager.connectToActorAssetUser(null, toConnect);
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
    public void onOptionMenuPrepared(Menu menu){
        super.onOptionMenuPrepared(menu);
//        inflater.inflate(R.menu.dap_community_user_home_menu, menu);
//        searchView = (SearchView) menu.findItem(R.id.action_community_user_search).getActionView();
        if (menuItemSize == 0 || menuItemSize == menu.size()) {
            menuItemSize = menu.size();

            searchView = (SearchView) menu.findItem(6).getActionView();
            searchView.setQueryHint(getResources().getString(R.string.action_community_user_search_hint));
//        toolbar = getToolbar();
//        toolbar.addView(searchView);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.equals(searchView.getQuery().toString())) {
                        adapter.changeDataSet(actors);
                        adapter.getFilter().filter(s);
                        appSession.setData(SEARCH, s);
                    }
                    return false;
                }
            });
            if (appSession.getData(SEARCH) != null) {
                String s = appSession.getData(SEARCH).toString();
                searchView.setQuery(s, true);
                if (s.length() > 0) searchView.setIconified(false);
            }

//        this.menu = menu;
//        inflater.inflate(R.menu.dap_community_user_home_menu, menu);
//        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_CONNECT, 1, "Connect").setIcon(R.drawable.ic_sub_menu_connect)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_DISCONNECT, 2, "Disconnect")//.setIcon(R.drawable.ic_sub_menu_connect)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_CANCEL_CONNECTING, 3, "Cancel Connecting")//.setIcon(R.drawable.ic_sub_menu_connect)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_SELECT_ALL, 4, "Select All")//.setIcon(R.drawable.dap_community_user_help_icon)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_UNSELECT_ALL, 5, "Unselect All")//.setIcon(R.drawable.dap_community_user_help_icon)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.add(0, SessionConstantsAssetUserCommunity.IC_ACTION_USER_COMMUNITY_HELP_PRESENTATION, 6, "Help").setIcon(R.drawable.dap_community_user_help_icon)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            menuItemSize = menu.size();
            menuItemConnect = menu.findItem(0);
            menuItemDisconnect = menu.findItem(1);
            menuItemCancel = menu.findItem(2);
            menuItemSelect = menu.findItem(3);
            menuItemUnselect = menu.findItem(4);
            restartButtons();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        try {
            List<ActorAssetUser> actorsSelected;
            switch (id) {
                case 0://IC_ACTION_USER_COMMUNITY_CONNECT
                    actorsSelected = new ArrayList<>();
                    for (Actor actor : actors) {
                        if (actor.selected)
                            actorsSelected.add(actor);
                    }
                    if (actorsSelected.size() > 0) {
                        ConnectDialog connectDialog;

                        connectDialog = new ConnectDialog(getActivity(), appSession, null) {
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
                                                if (actor.selected && !(actor.getDapConnectionState().equals(DAPConnectionState.CONNECTING))) {
                                                    toConnect.add(actor);
                                                }
                                            }
                                            // TODO: 28/10/15 get Actor asset Redeem Point
                                            moduleManager.askActorAssetUserForConnection(toConnect);

                                            Intent broadcast = new Intent(SessionConstantsAssetUserCommunity.LOCAL_BROADCAST_CHANNEL);
                                            broadcast.putExtra(SessionConstantsAssetUserCommunity.BROADCAST_CONNECTED_UPDATE, true);
                                            sendLocalBroadcast(broadcast);

                                            //moduleManager.connectToActorAssetUser(null, toConnect);
                                            return true;
                                        }
                                    };
                                    worker.setContext(getActivity());
                                    worker.setCallBack(new FermatWorkerCallBack() {
                                        @Override
                                        public void onPostExecute(Object... result) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), R.string.connection_request_send, Toast.LENGTH_SHORT).show();
                                            restartButtons();
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
                        connectDialog.setUsername((actorsToConnect.size() > 1) ? "" + actorsToConnect.size() +
                                " Users" : actorsToConnect.get(0).getName());
                        connectDialog.setSecondDescription("a connection request");
                        connectDialog.show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), "No User selected to connect.", Toast.LENGTH_LONG).show();
                        return false;
                    }
//                    break;
                case 1://IC_ACTION_USER_COMMUNITY_DISCONNECT
                    actorsSelected = new ArrayList<>();
                    for (Actor actor : actors) {
                        if (actor.selected)
                            actorsSelected.add(actor);
                    }
                    if (actorsSelected.size() > 0) {

                        DisconnectDialog disconnectDialog;

                        disconnectDialog = new DisconnectDialog(getActivity(), appSession, null) {
                            @Override
                            public void onClick(View v) {
                                int i = v.getId();
                                if (i == R.id.positive_button) {

                                    final ProgressDialog dialog = new ProgressDialog(getActivity());
                                    dialog.setMessage("Disconnecting please wait...");
                                    dialog.setCancelable(false);
                                    dialog.show();
                                    FermatWorker worker = new FermatWorker() {
                                        @Override
                                        protected Object doInBackground() throws Exception {
                                            List<ActorAssetUser> toDisconnect = new ArrayList<>();
                                            for (Actor actor : actors) {
                                                if (actor.selected)
                                                    toDisconnect.add(actor);
                                            }
                                    /*TODO implementar disconnect*/
                                            for (ActorAssetUser actor : toDisconnect) {
                                                moduleManager.disconnectToActorAssetUser(actor);
                                            }

                                    /*Intent broadcast = new Intent(SessionConstantsAssetUserCommunity.LOCAL_BROADCAST_CHANNEL);
                                    broadcast.putExtra(SessionConstantsAssetUserCommunity.BROADCAST_CONNECTED_UPDATE, true);
                                    sendLocalBroadcast(broadcast);*/

//                                    moduleManager.connectToActorAssetUser(null, toConnect);
                                            return true;
                                        }
                                    };
                                    worker.setContext(getActivity());
                                    worker.setCallBack(new FermatWorkerCallBack() {
                                        @Override
                                        public void onPostExecute(Object... result) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "Disconnection performed successfully", Toast.LENGTH_SHORT).show();
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
                                    /*TODO aun no se que error deberia ir aqui*/
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
                        disconnectDialog.setTitle("Disconnection request");
                        disconnectDialog.setDescription("Do you want to disconnect from ");
                        disconnectDialog.setUsername((actorsSelected.size() > 1) ? "" + actorsSelected.size() +
                                " Users" : actorsSelected.get(0).getName());
                        //connectDialog.setSecondDescription("a connection request");
                        disconnectDialog.show();
                        return true;
                    } else {
                        Toast.makeText(getActivity(), "No User selected to disconnect.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                case 2://IC_ACTION_USER_COMMUNITY_CANCEL_CONNECTING
                    CancelDialog cancelDialog;

                    cancelDialog = new CancelDialog(getActivity(), appSession, null) {
                        @Override
                        public void onClick(View v) {
                            int i = v.getId();
                            if (i == R.id.positive_button) {

                                final ProgressDialog dialog = new ProgressDialog(getActivity());
                                dialog.setMessage("Canceling, please wait...");
                                dialog.setCancelable(false);
                                dialog.show();
                                FermatWorker worker = new FermatWorker() {
                                    @Override
                                    protected Object doInBackground() throws Exception {

                                        for (ActorAssetUser actor : actorsConnecting) {
                                            //TODO revisar si esto es asi
                                            moduleManager.cancelActorAssetUser(actor.getActorPublicKey());
                                        }

                                    /*Intent broadcast = new Intent(SessionConstantsAssetUserCommunity.LOCAL_BROADCAST_CHANNEL);
                                    broadcast.putExtra(SessionConstantsAssetUserCommunity.BROADCAST_CONNECTED_UPDATE, true);
                                    sendLocalBroadcast(broadcast);*/
                                        return true;
                                    }
                                };
                                worker.setContext(getActivity());
                                worker.setCallBack(new FermatWorkerCallBack() {
                                    @Override
                                    public void onPostExecute(Object... result) {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Cancelation performed successfully", Toast.LENGTH_SHORT).show();
                                        restartButtons();
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
                                    /*TODO aun no se que error deberia ir aqui*/
//                                Toast.makeText(getActivity(), String.format("An exception has been thrown: %s", ex.getMessage()), Toast.LENGTH_LONG).show();
                                        Toast.makeText(getActivity(), "Can't cancel connection to selected users", Toast.LENGTH_LONG).show();
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
                    cancelDialog.setTitle("Cancel request");
                    cancelDialog.setDescription("Do you want to cancel connection with ");
                    cancelDialog.setUsername((actorsConnecting.size() > 1) ? "" + actorsConnecting.size() +
                            " Users" : actorsConnecting.get(0).getName());
                    //connectDialog.setSecondDescription("a connection request");
                    cancelDialog.show();
                    return true;
//                break;
                case 3://IC_ACTION_USER_COMMUNITY_HELP_SELECT_ALL
                    for (Actor actorIssuer : actors) {
                        if (actorIssuer.getCryptoAddress() == null) {
                            actorIssuer.selected = true;
                        }
                    }
                    adapter.changeDataSet(actors);
                    adapter.getAdapterChangeListener().onDataSetChanged(actors);
                    changeOptionMenuVisibility(menuItemConnect.getItemId(), true, true);
                    changeOptionMenuVisibility(menuItemSelect.getItemId(), false, true);
                    changeOptionMenuVisibility(menuItemUnselect.getItemId(), true, true);

//                    menuItemConnect.setVisible(true);
//                    menuItemSelect.setVisible(false);
//                    menuItemUnselect.setVisible(true);
                    break;
                case 4://IC_ACTION_USER_COMMUNITY_HELP_UNSELECT_ALL
                    for (Actor actorIssuer : actors) {
                        actorIssuer.selected = false;
                    }
                    adapter.changeDataSet(actors);
                    adapter.getAdapterChangeListener().onDataSetChanged(actors);
                    restartButtons();
                    break;
                case 5://case IC_ACTION_USER_COMMUNITY_HELP_PRESENTATION:
                    setUpPresentation(moduleManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
                    break;
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
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        } else {
            Log.e(TAG, "updateNotificationsBadge activity null, please check this, class" + getClass().getName() + " line: " + new Throwable().getStackTrace()[0].getLineNumber());
        }
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
                            adapter.getFilter().filter(searchView.getQuery().toString());
                            if (actors.isEmpty()) {
                                showEmpty(true, emptyView);
                            } else {
                                showEmpty(false, emptyView);
                            }
                        }
                    } else
                        showEmpty(true, emptyView);
                    restartButtons();
                }


                @Override
                public void onErrorOccurred(Exception ex) {
                    isRefreshing = false;
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (getActivity() != null)
                        showEmpty(true, emptyView);
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
//                    ex.printStackTrace();
                }
            });
            worker.execute();
        }
    }

    private synchronized List<Actor> getMoreData() throws Exception {
        List<Actor> dataSet = new ArrayList<>();
        List<AssetUserActorRecord> result = null;
        if (moduleManager == null)
            throw new NullPointerException("AssetUserCommunitySubAppModuleManager is null");

        try {
            result = moduleManager.getAllActorAssetUserRegistered(MAX, offset);
            if (result != null && result.size() > 0) {
                for (AssetUserActorRecord record : result) {
                    dataSet.add((new Actor(record)));
                }
            }
        } catch (CantGetAssetUserActorsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSet;
    }

    @Override
    public void onItemClickListener(Actor data, int position) {
        appSession.setData(USER_SELECTED, data);
        changeActivity(Activities.DAP_ASSET_USER_COMMUNITY_ACTIVITY_PROFILE.getCode(), appSession.getAppPublicKey());
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

    private void restartButtons() {
        try {
            changeOptionMenuVisibility(menuItemCancel.getItemId(), false, true);
            changeOptionMenuVisibility(menuItemSelect.getItemId(), true, true);
            changeOptionMenuVisibility(menuItemUnselect.getItemId(), false, true);
            changeOptionMenuVisibility(menuItemConnect.getItemId(), false, true);
            changeOptionMenuVisibility(menuItemDisconnect.getItemId(), false, true);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
//        menuItemCancel.setVisible(false);
//        menuItemSelect.setVisible(true);
//        menuItemUnselect.setVisible(false);
//        menuItemConnect.setVisible(false);
//        menuItemDisconnect.setVisible(false);
    }
}
