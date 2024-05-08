package com.example.test3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    static List<String> nazwyObiektów;

    ArrayAdapter<String> arrayAdapter;

    ListView listView;

    EditText editText;

     static int  globalPosition;

     static  List<MainObject> mainObjects = new ArrayList<>(); // lista obietków
    static class MainObject {
        String name;
        private List<String> subObjects;

        public MainObject(String name) {
            this.name = name;
            this.subObjects = new ArrayList<>();
        }


        public List<String> getSubObjects() {
            return subObjects;
        }

        public void setSubObjects(List<String> subObjects) {
            this.subObjects = subObjects;
        }

    }



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button chartButton = findViewById(R.id.button); // przycisk wykres
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PieChartActivity.class);
                startActivity(intent);
            }
        });

        nazwyObiektów = new ArrayList<>(); // Lista z nazwami obiektów

        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_view_layout, nazwyObiektów);

        listView = findViewById(R.id.id_list_view); //lista
        setUpListViewListener();
        listView.setAdapter(arrayAdapter);

        editText = findViewById(R.id.id_edit_text); //pole 1
        loadData();

    }

    public void addItemToList(View view) {  //Dodawanie

        if(czyIstnieje(editText.getText().toString())) {
            Context context = getApplicationContext();
            Toast.makeText(context, "Such a wallet already exists!", Toast.LENGTH_SHORT).show();
        }
        else {
            mainObjects.add(new MainObject(editText.getText().toString()));
            nazwyObiektów.add(editText.getText().toString().trim());
            arrayAdapter.notifyDataSetChanged();
            editText.setText("");
            saveData();
        }

    }

    private void setUpListViewListener() {   //Klikanie w element na liście

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // wykonaj jakieś działanie po kliknięciu na elemencie listy
                showSubList(position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // usuń element z listy

                Iterator<MainObject> iterator = mainObjects.iterator();
                while (iterator.hasNext()) {
                    MainObject object = iterator.next();
                    if (object.name.equals(nazwyObiektów.get(i))) {
                        iterator.remove();
                        nazwyObiektów.remove(i);
                        break;
                    }
                }

                // poinformuj adapter o zmianach w danych
                arrayAdapter.notifyDataSetChanged();
                // zapisz zmienione dane
               saveData();
                // zwróć wartość true, aby wskazać, że zdarzenie zostało obsłużone
                return true;
            }
        });
    }



    @SuppressLint("SuspiciousIndentation")
    public boolean czyIstnieje(String str) {
        for (MainObject object: mainObjects) {
            if(object.name.equals(str))
            return true;
        }
        return false;
    }


    private void showSubList(int position) {

        List<String> subList = new ArrayList<>();
        double totalGlobal;
        totalGlobal = getMax();
       // System.out.println("TUTAJ "+totalGlobal);
        for (MainObject object: mainObjects) {


            if(object.name.equals(nazwyObiektów.get(position))) {
                for ( String str :object.getSubObjects()) {
                    String[] parts = str.split(" ");
                    double liczba = Double.parseDouble(parts[1]);
                    double procent = (liczba/totalGlobal) * 100;
                    subList.add(str +" | " +String.format("%.2f", procent) + " %");
                }

            }

        }
        globalPosition = position;
        ArrayAdapter<String> subListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subList);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(nazwyObiektów.get(position));


        builder.setAdapter(subListAdapter, null);

        // Dodanie przycisku "Edytuj"
        builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Otwarcie nowej pustej aktywności po kliknięciu przycisku "Edytuj"
                Intent intent = new Intent(getApplicationContext(), SubList.class);
                startActivity(intent);
            }
        });


        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }


public double getMax() {
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
        iloscDanegoPortfela = 0;

    }
    return totalGlobal;
}

    public void saveData() {
        //Dla listy
        Gson gson = new Gson();
        String jsonData = gson.toJson(nazwyObiektów);
        try {
            FileOutputStream fileOutputStream = openFileOutput("data.txt", MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(jsonData);
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Dla obiektów
        // uzyskanie instancji SharedPreferences
         try {
             SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
             SharedPreferences.Editor editor = sharedPreferences.edit();

             // przekształcenie listy obiektów na JSON
             Gson gson1 = new Gson();
             String json = gson.toJson(mainObjects);

             // zapisanie JSON-a do SharedPreferences
             editor.putString("mainObjects", json);
             editor.apply();
         } catch (Exception e) {
             e.printStackTrace();
         }



    }


    private void loadData() {

        //Dla listy
        Gson gson = new Gson();
        try {
            FileInputStream fileInputStream = openFileInput("data.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Type type = new TypeToken<List<String>>() {}.getType();
                nazwyObiektów = gson.fromJson(bufferedReader, type);
                arrayAdapter = new ArrayAdapter<>(this, R.layout.list_view_layout, nazwyObiektów);
                listView.setAdapter(arrayAdapter);
                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Dla obiektów
        // uzyskanie instancji SharedPreferences
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);

            // pobranie JSON-a z SharedPreferences
            String json = sharedPreferences.getString("mainObjects", "");

            // przekształcenie JSON-a na listę obiektów
            Gson gson1 = new Gson();
            Type type = new TypeToken<List<MainObject>>() {
            }.getType();
            if(gson.fromJson(json, type) != null) {
                mainObjects = gson.fromJson(json, type);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

}
