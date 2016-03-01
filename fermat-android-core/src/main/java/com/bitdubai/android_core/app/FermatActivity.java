package com.bitdubai.android_core.app;

import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.bitdubai.android_core.app.common.version_1.ApplicationConstants;
import com.bitdubai.android_core.app.common.version_1.adapters.ScreenPagerAdapter;
import com.bitdubai.android_core.app.common.version_1.adapters.TabsPagerAdapter;
import com.bitdubai.android_core.app.common.version_1.bottom_navigation.BottomNavigation;
import com.bitdubai.android_core.app.common.version_1.builders.FooterBuilder;
import com.bitdubai.android_core.app.common.version_1.builders.SideMenuBuilder;
import com.bitdubai.android_core.app.common.version_1.classes.BroadcastManager;
import com.bitdubai.android_core.app.common.version_1.classes.NetworkStateReceiver;
import com.bitdubai.android_core.app.common.version_1.connection_manager.FermatAppConnectionManager;
import com.bitdubai.android_core.app.common.version_1.provisory.DesktopManager;
import com.bitdubai.android_core.app.common.version_1.provisory.FermatDesktopManager;
import com.bitdubai.android_core.app.common.version_1.provisory.ProvisoryData;
import com.bitdubai.android_core.app.common.version_1.runtime_estructure_manager.RuntimeStructureManager;
import com.bitdubai.android_core.app.common.version_1.sessions.FermatSessionManager;
import com.bitdubai.android_core.app.common.version_1.top_settings.AppStatusDialog;
import com.bitdubai.android_core.app.common.version_1.top_settings.AppStatusListener;
import com.bitdubai.android_core.app.common.version_1.util.AndroidCoreUtils;
import com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils;
import com.bitdubai.android_core.app.common.version_1.util.ServiceCallback;
import com.bitdubai.fermat.R;
import com.bitdubai.fermat_android_api.engine.DesktopHolderClickCallback;
import com.bitdubai.fermat_android_api.engine.ElementsWithAnimation;
import com.bitdubai.fermat_android_api.engine.FermatApplicationSession;
import com.bitdubai.fermat_android_api.engine.FermatFragmentFactory;
import com.bitdubai.fermat_android_api.engine.FooterViewPainter;
import com.bitdubai.fermat_android_api.engine.HeaderViewPainter;
import com.bitdubai.fermat_android_api.engine.NavigationViewPainter;
import com.bitdubai.fermat_android_api.engine.NotificationPainter;
import com.bitdubai.fermat_android_api.engine.PaintActivityFeatures;
import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.ActivityType;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatActivityManager;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.WizardConfiguration;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatListItemListeners;
import com.bitdubai.fermat_android_api.ui.util.FermatAnimationsUtils;
import com.bitdubai.fermat_api.AndroidCoreManager;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.FermatStates;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantGetModuleManagerException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.ModuleManagerNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Engine;
import com.bitdubai.fermat_api.layer.all_definition.enums.FermatApps;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.Activity;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MainMenu;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.SideMenu;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.StatusBar;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.TabStrip;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.TitleBar;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.Wizard;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.FermatAppType;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatFooter;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatHeader;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatRuntime;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatStructure;
import com.bitdubai.fermat_api.layer.all_definition.runtime.FermatApp;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.CantGetSettingsException;
import com.bitdubai.fermat_api.layer.all_definition.settings.exceptions.SettingsNotFoundException;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.SubApp;
import com.bitdubai.fermat_api.layer.modules.exceptions.ActorIdentityNotSelectedException;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;
import com.bitdubai.fermat_api.layer.pip_engine.desktop_runtime.DesktopObject;
import com.bitdubai.fermat_api.layer.pip_engine.desktop_runtime.DesktopRuntimeManager;
import com.bitdubai.fermat_pip_api.layer.module.android_core.interfaces.AndroidCoreModule;
import com.bitdubai.fermat_pip_api.layer.module.android_core.interfaces.AndroidCoreSettings;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.sub_app.manager.fragment.DesktopSubAppFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getDesktopRuntimeManager;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getErrorManager;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getSubAppManager;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getSubAppResourcesProviderManager;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getSubAppRuntimeMiddleware;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getWalletManager;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getWalletResourcesProviderManager;
import static com.bitdubai.android_core.app.common.version_1.util.FermatSystemUtils.getWalletRuntimeManager;
import static java.lang.System.gc;

/**
 * Created by Matias Furszyfer
 */

public abstract class FermatActivity extends AppCompatActivity
        implements
        WizardConfiguration,
        PaintActivityFeatures,
        NavigationView.OnNavigationItemSelectedListener,
        NetworkStateReceiver.NetworkStateReceiverListener,
        FermatStates,
        FermatActivityManager,
        FermatListItemListeners<com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem>,
        ServiceCallback {


    private static final String TAG = "fermat-core";
    private MainMenu mainMenu;

    /**
     * Screen adapters
     */
    protected TabsPagerAdapter adapter;
    private ScreenPagerAdapter screenPagerAdapter;

    /**
     * Manager
     */
    BroadcastManager broadcastManager;
    /**
     * WizardTypes
     */
    private Map<String, Wizard> wizards;

    /**
     * Activity type
     */
    private ActivityType activityType;

    /**
     * Constans
     */
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    /**
     * Handlers
     */
    private final Handler mDrawerActionHandler = new Handler();
    private final Handler refreshHandler = new Handler();
    /**
     * Receivers
     */
    //private NetworkStateReceiver networkStateReceiver;

    /**
     * UI
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private Toolbar mToolbar;
    private RecyclerView navigation_recycler_view;
    private NavigationView navigationView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager pagertabs;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout mDrawerLayout;

    /**
     * This code will be in a manager in the new core
     */
    private List<ElementsWithAnimation> elementsWithAnimation = new ArrayList<>();
    private BottomNavigation bottomNavigation;

    private boolean hidden = true;
    /**
     * Listeners
     */
    private AppStatusListener appStatusListener;
    private RuntimeStructureManager runtimeStructureManager;

    /**
     * Service
     */
//    boolean mServiceConnected = false;


    /**
     * Called when the activity is first created
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            // The Activity is being created for the first time, so create and
            // add new fragments.
            super.onCreate(savedInstanceState);
        } else {

            super.onCreate(new Bundle());
            // Otherwise, the activity is coming back after being destroyed.
            // The FragmentManager will restore the old Fragments so we don't
            // need to create any new ones here.
        }
        broadcastManager = new BroadcastManager(this);
        AndroidCoreUtils.getInstance().setContextAndResume(broadcastManager);
        runtimeStructureManager = new RuntimeStructureManager(this);


//        try {
//            networkStateReceiver = new NetworkStateReceiver();
//            networkStateReceiver.addListener(this);
//            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
//        }catch (Exception e){
//
//        }

    }

    /**
     * Initialize the contents of the Activity's standard options menu
     *
     * @param menu
     * @return true if all is okey
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            menu.clear();
            //mainMenu = getActivityUsedType().getMainMenu();
            if (mainMenu != null) {
                for (com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem menuItem : mainMenu.getMenuItems()) {
                    MenuItem item = menu.add(menuItem.getLabel());

//                item.setOnMenuItemClickListener (new ActionMenuView.OnMenuItemClickListener(){
//                    @Override
//                    public boolean onMenuItemClick (MenuItem item){
//
//                        return true;
//                    }
//                });
                }
                //getMenuInflater().inflate(R.menu.wallet_store_activity_wallet_menu, menu);

            }
            return true;


        } catch (Exception e) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getApplicationContext(), "Oooops! recovering from system error",
                    LENGTH_LONG).show();
        }

        return super.onCreateOptionsMenu(menu);

    }
    /**
     * Dispatch onStop() to all fragments.  Ensure all loaders are stopped.
     */
    @Override
    protected void onStop() {
        try {
            super.onStop();
            try{
                //AndroidCoreUtils.getInstance().clear();
            }catch (Exception e){
                e.printStackTrace();
            }
//            if(networkStateReceiver!=null) {
//                unregisterReceiver(networkStateReceiver);
//                networkStateReceiver.removeListener(this);
//            }
       //     networkStateReceiver.removeListener(this);


            /**
             * Service
             */
//            if (mServiceConnected) {
//                unbindService(mServiceConnection);
//                mServiceConnected = false;
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item
     * @return true if button is clicked
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {

            int id = item.getItemId();

            /**
             *  Our future code goes here...
             */


        } catch (Exception e) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getApplicationContext(), "Oooops! recovering from system error",
                    LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that loads the UI
     */
    protected void loadBasicUI(Activity activity,AppConnections appConnections) {
        // rendering UI components
        try {

            ModuleManager moduleManager = null;
            if(appConnections!=null) {
                moduleManager = getModuleManager(appConnections.getPluginVersionReference());
                if(moduleManager!=null)appConnections.setActiveIdentity(moduleManager.getSelectedActorIdentity());
            }
            TabStrip tabs = activity.getTabStrip();
            TitleBar titleBar = activity.getTitleBar();
            MainMenu mainMenu = activity.getMainMenu();

            SideMenu sideMenu = activity.getSideMenu();

            setMainLayout(sideMenu, activity.getHeader());

            setMainMenu(mainMenu);

            paintTabs(tabs, activity);

            paintStatusBar(activity.getStatusBar());

            paintTitleBar(titleBar, activity);

            if(moduleManager!=null && sideMenu!=null) sideMenu.setNotifications(moduleManager.getMenuNotifications());
            paintSideMenu(activity, sideMenu, appConnections);

            if(appConnections!=null) {
                paintFooter(activity.getFooter(), appConnections.getFooterViewPainter());

                pantHeader(activity.getHeader(), appConnections.getHeaderViewPainter());
            }

            setScreen(activity);
            // rendering wizards components
            if (tabs != null && tabs.getWizards() != null)
                setWizards(tabs.getWizards());
            if (activity.getWizards() != null)
                setWizards(activity.getWizards());
        } catch (Exception e) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getApplicationContext(), "Oooops! recovering from system error",
                    LENGTH_LONG).show();
        }
    }

    private void pantHeader(FermatHeader header, HeaderViewPainter headerViewPainter) {
        if(header!=null && headerViewPainter!=null){
            if(header.hasExpandable()){
                headerViewPainter.addExpandableHeader(getToolbarHeader());
            }

        }
    }

    @Override
    public void setMenuSettings(View viewGroup, final View container_title){
        final LinearLayout mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        final View view = findViewById(R.id.reveal);
        final View txt_settings = view.findViewById(R.id.txt_settings);
        txt_settings.setVisibility(View.INVISIBLE);
        View.OnClickListener onClickListener = new View.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(View v) {
                                                                         int cx = (mRevealView.getLeft() + mRevealView.getRight());
                                                                        //                int cy = (mRevealView.getTop() + mRevealView.getBottom())/2;
                                                                         int cy = mRevealView.getTop();

                                                                         int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                                                                         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


                                                                             SupportAnimator animator =
                                                                                     ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                                                                             animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                                                             animator.setDuration(800);

                                                                             SupportAnimator animator_reverse = animator.reverse();

                                                                             if (hidden) {
                                                                                 mRevealView.setVisibility(View.VISIBLE);
                                                                                 view.bringToFront();
                                                                                 animator.start();
                                                                                 hidden = false;
                                                                             } else {
                                                                                 animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                                                                                     @Override
                                                                                     public void onAnimationStart() {

                                                                                     }

                                                                                     @Override
                                                                                     public void onAnimationEnd() {
                                                                                         mRevealView.setVisibility(View.INVISIBLE);
                                                                                         hidden = true;

                                                                                     }

                                                                                     @Override
                                                                                     public void onAnimationCancel() {

                                                                                     }

                                                                                     @Override
                                                                                     public void onAnimationRepeat() {

                                                                                     }
                                                                                 });
                                                                                 animator_reverse.start();

                                                                             }
                                                                         } else {
                                                                             if (hidden) {
                                                                                 android.animation.Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, 0, cy, 0, radius);
                                                                                 mRevealView.setVisibility(View.VISIBLE);
                                                                                 FrameLayout frameLayout = (FrameLayout) findViewById(R.id.container_main);
                                                                                 frameLayout.bringChildToFront(mRevealView);
                                                                                 FermatAnimationsUtils.showEmpty(getApplicationContext(),true,txt_settings);
                                                                                 anim.start();
                                                                                 hidden = false;

                                                                             } else {
                                                                                 android.animation.Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, 0, cy, radius, 0);
                                                                                 anim.addListener(new AnimatorListenerAdapter() {
                                                                                     @Override
                                                                                     public void onAnimationEnd(android.animation.Animator animation) {
                                                                                         super.onAnimationEnd(animation);
                                                                                         mRevealView.setVisibility(View.INVISIBLE);
                                                                                         container_title.setVisibility(View.VISIBLE);
                                                                                         FermatAnimationsUtils.showEmpty(getApplicationContext(), false, txt_settings);
                                                                                         hidden = true;
                                                                                     }
                                                                                 });
                                                                                 anim.start();

                                                                             }
                                                                         }
                                                                     }
                                                                 };
        view.findViewById(R.id.img_fermat_setting_1).setOnClickListener(onClickListener);
        view.findViewById(R.id.img_fermat_setting).setOnClickListener(onClickListener);



        ImageButton btn_fermat_apps_status = (ImageButton)mRevealView.findViewById(R.id.btn_fermat_apps_status);
        final AndroidCoreModule androidCoreModule = FermatSystemUtils.getAndroidCoreModule();

        if(appStatusListener==null){
            appStatusListener = new AppStatusListener(this,btn_fermat_apps_status);
        }

        try {
            AndroidCoreSettings androidCoreSettings = (AndroidCoreSettings) androidCoreModule.getSettingsManager().loadAndGetSettings(ApplicationConstants.SETTINGS_CORE);
            switch (androidCoreSettings.getAppsStatus()){
                case RELEASE:
                    btn_fermat_apps_status.setBackgroundResource(R.drawable.icon_relese);
                    break;
                case BETA:
                    btn_fermat_apps_status.setBackgroundResource(R.drawable.icons_beta);
                    break;
                case ALPHA:
                    btn_fermat_apps_status.setBackgroundResource(R.drawable.icons_alfa);
                    break;
                case DEV:
                    btn_fermat_apps_status.setBackgroundResource(R.drawable.icons_developer);
                    break;
                default:
                    btn_fermat_apps_status.setBackgroundResource(R.drawable.icon_relese);
                    break;
            }
        } catch (CantGetSettingsException | SettingsNotFoundException e) {
            btn_fermat_apps_status.setBackgroundResource(R.drawable.icon_relese);
           // e.printStackTrace();
        }
        btn_fermat_apps_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppStatusDialog(view.getContext(), androidCoreModule, appStatusListener).show();
            }
        });


        ImageButton btn_fermat_network = (ImageButton)mRevealView.findViewById(R.id.btn_fermat_network);
        btn_fermat_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(Activities.DESKTOP_SETTING_FERMAT_NETWORK.getCode(),ApplicationConstants.SETTINGS_FERMAT_NETWORK);
            }
        });




//        TopSettings topSettings = new TopSettings((ViewGroup) mRevealView.findViewById(R.id.horizontal_container));
//        topSettings.init();
    }



    private void paintFooter(FermatFooter footer,FooterViewPainter footerViewPainter) {
        try {
            SlidingDrawer slidingDrawer = (SlidingDrawer) findViewById(R.id.SlidingDrawer);
            FrameLayout slide_container = (FrameLayout) findViewById(R.id.slide_container);
            RelativeLayout footer_container = (RelativeLayout) findViewById(R.id.footer_container);
            if (footer != null && footerViewPainter != null) {
                slide_container.setVisibility(View.VISIBLE);
                footer_container.setVisibility(View.VISIBLE);
                slidingDrawer.setVisibility(View.VISIBLE);
                if (footer.getBackgroundColor() != null) {
                    footer_container.setBackgroundColor(Color.parseColor(footer.getBackgroundColor()));
                }
                FooterBuilder.Builder.build(getLayoutInflater(), slide_container, footer_container, footerViewPainter);
            } else {
                if (slide_container != null) slide_container.setVisibility(View.GONE);
                if (footer_container != null) footer_container.setVisibility(View.GONE);
                findViewById(R.id.SlidingDrawer).setVisibility(View.GONE);
            }
        }catch (Exception e){
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, e);
        }
    }

    private void paintSideMenu(Activity activity, SideMenu sideMenu,AppConnections appConnections) {
        try {
            if (sideMenu != null) {
                String backgroundColor = sideMenu.getBackgroudColor();
                if (backgroundColor != null) {
                    navigationView.setBackgroundColor(Color.parseColor(backgroundColor));
                }
                if(sideMenu.getNavigationIconColor()!=null)
                if(sideMenu.getNavigationIconColor().equals("#ffffff")){
                    mToolbar.setNavigationIcon(R.drawable.ic_actionbar_menu);
                }

                final NavigationViewPainter viewPainter = appConnections.getNavigationViewPainter();
                if(viewPainter!=null) {
                    /**
                     * Set header
                     */
                    FrameLayout frameLayout = SideMenuBuilder.setHeader(this, viewPainter,appConnections.getActiveIdentity());
                    assert frameLayout != null;
                    frameLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Object[] object = new Object[2];
                            if (viewPainter.hasClickListener())
                                connectWithOtherApp(Engine.BITCOIN_WALLET_CALL_INTRA_USER_IDENTITY, "public_key_ccp_intra_user_identity", object);
                        }
                    });
                    /**
                     * Set adapter
                     */
                    FermatAdapter mAdapter = viewPainter.addNavigationViewAdapter();
                    List<com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem> lstItems = getNavigationMenu();
                    SideMenuBuilder.setAdapter(
                            navigation_recycler_view,
                            mAdapter,
                            viewPainter.addItemDecoration(),
                            lstItems,
                            this,
                            activity.getActivityType()
                    );
                    /**
                     * Body
                     */
                    RelativeLayout navigation_view_footer = (RelativeLayout) findViewById(R.id.navigation_view_footer);
                    SideMenuBuilder.setBody(navigation_view_footer,sideMenu.hasFooter(),viewPainter,getLayoutInflater());
                    /**
                     * Background color
                     */
                    final RelativeLayout navigation_view_body_container = (RelativeLayout) findViewById(R.id.navigation_view_body_container);
                    SideMenuBuilder.setBackground(navigation_view_body_container, viewPainter, getResources());
            }

            } else {
                mDrawerLayout.setEnabled(false);
                //test
                //mDrawerToggle.onDrawerClosed(mDrawerLayout);
            }
        } catch (Exception e) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, e);
        }
    }

    private void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public Activity getActivityUsedType() {
        Activity activity = null;
        if (ActivityType.ACTIVITY_TYPE_SUB_APP == activityType) {
            SubApp subApp = getSubAppRuntimeMiddleware().getLastApp();
            activity = subApp.getLastActivity();
        } else if (ActivityType.ACTIVITY_TYPE_WALLET == activityType) {
            //activity = getWalletRuntimeManager().getLasActivity();
        } else if (ActivityType.ACTIVITY_TYPE_DESKTOP == activityType){
            activity = getDesktopRuntimeManager().getLastDesktopObject().getLastActivity();
        }
        return activity;
    }

    /**
     * @param titleBar
     */
    protected void paintTitleBar(TitleBar titleBar, Activity activity) {
        try {
            if (titleBar != null) {
                getSupportActionBar().setWindowTitle("");
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                mToolbar.setTitleTextColor(Color.TRANSPARENT);
                Typeface typeface = null;
                if(titleBar.getFont()!=null)
                typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/"+titleBar.getFont());

                String title = titleBar.getLabel();

                if(titleBar.isTitleTextStatic()){
                    View toolabarContainer = getLayoutInflater().inflate(R.layout.text_view, null);
                    FermatTextView txt_title = (FermatTextView) toolabarContainer.findViewById(R.id.txt_title);
                    txt_title.setText(title);
                    txt_title.setTypeface(typeface);
                    txt_title.setTextSize(titleBar.getLabelSize());
                    if(titleBar.getTitleColor()!=null)txt_title.setTextColor(Color.parseColor(titleBar.getTitleColor()));
                    mToolbar.addView(toolabarContainer);
                }else {

                    if (collapsingToolbarLayout != null) {
                        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
                        collapsingToolbarLayout.setCollapsedTitleTypeface(typeface);
                        collapsingToolbarLayout.setTitle(title);
                    }
                        mToolbar.setTitle(title);

                }

                if (titleBar.getColor() != null) {

                    if (collapsingToolbarLayout != null) {

                        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(titleBar.getColor()));
                        collapsingToolbarLayout.setScrimsShown(true);
                        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(titleBar.getColor()));
                        mToolbar.setBackgroundColor(Color.parseColor(titleBar.getColor()));
                        appBarLayout.setBackgroundColor(Color.parseColor(titleBar.getColor()));
                        //  mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(R.color.gps_friends_green_main));
//                        if (titleBar.getTitleColor() != null) {
//                            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor(titleBar.getTitleColor()));
//                        }
                    } else {
                        mToolbar.setBackgroundColor(Color.parseColor(titleBar.getColor()));
                        appBarLayout.setBackgroundColor(Color.parseColor(titleBar.getColor()));


                        if (titleBar.getTitleColor() != null) {
                            mToolbar.setTitleTextColor(Color.parseColor(titleBar.getTitleColor()));
                        }
                    }


                }


                setActionBarProperties(title, activity);
                paintToolbarIcon(titleBar);
            } else {
                if(appBarLayout!=null)
                appBarLayout.setVisibility(View.GONE);
                if (collapsingToolbarLayout != null)
                    collapsingToolbarLayout.setVisibility(View.GONE);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void paintToolbarIcon(TitleBar titleBar) {
        if (titleBar.getIconName() != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_action_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if no view has focus:
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    onBackPressed();
                }
            });
        }
        byte[] toolbarIcon = titleBar.getNavigationIcon();
        if (toolbarIcon != null)
            if (toolbarIcon.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(titleBar.getNavigationIcon(), 0, titleBar.getNavigationIcon().length);
                mToolbar.setNavigationIcon(new BitmapDrawable(getResources(), bitmap));
            }

    }

    /**
     * @param title
     */
    protected void setActionBarProperties(String title, Activity activity) {
        if(title!=null) {
            SpannableString s = new SpannableString(title);


//        s.setSpan(new MyTypefaceSpan(getApplicationContext(), "Roboto-Regular.ttf"), 0, s.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        // Update the action bar title with the TypefaceSpan instance
//        if (collapsingToolbarLayout != null)
//            collapsingToolbarLayout.setTitle(s);
//        mToolbar.setTitle(s);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//
//            Drawable colorDrawable = new ColorDrawable(Color.parseColor(activity.getColor()));
//            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
//            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
//
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                //ld.setCallback(drawableCallback);
//                Log.d(getClass().getSimpleName(), "Version incompatible con status bar");
//            } else {
//                collapsingToolbarLayout.setBackgroundDrawable(ld);
//            }
//        }
        }
    }

    /**
     * Method used from app to paint tabs
     */
    protected void setPagerTabs(TabStrip tabStrip, FermatSession fermatSession,FermatFragmentFactory fermatFragmentFactory) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
        pagertabs = (ViewPager) findViewById(R.id.pager);
        pagertabs.setVisibility(View.VISIBLE);
        adapter = new TabsPagerAdapter(getFragmentManager(),
                getApplicationContext(),
                fermatFragmentFactory,
                tabStrip,
                fermatSession,
                (activityType==ActivityType.ACTIVITY_TYPE_WALLET) ? getWalletResourcesProviderManager() : getSubAppResourcesProviderManager());
        pagertabs.setAdapter(adapter);
        if(tabStrip.isHasIcon()){
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                byte[] image = tabStrip.getTabs().get(i).getIcon();
                tabLayout.getTabAt(i).setIcon(new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(image,0, image.length)));
            }
        }
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pagertabs.setPageMargin(pageMargin);
        pagertabs.setCurrentItem(tabStrip.getStartItem(), true);
        tabLayout.setupWithViewPager(pagertabs);
    }


    protected void setOneFragmentInScreen(FermatFragmentFactory fermatFragmentFactory) {

        try {
            FermatStructure fermatStructure = getAppInUse();
            String appPublicKey = fermatStructure.getPublicKey();
            FermatSession fermatSession = getFermatSessionInUse(appPublicKey);
            String fragment = fermatStructure.getLastActivity().getLastFragment().getType();

            if (fermatFragmentFactory != null) {

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                tabLayout.setVisibility(View.GONE);

                pagertabs = (ViewPager) findViewById(R.id.pager);
                pagertabs.setVisibility(View.VISIBLE);


                adapter = new TabsPagerAdapter(getFragmentManager(),
                        getApplicationContext(),
                        fermatFragmentFactory,
                        fragment,
                        fermatSession,
                        (fermatStructure.getFermatAppType()== FermatAppType.WALLET)?getWalletResourcesProviderManager():getSubAppResourcesProviderManager());
                pagertabs.setAdapter(adapter);


                //pagertabs.setCurrentItem();
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
                pagertabs.setPageMargin(pageMargin);
                //pagertabs.setCurrentItem(tabStrip.getStartItem(), true);


                //tabLayout.setupWithViewPager(pagertabs);
                //pagertabs.setOffscreenPageLimit(tabStrip.getTabs().size());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Select the xml based on the activity type
     *
     * @param sidemenu
     * @param header
     */
    protected void setMainLayout(SideMenu sidemenu, FermatHeader header) {
        try {
            if (header != null) {
                setContentView(R.layout.new_wallet_runtime);
            } else {
                setContentView(R.layout.base_layout_without_collapse);
                if(activityType.equals(ActivityType.ACTIVITY_TYPE_DESKTOP)){
                    findViewById(R.id.bottom_navigation_container).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.bottom_navigation_container).setVisibility(View.GONE);
                    findViewById(R.id.reveal).setVisibility(View.GONE);
                }
            }

            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);


            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null)
                    setSupportActionBar(mToolbar);

            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
                collapsingToolbarLayout.setTitle("");
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
            }


            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

            if (appBarLayout != null)
                appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = false;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            collapsingToolbarLayout.setTitle("");
                            if(!isShow)
                                for(ElementsWithAnimation element : elementsWithAnimation){
                                    element.startCollapseAnimation(scrollRange);
                                }

                            isShow = true;
                        } else if (isShow) {
                            collapsingToolbarLayout.setTitle("");
                            for(ElementsWithAnimation element : elementsWithAnimation){
                                element.startExpandAnimation(scrollRange);
                            }
                            isShow = false;
                        }
                    }
                });

            if (header == null) {
                if (appBarLayout != null) {
                    appBarLayout.setExpanded(false);
                    appBarLayout.setEnabled(false);
                }

            }

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


            // listen for navigation events
            navigationView = (NavigationView) findViewById(R.id.navigation);


            if (sidemenu != null) {

                if (navigationView != null) {
                    navigationView.setNavigationItemSelectedListener(this);


                    navigation_recycler_view = (RecyclerView) findViewById(R.id.navigation_recycler_view);
                    RecyclerView.LayoutManager mLayoutManager;
                    // Letting the system know that the list objects are of fixed size
                    navigation_recycler_view.setHasFixedSize(true);

                    // Creating a layout Manager
                    mLayoutManager = new LinearLayoutManager(this);
                    // Setting the layout Manager
                    navigation_recycler_view.setLayoutManager(mLayoutManager);

                    // select the correct nav menu item
                    //navigationView.getMenu().findItem(mNavItemId).setChecked(true);

                    mToolbar.setNavigationIcon(R.drawable.ic_actionbar_menu);
                            /* setting up drawer layout */
                    mDrawerToggle = new ActionBarDrawerToggle(this,
                            mDrawerLayout,
                            mToolbar,
                            R.string.open, R.string.close) {
                        @Override
                        public void onDrawerOpened(View drawerView) {
                            super.onDrawerOpened(drawerView);
                            //setTitle(mTitle);
                            //invalidateOptionsMenu();
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            //setTitle(mTitle);
                            //invalidateOptionsMenu();
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            InputMethodManager imm =
                                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (getCurrentFocus() != null && imm != null && imm.isActive()) {
                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                            super.onDrawerSlide(drawerView, slideOffset);
                            float moveFactor = (navigationView.getWidth() * slideOffset);
                            //findViewById(R.id.content).setTranslationX(moveFactor);
                        }
                    };

                    mDrawerLayout.setDrawerListener(mDrawerToggle);
                    mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerToggle.syncState();
                        }
                    });

                    mDrawerToggle.setDrawerIndicatorEnabled(false);


                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawerLayout.openDrawer(GravityCompat.START);
                        }
                    });

                    navigate(mNavItemId);
                }
            } else {
                navigationView.setVisibility(View.GONE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

        } catch (Exception e) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, e);
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(broadcastManager!=null)broadcastManager.resume(this);
            else broadcastManager = new BroadcastManager(this);
            AndroidCoreUtils.getInstance().setContextAndResume(broadcastManager);

            //getNotificationManager().addObserver(this);
            //getNotificationManager().addCallback(this);



        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (notification != null) {
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(NOTIFICATION_ID, notification.build());
//        } else {
//            try {
//                if (getCloudClient().getCommunicationsCloudClientConnection().isConnected()) {
//                    launchIntent("running");
//                } else {
//                    launchIntent("closed");
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//        try {
//            networkStateReceiver = NetworkStateReceiver.getInstance();
//            networkStateReceiver.addListener(this);
//            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        try {
//            getNotificationManager().deleteObserver(this);
//            getNotificationManager().deleteCallback(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if(broadcastManager!=null)broadcastManager.stop();
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();


//        networkStateReceiver.removeListener(this);
        //mNotificationManager.notify(NOTIFICATION_ID, notification.build());
    }
    /**
     * @param tabs
     * @param activity
     */
    protected void paintTabs(TabStrip tabs, Activity activity) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        if (tabs == null)
            tabLayout.setVisibility(View.GONE);
        else {
            Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Regular.ttf");
            for (int position = 0; position < tabLayout.getTabCount(); position++) {
                ((TextView) tabLayout.getTabAt(position).getCustomView()).setTypeface(tf);
            }
            tabLayout.setVisibility(View.VISIBLE);
            if (tabs.getTabsColor() != null) {
                tabLayout.setBackgroundColor(Color.parseColor(activity.getTabStrip().getTabsColor()));
            }
            if (tabs.getTabsTextColor() != null) {
                tabLayout.setTabTextColors(Color.parseColor(activity.getTabStrip().getTabsTextColor()), Color.WHITE);
            }
            if (tabs.getTabsIndicateColor() != null) {
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor(activity.getTabStrip().getTabsIndicateColor()));
            }
            if (tabs.getIndicatorHeight() != -1) {
                tabLayout.setSelectedTabIndicatorHeight(tabs.getIndicatorHeight());
            }
        }
    }

    /**
     * Method to set status bar color in different version of android
     */

    protected void paintStatusBar(StatusBar statusBar) {
        if (statusBar != null) {
            if (statusBar.getColor() != null) {
                if (Build.VERSION.SDK_INT > 20) {
                    try {
                        Window window = this.getWindow();
                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        // finally change the color
                        Color color_status = new Color();
                        window.setStatusBarColor(Color.parseColor(statusBar.getColor()));
                    } catch (Exception e) {
                        getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.NOT_IMPORTANT, FermatException.wrapException(e));
                        Log.d("WalletActivity", "Sdk version not compatible with status bar color");
                    }
                }
            } else {
                try {
                    Window window = this.getWindow();
                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    // finally change the color
                    if (Build.VERSION.SDK_INT > 20)
                        window.setStatusBarColor(Color.TRANSPARENT);
                    gc();
                    //InputStream inputStream = getAssets().open("drawables/mdpi.jpg");
                    //window.setBackgroundDrawable(Drawable.createFromStream(inputStream, null));
                } catch (Exception e) {
                    getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.NOT_IMPORTANT, FermatException.wrapException(e));
                    Log.d("WalletActivity", "Sdk version not compatible with status bar color");
                }
            }
        } else {
            if (Build.VERSION.SDK_INT > 20) {
                try {
                    Window window = this.getWindow();
                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                } catch (Exception e) {
                    getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.NOT_IMPORTANT, FermatException.wrapException(e));
                    Log.d("WalletActivity", "Sdk version not compatible with status bar color");
                } catch (OutOfMemoryError outOfMemoryError) {
                    getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, new Exception());
                    makeText(this, "out of memory exception", LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Set the activity type
     *
     * @param activityType Enum value
     */

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    /**
     * Method used to clean the screen
     */

    protected void resetThisActivity() {

        try {
            //clean page adapter

            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            if (pager != null) {
                pager.removeAllViews();
                pager.removeAllViewsInLayout();
                pager.clearOnPageChangeListeners();
                pager.setVisibility(View.GONE);
                ((ViewGroup) pager.getParent()).removeView(pager);
                pager = null;
            }
            System.gc();

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            if (tabLayout != null) {
                tabLayout.removeAllTabs();
                tabLayout.removeAllViews();
                tabLayout.removeAllViewsInLayout();
            }

            final io.codetail.widget.RevealFrameLayout mRevealView = (io.codetail.widget.RevealFrameLayout) findViewById(R.id.reveal);
            if(mRevealView!=null){
                mRevealView.removeAllViews();
                mRevealView.setVisibility(View.GONE);
            }


            removecallbacks();
            onRestart();
        } catch (Exception e) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));
            makeText(getApplicationContext(), "Oooops! recovering from system error",
                    LENGTH_LONG).show();
        }
    }

    protected void removecallbacks() {
        try {

            if(adapter!=null) adapter.destroyCurrentFragments();
            this.adapter = null;
            paintStatusBar(null);
            if (navigation_recycler_view != null) {
                navigation_recycler_view.removeAllViews();
                navigation_recycler_view.removeAllViewsInLayout();
            }

            if (navigationView != null) {
                navigationView.removeAllViews();
                navigationView.removeAllViewsInLayout();

            }

            List<AbstractFermatFragment> fragments = new Vector<AbstractFermatFragment>();

            elementsWithAnimation = new ArrayList<>();
            if(bottomNavigation!=null) {
                bottomNavigation.reset();
                bottomNavigation = null;
            }

            this.screenPagerAdapter = new ScreenPagerAdapter(getFragmentManager(), fragments);

            System.gc();
            closeContextMenu();
            closeOptionsMenu();

            // Check if no view has focus:
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }


        } catch (Exception e) {

            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(e));

            makeText(getApplicationContext(), "Oooops! recovering from system error",
                    LENGTH_LONG).show();
        }
    }

    /**
     * Initialise the fragments to be paged
     */
    protected void initialisePaging() {

        try {
            List<AbstractFermatFragment> fragments = new Vector<>();
            DesktopRuntimeManager desktopRuntimeManager = getDesktopRuntimeManager();

            AbstractFermatFragment[] fragmentsArray = new AbstractFermatFragment[2];


            for (DesktopObject desktopObject : desktopRuntimeManager.listDesktops().values()) {
                //TODO: este switch se cambiara por un FragmentFactory en algún momento al igual que el Activity factory
                switch (desktopObject.getType()) {
                    case "DCCP":
                        //por ahora va esto
                        AppConnections appConnections = FermatAppConnectionManager.getFermatAppConnection(desktopObject.getPublicKey(), this);
                            //WalletDesktopFragment walletDesktopFragment = WalletDesktopFragment.newInstance(0, manager);

//                            DesktopFragment desktopFragment = DesktopFragment.newInstance((WalletManagerModule) manager);

                        String type = desktopObject.getLastActivity().getLastFragment().getType();


                        fragmentsArray[0] = appConnections.getFragmentFactory().getFragment(
                                type,
                                createOrGetSession(getDesktopManager()),
                                null
                        );


                        break;
                    case "WPD":
                            DesktopSubAppFragment subAppDesktopFragment = DesktopSubAppFragment.newInstance();
                            fragmentsArray[1] =  subAppDesktopFragment;
                        break;

                }
            }
            fragments = Arrays.asList(fragmentsArray);
            screenPagerAdapter = new ScreenPagerAdapter(getFragmentManager(), fragments);

            pagertabs = (ViewPager) super.findViewById(R.id.pager);
            pagertabs.setVisibility(View.VISIBLE);

            //set default page to show
            pagertabs.setCurrentItem(0);

            final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
            radioGroup.setVisibility(View.VISIBLE);
            pagertabs.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            radioGroup.check(R.id.radioButton);
                            break;
                        case 1:
                            radioGroup.check(R.id.radioButton2);
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            pagertabs.setAdapter(this.screenPagerAdapter);

            for(int childPos = 0 ; childPos < radioGroup.getChildCount();childPos++){
                final int finalChildPos = childPos;
                radioGroup.getChildAt(childPos).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pagertabs.setCurrentItem(finalChildPos, true);
                    }
                });
            }

            if (pagertabs.getBackground() == null) {
                if(ApplicationSession.applicationState==ApplicationSession.STATE_STARTED) {
                    //Drawable d = Drawable.createFromStream(getAssets().open("drawables/mdpi.jpg"), null);
                    //getWindow().setBackgroundDrawable(Drawable.createFromStream(getAssets().open("drawables/mdpi.jpg"), null));
                    //pager.setBackground(d);
                    //getWindow().addFlags(WindowManager.LayoutParams.);
                    if (Build.VERSION.SDK_INT > 20) {
                        getWindow().setStatusBarColor(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow().setStatusBarColor(Color.TRANSPARENT);
                    }
                    final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                    Drawable wallpaperDrawable = null;
                    if (Build.VERSION.SDK_INT > 19)
                        wallpaperDrawable = wallpaperManager.getBuiltInDrawable();

//                    Display display = getWindowManager().getDefaultDisplay();
//                    Point size = new Point();
//                    display.getSize(size);
//                    Bitmap bmp = Bitmap.createScaledBitmap(((BitmapDrawable) wallpaperDrawable).getBitmap(), size.x + 300, size.y, true);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
                            WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

                    ApplicationSession.applicationState = ApplicationSession.STATE_STARTED_DESKTOP;
                }
            }


        } catch (Exception ex) {
            getErrorManager().reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(ex));
            makeText(getApplicationContext(), "Oooops! recovering from system error", LENGTH_SHORT).show();
        }
    }



    protected void bottomNavigationEnabled(boolean enabled){
        try {
            if (enabled) {
                bottomNavigation = new BottomNavigation(this, ProvisoryData.getBottomNavigationProvisoryData(), null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Method to create or get a session in the session manager
     *
     * @param fermatApp
     * @return
     */
    protected FermatSession createOrGetSession(FermatApp fermatApp){
        FermatSession fermatSession = null;
        AppConnections fermatAppConnection = FermatAppConnectionManager.getFermatAppConnection(fermatApp.getAppPublicKey(), this);
        if (getFermatSessionManager().isSessionOpen(fermatApp.getAppPublicKey())) {
            fermatSession = getFermatSessionManager().getAppsSession(fermatApp.getAppPublicKey());
        } else {
            ModuleManager moduleManager = getModuleManager(fermatAppConnection.getPluginVersionReference());
            fermatSession = getFermatSessionManager().openAppSession(fermatApp,getErrorManager(),moduleManager,fermatAppConnection);
        }
        return fermatSession;
    }

    /**
     *  Get app session manager
     */
    @Deprecated
    public FermatSessionManager getFermatSessionManager() {
        return ApplicationSession.getInstance().getFermatSessionManager();
    }



    //TODO: esto es un plugin más para el manejo de los desktops
    protected DesktopManager getDesktopManager(){
        return new FermatDesktopManager();
    }

    /**
     *  Return an instance of module manager
     * @param pluginVersionReference
     * @return
     */
    public ModuleManager getModuleManager(PluginVersionReference pluginVersionReference){
        try {
            return getApplicationSession().getFermatSystem().getModuleManager(pluginVersionReference);
        } catch (ModuleManagerNotFoundException | CantGetModuleManagerException e) {
            System.err.println(e.getMessage());
            System.err.println(e.toString());
            return null;
        } catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }
    }

    protected FermatApplicationSession getApplicationSession(){
        return ApplicationSession.getInstance();
    }

    /**
     * Set up wizards to this activity can be more than one.
     *
     * @param wizards
     */
    public void setWizards(Map<String, Wizard> wizards) {
        if (wizards != null && wizards.size() > 0) {
            if (this.wizards == null)
                this.wizards = new HashMap<>();
            this.wizards.putAll(wizards);
        }
    }

    /**
     * Launch wizard configuration from key
     *
     * @param key  Name of FermatWizard Enum
     * @param args Object... arguments to passing to the wizard fragment
     */
    @Override
    public void showWizard(String key, Object... args) {
        try {
            if (wizards == null)
                throw new NullPointerException("the wizard is null");

            Set<String> keys = wizards.keySet();
            System.out.println(keys);
            Wizard wizard = wizards.get(key);
            if (wizard != null) {
            /* Starting Wizard Activity */
                WizardActivity.open(this, args, wizard);
            } else {
                Log.e(TAG, "Wizard not found...");
            }
        } catch (Exception ex) {
            makeText(this, "Cannot instantiate wizard runtime because the wizard called is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {

        wizards = null;
//        Intent intent = new Intent(this,NotificationService.class);
//        stopService(intent);

        //navigationDrawerFragment.onDetach();
        if(appStatusListener != null)
             appStatusListener.clear();
        if(runtimeStructureManager!=null){
            runtimeStructureManager.clear();
        }

        resetThisActivity();
        super.onDestroy();
    }

    protected void hideBottonIcons(){
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.icons_container);
        if(linearLayout!=null)
        linearLayout.setVisibility(View.GONE);
    }


    public void notificateBroadcast(String appPublicKey,String code){
        try {
            if (appPublicKey != null) {
                FermatStructure fermatStructure = getAppInUse(appPublicKey);
                AppConnections fermatAppConnection = FermatAppConnectionManager.getFermatAppConnection(fermatStructure.getPublicKey(), this);
                NotificationPainter notificationPainter = fermatAppConnection.getNotificationPainter(code);
                if (notificationPainter != null) {
                    RemoteViews remoteViews = notificationPainter.getNotificationView(code);
                    Intent intent = new Intent(this, (fermatStructure.getFermatAppType() == FermatAppType.WALLET) ? WalletActivity.class : SubAppActivity.class);
                    intent.putExtra((fermatStructure.getFermatAppType() == FermatAppType.WALLET) ? WalletActivity.WALLET_PUBLIC_KEY : SubAppActivity.SUB_APP_PUBLIC_KEY, fermatStructure.getPublicKey());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pi = PendingIntent
                            .getActivity(this, 0, intent, 0);

                    Notification.Builder builder = null;
                    if (remoteViews != null) {
                        builder = new Notification.Builder(this).setSmallIcon(R.drawable.fermat_logo_310_x_310).setTicker("ticker")
                                .setPriority(Notification.PRIORITY_LOW).setAutoCancel(true)
                                .setAutoCancel(true)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setLights(Color.YELLOW, 3000, 3000)
                                .setContent(remoteViews)
                                .setWhen(System.currentTimeMillis());
                    } else {
                        builder = new Notification.Builder(this)
                                .setTicker(notificationPainter.getNotificationTitle())
                                .setSmallIcon((notificationPainter.getIcon() <= 0) ? R.drawable.fermat_logo_310_x_310 : notificationPainter.getIcon())
                                .setContentTitle(notificationPainter.getNotificationTitle())
                                .setContentText(notificationPainter.getNotificationTextBody())
                                .setContentIntent(pi)
                                .setAutoCancel(true)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setLights(Color.YELLOW, 3000, 3000);
                    }

                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());
                }
            } else {
                Notification.Builder builder = new Notification.Builder(this)
                        .setTicker("Something arrive")
                        .setSmallIcon(R.drawable.fermat_logo_310_x_310)
                        .setContentTitle("Fermat: new notification")
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.YELLOW, 3000, 3000);

                NotificationManager notificationManager = (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void addDesktopCallBack(DesktopHolderClickCallback desktopHolderClickCallback ){
        if(bottomNavigation!=null) bottomNavigation.setDesktopHolderClickCallback(desktopHolderClickCallback);
    }




    @Override
    public void invalidate() {
        FermatStructure fermatStructure = getAppInUse();
        Activity activity = fermatStructure.getLastActivity();
        FermatSession fermatSession = getFermatSessionInUse(fermatStructure.getPublicKey());
        AppConnections appsConnections = FermatAppConnectionManager.getFermatAppConnection(fermatStructure.getPublicKey(), this,fermatSession);
        try {
            appsConnections.setActiveIdentity(getModuleManager(appsConnections.getPluginVersionReference()).getSelectedActorIdentity());
        } catch (CantGetSelectedActorIdentityException|ActorIdentityNotSelectedException e) {
            e.printStackTrace();
        }
        paintSideMenu(activity,activity.getSideMenu(),appsConnections);
        paintFooter(activity.getFooter(),appsConnections.getFooterViewPainter());

    }

    protected void refreshSideMenu(){
        //TODO: acá seria bueno un getLastApp
        if(ActivityType.ACTIVITY_TYPE_DESKTOP != activityType) {
            final FermatStructure fermatStructure = getAppInUse();
            FermatSession fermatSession = getFermatSessionInUse(fermatStructure.getPublicKey());
            AppConnections appConnections = FermatAppConnectionManager.getFermatAppConnection(fermatStructure.getPublicKey(), this,fermatSession);
            final NavigationViewPainter viewPainter = appConnections.getNavigationViewPainter();
            final FermatAdapter mAdapter = viewPainter.addNavigationViewAdapter();
            final List<com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem> lstItems = getNavigationMenu();
            refreshHandler.post(new Runnable() {
                @Override
                public void run() {
                    SideMenuBuilder.setAdapter(
                            navigation_recycler_view,
                            mAdapter,
                            viewPainter.addItemDecoration(),
                            lstItems,
                            getLisItemListenerMenu(),
                            //TODO: acá seria bueno un getLastActivity
                            fermatStructure.getLastActivity().getActivityType()
                    );
                }
            });

        }
    }



    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // update highlighted item in the navigation menu
        item.setChecked(true);
        mNavItemId = item.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(item.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);

        return true;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            if(mDrawerToggle!=null) mDrawerToggle.onConfigurationChanged(newConfig);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        //Toast.makeText(this,"holas",LENGTH_SHORT).show();
    }

//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
//            return mDrawerToggle.onOptionsItemSelected(item);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout!=null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }


        try{





        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Navigation menu event handlers
     */

    @Override
    public void onItemClickListener(com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem data, int position) {
        if(getWalletRuntimeManager().getLastWallet() != null)
            getWalletRuntimeManager().getLastWallet().clear();

        onNavigationMenuItemTouchListener(data, position);
    }

    @Override
    public void onLongItemClickListener(com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem data, int position) {

    }
    @Override
    public void setActivityBackgroundColor(Drawable drawable){
        if(drawable!=null) pagertabs.setBackground(drawable);

    }

    public void addCollapseAnimation(ElementsWithAnimation elementsWithAnimation){
        this.elementsWithAnimation.add(elementsWithAnimation);
    }



    /**
     * This methos is a touch listener from the navigation view.
     * the class that implement this methis have to use changeActivity, changeFragment, selectWallet or selectSubApp
     *
     * @param data
     * @param position
     */
    protected abstract void onNavigationMenuItemTouchListener(com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem data, int position);

    public void setScreen(Activity activity) {
        try {
            if (activity.isFullScreen()) {
                // finally change the color
                try {
                    requestWindowFeature(Window.FEATURE_NO_TITLE);
                }catch (Exception e){

                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            if(activity.getBackgroundColor()!=null && coordinatorLayout!=null){
                    coordinatorLayout.setBackgroundColor(Color.parseColor(activity.getBackgroundColor()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String searchAppFromPlatformIdentifier(FermatApps fermatApps) {
        String appPublicKey = null;
        try {
            appPublicKey = getWalletManager().getInstalledWallet(fermatApps.getCode()).getWalletPublicKey();
        }catch (NullPointerException i){
            appPublicKey = getSubAppManager().getInstalledSubApp(fermatApps.getCode()).getAppPublicKey();
        }catch (Exception e){

        }
        return appPublicKey;
    }



    private FermatListItemListeners getLisItemListenerMenu(){
        return this;
    }

    private FermatSession getFermatSessionInUse(String appPublicKey){
        return getFermatSessionManager().getAppsSession(appPublicKey);
    }

    /**
     * Abstract methods
     */

    public abstract FermatStructure getAppInUse();
    public abstract FermatStructure getAppInUse(String publicKey) throws Exception;
    public abstract void changeActivity(String activityName,String appBackPublicKey, Object... objects);
    protected abstract List<com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem> getNavigationMenu();

    //TODO: to remove
    public abstract void connectWithOtherApp(Engine engine,String fermatAppPublicKey,Object[] objectses);


    @Override
    public void networkAvailable() {
        Log.i(TAG, "NETWORK AVAILABLE MATIIIII");
        try {
            //getCloudClient().setNetworkState(true);
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    @Override
    public void networkUnavailable() {
        Log.i(TAG, "NETWORK UNAVAILABLE MATIIIII");
        try{
            //getCloudClient().setNetworkState(false);
        }catch (Exception e){
          //  e.printStackTrace();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
//        Intent intent = new Intent(this, BoundService.class);
//        intent.putExtra(BoundService.LOG_TAG,"Activity 1");
//        startService(intent);
//        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


//    private BoundService mBoundService;
//    /**
//     * Service
//     */
//
//
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mServiceConnected = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mBoundService = ((BoundService.LocalBinder)service).getService();
//            mBoundService.registerCallback(registerCallback());
//            mServiceConnected = true;
//        }
//    };
//
//    private ServiceCallback registerCallback(){
//        return this;
//    }


    @Override
    public void callback(int option) {
        if (option==1){

        }
    }

    @Override
    public FermatRuntime getRuntimeManager(){
        return runtimeStructureManager;
    }

    public ScreenPagerAdapter getScreenAdapter() {
        return screenPagerAdapter;
    }

    public ViewPager getPagertabs() {
        return pagertabs;
    }

    public ActivityType getType() {
        return activityType;
    }

    @Override
    public AndroidCoreManager getFermatStates(){
        return FermatSystemUtils.getAndroidCoreModule().getAndroidCoreManager();
    }

    public TabsPagerAdapter getAdapter() {
        return adapter;
    }


    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    public RelativeLayout getToolbarHeader() {
        return (RelativeLayout) findViewById(R.id.toolbar_header_container);
    }


}
