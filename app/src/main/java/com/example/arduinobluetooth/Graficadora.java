package com.example.arduinobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.juang.jplot.PlotPlanitoXY;

import java.util.ArrayList;
import java.util.TimerTask;

public class Graficadora extends AppCompatActivity {
   private PlotPlanitoXY plot;
    private LinearLayout pantalla;
    Context context;
    float X[];
    float Y[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficadora);
        context=this;
        pantalla= (LinearLayout) (findViewById(R.id.pantalla));

         float x[]=getIntent().getFloatArrayExtra("ArrayX");
        float y[]=getIntent().getFloatArrayExtra("ArrayY");
        float z[]=getIntent().getFloatArrayExtra("ArrayZ");
        float time[]=getIntent().getFloatArrayExtra("ArrayTime");


        plot = new PlotPlanitoXY(context,"Grafico","Variable","Tiempo");

        plot.SetSerie1(time,x,"X/T",5,true);
        plot.SetSerie2(time,y,"Y/T",5,true,2);
        plot.SetSerie3(time,z,"Z/T",5,true,2);
        plot.SetSizeTituloX(24);
        plot.SetSizeTituloY1(24);
        plot.SetSizeTextX(11);
        plot.SetSizeTextY1(11);
        plot.SetSizeLeyend(11);
        plot.SetSizeTextY2(11);
        plot.SetTouch(false);
        pantalla.addView(plot);
    }


}
