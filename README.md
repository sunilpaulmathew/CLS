# Covid Live Statistics (CLS)

![](https://img.shields.io/github/languages/top/sunilpaulmathew/covidlivestats)
![](https://img.shields.io/github/contributors/sunilpaulmathew/covidlivestats)
![](https://img.shields.io/github/license/sunilpaulmathew/covidlivestats)

A simple library to parse latest Covid-19 live statistics from "Our World in Data"!

## Credits
Covid Live Statistics (CLS) library uses data from Our World in Data. The authors/contributors of Our World in Data deserves (and should be given) all the credits. Please visit [Our World in Data](https://ourworldindata.org/) for more information.

## Download

Step 1: Add it in your root-level build.gradle at the end of repositories:
```
allprojects {
        repositories {
                ...
                maven { url 'https://jitpack.io' }
        }
}
```

Step 2: Add dependency to the app-level build.gradle:
```
dependencies {
        implementation 'com.github.sunilpaulmathew:CLS:Tag'
}
```
*Please Note: **Tag** should be replaced with the latest **[commit id](https://github.com/sunilpaulmathew/CLS/commits/master)**.*

Step 3: Add permission to your manifest (AndroidManifest.xml) file:
```
<uses-permission android:name="android.permission.INTERNET" />
```

## Tutorial

Initialize library
```
CovidStats mCovidStats = new CovidStats();
```

Now call the initialize() method in background. The Executor class included in this library shall be used for this purpose.
```
new Executor() {

        @Override
        public void onPreExecute() {
            // Do something, if needed
        }

        @Override
        public void doInBackground() {           
            mCovidStats.initialize();
            // Do other things, if needed
        }

        @Override
        public void onPostExecute() {
            // Do something, if needed
        }
}.execute();
```

### Get Global data
```
Total Cases (int): mCovidStats.getGlobalCases();
New Cases (int): mCovidStats.getNewGlobalCases();
Total Deaths (int): mCovidStats.getGlobalDeaths();
New Deaths (int): mCovidStats.getNewGlobalDeaths()
```

### Get data for your country
```
Total Cases (int): mCovidStats.getTotalCountryCases(this);
New Cases (int): mCovidStats.getCountryNewCases(this);
Total Deaths (int): mCovidStats.getTotalCountryDeaths(this);
New Deaths (int): mCovidStats.getCountryNewDeaths(this);
Country Name (String): mCovidStats.getCountryContinent(this);
Continent Name (String): mCovidStats.getCountryName(this);
Updated Date (String): mCovidStats.getCountryUpdatedDate(this);
```

### Get data for a specific country

Replace *'country'* with the 3 digit ISO code (e.g., IND for India)
```
Total Cases (int): mCovidStats.getTotalCases(country);
New Cases (int): mCovidStats.getNewCases(country);
Total Deaths (int): mCovidStats.getTotalDeaths(country);
New Deaths (int): mCovidStats.getNewDeaths(country);
Country Name (String): mCovidStats.getContinent(country);
Continent Name (String): mCovidStats.getLocation(country);
Updated Date (String): mCovidStats.getLastUpdatedDate(country);
```