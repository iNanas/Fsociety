package com.mlp.elrond.fsociety;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TVFragment extends Fragment {

    private RecyclerView mTvRv;
    private List<TvShows> mTvShows;
    private List<TvShows> mWatchListShows = new ArrayList<>();
    private static final String IMG_PATH = "http://api.themoviedb.org/3/tv/";

    public static TVFragment newInstance() {
        return new TVFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(ManageSharedPref.getShowFilter(getActivity()) == null){
            startGetShowAsyncTask("on_the_air");
            Toast.makeText(getActivity(), "On the Air shows", Toast.LENGTH_LONG).show();
        }else{
            startGetShowAsyncTask(ManageSharedPref.getShowFilter(getActivity()));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_tv, container, false);
        mTvRv = ((RecyclerView)v.findViewById(R.id.fragment_tv_RV));
        mTvRv.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.gallery_columns)));

        return v;
    }

    private void setupAdapter(List<TvShows> tvshows) {
        if (isAdded()) {
            mTvRv.setAdapter(new TvShowsAdapter(tvshows));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tv_show_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_on_the_air:
                ManageSharedPref.setShowFilter(getActivity(), "on_the_air");
                startGetShowAsyncTask("on_the_air");
                return true;
            case R.id.menu_popular:
                ManageSharedPref.setShowFilter(getActivity(), "popular");
                startGetShowAsyncTask("popular");
                return true;
            case R.id.menu_top_rated:
                ManageSharedPref.setShowFilter(getActivity(), "top_rated");
                startGetShowAsyncTask("top_rated");
                return true;
            case R.id.menu_watch_list:
                if(new ManageSharedPref().loadTvShows(getActivity()).size() != 0){
                    mWatchListShows = new ManageSharedPref().loadTvShows(getActivity());
                    setupAdapter(mWatchListShows);
                }else{
                    Toast.makeText(getActivity(), "WatchList is empty", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class PostersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mPosterImageView;
        private TvShows mTvShow;

        public PostersViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.img_view);
        }

        public void bindImage(TvShows show) {
            mTvShow = show;
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w185/" + mTvShow.getPosterUrl())
                    .placeholder(R.drawable.background)
                    .into(mPosterImageView);
        }


        @Override
        public void onClick(View view) {
            startActivity(DetailTVActivity.newIntent(getActivity(), mTvShow));
        }
    }

    private class TvShowsAdapter extends RecyclerView.Adapter<PostersViewHolder> {
        private List<TvShows> mShowList;

        public TvShowsAdapter(List<TvShows> showList) {
            mShowList = showList;
        }

        @Override
        public PostersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PostersViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.display_image, parent, false));
        }

        @Override
        public void onBindViewHolder(PostersViewHolder holder, int position) {
            holder.bindImage(mShowList.get(position));
        }

        @Override
        public int getItemCount() {
            return mShowList.size();
        }
    }

    private class GetShowAsyncTask extends AsyncTask<String,Void,List<TvShows>>{

        @Override
        protected List<TvShows> doInBackground(String... str) {
            return new TMDB().getJson(str[0]);
        }

        @Override
        protected void onPostExecute(List<TvShows> tvShows) {
            mTvShows = tvShows;
            setupAdapter(mTvShows);
        }
    }

    private void startGetShowAsyncTask(String filter){
        new GetShowAsyncTask().execute(IMG_PATH + filter);
    }


}


