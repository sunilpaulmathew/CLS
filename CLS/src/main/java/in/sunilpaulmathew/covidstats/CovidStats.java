package in.sunilpaulmathew.covidstats;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 25, 2021
 */
public class CovidStats {

    private static JSONObject mJSONObject = null;

    public CovidStats() {
    }

    public boolean isDataLoaded() {
        return mJSONObject != null;
    }

    private JSONObject getCountry(String country) {
        if (mJSONObject == null) return null;
        try {
            return new JSONObject(getJSONObject().getString(country));
        } catch (JSONException ignored) {
        }
        return null;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    public int getTotalCountryCases(Context context) {
        return getTotalCases(context.getResources().getConfiguration().locale.getISO3Country());
    }

    public int getTotalCountryDeaths(Context context) {
        return getTotalDeaths(context.getResources().getConfiguration().locale.getISO3Country());
    }

    public int getCountryNewCases(Context context) {
        return getNewCases(context.getResources().getConfiguration().locale.getISO3Country());
    }

    public int getCountryNewDeaths(Context context) {
        return getNewDeaths(context.getResources().getConfiguration().locale.getISO3Country());
    }

    public String getCountryContinent(Context context) {
        return getContinent(context.getResources().getConfiguration().locale.getISO3Country());
    }

    public String getCountryName(Context context) {
        return getLocation(context.getResources().getConfiguration().locale.getISO3Country());
    }

    public String getCountryUpdatedDate(Context context) {
        return getLastUpdatedDate(context.getResources().getConfiguration().locale.getISO3Country());
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public int getGlobalCases() {
        int total = 0;
        for (String country : getCountries()) {
            try {
                total += Objects.requireNonNull(getCountry(country)).getInt("total_cases");
            } catch (JSONException | NullPointerException ignored) {
            }
        }
        return total;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public int getGlobalDeaths() {
        int total = 0;
        for (String country : getCountries()) {
            try {
                total += Objects.requireNonNull(getCountry(country)).getInt("total_deaths");
            } catch (JSONException | NullPointerException ignored) {
            }
        }
        return total;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public int getNewGlobalCases() {
        int total = 0;
        for (String country : getCountries()) {
            try {
                total += Objects.requireNonNull(getCountry(country)).getInt("new_cases");
            } catch (JSONException | NullPointerException ignored) {
            }
        }
        return total;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public int getNewGlobalDeaths() {
        int total = 0;
        for (String country : getCountries()) {
            try {
                total += Objects.requireNonNull(getCountry(country)).getInt("new_deaths");
            } catch (JSONException | NullPointerException ignored) {
            }
        }
        return total;
    }

    public int getTotalCases(String country) {
        if (getCountry(country) == null) return 0;
        try {
            return Objects.requireNonNull(getCountry(country)).getInt("total_cases");
        } catch (JSONException | NullPointerException ignored) {
            return 0;
        }
    }

    public int getTotalDeaths(String country) {
        if (getCountry(country) == null) return 0;
        try {
            return Objects.requireNonNull(getCountry(country)).getInt("total_deaths");
        } catch (JSONException | NullPointerException ignored) {
            return 0;
        }
    }

    public int getNewCases(String country) {
        if (getCountry(country) == null) return 0;
        try {
            return Objects.requireNonNull(getCountry(country)).getInt("new_cases");
        } catch (JSONException | NullPointerException ignored) {
            return 0;
        }
    }

    public int getNewDeaths(String country) {
        if (getCountry(country) == null) return 0;
        try {
            return Objects.requireNonNull(getCountry(country)).getInt("new_deaths");
        } catch (JSONException | NullPointerException ignored) {
            return 0;
        }
    }

    public List<String> getCountries() {
        List<String> mCountries = new ArrayList<>();
        for (String country : Locale.getISOCountries()) {
            if (getLocation(new Locale("", country).getISO3Country()) != null) {
                mCountries.add(new Locale("", country).getISO3Country());
            }
        }
        return mCountries;
    }

    public String getContinent(String country) {
        if (getCountry(country) == null) return null;
        try {
            return Objects.requireNonNull(getCountry(country)).getString("continent");
        } catch (JSONException ignored) {
            return null;
        }
    }

    public String getLocation(String country) {
        if (getCountry(country) == null) return null;
        try {
            return Objects.requireNonNull(getCountry(country)).getString("location");
        } catch (JSONException ignored) {
            return null;
        }
    }

    public String getLastUpdatedDate(String country) {
        if (getCountry(country) == null) return null;
        try {
            DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
            List<String> months = new ArrayList<>();
            Collections.addAll(months, dfs.getMonths());
            String[] rawDate = Objects.requireNonNull(getCountry(country)).getString("last_updated_date").split("-");
            String month = null;
            switch (rawDate[1]) {
                case "01":
                    month = months.get(0) + " ";
                    break;
                case "02":
                    month = months.get(1) + " ";
                    break;
                case "03":
                    month = months.get(2) + " ";
                    break;
                case "04":
                    month = months.get(3) + " ";
                    break;
                case "05":
                    month = months.get(4) + " ";
                    break;
                case "06":
                    month = months.get(5) + " ";
                    break;
                case "07":
                    month = months.get(6) + " ";
                    break;
                case "08":
                    month = months.get(7) + " ";
                    break;
                case "09":
                    month = months.get(8) + " ";
                    break;
                case "10":
                    month = months.get(9) + " ";
                    break;
                case "11":
                    month = months.get(10) + " ";
                    break;
                case "12":
                    month = months.get(11) + " ";
                    break;
            }
            return month + rawDate[2] + ", " + rawDate[0];
        } catch (JSONException ignored) {
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void parseJSON() {
        try (InputStream is = new URL("https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/latest/owid-covid-latest.json").openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            mJSONObject = new JSONObject(jsonText);
        } catch (JSONException | IOException ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initialize() {
        parseJSON();
    }

}