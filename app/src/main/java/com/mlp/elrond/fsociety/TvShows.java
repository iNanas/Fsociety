package com.mlp.elrond.fsociety;

import android.os.Parcel;
import android.os.Parcelable;

public class TvShows implements Parcelable {
    private String mName;
    private String mPosterUrl;
    private String mBackgroundUrl;
    private String mOverview;
    private String mFAD;
    private String mRatings;
    private String mShowId;

    public TvShows() {

    }

    @Override
    public String toString() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        mPosterUrl = posterUrl;
    }

    public String getBackgroundUrl() {
        return mBackgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        mBackgroundUrl = backgroundUrl;
    }

    public String getFAD() {
        return mFAD;
    }

    public void setFAD(String FAD) {
        mFAD = FAD;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getRatings() {
        return mRatings;
    }

    public void setRatings(String ratings) {
        mRatings = ratings;
    }

    public String getShowId() {
        return mShowId;
    }

    public void setShowId(String showId) {
        mShowId = showId;
    }

    protected TvShows(Parcel in) {
        mName = in.readString();
        mPosterUrl = in.readString();
        mBackgroundUrl = in.readString();
        mOverview = in.readString();
        mFAD = in.readString();
        mRatings = in.readString();
        mShowId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPosterUrl);
        dest.writeString(mBackgroundUrl);
        dest.writeString(mOverview);
        dest.writeString(mFAD);
        dest.writeString(mRatings);
        dest.writeString(mShowId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TvShows> CREATOR = new Parcelable.Creator<TvShows>() {
        @Override
        public TvShows createFromParcel(Parcel in) {
            return new TvShows(in);
        }

        @Override
        public TvShows[] newArray(int size) {
            return new TvShows[size];
        }
    };
}