package com.example.test3;

import static com.example.test3.MainActivity.mainObjects;
import static com.example.test3.MainActivity.nazwyObiektów;
import static com.example.test3.MainActivity.globalPosition;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubList extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;
    ListView listView;

    EditText editText, editText2;
    List<String> subList = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);




        for (MainActivity.MainObject object : mainObjects) {
            if (object.name.equals(nazwyObiektów.get(globalPosition))) {
                 subList = object.getSubObjects();
            }
        }

        // pobieranie TextView i ustawianie tekstu
        TextView textView = findViewById(R.id.id_text_view);
        textView.setText(nazwyObiektów.get(globalPosition));

        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_view_layout, subList);
        listView = findViewById(R.id.id_list_view); //lista

        listView.setAdapter(arrayAdapter);

        editText = findViewById(R.id.id_edit_text); //pole 1
        editText2 = findViewById(R.id.id_edit_text2);   //pole 2

        setUpListViewListener(); //dodanie obsługi długiego kliknięcia




    }

    private void setUpListViewListener() {
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            subList.remove(i);
            arrayAdapter.notifyDataSetChanged();
            updateSublist();
            saveData();
            return true;
        });
    }


    public void addItemToList(View view) {  //Dodawanie

        if(czyZawieraLitere(editText2.getText().toString()) || editText.getText().toString().contains(" ")) {
            Context context = getApplicationContext();
            Toast.makeText(context, "Incorrect data!", Toast.LENGTH_SHORT).show();
        }
        else {

            subList.add(editText.getText().toString().trim() + " " + editText2.getText().toString().trim() + " PLN");
            updateSublist(); //przypisuje subliste do głównej sublisty obiektu
            //Spróbuj na replejsach
            arrayAdapter.notifyDataSetChanged();
            editText.setText("");
            editText2.setText("");

            saveData();

        }

    }

    public void goBack(View view) {
        onBackPressed();
    }


    public void updateSublist() {
        for (MainActivity.MainObject object : mainObjects) {
            if (object.name.equals(nazwyObiektów.get(globalPosition))) {
                object.setSubObjects(subList);
            }
        }
    }

    public boolean czyZawieraLitere(String str) {
        for (int i = 0; i < str.length() ; i++) {
            if(Character.isDigit(str.charAt(i)) || str.charAt(i) == '.') {
                // return false;
            }
            else {
                return true;
            }
        }
        return false;
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

        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // przekształcenie listy obiektów na JSON
        Gson gson1 = new Gson();
        String json = gson.toJson(mainObjects);

        // zapisanie JSON-a do SharedPreferences
        editor.putString("mainObjects", json);
        editor.apply();

    }

}