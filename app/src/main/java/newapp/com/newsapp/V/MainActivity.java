package newapp.com.newsapp.V;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import newapp.com.newsapp.M.CountryGeoDecoder;
import newapp.com.newsapp.M.NewsAdapter;
import newapp.com.newsapp.M.NewsPost;
import newapp.com.newsapp.M.RetroClient;
import newapp.com.newsapp.M.RetroInterface;
import newapp.com.newsapp.M.RetrofitResponseListener;
import newapp.com.newsapp.R;
import newapp.com.newsapp.VM.NewsPostViewModel;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private String country;
    private RetroInterface apiInterface;
    private List<NewsPost.Datum> listView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private NewsPostViewModel newsScreenViewModel;
    private ProgressDialog progressDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();
    }

    private void initialise() {


        adinitialiser();

        onesignalInitialiser();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        apiInterface = RetroClient.getClient().create(RetroInterface.class);
        recyclerView = (RecyclerView) findViewById(R.id.newslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            progressDialog.show();

            location = getLocationData();
            country = CountryGeoDecoder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());


            newsScreenViewModel = new ViewModelProvider(this ,new NewsFactory(getApplicationContext(), country, "business" )).get(NewsPostViewModel.class);

            newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onSuccess(NewsPost body) {

                    listView = body.articles;
                    adapter = new NewsAdapter(getApplicationContext(),listView , MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Location Permission not granted",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


    }

    private void onesignalInitialiser() {
        // OneSignal Initialization
        OneSignal.startInit(this)

                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenHandler())
                .init();
    }

    private class NotificationOpenHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {


            Log.d("APP  " +result.notification.payload.launchURL, "Notification clicked");


            String url = result.notification.payload.launchURL;


            Intent mIntent = new Intent(MainActivity.this, NotificationWebview.class);
            mIntent.putExtra("url", url);
            startActivity(mIntent);

        }
    }

    private void adinitialiser() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    class Notification extends NotificationExtenderService {

        @Override
        protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
            return false;
        }
    }


    @SuppressLint("MissingPermission")
    private Location getLocationData() {

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(),"Location Permission granted",Toast.LENGTH_SHORT).show();

//                    progressDialog.show();

                    location = getLocationData();
                    country = CountryGeoDecoder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());


                    newsScreenViewModel = new ViewModelProvider(this , new NewsFactory(getApplicationContext(),country,"business" )).get(NewsPostViewModel.class);


                    newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                        @Override
                        public void onFailure() {
                            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onSuccess(NewsPost body) {

                            listView = body.articles;
                            adapter = new NewsAdapter(getApplicationContext(),listView , MainActivity.this);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();

                        }
                    });

                } else {
                    // permission denied
                    // load default country us
                    Toast.makeText(getApplicationContext(),"loading default",Toast.LENGTH_SHORT).show();
                    newsScreenViewModel = new ViewModelProvider(this , new NewsFactory(getApplicationContext(),"us","business" )).get(NewsPostViewModel.class);
                    newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                        @Override
                        public void onFailure() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(NewsPost body) {

                            listView = body.articles;
                            adapter = new NewsAdapter(getApplicationContext(),listView , MainActivity.this);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                    });

                }
                return;
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        if(country == null){
            country = CountryGeoDecoder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());
        }
        else {

            if(country.equals(CountryGeoDecoder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude()))){
                //same country don't load new data
            }
            else{
                //load data related to another country
                country = CountryGeoDecoder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());


                newsScreenViewModel = new ViewModelProvider(this , new NewsFactory(getApplicationContext(),country,"business" )).get(NewsPostViewModel.class);
                newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                    @Override
                    public void onFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(NewsPost body) {

                        listView = body.articles;
                        adapter = new NewsAdapter(getApplicationContext(),listView , MainActivity.this);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                });


            }

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


}
