package com.example.arduinobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button btnConectar,btnDesconectar, btnGraficar;
    TextView inputData;
    Handler bluetoothIn;
    ImageButton imgBtnPlay,imgBtnPause;
    TableLayout table;
    EditText txtTime;
    float x[],y[],z[],times[];

    private boolean isConnected;
    private BluetoothAdapter adaptadorBT;
    BluetoothSocket btSocket;
    boolean play;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    private Acelerometro acelerometro;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final int handlerState = 0;
    private int contador=0;
    private static String address = null;
    private String[] hearder={"x","y","z","time"};
    private ArrayList<String[]> listaDatos=new ArrayList<String[]>();
    private TableDynamic tableDynamic;
    private ArrayList<Acelerometro> listAcelerometro;
    float time;
    TimerTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConectar = (Button) findViewById(R.id.btnConectar);
        btnDesconectar = (Button) findViewById(R.id.btnDesconectar);
        btnGraficar=findViewById(R.id.btnGraficar);
        imgBtnPlay=findViewById(R.id.imgBtnPlay);
        imgBtnPause=findViewById(R.id.imgBtnPause);
        txtTime=findViewById(R.id.txtTime);
        btnDesconectar.setVisibility(View.INVISIBLE);
        play=false;
    time=0;
        isConnected=false;
        btSocket = null;
        listAcelerometro= new ArrayList<>();
        table= findViewById(R.id.table);
        inputData = findViewById(R.id.inputData);
imgBtnPause.setVisibility(View.INVISIBLE);
imgBtnPlay.setVisibility(View.VISIBLE);
        tableDynamic= new TableDynamic(table,getApplicationContext());

        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DispositivosBT.class));
                if(isConnected==true){
                    btnDesconectar.setVisibility(View.VISIBLE);
                    btnConectar.setVisibility(View.INVISIBLE);
                }
            }
        });

btnGraficar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

int i=0;
x=new float[listAcelerometro.size()];
        y=new float[listAcelerometro.size()];
        z=new float[listAcelerometro.size()];
        times=new float[listAcelerometro.size()];
try
{
        for (Acelerometro item: listAcelerometro             ) {
            x[i]=item.getX();
            y[i]=item.getY();
            z[i]=item.getZ();
            times[i]=item.getTime();
            i++;

        }
}catch (Exception e){
    ShowToast(e.toString());
}
        Intent intent= new Intent(MainActivity.this,Graficadora.class);
      intent.putExtra("ArrayX",x);
        intent.putExtra("ArrayY",y);
        intent.putExtra("ArrayZ",z);
        intent.putExtra("ArrayTime",times);
        startActivity(intent);
    }
});
imgBtnPlay.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(!txtTime.getText().toString().equals("") && Float.parseFloat(txtTime.getText().toString())>=250){
            MyConexionBT.write(txtTime.getText().toString());
txtTime.setKeyListener(null);
        play=true;
imgBtnPlay.setVisibility(View.INVISIBLE);
imgBtnPause.setVisibility(View.VISIBLE);
        }else{
            ShowToast("Tiempo muy bajo");

        }
    }
});

imgBtnPause.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        play=false;
        imgBtnPlay.setVisibility(View.VISIBLE);
        imgBtnPause.setVisibility(View.INVISIBLE);
    }
});


        tableDynamic.AddHeader(hearder);



        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if (msg.what == handlerState) {

                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {

                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        String[] datos= dataInPrint.split(";");
                         acelerometro=new Acelerometro();
                        acelerometro.setX(Float.parseFloat(datos[0]));
                        acelerometro.setY(Float.parseFloat(datos[1]));
                        acelerometro.setZ(Float.parseFloat(datos[2]));


                        if(play==true) {
                            try{
                                if(contador==0){

                                    time=0;
                                }else {
                                    time = (time + (Float.parseFloat(txtTime.getText().toString())) / 1000);
                                }
                        }catch (Exception e){

                          time=0;
                        }
                            acelerometro.setTime(time);
                            MyConexionBT.write(txtTime.getText().toString());
                            tableDynamic.AddItem(new String[]{"" + acelerometro.getX(), "" + acelerometro.getY(), "" + acelerometro.getZ(), "" + acelerometro.getTime()});
                            listAcelerometro.add(acelerometro);
                            contador++;
                        }


                            //listaDatos=new ArrayList<>();
                            //listaDatos.add(new String[]{""+acelerometro.getX(), ""+acelerometro.getY(), ""+acelerometro.getZ(), ""+acelerometro.getTime()});







                        inputData.setText("x: "+ acelerometro.getX() +" y: "+ acelerometro.getY() +" z: "+ acelerometro.getZ());//<-<- PARTE A MODIFICAR >->->
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        adaptadorBT = BluetoothAdapter.getDefaultAdapter();
        ValidarAdaptadorB(adaptadorBT);
        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
imgBtnPause.setVisibility(View.INVISIBLE);
                imgBtnPlay.setVisibility(View.INVISIBLE);
                //   MyConexionBT.write("1");
                //  ShowToast("1");
                isConnected=false;
                if(isConnected==false){
                    btnConectar.setVisibility(View.VISIBLE);
                    btnDesconectar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void ValidarAdaptadorB(BluetoothAdapter adaptador) {
        if (adaptador == null) {
            ShowToast("Bluetooth no disponible");

        } else {
            if (!adaptador.isEnabled()) {
                Intent intento = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intento, 0);

            }

        }

    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    @Override
    public void onResume()
    {

        super.onResume();


        //Consigue la direccion MAC desde DeviceListActivity via intent

        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);

        //<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC


        BluetoothDevice  device = adaptadorBT.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
            ShowToast("socket creado");


        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();


        } catch (Exception e) {
           /* try {//Edite esto
                btSocket.close();
                ShowToast("trying fallback...");

                btSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                btSocket.connect();


            } catch (Exception e2) {

                ShowToast(""+e2.getMessage());
            }*/
        }
        if (btSocket.isConnected()){
            MyConexionBT = new ConnectedThread(btSocket);
            MyConexionBT.start();

            ShowToast("Conectado a "+device.getName());
            isConnected=true;

        }else{
            isConnected=false;
        }
        if(isConnected==true){
            btnConectar.setVisibility(View.INVISIBLE);
            btnDesconectar.setVisibility(View.VISIBLE);
        }else{


            btnDesconectar.setVisibility(View.INVISIBLE);
            btnConectar.setVisibility(View.VISIBLE);
        }


    }
     public void onPause()
     {
         super.onPause();
         try
         { // Cuando se sale de la aplicación esta parte permite
             // que no se deje abierto el socket
             btSocket.close();
         } catch (IOException e2) {}
     }
    private void ShowToast(String msg) {


        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                ShowToast(e.toString());
                // Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}