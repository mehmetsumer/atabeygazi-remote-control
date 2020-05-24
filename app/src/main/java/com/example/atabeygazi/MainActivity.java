package com.example.atabeygazi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atabeygazi.adapters.PairedAdapter;
import com.example.atabeygazi.models.Device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.example.atabeygazi.FragmentSettings.sw_bluetooth;

public class MainActivity extends AppCompatActivity {

    //private BottomNavigationView bottomNav;
    public static final String DEVICE_ADRESS = "DeviceAdress";
    private Toolbar toolbar;
    private Fragment selected;
    private int tempMenu;
    public static BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    public static ArrayList<Device> list; // fragmentda bu lazım.
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LAST_NAME = "last_name";
    public static final String LAST_ADRESS = "last_adress";
    private static final String TAG = "tag22";

    //*******************************************************************
    private ImageView img; // Loading Dialogda kullanılan icon
    private Dialog dialog;
    private String address= null;
    BluetoothSocket btSocket = null;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket mServer;
    public static boolean isBtConnected = false;
    public static boolean isSettingsVisible = false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //*******************************************************************

    public void connectDevice(String address){
        this.address = address;
        new BTbaglan().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //bottomNav = findViewById(R.id.bottom_nav);
        //bottomNav.setOnNavigationItemSelectedListener(navListener);
        selected = new FragmentHome();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selected).commit();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isSettingsVisible = false;
        if(bluetoothAdapter.isEnabled()){
            // BT AÇIK İSE EN SON BAĞLANILAN ADRESİ AYARLARDAN ÇEKİP OTOMATİK BAĞLAN.
            if(getConnectedDevice() != null) connectDevice(getConnectedDevice());
        }
        tempMenu = 1;
    }

    public void toggleHeadligths(int status){
        try{
            Log.i(TAG, "toggleHeadligths: STAT= " + status);
            switch (status){
                case 0: // 0 - kapalı h0
                    btSocket.getOutputStream().write("h0".toString().getBytes());
                    Log.i(TAG, "toggleHeadligths: h0");
                    break;
                case 1: // 1 - kısalar h1
                    btSocket.getOutputStream().write("h1".toString().getBytes());
                    Log.i(TAG, "toggleHeadligths: h1");
                    break;
                case 2: // 2 - uzunlar h2
                    btSocket.getOutputStream().write("h2".toString().getBytes());
                    Log.i(TAG, "toggleHeadligths: h2");
                    break;
                case 3: // 3 - sellektör h3
                    btSocket.getOutputStream().write("h3".toString().getBytes());
                    Log.i(TAG, "toggleHeadligths: h3");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            isBtConnected = false;
            updateHomeFragment();
            Toast.makeText(this, "Önce bluetooth bağlantınızı kurun", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleBluetooth(boolean toggle){
        Log.i("tag22", "toggleBluetooth: " + toggle);
        if(bluetoothAdapter == null){
            Toast.makeText(this, "Cihazda bluetooth yok.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(toggle){ // AÇ
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,101);
        }else{ // KAPAT
            isBtConnected = false;
            bluetoothAdapter.disable();
            getDevices(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0) sw_bluetooth.setChecked(false);
        else {
            getDevices(true);
            if(getConnectedDevice() != null) connectDevice(getConnectedDevice());
        }
    }

    public void getDevices(boolean toggle) {
        Log.i("tag22", "getDevices: ÇALIŞTI");
        if(toggle) pairedDevices = bluetoothAdapter.getBondedDevices();
        else pairedDevices = Collections.emptySet();
        list = new ArrayList<>();
        if(pairedDevices.size() > 0){
            Log.i("tag22", "getDevices: Size: " + pairedDevices.size());
            for(BluetoothDevice bt : pairedDevices){
                Device dv = new Device(bt.getName(), bt.getAddress());
                list.add(dv);
            }
        }
        try{
            FragmentSettings mSettings = (FragmentSettings) getSupportFragmentManager().getFragments().get(0);
            mSettings.buildRecyclerView();
        }catch (Exception e){
            Log.i("tag22", "getDevices: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private FragmentTransaction animate(int menu){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(menu < tempMenu){ // Sağa kaydır
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_left, R.anim.slide_out_right);
        }else if(menu > tempMenu){ // Sola kaydır
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, //Sağdan sola
                    R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return transaction;
    }

    public void showViews(){
        super.onBackPressed();
        new CountDownTimer(370, 370) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                toolbar.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onBackPressed() { // GERİ TUŞUNA BASINCA BU ÇALIŞIR.
        if(isSettingsVisible){
            isSettingsVisible = false;
            showViews();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Çıkış");
            builder.setMessage("Uygulama kapatılsın mı?");
            //builder.setIcon(R.drawable.delete_black);
            builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int x) {
                    finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         /*switch (item.getItemId()){
            case R.id.toolbar_settings:*/
        //bottomNav.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right).addToBackStack(null)
                .replace(R.id.frameLayout, new FragmentSettings()).commit();
        //MainSettings.getView().bringToFront();
              /*  break;
        } */

        return super.onOptionsItemSelected(item);
    }

    /*private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selected = null;
            FragmentTransaction transaction = null;
            switch (item.getItemId()){
                case R.id.nav_home:
                    selected = new FragmentHome();
                    transaction = animate(1);
                    tempMenu = 1;
                    break;
                case R.id.nav_history:
                    selected = new FragmentHome();
                    transaction = animate(2);
                    tempMenu = 2;
                    break;
                /*case R.id.nav_store:
                    selected = new MainStore();
                    transaction = animate(3);
                    tempMenu = 3;
                    break;
            }
            transaction.replace(R.id.frameLayout, selected).commit();
            return true;
        }
    };*/

    private void saveConnectedDevice(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_ADRESS, address);
        editor.apply();
        //Toast.makeText(this, "Ayarlar Kaydedildi", Toast.LENGTH_SHORT).show();
    }

    private String getConnectedDevice() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(LAST_ADRESS,null);
    }

    private void openLoadingDialog(){
        dialog = new Dialog(this);
        View layout = getLayoutInflater().inflate(R.layout.bt_loading_dialog, null);
        dialog.setContentView(layout);
        img = dialog.findViewById(R.id.imageView);
        Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        img.startAnimation(aniRotate);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void updateHomeFragment(){
        try{
            FragmentHome mHome = (FragmentHome) getSupportFragmentManager().getFragments().get(0);
            mHome.editStatus();
        }catch (Exception e){
            Log.i("tag22", "getDevices: " + e.getMessage());
        }
    }

    private void updateLoadingDialog(boolean succeed){
        if(succeed){
            img.setImageResource(R.drawable.bluetooth_connected);
        }else{
            img.setImageResource(R.drawable.bluetooth_disabled);
        }
        isBtConnected = succeed;
        updateHomeFragment();
        new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                dialog.dismiss();
            }
        }.start();
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) { }
        }
        finish();
    }

    public class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            openLoadingDialog();
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = bluetoothAdapter.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                updateLoadingDialog(false);
            } else {
                saveConnectedDevice();
                updateLoadingDialog(true);
            }
        }
    }

}
