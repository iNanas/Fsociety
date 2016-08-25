package com.mlp.elrond.fsociety;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailTVActivity extends AppCompatActivity {
    private static final String SHOW_OBJECT= "com.elrond.mlp.show_object";

    public Fragment createFragment(){
        TvShows tvShows = getIntent().getParcelableExtra(SHOW_OBJECT);
        return DetailTVFragment.newInstance( tvShows);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tv);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_detail_container);
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_detail_container, fragment).commit();

        }
    }

    public static Intent newIntent(Context packageContext, TvShows tvShows){
        Intent intent = new Intent(packageContext, DetailTVActivity.class);
        intent.putExtra(SHOW_OBJECT, tvShows);
        return intent;
    }
}
