package com.bitdubai.sub_app.chat_community.fragments;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatListItemListeners;
import com.bitdubai.fermat_android_api.ui.interfaces.FermatWorkerCallBack;
import com.bitdubai.fermat_android_api.ui.util.FermatWorker;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.enums.NetworkStatus;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantGetCommunicationNetworkStatusException;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.settings.structure.SettingsManager;
import com.bitdubai.fermat_api.layer.modules.exceptions.ActorIdentityNotSelectedException;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantListChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunityInformation;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.interfaces.ChatActorCommunitySubAppModuleManager;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.chat_actor_community.settings.ChatActorCommunitySettings;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.sub_app.chat_community.adapters.CommunityListAdapter;
import com.bitdubai.sub_app.chat_community.R;
import com.bitdubai.sub_app.chat_community.common.popups.ErrorConnectingFermatNetworkDialog;
import com.bitdubai.sub_app.chat_community.common.popups.PresentationChatCommunityDialog;
import com.bitdubai.sub_app.chat_community.constants.Constants;
import com.bitdubai.sub_app.chat_community.interfaces.ErrorConnectingFermatNetwork;
import com.bitdubai.sub_app.chat_community.session.ChatUserSubAppSession;
import com.bitdubai.sub_app.chat_community.util.CommonLogger;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * ConnectionsWorldFragment
 *
 * @author Jose Cardozo josejcb (josejcb89@gmail.com) on 13/04/16.
 * @version 1.0
 */

public class ConnectionsWorldFragment extends AbstractFermatFragment implements
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, FermatListItemListeners<ChatActorCommunityInformation> {


    public static final String CHAT_USER_SELECTED = "chat_user";

    private static final int MAX = 20;
    /**
     * MANAGERS
     */
    private static ChatActorCommunitySubAppModuleManager moduleManager;
    private static ErrorManager errorManager;

    protected final String TAG = "Recycler Base";
    FermatWorker worker;
    SettingsManager<ChatActorCommunitySettings> settingsManager;
    ChatActorCommunitySettings chatUserSettings = null;
    private ErrorConnectingFermatNetwork errorConnectingFermatNetwork;
    private int offset = 0;
    private int mNotificationsCount = 0;
    private SearchView mSearchView;
    private CommunityListAdapter adapter;
    private boolean isStartList = false;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefresh;
    private View searchView;
    // flags
    private boolean isRefreshing = false;
    private View rootView;
    private ChatUserSubAppSession chatUserSubAppSession;
    private String searchName;
    private LinearLayout emptyView;
    private ArrayList<ChatActorCommunityInformation> lstChatUserInformations;
    private List<ChatActorCommunityInformation> dataSet = new ArrayList<>();
    private android.support.v7.widget.Toolbar toolbar;
    private EditText searchEditText;
    private List<ChatActorCommunityInformation> dataSetFiltered;
    private ImageView closeSearch;
    private LinearLayout searchEmptyView;
    private LinearLayout noNetworkView;
    private LinearLayout noFermatNetworkView;
    private Handler handler = new Handler();

    /**
     * Create a new instance of this fragment
     *
     * @return InstalledFragment instance object
     */
    public static ConnectionsWorldFragment newInstance() {
        return new ConnectionsWorldFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setHasOptionsMenu(true);
            // setting up  module
            chatUserSubAppSession = ((ChatUserSubAppSession) appSession);
            moduleManager = chatUserSubAppSession.getModuleManager();
            errorManager = appSession.getErrorManager();

            settingsManager = chatUserSubAppSession.getModuleManager().getSettingsManager();
            //TODO: SETTINGS OF ACTOR LAYER
            try {
                chatUserSettings =
                        settingsManager.loadAndGetSettings(chatUserSubAppSession.getAppPublicKey());
            } catch (Exception e) {
                chatUserSettings = null;
            }

            if (chatUserSubAppSession.getAppPublicKey() != null) //the identity not exist yet
            {
                if (chatUserSettings == null) {
                    chatUserSettings = new ChatActorCommunitySettings();
                    chatUserSettings.setIsPresentationHelpEnabled(true);
                    settingsManager.persistSettings(chatUserSubAppSession.getAppPublicKey(),
                            chatUserSettings);
                }
            }
            //mNotificationsCount = moduleManager.getChatActorWaitingYourAcceptanceCount(PublicKey, max, offset);
            new FetchCountTask().execute();
        } catch (Exception ex) {
            CommonLogger.exception(TAG, ex.getMessage(), ex);
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY,
                    UnexpectedUIExceptionSeverity.CRASH, ex);
        }
    }

    /**
     * Fragment Class implementation.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.cht_comm_connections_world_fragment, container, false);
            toolbar = getToolbar();
            //toolbar.setTitle("Chat Users");
            setUpScreen(inflater);
            searchView = inflater.inflate(R.layout.cht_comm_search_edit_text, null);
            setUpReferences();
            switch (getFermatState().getFermatNetworkStatus()) {
                case CONNECTED:
                   // setUpReferences();
                    break;
                case DISCONNECTED:
                    showErrorFermatNetworkDialog();
                    break;
            }

        } catch (Exception ex) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY,
                    UnexpectedUIExceptionSeverity.CRASH, FermatException.wrapException(ex));
            Toast.makeText(getActivity().getApplicationContext(),
                    "Oooops! recovering from system error", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    public void setUpReferences() {
        dataSet = new ArrayList<>();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    worker.shutdownNow();
                    return true;
                }
                return false;
            }
        });
        searchEditText = (EditText) searchView.findViewById(R.id.search);
        closeSearch = (ImageView) searchView.findViewById(R.id.close_search);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.gridView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommunityListAdapter(getActivity(), lstChatUserInformations);
        recyclerView.setAdapter(adapter);
        adapter.setFermatListEventListener(this);
        swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeColors(Color.BLUE, Color.BLUE);
        rootView.setBackgroundColor(Color.parseColor("#000b12"));
        emptyView = (LinearLayout) rootView.findViewById(R.id.empty_view);
        searchEmptyView = (LinearLayout) rootView.findViewById(R.id.search_empty_view);
        noNetworkView = (LinearLayout) rootView.findViewById(R.id.no_connection_view);
        noFermatNetworkView = (LinearLayout) rootView.findViewById(R.id.no_fermat_connection_view);
        try {
            dataSet.addAll(moduleManager.getCacheSuggestionsToContact(MAX, offset));
        } catch (CantListChatIdentityException e) {
            e.printStackTrace();
        }
        if (chatUserSettings.isPresentationHelpEnabled()) {
            showDialogHelp();
        } else {
            showCriptoUsersCache();
        }
    }

    public void showErrorNetworkDialog() {
        ErrorConnectingFermatNetworkDialog errorConnectingFermatNetworkDialog =
                new ErrorConnectingFermatNetworkDialog(getActivity(), chatUserSubAppSession, null);
        errorConnectingFermatNetworkDialog.setDescription("You are not connected  /n to the Fermat Network");
        errorConnectingFermatNetworkDialog.setRightButton("Connect", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        errorConnectingFermatNetworkDialog.setLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        errorConnectingFermatNetworkDialog.show();
    }

    public void showErrorFermatNetworkDialog() {
        final ErrorConnectingFermatNetworkDialog errorConnectingFermatNetworkDialog =
                new ErrorConnectingFermatNetworkDialog(getActivity(), chatUserSubAppSession, null);
        errorConnectingFermatNetworkDialog.setDescription("The access to the /n Fermat Network is disabled.");
        errorConnectingFermatNetworkDialog.setRightButton("Enable", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorConnectingFermatNetworkDialog.dismiss();
                try {
                    if (getFermatState().getFermatNetworkStatus() == NetworkStatus.DISCONNECTED) {
                        Toast.makeText(getActivity(), "Wait a minute please, trying to reconnect...",
                                Toast.LENGTH_SHORT).show();
                        //getActivity().onBackPressed();
                    }
                } catch (CantGetCommunicationNetworkStatusException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        errorConnectingFermatNetworkDialog.setLeftButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorConnectingFermatNetworkDialog.dismiss();
            }
        });
        errorConnectingFermatNetworkDialog.show();
    }

    @Override
    public void onRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            worker = new FermatWorker() {
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
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                    if (result != null &&
                            result.length > 0) {
                        if (getActivity() != null && adapter != null) {
                            lstChatUserInformations = (ArrayList<ChatActorCommunityInformation>) result[0];
                            adapter.changeDataSet(lstChatUserInformations);
                            if (lstChatUserInformations.isEmpty()) {
                                try {
                                    if (!moduleManager.getCacheSuggestionsToContact(MAX, offset).isEmpty()) {
                                        lstChatUserInformations
                                                .addAll(moduleManager.getCacheSuggestionsToContact(MAX, offset));
                                        showEmpty(false, emptyView);
                                        showEmpty(false, searchEmptyView);
                                    } else {
                                        showEmpty(true, emptyView);
                                        showEmpty(false, searchEmptyView);
                                    }
                                } catch (CantListChatIdentityException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                showEmpty(false, emptyView);
                                showEmpty(false, searchEmptyView);
                            }
                        }
                    } else {
                        try {
                            showEmpty(false, emptyView);
                            showEmpty(false, searchEmptyView);
                            lstChatUserInformations
                                    .addAll(moduleManager.getCacheSuggestionsToContact(MAX, offset));
                        } catch (CantListChatIdentityException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onErrorOccurred(Exception ex) {
                    isRefreshing = false;
                    if (swipeRefresh != null)
                        swipeRefresh.setRefreshing(false);
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();

                }
            });
            worker.execute();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.cht_comm_menu, menu);//cripto_users_menu

        try {
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            menu.findItem(R.id.action_help).setVisible(true);
            menu.findItem(R.id.action_search).setVisible(true);
            searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    menu.findItem(R.id.action_help).setVisible(false);
                    menu.findItem(R.id.action_search).setVisible(false);
                    toolbar = getToolbar();
                    toolbar.setTitle("");
                    toolbar.addView(searchView);
                    if (closeSearch != null)
                        closeSearch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                menu.findItem(R.id.action_help).setVisible(true);
                                menu.findItem(R.id.action_search).setVisible(true);
                                toolbar = getToolbar();
                                toolbar.removeView(searchView);
                                toolbar.setTitle("Chat Users");
                                onRefresh();
                            }
                        });

                    if (searchEditText != null) {
                        searchEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                                if (s.length() > 0) {
                                    worker = new FermatWorker() {
                                        @Override
                                        protected Object doInBackground() throws Exception {
                                            return getQueryData(s);
                                        }
                                    };
                                    worker.setContext(getActivity());
                                    worker.setCallBack(new FermatWorkerCallBack() {
                                        @SuppressWarnings("unchecked")
                                        @Override
                                        public void onPostExecute(Object... result) {
                                            isRefreshing = false;
                                            if (swipeRefresh != null)
                                                swipeRefresh.setRefreshing(false);
                                            if (result != null &&
                                                    result.length > 0) {
                                                if (getActivity() != null && adapter != null) {
                                                    dataSetFiltered = (ArrayList<ChatActorCommunityInformation>) result[0];
                                                    adapter.changeDataSet(dataSetFiltered);
                                                    if (dataSetFiltered != null) {
                                                        if (dataSetFiltered.isEmpty()) {
                                                            showEmpty(true, searchEmptyView);
                                                            showEmpty(false, emptyView);

                                                        } else {
                                                            showEmpty(false, searchEmptyView);
                                                            showEmpty(false, emptyView);
                                                        }
                                                    } else {
                                                        showEmpty(true, searchEmptyView);
                                                        showEmpty(false, emptyView);
                                                    }
                                                }
                                            } else {
                                                showEmpty(true, searchEmptyView);
                                                showEmpty(false, emptyView);
                                            }
                                        }
                                        @Override
                                        public void onErrorOccurred(Exception ex) {
                                            isRefreshing = false;
                                            if (swipeRefresh != null)
                                                swipeRefresh.setRefreshing(false);
                                            showEmpty(true, searchEmptyView);
                                            if (getActivity() != null)
                                                Toast.makeText(getActivity(), ex.getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            ex.printStackTrace();

                                        }
                                    });
                                    worker.execute();
                                } else {
                                    menu.findItem(R.id.action_help).setVisible(true);
                                    menu.findItem(R.id.action_search).setVisible(true);
                                    toolbar = getToolbar();
                                    toolbar.removeView(searchView);
                                    //toolbar.setTitle("Cripto wallet users");
                                    onRefresh();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });
                    }
                    return false;
                }
            });

        } catch (Exception e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == R.id.action_help)
                showDialogHelp();

        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY,
                    UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getActivity(), "Oooops! recovering from system error",
                    LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationsBadge(int count) {
        mNotificationsCount = count;
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private synchronized List<ChatActorCommunityInformation> getQueryData(final CharSequence charSequence) {
        if (dataSet != null && !dataSet.isEmpty()) {
            if (searchEditText != null && !searchEditText.getText().toString().isEmpty()) {
                dataSetFiltered = new ArrayList<ChatActorCommunityInformation>();
                for (ChatActorCommunityInformation chatUser : dataSet) {

                    if(chatUser.getAlias().toLowerCase().contains(charSequence.toString().toLowerCase()))
                        dataSetFiltered.add(chatUser);
                }
            } else dataSetFiltered = null;
        }
        return dataSetFiltered;
    }


    private synchronized List<ChatActorCommunityInformation> getMoreData() {
        List<ChatActorCommunityInformation> dataSet = new ArrayList<>();

         try {
            //verifico la cache para mostrar los que tenia antes y los nuevos
             List<ChatActorCommunityInformation> userCacheList = new ArrayList<>();
             try {
                     userCacheList = moduleManager.getCacheSuggestionsToContact(MAX, offset);
             } catch (CantListChatIdentityException e) {
                 e.printStackTrace();
             }

            List<ChatActorCommunityInformation> userList = moduleManager.getSuggestionsToContact(moduleManager.getSelectedActorIdentity().getPublicKey() ,MAX, offset);
            if(userCacheList.size() == 0)
            {
                //dataSet.addAll(userList);
                //moduleManager. .saveCacheChatUsersSuggestions(userList);
            }
            else
            {
                if(userList.size() == 0)
                {
                    dataSet.addAll(userCacheList);
                }
                else
                {
                    for (ChatActorCommunityInformation chatUserCache : userCacheList) {
                        boolean exist = false;
                        for (ChatActorCommunityInformation chatUser : userList) {
                            if(chatUserCache.getPublicKey().equals(chatUser.getPublicKey())){
                                exist = true;
                                break;
                            }
                        }
                        if(!exist)
                            userList.add(chatUserCache);
                    }
                    //guardo el cache

                    //moduleManager.saveCacheChatUsersSuggestions(userList);
                    //dataSet.addAll(userList);
                }
            }

            //offset = dataSet.size();

        } catch (CantListChatIdentityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSet;
    }


    @Override
    public void onItemClickListener(ChatActorCommunityInformation data, int position) {
        try {
            if (moduleManager.getSelectedActorIdentity() != null) {
                if (!moduleManager.getSelectedActorIdentity().getPublicKey().isEmpty())
                    appSession.setData(CHAT_USER_SELECTED, data);
                changeActivity(Activities.CHT_SUB_APP_CHAT_COMMUNITY_CONNECTION_OTHER_PROFILE.getCode(),
                        appSession.getAppPublicKey());
            }
        } catch (CantGetSelectedActorIdentityException e) {
            e.printStackTrace();
        } catch (ActorIdentityNotSelectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLongItemClickListener(ChatActorCommunityInformation data, int position) {

    }


    private void showDialogHelp() {
        try {
            if (moduleManager.getSelectedActorIdentity() != null) {
                if (!moduleManager.getSelectedActorIdentity().getPublicKey().isEmpty()) {
                    PresentationChatCommunityDialog presentationChatCommunityDialog =
                            new PresentationChatCommunityDialog(getActivity(),
                            chatUserSubAppSession,
                            null,
                            moduleManager,
                            PresentationChatCommunityDialog.TYPE_PRESENTATION_WITHOUT_IDENTITIES);
                    presentationChatCommunityDialog.show();
                    presentationChatCommunityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            showCriptoUsersCache();
                        }
                    });
                } else {
                    PresentationChatCommunityDialog presentationChatCommunityDialog =
                            new PresentationChatCommunityDialog(getActivity(),
                            chatUserSubAppSession,
                            null,
                            moduleManager,
                                    PresentationChatCommunityDialog.TYPE_PRESENTATION);
                    presentationChatCommunityDialog.show();
                    presentationChatCommunityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Boolean isBackPressed =
                                    (Boolean) chatUserSubAppSession.getData(Constants.PRESENTATION_DIALOG_DISMISS);
                            if (isBackPressed != null) {
                                if (isBackPressed) {
                                    getActivity().finish();
                                }
                            } else {
                                showCriptoUsersCache();
                                invalidate();
                            }
                        }
                    });
                }
            } else {
                PresentationChatCommunityDialog presentationChatCommunityDialog =
                        new PresentationChatCommunityDialog(getActivity(),
                        chatUserSubAppSession,
                        null,
                        moduleManager,
                                PresentationChatCommunityDialog.TYPE_PRESENTATION);
                presentationChatCommunityDialog.show();
                presentationChatCommunityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Boolean isBackPressed = (Boolean) chatUserSubAppSession.getData(Constants.PRESENTATION_DIALOG_DISMISS);
                        if (isBackPressed != null) {
                            if (isBackPressed) {
                                getActivity().onBackPressed();
                            }
                        } else
                            showCriptoUsersCache();
                    }
                });
            }
        } catch (CantGetSelectedActorIdentityException e) {
            e.printStackTrace();
        } catch (ActorIdentityNotSelectedException e) {
            e.printStackTrace();
        }
    }

    private void showCriptoUsersCache() {

        ChatActorCommunitySubAppModuleManager moduleManager = chatUserSubAppSession.getModuleManager();
        if(moduleManager==null){
            getActivity().onBackPressed();
        }else{
            invalidate();
        }if (dataSet.isEmpty()) {
            showEmpty(true, emptyView);
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(true);
                    onRefresh();
                }

            });
        } else {
            adapter.changeDataSet(dataSet);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            }, 1500);
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

    private void setUpScreen(LayoutInflater layoutInflater) throws CantGetSelectedActorIdentityException {

    }

    /*
    Sample AsyncTask to fetch the notifications count
    */
    class FetchCountTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // example count. This is where you'd
            // query your data store for the actual count.
            return mNotificationsCount;
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }

    @Override
    public void onUpdateViewOnUIThread(String code){
        try
        {
            //update user list
            if(code.equals("ACCEPTED_CONNECTION"))
              onRefresh();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}




