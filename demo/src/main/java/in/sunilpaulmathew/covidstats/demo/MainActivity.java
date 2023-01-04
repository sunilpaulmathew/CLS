package in.sunilpaulmathew.covidstats.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.covidstats.CovidStats;
import in.sunilpaulmathew.covidstats.Executor;

public class MainActivity extends AppCompatActivity {

    private AppCompatEditText mSearchWord;
    private boolean mExit;
    private final CovidStats mCovidStats = new CovidStats();
    private final Handler mHandler = new Handler();
    private static final List<RecyclerViewItem> mData = new ArrayList<>();
    private MaterialTextView mTitle;
    private static String mSearchText;

    @SuppressLint("SetTextI18n")
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
                reloadUI(mRecyclerView);
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
                reloadUI(mRecyclerView);
            }
        });

        mInfoButton.setOnClickListener(v -> {
            LayoutInflater mLayoutInflator = LayoutInflater.from(this);
            View aboutLayout = mLayoutInflator.inflate(R.layout.layout_about, null);
            MaterialTextView mAppTile = aboutLayout.findViewById(R.id.app_title);
            MaterialTextView mSource = aboutLayout.findViewById(R.id.source);
            MaterialTextView mDataCredits = aboutLayout.findViewById(R.id.credits_data);
            MaterialTextView mIconCredits = aboutLayout.findViewById(R.id.credits_icon);
            MaterialTextView mUpdated = aboutLayout.findViewById(R.id.updated);

            mAppTile.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
            mSource.setText(getString(R.string.source_code, "https://github.com/sunilpaulmathew/CLS"));
            mDataCredits.setText(getString(R.string.credits_data, "https://github.com/owid/covid-19-data/tree/master/public/data"));
            mIconCredits.setText(getString(R.string.credits_icon, "https://www.un.org/en/file/45419"));

            if (mCovidStats.isDataLoaded()) {
                mUpdated.setText(getString(R.string.last_updated, mCovidStats.getLastUpdatedDate(mCovidStats.getCountries().get(0))));
            } else {
                mUpdated.setVisibility(View.GONE);
            }

            new MaterialAlertDialogBuilder(this)
                    .setIcon(R.drawable.ic_info).setView(aboutLayout)
                    .setCancelable(false)
                    .setNegativeButton(R.string.share, (dialogInterface, i) ->
                            new Executor() {

                                @Override
                                public void onPreExecute() {
                                    mProgress.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void doInBackground() {
                                    try {
                                        FileInputStream inputStream = new FileInputStream(getPackageManager().getApplicationInfo(getPackageName(),
                                                PackageManager.GET_META_DATA).sourceDir);
                                        FileOutputStream outputStream = new FileOutputStream(new File(getExternalCacheDir(), "CLS_" +
                                                BuildConfig.VERSION_NAME + ".apk"));

                                        byte[] buf = new byte[1024 * 1024];
                                        int len;
                                        while ((len = inputStream.read(buf)) > 0) {
                                            outputStream.write(buf, 0, len);
                                        }

                                        inputStream.close();
                                        outputStream.close();
                                    } catch (IOException | PackageManager.NameNotFoundException ignored) {
                                    }
                                }

                                @Override
                                public void onPostExecute() {
                                    mProgress.setVisibility(View.GONE);
                                    Intent shareApp = new Intent(Intent.ACTION_SEND);
                                    shareApp.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
                                    shareApp.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",
                                            new File(getExternalCacheDir(), "CLS_" + BuildConfig.VERSION_NAME + ".apk")));
                                    shareApp.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                                    shareApp.setType("application/vnd.android.package-archive");
                                    shareApp.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    Intent shareIntent = Intent.createChooser(shareApp, getString(R.string.share_with));
                                    startActivity(shareIntent);
                                }
                            }.execute()
                    )
                    .setPositiveButton(getString(R.string.dismiss), (dialogInterface, i) -> {
                    }).show();
        });

        new Executor() {

            @Override
            public void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void doInBackground() {
                mCovidStats.initialize();
                mData.add( new RecyclerViewItem(mCovidStats.getGlobalCases(), mCovidStats.getNewGlobalCases(),
                        mCovidStats.getGlobalDeaths(), mCovidStats.getNewGlobalDeaths(), null,
                        getString(R.string.global), mCovidStats.getCountryUpdatedDate(MainActivity.this)));
                for (String country : mCovidStats.getCountries()) {
                    mData.add(new RecyclerViewItem(mCovidStats.getTotalCases(country), mCovidStats.getNewCases(country),
                            mCovidStats.getTotalDeaths(country), mCovidStats.getNewDeaths(country), mCovidStats.getContinent(country),
                            mCovidStats.getLocation(country), mCovidStats.getLastUpdatedDate(country)));
                }
            }

            @Override
            public void onPostExecute() {
                if (mCovidStats.isDataLoaded()) {
                    mRecyclerView.setAdapter(new RecyclerViewAdapter(mData));
                    mSearchButton.setVisibility(View.VISIBLE);
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

    private void reloadUI(RecyclerView recyclerView) {
        if (mCovidStats.isDataLoaded()) {
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
            snackBar(getString(R.string.network_failed), this);
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