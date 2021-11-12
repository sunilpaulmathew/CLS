# Covid Live Statistics (CLS)

![](https://img.shields.io/badge/CLS-v0.1-green)
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
        implementation 'com.github.sunilpaulmathew:CLS:v0.1'
}
```

Step 3: Add permission to your manifest (AndroidManifest.xml) file:
```
<uses-permission android:name="android.permission.INTERNET" />
```

## Tutorial

Initialize library
```
new CovidStats().initialize();
```

This library also includes an Executor class, which can probably be used.
```
new Executor() {

        @Override
        public void onPreExecute() {
            // Do something, if needed
        }

        @Override
        public void doInBackground() {           
            new CovidStats().initialize();
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
Total Cases (int): CovidStats.getGlobalCases();
New Cases (int): CovidStats.getNewGlobalCases();
Total Deaths (int): CovidStats.getGlobalDeaths();
New Deaths (int): CovidStats.getNewGlobalDeaths()
```

### Get data for your country
```
Total Cases (int): CovidStats.getTotalCountryCases(this);
New Cases (int): CovidStats.getCountryNewCases(this);
Total Deaths (int): CovidStats.getTotalCountryDeaths(this);
New Deaths (int): CovidStats.getCountryNewDeaths(this);
Country Name (String): CovidStats.getCountryContinent(this);
Continent Name (String): CovidStats.getCountryName(this);
Updated Date (String): CovidStats.getCountryUpdatedDate(this);
```

### Get data for a specific country

Replace *'country'* with the 3 digit ISO code (e.g., IND for India)
```
Total Cases (int): CovidStats.getTotalCases(country);
New Cases (int): CovidStats.getNewCases(country);
Total Deaths (int): CovidStats.getTotalDeaths(country);
New Deaths (int): CovidStats.getNewDeaths(country);
Country Name (String): CovidStats.getContinent(country);
Continent Name (String): CovidStats.getLocation(country);
Updated Date (String): CovidStats.getLastUpdatedDate(country);
```