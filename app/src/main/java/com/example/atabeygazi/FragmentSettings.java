package com.example.atabeygazi;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.atabeygazi.adapters.PairedAdapter;

import static com.example.atabeygazi.MainActivity.isSettingsVisible;
import static com.example.atabeygazi.MainActivity.bluetoothAdapter;
import static com.example.atabeygazi.MainActivity.list;

public class FragmentSettings extends Fragment {

    private Button bt_save;
    private ImageView bt_back;
    public static Switch sw_bluetooth;
    private RelativeLayout panelBluetooth;

    private RecyclerView mRecyclerView;
    private PairedAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public FragmentSettings() {
        // Required empty public constructor
    }

    public static FragmentSettings newInstance(String param1, String param2) {
        return new FragmentSettings();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_settings, container, false);
        /*bt_save = fragment.findViewById(R.id.btn_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });*/
        isSettingsVisible = true;
        panelBluetooth = fragment.findViewById(R.id.panelBluetooth);
        sw_bluetooth = fragment.findViewById(R.id.sw_bluetooth);
        sw_bluetooth.setChecked(bluetoothAdapter.isEnabled());
        sw_bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity)getActivity()).toggleBluetooth(isChecked);
            }
        });
        bt_back = fragment.findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showViews();
            }
        });

        mRecyclerView = fragment.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        ((MainActivity)getActivity()).getDevices(true);
        return fragment;
    }

    public void buildRecyclerView(){
        mAdapter = new PairedAdapter(getContext(), list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PairedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                ((MainActivity)getActivity()).connectDevice(list.get(i).getAdress());
            }
        });
    }


}
