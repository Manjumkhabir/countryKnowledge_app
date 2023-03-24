package edu.monash.fit2081.countryinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class CountryDetails extends AppCompatActivity {

    public static final String COUNTRY_KEY = "COUNTRY_KEY";


    private TextView name;
    private TextView capital;
    private TextView code;
    private TextView population;
    private TextView area;
    private TextView currency;
    private TextView languages;
    public String [] borders;

    String mssg="";





    ImageView flagView;
    Button btn_wiki;
    String wikiSelectedCountry;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);

        getSupportActionBar().setTitle(R.string.title_activity_country_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String selectedCountry = getIntent().getStringExtra("country");
        wikiSelectedCountry = selectedCountry;

        name = findViewById(R.id.country_name);
        capital =  findViewById(R.id.capital);
        code =  findViewById(R.id.country_code);
        population =  findViewById(R.id.population);
        area = findViewById(R.id.area);
        currency = findViewById(R.id.currency);
        languages = findViewById(R.id.languages);


        flagView = findViewById( R.id.flag_view );
        btn_wiki = findViewById( R.id.btn_wiki );

        new GetCountryDetails().execute(selectedCountry);// we get the country name from the button clicked
    }


    private class GetCountryDetails extends AsyncTask<String, String, CountryInfo> {
        //AsyncTask- task tha runs on seperate thread not the Ui thread

        @Override
        protected CountryInfo doInBackground(String... params) {
            CountryInfo countryInfo = null;
            try {
                // Create URL
                String selectedCountry = params[0];
                URL webServiceEndPoint = new URL("https://restcountries.eu/rest/v2/name/" + selectedCountry); //

                // Create connection, establisha connection
                HttpsURLConnection myConnection = (HttpsURLConnection) webServiceEndPoint.openConnection();

                if (myConnection.getResponseCode() == 200) {
                    //JSON data has arrived successfully, now we need to open a stream to it and get a reader
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                    //now use a JSON parser to decode data
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginArray(); //consume arrays's opening JSON brace
                    String keyName;
                    countryInfo = new CountryInfo(); //nested class (see below) to carry Country Data around in
                    boolean countryFound = false;
                    while (jsonReader.hasNext() && !countryFound) { //process array of objects
                        jsonReader.beginObject(); //consume object's opening JSON brace
                        while (jsonReader.hasNext()) {// process key/value pairs inside the current object
                            keyName = jsonReader.nextName();
                            if (keyName.equals("name")) {
                                countryInfo.setName(jsonReader.nextString());
                                if (countryInfo.getName().equalsIgnoreCase(selectedCountry)) {
                                    countryFound = true;
                                }
                                //create a local or extarnal class for image dowlaod and display in an image view
                            } else if (keyName.equals("alpha2Code")) {
                                countryInfo.setAlpha2Code(jsonReader.nextString());
                            } else if (keyName.equals("alpha3Code")) {
                                countryInfo.setAlpha3Code(jsonReader.nextString());
                            } else if (keyName.equals("capital")) {
                                countryInfo.setCapital(jsonReader.nextString());
                            } else if (keyName.equals("population")) {
                                countryInfo.setPopulation(jsonReader.nextInt());
                            }else if (keyName.equals("borders")) {
                               String borders= "";
                               //countryInfo.setBorders(jsonReader.nextString());
                                JsonReader readerList=jsonReader;
                                readerList.beginArray();
                                if(readerList.peek()!= JsonToken.NULL){
                                    borders="";
                                    while(readerList.hasNext()){
                                        borders+= readerList.nextString()+",";
                                    }
                                    readerList.endArray();

                                }countryInfo.setBorders(borders);



                                    //countryInfo.setBorders(jsonReader.nextString());

                               // countryInfo.setBorders(jsonReader.nextString());

                                /*while (jsonReader.hasNext()){
                                    while (jsonReader.hasNext()){
                                        keyName = jsonReader.nextName();
                                        if (keyName.equals("name")) {
                                            countryInfo.setLanguages(jsonReader.nextString());
                                        }else {
                                            jsonReader.skipValue();
                                            //create mrore elseis to get the currencies and how to extract multiple vallues,
                                            // so a country wirh multiple currencies eg: CUBA as multiple curriencies
                                        }

                                    }
                                    jsonReader.endObject();
                                }*/
                                jsonReader.endArray();

                            }else if (keyName.equals("languages")) {
                                //countryInfo.setCurrency(jsonReader.nextString());
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()){
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()){
                                        keyName = jsonReader.nextName();
                                        if (keyName.equals("name")) {
                                            countryInfo.setLanguages(jsonReader.nextString());
                                        }else {
                                            jsonReader.skipValue();
                                            //create mrore elseis to get the currencies and how to extract multiple vallues,
                                            // so a country wirh multiple currencies eg: CUBA as multiple curriencies
                                        }

                                    }
                                    jsonReader.endObject();
                                }
                                jsonReader.endArray();



                            }else if (keyName.equals("area")) {
                                countryInfo.setArea(jsonReader.nextDouble());


                            } else if (keyName.equals("currencies")) {
                            //countryInfo.setCurrency(jsonReader.nextString());
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()){
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()){
                                        keyName = jsonReader.nextName();
                                        if (keyName.equals("name")) {
                                            countryInfo.setCurrency(jsonReader.nextString());
                                        }else {
                                            jsonReader.skipValue();
                                            //create mrore elseis to get the currencies and how to extract multiple vallues,
                                            // so a country wirh multiple currencies eg: CUBA as multiple curriencies
                                        }

                                    }
                                    jsonReader.endObject();
                                }
                                jsonReader.endArray();



                             }else {
                                jsonReader.skipValue();
                                //create mrore elseis to get the currencies and how to extract multiple vallues,
                                // so a country wirh multiple currencies eg: CUBA as multiple curriencies
                            }
                        }
                        jsonReader.endObject();
                    }
                    jsonReader.endArray();
                } else {
                    Log.i("INFO", "Error:  No response");
                }

                // All your networking logic should be here
            } catch (Exception e) {
                Log.i("INFO", "Error " + e.toString());
            }
            return countryInfo;
        }

        @Override
        protected void onPostExecute(CountryInfo countryInfo) {
            super.onPostExecute(countryInfo);
            name.setText(countryInfo.getName());
            capital.setText(countryInfo.getCapital());
            code.setText(countryInfo.getAlpha3Code());
            population.setText(Integer.toString(countryInfo.getPopulation()));
            area.setText(Double.toString(countryInfo.getArea()));
            currency.setText(countryInfo.getCurrency());
            languages.setText(countryInfo.getLanguages());
             String  display= countryInfo.getBorders();
             View view= findViewById(R.id.border);
            Snackbar.make(view, "borders"+ display,Snackbar.LENGTH_LONG).show();




            String flagLink = "https://www.countryflags.io/" + countryInfo.getAlpha2Code() + "/flat/64.png";

            btn_wiki.setText( "WIKI " + countryInfo.getName() );

            new FindFlag().execute( flagLink );

        }
    }
    private class FindFlag extends AsyncTask<String, Integer, Bitmap> {//first parameter,marks the datatype of the execute
        // the input type(here its request) and passing it tot class getLogo
        //on pre execute is (in pic)
        //secound paremeter, shows the progress, eg-integeter:0-100%, String: loading, dowloading or finished
        //third paremeter is return type (here its bitmap and also Bitmap same as in doInBackground and also of OnpOstExecute
        //
        @Override
        protected Bitmap doInBackground(String... strings) {
            //mandotory method
            //this method is execute on a different thread, and so has no acces to Ui elements

            // three dots means execute method can tke multiple string"new GetLogo().execute(request);"
            try {
                java.net.URL url = new java.net.URL( strings[0] );
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();//istablsh connection

                connection.setDoInput( true );

                publishProgress( 20 );
                connection.connect();

                InputStream input = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream( input );

                return myBitmap;

            } catch (IOException e) {
                e.printStackTrace(); // trace the error
                return null;
            }
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            flagView.setImageBitmap( bitmap );



        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate( values );
        }
    }

    // Deal with the button Wiki.
    public void goWiki_btn_Handler(View view) {
        Intent intent = new Intent( this, WebWiki.class );
        intent.putExtra( COUNTRY_KEY, wikiSelectedCountry );
        startActivity( intent );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



     //   if (keyName.equals("alpha3Code")) {}



    private class CountryInfo {
        private String name;
        private String alpha3Code;
        private String alpha2Code;
        private String capital;
        private int population;
        private double area;
        private String currency;
        private String languages;
        private String borders;



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlpha3Code() {
            return alpha3Code;
        }

        public void setAlpha3Code(String alpha3Code) {
            this.alpha3Code = alpha3Code;
        }
        public String getAlpha2Code() {
            return alpha2Code;
        }
        public void setAlpha2Code(String alpha2Code) {
            this.alpha2Code = alpha2Code;
        }
        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public int getPopulation() {
            return population;
        }

        public void setPopulation(int population) {
            this.population = population;
        }

        public double getArea() {
            return area;
        }

        public void setArea(double area) {
            this.area = area;

        }

        public String getCurrency() {
            return currency;
        }
        public void setCurrency(String currency) {
            this.currency = currency;
        }
        public String getLanguages() {
            return languages;
        }
        public void setLanguages(String languages) {
            this.languages = languages;
        }
        public String getBorders() {
            return borders;
        }
        public void setBorders(String borders) {
            this.borders = borders;
        }

    }
}
