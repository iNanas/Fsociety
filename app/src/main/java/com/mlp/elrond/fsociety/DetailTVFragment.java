package com.mlp.elrond.fsociety;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailTVFragment  extends Fragment {
    private static final String OBJECT = "tv_show_id";
    private final static String BG_PATH = "http://image.tmdb.org/t/p/w500/";

    private String mTYid;
    private TvShows mTvShows;

    private TextView mShowName;
    private ImageView mShowBG;
    private TextView mShowOverview;
    private TextView mShowFAD;
    private TextView mShowRatings;
    private Button mYTButton;
    private Button mSaveToSP;

    public static DetailTVFragment newInstance( TvShows show){
        Bundle args = new Bundle();
        args.putParcelable(OBJECT, show);
        DetailTVFragment fragment = new DetailTVFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvShows = getArguments().getParcelable(OBJECT);
        new GetTrailerAsyncTask().execute();
        //title_test = getActivity().getIntent().getStringExtra(DetailTVActivity.SHOW_TITLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_tv, container, false);

        mShowName = (TextView) v.findViewById(R.id.name_label);
        mShowName.setText(mTvShows.getName());

        mShowBG = (ImageView) v.findViewById(R.id.bg_image);
        Picasso.with(getActivity())
                .load(BG_PATH + mTvShows.getBackgroundUrl())
                .placeholder(R.drawable.background)
                .into(mShowBG);

        mShowOverview = (TextView) v.findViewById(R.id.review_text);
        mShowOverview.setMovementMethod(new ScrollingMovementMethod());
        mShowOverview.setText(mTvShows.getOverview());

        mShowFAD = (TextView) v.findViewById(R.id.date_text);
        mShowFAD.setText("On the air since: " + mTvShows.getFAD());

        mShowRatings = (TextView) v.findViewById(R.id.rating_text);
        mShowRatings.setText(mTvShows.getRatings() + "/10");

        mYTButton = (Button) v.findViewById(R.id.YTButton);
        mYTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTYid.matches(".+")){
                    openInYT(mTYid);
                }else{
                    Toast.makeText(getActivity(), "Trailer is not available :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSaveToSP = (Button) v.findViewById(R.id.saveToSP);
        checkWatchList();
        mSaveToSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSaveToSP.getText().equals("Delete from list?") || mSaveToSP.getText().equals("Added! Delete it?")){
                    //Toast.makeText(getActivity(), "Already in your FUCKING LIST!", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(getActivity()).setTitle("Confirmation").setMessage("Delete this TV show from WatchList?")
                            .setPositiveButton("Yep!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new ManageSharedPref().removeTvShow(getActivity(), mTvShows);
                                    mSaveToSP.setText("Add to WatchList");
                                    mSaveToSP.setBackgroundColor(Color.parseColor("#607d8b"));
                                }
                            })
                            .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else{
                    new ManageSharedPref().addTvShow(getActivity(), mTvShows);
                    mSaveToSP.setText("Added! Delete it?");
                    mSaveToSP.setBackgroundColor(Color.parseColor("#37474f"));
                }

            }
        });

        return v;
    }

    public void openInYT(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    private void checkWatchList(){
        ArrayList<TvShows> temp_show_list = new ManageSharedPref().loadTvShows(getActivity());
        for(int i = 0; i < temp_show_list.size(); i++){
            if(temp_show_list.get(i).getShowId().equals(mTvShows.getShowId())){
                mSaveToSP.setText("Delete from list?");
                mSaveToSP.setBackgroundColor(Color.parseColor("#37474f"));
                break;
            }
        }
    }

    private class GetTrailerAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return new TMDB().parseYTid(mTvShows.getShowId());
        }

        @Override
        protected void onPostExecute(String id) {
            mTYid = id;
        }
    }


}