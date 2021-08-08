package ht.solutions.patientdata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView tv;
    ListView lv;
    ListAdapter adapter;
    ArrayList arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        tv = (TextView) findViewById(R.id.tv);
        lv = findViewById(R.id.lv);
        new getData().execute("http://192.168.0.165/PatientAPI/api/values");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Personne obj = new Personne();
                obj.setId("4");
                obj.setNom("Lovelace");
                obj.setPrenom("Herlandy");
                obj.setSexe("F");
                //new getData().execute("http://192.168.0.165/PatientAPI/api/values");
                new postData().execute("http://192.168.0.165/PatientAPI/api/values", new Gson().toJson(obj));


            }
        });
    }

    public class getData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader bf = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                connection.setRequestMethod("GET");
                bf = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer sbf = new StringBuffer();
                String test = "";
                while ((test = bf.readLine()) != null) {
                    sbf.append(test);
                }
                return sbf.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray dataj = new JSONArray(s);
                JSONObject reader = new JSONObject();
                reader.put("personne", dataj);
                JSONArray personne = reader.getJSONArray("personne");
                for (int i = 0; i < personne.length(); i++) {
                    JSONObject pers = personne.getJSONObject(i);
                    String id = pers.getString("id");
                    String nom = pers.getString("nom");
                    String prenom = pers.getString("prenom");
                    String sexe = pers.getString("sexe");
                    System.out.println(id + " " + nom + " " + prenom + " " + sexe);
                    HashMap<String, String> data = new HashMap<>();

                    data.put("id", id);
                    data.put("nom", nom);
                    data.put("prenom", prenom);
                    data.put("sexe", sexe);

                    arrayList.add(data);


                    ListAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.listpersonne, new String[]{"id", "nom", "prenom", "sexe"}, new int[]{R.id.id, R.id.nom, R.id.prenom, R.id.sexe});
                    lv.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //tv.setText(s);
        }
    }
    public class postData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection=null;
            BufferedReader bf=null;
            try {
                URL url=new URL(strings[0]);
                connection= (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type","application/json;utf-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                OutputStream os =connection.getOutputStream();
                os.write(strings[1].getBytes("UTF-8"));
                os.close();

                int resp=connection.getResponseCode();
                bf=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer sbf =new StringBuffer();
                String test="";
                while ((test=bf.readLine())!=null){
                    sbf.append(test);
                }
                return sbf.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            tv.setText(s);
            super.onPostExecute(s);
        }
    }
}