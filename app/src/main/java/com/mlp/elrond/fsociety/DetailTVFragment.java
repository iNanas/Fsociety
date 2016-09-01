package com.mlp.elrond.fsociety;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    private Button mSaveToShrPrfButton;
    private Button mSetDateButton;

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
                    openInYouTube(mTYid);
                }else{
                    Toast.makeText(getActivity(), "Trailer is not available :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSaveToShrPrfButton = (Button) v.findViewById(R.id.saveToSP);
        mSaveToShrPrfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSaveToShrPrfButton.getText().equals("Delete from list?")
                        || mSaveToShrPrfButton.getText().equals("Added! Delete it?")){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Confirmation")
                            .setMessage("Delete this TV show from WatchList?")
                            .setPositiveButton("Yep!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new ManageSharedPref().removeTvShow(getActivity(), mTvShows);
                                    mSaveToShrPrfButton.setText("Add to WatchList");
                                    mSaveToShrPrfButton.setBackgroundColor(Color.parseColor("#607d8b"));
                                    mSetDateButton.setVisibility(View.INVISIBLE);
                                    //raiseNotification();
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
                    mSaveToShrPrfButton.setText("Added! Delete it?");
                    mSaveToShrPrfButton.setBackgroundColor(Color.parseColor("#37474f"));
                    mSetDateButton.setVisibility(View.VISIBLE);
                }

            }
        });

        mSetDateButton = (Button) v.findViewById(R.id.onAirDate);
        if(mTvShows.getOnAirDate() != null){
            mSetDateButton.setText(DateFormat.format("EEEE, MMMM d, yyyy", mTvShows.getOnAirDate()));
        }
        mSetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager manager = getFragmentManager();
                SetOnAirDate calendar = new SetOnAirDate();
                calendar.show(manager, "MyDate");
            }
        });

        checkWatchList();
        return v;
    }

    public void openInYouTube(String id){
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
                mSaveToShrPrfButton.setText("Delete from list?");
                mSaveToShrPrfButton.setBackgroundColor(Color.parseColor("#37474f"));
                if(temp_show_list.get(i).getOnAirDate() != null){
                    mSetDateButton.setText(DateFormat.format("EEEE, MMMM d, yyyy",
                            temp_show_list.get(i).getOnAirDate()));
                }
                mSetDateButton.setVisibility(View.VISIBLE);
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

    private class SetOnAirDate  extends DialogFragment {
        private Date mAirDate;
        private DatePicker mDatePicker;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);

            mDatePicker = (DatePicker) v.findViewById(R.id.calendar_date_picker);
            mDatePicker.init(year, month, day, null);

            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle("Set On the Air Date")
                    .setPositiveButton("Do it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAirDate = new GregorianCalendar(mDatePicker.getYear(),
                                    mDatePicker.getMonth(), mDatePicker.getDayOfMonth()).getTime();
                            mTvShows.setOnAirDate(mAirDate);
                            new ManageSharedPref().removeTvShow(getActivity(), mTvShows);
                            new ManageSharedPref().addTvShow(getActivity(), mTvShows);
                            mSetDateButton.setText(DateFormat.format("EEEE, MMMM d, yyyy",
                                    mTvShows.getOnAirDate()));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
        }
    }
}
