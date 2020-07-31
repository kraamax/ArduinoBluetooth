package com.example.arduinobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class DispositivosBT extends AppCompatActivity {
    BluetoothAdapter adaptadorBluetooth;
    ListView listaDispositivos;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_bt);
        adaptadorBluetooth= BluetoothAdapter.getDefaultAdapter();
        listaDispositivos= findViewById(R.id.listaDispositivos);
    }
    public void onPause()
    {
        super.onPause();

    }
    @Override
    public void onResume()
    {
        super.onResume();

        ValidarAdaptadorB(adaptadorBluetooth);

        EncenderBluetooth(adaptadorBluetooth);
        Set<BluetoothDevice> devices = adaptadorBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        for(BluetoothDevice device: devices){
            list.add(device.getName() + "\n" + device.getAddress());

        }

        ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listaDispositivos.setAdapter(adapter);
        listaDispositivos.setOnItemClickListener(mDeviceClickListener);
    }

    private void EncenderBluetooth(BluetoothAdapter adaptadorBluetooth){

        if(!adaptadorBluetooth.isEnabled()){
            Intent intento= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intento,0);

        }
    }
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            // Realiza un intent para iniciar la siguiente actividad
            // mientras toma un EXTRA_DEVICE_ADDRESS que es la dirección MAC.
            Intent intento = new Intent(DispositivosBT.this, MainActivity.class);//<-<- PARTE A MODIFICAR >->->
            intento.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intento);

        }
    };


    private void ValidarAdaptadorB(BluetoothAdapter adaptador){
        if (adaptador==null){
            ShowToast("Bluetooth no disponible");

        }else{
            ShowToast("Bluetooth disponible");

        }

    }
    private void ShowToast(String msg){


        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
