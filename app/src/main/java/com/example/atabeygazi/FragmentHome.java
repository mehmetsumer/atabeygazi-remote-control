package com.example.atabeygazi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.example.atabeygazi.MainActivity.bluetoothAdapter;
import static com.example.atabeygazi.MainActivity.isBtConnected;

public class FragmentHome extends Fragment {

    private TextView tv_status;
    private ImageView img_status;
    private ImageView bt_headlights;
    private final String TAG = "tag22";
    private int stat;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance() {
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragment = inflater.inflate(R.layout.fragment_home, container, false);
        img_status = fragment.findViewById(R.id.img_status);
        tv_status = fragment.findViewById(R.id.tv_status);
        bt_headlights = fragment.findViewById(R.id.bt_headlights);
        editStatus();
        stat = 0;
        img_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBtConnected) {
                    ((MainActivity)getActivity()).toggleBluetooth(true);

                }
            }
        });
        bt_headlights.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(isBtConnected) {
                    Log.i(TAG, "onClick: SELLEKTOR");
                    ((MainActivity) getActivity()).toggleHeadligths(3);
                } else Toast.makeText(getActivity(), "Önce bluetooth bağlantınızı kurun", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        bt_headlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBtConnected){
                    if(stat == 0){
                        Log.i(TAG, "onClick: KISA");
                        bt_headlights.setImageResource(R.drawable.headlighs_low);
                        stat = 1;
                    }else if(stat == 1){
                        Log.i(TAG, "onClick: UZUN");
                        bt_headlights.setImageResource(R.drawable.headlights_high);
                        stat = 2;
                    }else if (stat == 2){
                        Log.i(TAG, "onClick: KAPALI");
                        bt_headlights.setImageResource(R.drawable.headlights_off);
                        stat = 0;
                    }
                    ((MainActivity)getActivity()).toggleHeadligths(stat);
                } else Toast.makeText(getActivity(), "Önce bluetooth bağlantınızı kurun", Toast.LENGTH_SHORT).show();
            }
        });
        return fragment;
    }

    public void editStatus(){
        if(isBtConnected) {
            img_status.setImageResource(R.drawable.bluetooth_connected);
            tv_status.setText("Bağlantı Kuruldu");
        }
        else if(!bluetoothAdapter.isEnabled()){
            img_status.setImageResource(R.drawable.bluetooth_disabled_blue);
            tv_status.setText("Bluetooth Kapalı");
        } else {
            img_status.setImageResource(R.drawable.bluetooth_disabled);
            tv_status.setText("Bağlantı Kurulamadı");
        }
    }
}
