package in.sunilpaulmathew.covidstats.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.covidstats.CovidStats;
import in.sunilpaulmathew.covidstats.Executor;

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText mSearchWord;
    private boolean mExit;
    private final Handler mHandler = new Handler();
    private static final List<RecyclerViewItem> mData = new ArrayList<>();
    private MaterialTextView mTitle;
    private static String mSearchText;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchWord = findViewById(R.id.search_word);
        AppCompatImageButton mSearchButton = findViewById(R.id.search_button);
        AppCompatImageButton mInfoButton = findViewById(R.id.info_button);
        mTitle = findViewById(R.id.title);
        ProgressBar mProgress = findViewById(R.id.progress);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, getOrientation(this) == Configuration
                .ORIENTATION_LANDSCAPE ? 2 : 1));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mSearchButton.setOnClickListener(v -> {
            if (mSearchWord.getVisibility() == View.VISIBLE) {
                mSearchWord.setVisibility(View.GONE);
                mTitle.setVisibility(View.VISIBLE);
                mSearchWord.setText(null);
                reloadUI(mRecyclerView, this);
                mSearchText = null;
                toggleKeyboard(0);
            } else {
                mSearchWord.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.GONE);
                toggleKeyboard(1);
            }
        });

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchText = s.toString().toLowerCase();
                reloadUI(mRecyclerView, MainActivity.this);
            }
        });

        mInfoButton.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.drawable.ic_info)
                        .setTitle(getString(R.string.app_name) + "\n" + BuildConfig.VERSION_NAME)
                        .setMessage(getString(R.string.app_name_summary)
                                + "\n\nCopyright: © 2021-2022, sunilpaulmathew"
                                + "\n\n" + getString(R.string.last_updated, CovidStats.getLastUpdatedDate(CovidStats.getCountries().get(0))))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.dismiss), (dialogInterface, i) -> {
                        })
                        .setPositiveButton(getString(R.string.source_code), (dialogInterface, i) -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://github.com/sunilpaulmathew/CLS"));
                                startActivity(intent);
                            } catch (ActivityNotFoundException ignored) {
                            }
                        }).show()
        );

        new Executor() {

            @Override
            public void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                new CovidStats().initialize();
                mData.add( new RecyclerViewItem(CovidStats.getGlobalCases(), CovidStats.getNewGlobalCases(),
                        CovidStats.getGlobalDeaths(), CovidStats.getNewGlobalDeaths(), null,
                        getString(R.string.global), CovidStats.getCountryUpdatedDate(MainActivity.this)));
                for (String country : CovidStats.getCountries()) {
                    mData.add(new RecyclerViewItem(CovidStats.getTotalCases(country), CovidStats.getNewCases(country),
                            CovidStats.getTotalDeaths(country), CovidStats.getNewDeaths(country), CovidStats.getContinent(country),
                            CovidStats.getLocation(country), CovidStats.getLastUpdatedDate(country)));
                }
            }

            @Override
            public void onPostExecute() {
                if (CovidStats.isDataLoaded()) {
                    mRecyclerView.setAdapter(new RecyclerViewAdapter(mData));
                    mSearchButton.setVisibility(View.VISIBLE);
                    mInfoButton.setVisibility(View.VISIBLE);
                } else {
                    snackBar(getString(R.string.network_failed), MainActivity.this);
                }
                mProgress.setVisibility(View.GONE);
            }
        }.execute();
    }

    private static void snackBar(String message, Activity activity) {
        Snackbar snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.dismiss, v -> snackBar.dismiss());
        snackBar.show();
    }

    private static int getOrientation(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode() ?
                Configuration.ORIENTATION_PORTRAIT : activity.getResources().getConfiguration().orientation;
    }

    private void toggleKeyboard(int mode) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (mode == 1) {
            if (mSearchWord.requestFocus()) {
                imm.showSoftInput(mSearchWord, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            imm.hideSoftInputFromWindow(mSearchWord.getWindowToken(), 0);
        }
    }

    private static void reloadUI(RecyclerView recyclerView, Activity activity) {
        if (CovidStats.isDataLoaded()) {
            List<RecyclerViewItem> mItems = new ArrayList<>();
            for (RecyclerViewItem item : mData) {
                if (mSearchText != null) {
                    if (item.getCountry().toLowerCase().contains(mSearchText.toLowerCase())) {
                        mItems.add(item);
                    }
                } else {
                    mItems.add(item);
                }
            }
            recyclerView.setAdapter(new RecyclerViewAdapter(mItems));
        } else {
            snackBar(activity.getString(R.string.network_failed), activity);
        }
    }

    public void onBackPressed() {
        if (mSearchText != null) {
            mSearchWord.setText(null);
            mSearchText = null;
            return;
        }
        if (mSearchWord.getVisibility() == View.VISIBLE) {
            mSearchWord.setVisibility(View.GONE);
            mTitle.setVisibility(View.VISIBLE);
        } else if (mExit) {
            mExit = false;
            finish();
        } else {
            snackBar("Press the 'BACK' button again to Exit!", this);
            mExit = true;
            mHandler.postDelayed(() -> mExit = false, 2000);
        }
    }

}