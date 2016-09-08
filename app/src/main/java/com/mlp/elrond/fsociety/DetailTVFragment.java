package com.mlp.elrond.fsociety;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailTVFragment extends Fragment {
    private static final String OBJECT = "tv_show_id";
    private final static String BG_PATH = "http://image.tmdb.org/t/p/w500/";
    private static final String ADD_DATE = "com.mlp.elrond.fsociety.add_date";

    private String mTYid;
    private static TvShows mTvShows;
    private Button mSaveToShrPrfButton;
    private static Button mSetDateButton;

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
        TextView mShowName;
        ImageView mShowBG;
        TextView mShowOverview;
        TextView mShowFirstAirDate;
        TextView mShowRatings;
        Button mYTButton;
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

        mShowFirstAirDate = (TextView) v.findViewById(R.id.date_text);
        mShowFirstAirDate.setText(String.format(getResources().getString(R.string.on_the_air_since),
                mTvShows.getFirstAirDate()));

        mShowRatings = (TextView) v.findViewById(R.id.rating_text);
        mShowRatings.setText(String.format(getResources().getString(R.string.ratings_max),
                mTvShows.getRatings()));

        mYTButton = (Button) v.findViewById(R.id.YTButton);
        mYTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTYid.matches(".+")){
                    openInYouTube(mTYid);
                }else{
                    Toast.makeText(getActivity(), R.string.trailer_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSaveToShrPrfButton = (Button) v.findViewById(R.id.saveToSP);
        mSaveToShrPrfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShowToWatchList();
            }
        });

        mSetDateButton = (Button) v.findViewById(R.id.onAirDate);
        if(mTvShows.getOnAirDate() != null){
            mSetDateButton.setText(DateFormat.format("EEE, MMM d, h:mm a", mTvShows.getOnAirDate()));
        }
        mSetDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                SetOnAirDate calendar = new SetOnAirDate();
                calendar.show(manager, ADD_DATE);
            }
        });

        checkWatchList();
        return v;
    }

    private void addShowToWatchList() {
        if(mSaveToShrPrfButton.getText().equals("Delete from list?")
                || mSaveToShrPrfButton.getText().equals("Added! Delete it?")){
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_confirmation)
                    .setMessage(R.string.dialog_delete_message)
                    .setPositiveButton(R.string.dialog_positive_answer, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new ManageSharedPref().removeTvShow(getActivity(), mTvShows);
                            mSaveToShrPrfButton.setText(R.string.add_to_watch_list);
                            mSaveToShrPrfButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                                    R.color.colorBlueGray500));
                            mSetDateButton.setVisibility(View.INVISIBLE);
                        }
                    })
                    .setNegativeButton(R.string.dialog_negative_answer, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }else{
            new ManageSharedPref().addTvShow(getActivity(), mTvShows);
            mSaveToShrPrfButton.setText(R.string.delete_after_adding);
            mSaveToShrPrfButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.colorBlueGray800));
            mSetDateButton.setVisibility(View.VISIBLE);
        }
    }

    private void checkWatchList(){
        ArrayList<TvShows> temp_show_list = new ManageSharedPref().loadTvShows(getActivity());
        for(int i = 0; i < temp_show_list.size(); i++){
            if(temp_show_list.get(i).getShowId().equals(mTvShows.getShowId())){
                mSaveToShrPrfButton.setText(R.string.button_delete_text);
                mSaveToShrPrfButton.setBackgroundColor(ContextCompat.getColor(getContext(),
                        R.color.colorBlueGray800));
                if(temp_show_list.get(i).getOnAirDate() != null){
                    mSetDateButton.setText(DateFormat.format("EEE, MMM d, h:mm a",
                            temp_show_list.get(i).getOnAirDate()));
                }
                mSetDateButton.setVisibility(View.VISIBLE);
                break;
            }
        }
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

    public static class SetOnAirDate extends DialogFragment {
        private Date mAirDate;
        private DatePicker mDatePicker;
        private TimePicker mTimePicker;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);

            mDatePicker = (DatePicker) v.findViewById(R.id.calendar_date_picker);
            mTimePicker = (TimePicker) v.findViewById(R.id.calendar_timePicker);

            return new AlertDialog.Builder(getActivity())
                    .setView(v)
                    .setTitle(R.string.set_air_date)
                    .setPositiveButton(R.string.set_it, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Calendar set_date = Calendar.getInstance();
                            set_date.set(Calendar.YEAR, mDatePicker.getYear());
                            set_date.set(Calendar.MONTH, mDatePicker.getMonth());
                            set_date.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                            set_date.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                            set_date.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                            mAirDate = set_date.getTime();
                            mTvShows.setOnAirDate(mAirDate);
                            new ManageSharedPref().removeTvShow(getActivity(), mTvShows);
                            new ManageSharedPref().addTvShow(getActivity(), mTvShows);
                            mSetDateButton.setText(DateFormat.format("EEE, MMM d, h:mm a",
                                    mTvShows.getOnAirDate()));
                        }
                    })
                    .setNegativeButton(R.string.dialog_negative_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
        }
    }
}
