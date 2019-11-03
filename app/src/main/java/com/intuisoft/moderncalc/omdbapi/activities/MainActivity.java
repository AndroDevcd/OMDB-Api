package com.intuisoft.moderncalc.omdbapi.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.intuisoft.moderncalc.omdbapi.R;
import com.intuisoft.moderncalc.omdbapi.adapters.RecyclerViewAdapter;
import com.intuisoft.moderncalc.omdbapi.data.SearchResponse;
import com.intuisoft.moderncalc.omdbapi.data.SearchResult;
import com.intuisoft.moderncalc.omdbapi.util.CustomEditText;
import com.intuisoft.moderncalc.omdbapi.util.Preferences;
import com.intuisoft.moderncalc.omdbapi.util.RetrofitManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.intuisoft.moderncalc.omdbapi.Omdb.API_KEY;
import static com.intuisoft.moderncalc.omdbapi.Omdb.getContext;

public class MainActivity extends AppCompatActivity {
    @BindView (R.id.filterBtn)
    ImageView filterBtn;
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.filterText)
    TextView filterText;
    @BindView(R.id.noResultsView)
    ConstraintLayout noResultsLayout;
    @BindView(R.id.noResultsImageView)
    ImageView noResultsImgView;
    @BindView(R.id.noResultsMessage)
    TextView noResultsMsg;
    @BindView(R.id.resultsContainer)
    ScrollView resultsContainer;
    @BindView(R.id.resultsView)
    RecyclerView resultsView;
    AlertDialog filterDialog;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard();
                    search();
                    return true;
                }
                return false;
            }

            public void hideKeyboard(){
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
        });

        resultsView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        resultsView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerViewAdapter(new ArrayList<>());
        resultsView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateContents();

    }

    void updateContents() {
        updateFiltersText();
        if(!searchEditText.getText().toString().isEmpty())
            search();
    }

    public void onFilterClicked(View filterView) {
        showFilterDialog();
    }

    void search() {
        if(Preferences.hasMoviesFilter() || Preferences.hasTvSeriesFilter()) {
            Call<SearchResponse> call = RetrofitManager.getClient().performFilteredSearch(searchEditText.getText().toString(),
                    Preferences.hasMoviesFilter() ? "movie" : "series", API_KEY);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if(response.body() != null && response.body().Search != null) {
                        onResultsFound();
                        mAdapter = new RecyclerViewAdapter(response.body().Search);
                        resultsView.setAdapter(mAdapter);

                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(MainActivity.this, R.anim.layout_anim);
                        resultsView.setLayoutAnimation(animation);
                    } else {
                        onNoResultsFound();
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    checkNetwork();
                }
            });
        } else {
            Call<SearchResponse> call = RetrofitManager.getClient().performSearch(searchEditText.getText().toString(), API_KEY);
            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if(response.body() != null && response.body().Search != null) {
                        onResultsFound();
                        mAdapter = new RecyclerViewAdapter(response.body().Search);
                        resultsView.setAdapter(mAdapter);

                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(MainActivity.this, R.anim.layout_anim);
                        resultsView.setLayoutAnimation(animation);
                    } else {
                        onNoResultsFound();
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    checkNetwork();
                }
            });
        }
    }

    void checkNetwork() {
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if(netInfo == null) {
            onNoNetwork();
        }
    }

    void onResultsFound() {
        noResultsLayout.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.VISIBLE);
    }

    void onNoResultsFound() {
        noResultsLayout.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.GONE);
        noResultsImgView.setImageDrawable(getDrawable(R.drawable.ico_no_results));
        noResultsMsg.setText(getString(R.string.invalid_search_message));
    }

    void onNoNetwork() {
        noResultsLayout.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.GONE);
        noResultsImgView.setImageDrawable(getDrawable(R.drawable.no_network_img));
        noResultsMsg.setText(getString(R.string.no_network_message));
    }

    private void showFilterDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.filter_dialog, viewGroup, false);

        CheckBox moviesCheckbox = dialogView.findViewById(R.id.movies_checkbox);
        CheckBox tvSeriesCheckbox = dialogView.findViewById(R.id.tv_series_checkbox);

        moviesCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    tvSeriesCheckbox.setChecked(false);
            }
        });

        tvSeriesCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    moviesCheckbox.setChecked(false);
            }
        });


        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.cancel();
            }
        });
        dialogView.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.setMovieFilterEnabled(moviesCheckbox.isChecked());
                Preferences.setTvSeriesFilterEnabled(tvSeriesCheckbox.isChecked());
                updateFiltersText();
                filterDialog.cancel();
                search();
            }
        });


        moviesCheckbox.setChecked(Preferences.hasMoviesFilter());
        tvSeriesCheckbox.setChecked(Preferences.hasTvSeriesFilter());


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        filterDialog = builder.create();
        filterDialog.show();
    }

    void updateFiltersText() {
        String filters="";

        if(Preferences.hasTvSeriesFilter() || Preferences.hasMoviesFilter()) {
            if(Preferences.hasTvSeriesFilter())
                filters = "Tv Series";
            else if(Preferences.hasMoviesFilter()) {
                filters = "Movies";
            }
        } else
            filters = "None";
        filterText.setText(filters);
    }
}
