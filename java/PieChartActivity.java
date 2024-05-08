package com.example.test3;


import androidx.appcompat.app.AppCompatActivity;
import static com.example.test3.MainActivity.mainObjects;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import java.util.ArrayList;
import java.util.List;

public class PieChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();

        double iloscDanegoPortfela = 0;
        double totalGlobal = 0 ;
        for (MainActivity.MainObject object: mainObjects) {
            //idzie po kolei po kazdym elemencie pod listy
            for ( String str :object.getSubObjects()) {
                String[] parts = str.split(" ");
                double liczba = Double.parseDouble(parts[1]);
                iloscDanegoPortfela += liczba ;
            }
            totalGlobal += iloscDanegoPortfela;
            data.add(new ValueDataEntry(object.name,iloscDanegoPortfela));
            iloscDanegoPortfela = 0;
        }

        pie.data(data);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setChart(pie);

        TextView chartTitle = findViewById(R.id.chart_title);
        chartTitle.setText("Total: " + totalGlobal + " PLN");


        ListView listView = findViewById(R.id.list_view);


        // Utworzenie listy elementów ListView
        List<String> list = new ArrayList<>();
        // Utworzenie adaptera dla elementów ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        // Przypisanie adaptera do ListView
        listView.setAdapter(adapter);

        double finalTotalGlobal = totalGlobal; //kopia Globala

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value", "color"}) {
            @Override
            public void onClick(com.anychart.chart.common.listener.Event event) {
                String pointName = event.getData().get("x");
                list.clear();

                for (MainActivity.MainObject object : MainActivity.mainObjects) {
                     if(object.name.equals(pointName)) {
                    for (String str : object.getSubObjects()) {

                        String[] parts = str.split(" ");
                        double liczba = Double.parseDouble(parts[1]);
                        double procent = (liczba / finalTotalGlobal) * 100;
                        list.add(object.name +": "+str + " | " + String.format("%.2f", procent) + " %");

                    }
                     }
                }
                adapter.notifyDataSetChanged();
            }

        });


    }


}