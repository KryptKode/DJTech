package com.kryptkode.cyberman.djtech.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kryptkode.cyberman.djtech.OptionsActivity;
import com.kryptkode.cyberman.djtech.R;
import com.kryptkode.cyberman.djtech.adapters.HomeScreenAdapter;
import com.kryptkode.cyberman.djtech.models.BlogPosts;
import com.kryptkode.cyberman.djtech.utils.HomeScreenFragmentHelper;
import com.kryptkode.cyberman.djtech.utils.ItemDivider;
import com.kryptkode.cyberman.djtech.utils.JsonHelper;
import com.kryptkode.cyberman.djtech.utils.NetworkUtils;

import java.util.ArrayList;

import static com.kryptkode.cyberman.djtech.OptionsActivity.IMAGE_PATH;
import static com.kryptkode.cyberman.djtech.OptionsActivity.StringToBitMap;
import static com.kryptkode.cyberman.djtech.OptionsActivity.WALLPAPER_FLAGS;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements HomeScreenAdapter.HomeScreenAdapterListener,
        LoaderManager.LoaderCallbacks<BlogPosts[]> {

    public static final int POST_LOADER_ID = 100;
    public static final String TAG = HomeScreenFragment.class.getSimpleName();

    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String AUTHOR = "author";
    private static final String RENDERED = "rendered";
    public static final String POSTS = "dj_tech_posts";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    private  boolean isLoaded;

    private TextView errorTextView;
    private ImageView errorImageView;
    private ProgressBar progressBar;
    private TextView loadingTextView;

    private LinearLayoutManager linearLayoutManager;
    private HomeScreenAdapter adapter;
    private HomeScreenFragmentListener homeScreenFragmentListener;

    private ArrayList<BlogPosts> postsArrayList;


    private ConnectivityChangeReceiver receiver;
    private SharedPreferences shrep;


    public interface HomeScreenFragmentListener {
        void onItemClicked(int position, BlogPosts blogPostsArrayList);

        void onAuthorAvatarClicked(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeScreenFragmentListener = (HomeScreenFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeScreenFragmentListener = null;
    }

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        shrep = getContext().getSharedPreferences(OptionsActivity.MYPRE, Context.MODE_PRIVATE);
        switch (shrep.getInt(WALLPAPER_FLAGS, 0)){
            case 1: view.setBackgroundResource(R.drawable.dark_brown);break;
            case 2: view.setBackgroundResource(R.drawable.dark_red);break;
            case 3: view.setBackgroundResource(R.drawable.darkblue_background);break;
            case 4: view.setBackgroundResource(R.drawable.light_green);break;
            case 5: view.setBackgroundResource(R.drawable.dark_violet);break;
            case 6: view.setBackgroundResource(R.drawable.bluee);break;
            case 7: view.setBackgroundResource(R.drawable.yellow);break;
            case 8: view.setBackgroundResource(R.drawable.solid);break;
            case 9: view.setBackgroundResource(R.drawable.dark_green);break;
            case 10: view.setBackground(null);break;
            case 11: view.setBackgroundResource(R.drawable.orange);break;
            case 15: Bitmap image = StringToBitMap(shrep.getString(IMAGE_PATH,""));
                Log.d("TAG", "retrieved string" + shrep.getString(IMAGE_PATH, ""));
                view.setBackground(new BitmapDrawable(getResources(), image));break;
        }
        if ((shrep.getString(IMAGE_PATH,"") == null)) view.setBackground(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        this.view = view;
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.post_swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.post_recycler_view);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSTS)) {
            postsArrayList = savedInstanceState.getParcelableArrayList(POSTS);
        } else {

            postsArrayList = new ArrayList<>();
        }


        linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new HomeScreenAdapter(getContext(), postsArrayList);
        adapter.setHomeScreenAdapterListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new ItemDivider(getContext()));
        swipeRefreshLayout.setOnRefreshListener(refreshListener);


        shrep = getContext().getSharedPreferences(OptionsActivity.MYPRE, Context.MODE_PRIVATE);
        switch (shrep.getInt(WALLPAPER_FLAGS, 0)){
            case 1: view.setBackgroundResource(R.drawable.dark_brown);break;
            case 2: view.setBackgroundResource(R.drawable.dark_red);break;
            case 3: view.setBackgroundResource(R.drawable.darkblue_background);break;
            case 4: view.setBackgroundResource(R.drawable.light_green);break;
            case 5: view.setBackgroundResource(R.drawable.dark_violet);break;
            case 6: view.setBackgroundResource(R.drawable.bluee);break;
            case 7: view.setBackgroundResource(R.drawable.yellow);break;
            case 8: view.setBackgroundResource(R.drawable.solid);break;
            case 9: view.setBackgroundResource(R.drawable.dark_green);break;
            case 10: view.setBackground(null);break;
            case 11: view.setBackgroundResource(R.drawable.orange);break;
            case 15: Bitmap image = StringToBitMap(shrep.getString(IMAGE_PATH,""));
                Log.d("TAG", "retrieved string" + shrep.getString(IMAGE_PATH, ""));
                view.setBackground(new BitmapDrawable(getResources(), image));break;
        }
        if ((shrep.getString(IMAGE_PATH,"") == null)) view.setBackground(null);




        errorImageView = (ImageView) view.findViewById(R.id.error_image_view);
        errorTextView = (TextView) view.findViewById(R.id.error_text_view);
        loadingTextView = (TextView) view.findViewById(R.id.loading_text_view);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_progress_bar);




        return view;
    }

    private void createLoader() {
        if (getLoaderManager() != null) {
            getLoaderManager().restartLoader(POST_LOADER_ID, null, this);
        } else {
            getLoaderManager().initLoader(POST_LOADER_ID, null, this);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkUtils.isOnline(getContext())) {

            if(!isLoaded){

                createLoader();
            }
        } else {
            //if the device is not online show the error indicatiors
            HomeScreenFragmentHelper.indicatorsAppear(new View[]{errorTextView, errorImageView}, true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(POSTS, postsArrayList);

        super.onSaveInstanceState(outState);

    }

    //swipe to refresh
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshRecyclerView();
        }
    };

    private void refreshRecyclerView() {
        // addNewItemsToList(10);
        onItemsLoaded();
        Snackbar.make(view.findViewById(R.id.post_root), R.string.refreshing, Snackbar.LENGTH_SHORT).show();
    }

    private void addNewItemsToList(BlogPosts[] arrayList) {
        for (BlogPosts post : arrayList) {
            postsArrayList.add(post);
            Log.i(TAG, "addNewItemsToList: -->" + post.getId());
        }


    }

    private void onItemsLoaded() {
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    //checks for network connectivity //TODO Implemnt the receiver
    public class ConnectivityChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isOnline(getContext())) {
               /* if (state == null){
                    getData();
                    Snackbar.make(findViewById(R.id.root_main), R.string.is_connected_text, Snackbar.LENGTH_SHORT).show();
                }
                showRecyclerView();
            } else {
                Snackbar.make(findViewById(R.id.root_main), R.string.is_not_connected_text, Snackbar.LENGTH_SHORT).show();
                if (!dataLoaded){
                    displayError();
                }*/
            }
        }
    }

    @Override
    public void onItemClicked(int position) {
        homeScreenFragmentListener.onItemClicked(position, postsArrayList.get(position));
    }

    @Override
    public void onAuthorAvatarClicked(int position) {
        homeScreenFragmentListener.onAuthorAvatarClicked(position);

    }

    //loader methods
    @Override
    public Loader<BlogPosts[]> onCreateLoader(int id, Bundle args) {
        HomeScreenFragmentHelper.indicatorsAppear(new View[]{progressBar, loadingTextView}, true);
        HomeScreenFragmentHelper.indicatorsAppear(new View[]{recyclerView}, false);
        switch (id) {
            case POST_LOADER_ID:
                return new HomeScreenFragmentHelper.HomeAsyncTaskLoader(getContext());
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<BlogPosts[]> loader, BlogPosts[] data) {
        if (data != null) {
            addNewItemsToList(data);
            isLoaded = true;
        } else {
            Toast.makeText(getContext(), "Error occured", Toast.LENGTH_LONG).show();
            return;
        }
        HomeScreenFragmentHelper.indicatorsAppear(new View[]{progressBar, loadingTextView}, false);
        HomeScreenFragmentHelper.indicatorsAppear(new View[]{recyclerView}, true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<BlogPosts[]> loader) {

    }


}
