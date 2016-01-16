package android.fr.runforall;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.location.Location.distanceBetween;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //latitude1,longitude1
    double[] enregistrement_coordonnees = new double[2];
    float[] result = new float[1];

    static float distance = 0;
    double longitude = 0;
    double latitude = 0;

    long basePause = 0; //Pour la gestion de la pause du chrono
    static long temps = 0; //Temps de course
    long timestamp = 0; //Timestamp du debut du run pour ordonner le graphe

    boolean first_passage = true;
    boolean bool_pause = false;

    Chronometer chrono;
    ImageButton pause = null;
    ImageButton stop = null;
    GoogleMap mMap;
    DataBaseHandler bdd;
    LocationManager locationManager;
    LocationListener locationListener;

    TextView txtDistance;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //====TEST POUR LA GESTION DU LOCK DU TEL===========
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //===================================================

        timestamp = System.currentTimeMillis();

        bdd = new DataBaseHandler(this);

        chrono = (Chronometer) findViewById(R.id.chronometer);
        pause = (ImageButton) findViewById(R.id.pause);
        stop = (ImageButton) findViewById(R.id.stop);
        txtDistance = (TextView)findViewById(R.id.textViewDistance);
        txtDistance.setText("No Location Update");
        chrono.start();

        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bool_pause = !bool_pause;
                basePause = gestionPauseChrono(bool_pause,chrono,basePause);
            }

        });

        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!AlertDFragment.ok_pressed) {
                    temps = (SystemClock.elapsedRealtime() - chrono.getBase()) / 1000;//Recupération du temps de course
                    distance = (float) (Math.round(distance / 100) / 10);
                    Log.d("Debug",String.valueOf(timestamp));
                    bdd.insertRunData(timestamp, temps, distance, distance / (temps * 60));//Envoie des données à la BDD

                    pause.setEnabled(false);

                    //Affichage de la boite de dialog pour fin
                    AlertDFragment alertdFragment = new AlertDFragment();
                    alertdFragment.show(fm, "Run Terminé");

                    onPause();

                    stop.setBackgroundResource(R.drawable.image_quit);
                }

                else{
                    AlertDFragment.ok_pressed = false;
                    finish();
                }

            }

        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Definition des critères de selection du fournisseur de position.
        Criteria critere = new Criteria();
        critere.setAccuracy(Criteria.ACCURACY_FINE);
        critere.setCostAllowed(false);
        critere.setPowerRequirement(Criteria.POWER_HIGH);
        critere.setSpeedRequired(true);
        String provider = locationManager.getBestProvider(critere, false); //True = resultat ne peux que correspondre aux critères
        locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {
                if (!bool_pause) {
                    /*====Definition de la position====*/
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    LatLng position = new LatLng(latitude, longitude);
                    mMap.clear();

                    /*====Gestion du marker====*/
                    MarkerOptions mp = new MarkerOptions();
                    mp.position(position);
                    mp.title("Ma position");
                    mMap.addMarker(mp);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));

                    /*====Calcul de la position====*/
                    if (first_passage) {
                        enregistrement_coordonnees[0] = latitude;
                        enregistrement_coordonnees[1] = longitude;
                        first_passage = false;
                    } else {
                        distanceBetween(enregistrement_coordonnees[0], enregistrement_coordonnees[1], latitude, longitude, result);
                        txtDistance.setText("Distance parcourue : "+String.valueOf(distance));
                        enregistrement_coordonnees[0] = latitude;
                        enregistrement_coordonnees[1] = longitude;
                        distance += result[0];
                        Log.d("Distance", "Distance = " + distance);
                    }
                } else {
                    first_passage = true;
                }
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //==========Quand l'activité reviens active, après un onPause===========
    public void onResume(){
        super.onResume();
        basePause = gestionPauseChrono(false,chrono,basePause);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 15, locationListener);
    }

    //=======Quand on quitte cette activité=============
    //Arrêt du chrono et du LocationUpdate
    @Override
    public void onPause(){
        super.onPause();
        basePause = gestionPauseChrono(true,chrono,basePause);
        //Durée du rafraichissement (ms)/distance de rafr (m)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(position_initiale).title("Ma position"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position_initiale));
    }

    public long gestionPauseChrono(boolean bool, Chronometer chrononometre, long base){
        if (bool) {
            //Sauvegarde de la valeur du chrono
            base = chrononometre.getBase() - SystemClock.elapsedRealtime();
            pause.setBackgroundResource(R.drawable.image_play);
            chrononometre.stop();

        } else {
            //Recalibrage la base du chrono pour qu'il continu là où il s'est arreté
            chrononometre.setBase(SystemClock.elapsedRealtime() + base);
            pause.setBackgroundResource(R.drawable.image_pause);
            chrononometre.start();
        }
        return base;
    }

    public static String calculTempsString(long temps) {
        double heure = Math.floor(temps / 3600);
        double minutes = Math.floor((temps % 3600) / 60);
        double secondes = temps % 60;

        if (heure == 0 && minutes == 0) {
            return String.valueOf((int)secondes) + " sec";
        } else if (heure == 0) {
            return String.valueOf((int)minutes) + " min " + String.valueOf((int)secondes) + " sec";
        } else {
            return String.valueOf((int)heure) + " h " + String.valueOf((int)minutes) + " min " + String.valueOf((int)secondes) + " sec";
        }
    }
}

