package com.example.arduinobluetooth;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TableDynamic {
    private TableLayout table;
    private Context context;
    private TextView cell;
    private TableRow tableRow;
    private int indexC, indexR;
    private String[] header;
    private ArrayList<String[]> data;
    private boolean multicolor=false;

    public TableDynamic(TableLayout tableLayout, Context context) {
        this.table=tableLayout;
        this.context=context;
        data=new ArrayList<>();

    }
    public  void AddRow(){
        tableRow= new TableRow(context);

    }
    public  void AddCell(){
        cell= new TextView(context);
        cell.setGravity(Gravity.CENTER);
        cell.setTextSize(25);

    }
    public void AddHeader(String[] header){

        this.header=header;
        CreateHeader();

    }
    public  void AddData(ArrayList<String[]> data){

        this.data=data;
        CreateDataTable();
    }
    public void CreateHeader(){

        indexC=0;
        AddRow();
        while(indexC<header.length){

            AddCell();
            cell.setText(header[indexC++]);
            cell.setBackgroundColor(Color.parseColor("#008577"));
            tableRow.addView(cell,AddTableRowParams());


        }
        table.addView(tableRow);
    }
    public void CreateDataTable(){
        String info;
        for (indexR=1;indexR<=header.length;indexR++){
            AddRow();
            for(indexC=0;indexC<header.length;indexC++){
                AddCell();
                String[] row=data.get(indexR-1);
                info=(indexC<row.length)?row[indexC]:"";
                cell.setText(info);
                tableRow.addView(cell,AddTableRowParams());

            }
            table.addView(tableRow);
        }

    }
    public TableRow.LayoutParams AddTableRowParams(){
        TableRow.LayoutParams params= new TableRow.LayoutParams();
        params.setMargins(1,1,1,1);
        params.weight=1;
        return params;
    }
    public void AddItem(String[] item){

        String info;
        data.add(item);
        indexC=0;
        AddRow();
        multicolor=!multicolor;
        while(indexC<header.length){

            AddCell();
            info=(indexC<item.length)?item[indexC++]:"";
            cell.setText(info);
            if(multicolor==false){
                cell.setBackgroundColor(Color.parseColor("#5B009688"));
            }else{
                cell.setBackgroundColor(Color.parseColor("#20009688"));

            }
            tableRow.addView(cell,AddTableRowParams());
        }
        table.addView(tableRow,data.size());

    }
}
