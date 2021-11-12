package in.sunilpaulmathew.covidstats.demo;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 25, 2021
 */
public class RecyclerViewItem implements Serializable {

    private final int mTotalCases, mNewCases, mTotalDeaths, mNewDeaths;
    private final String mContinent, mCountry, mLastUpdated;

    public RecyclerViewItem(int totalCases, int newCases, int totalDeaths, int newDeaths, String continent,
                            String country, String lastUpdated) {
        this.mTotalCases = totalCases;
        this.mNewCases = newCases;
        this.mTotalDeaths = totalDeaths;
        this.mNewDeaths = newDeaths;
        this.mContinent = continent;
        this.mCountry = country;
        this.mLastUpdated = lastUpdated;
    }

    public int getTotalCases() {
        return mTotalCases;
    }

    public int getNewCases() {
        return mNewCases;
    }

    public int getTotalDeaths() {
        return mTotalDeaths;
    }

    public int getNewDeaths() {
        return mNewDeaths;
    }

    public String getContinent() {
        return mContinent;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getLastUpdated() {
        return mLastUpdated;
    }

}