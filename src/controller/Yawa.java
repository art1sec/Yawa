package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Location;
import model.OneCallContainer;
import model.Settings;


public class Yawa {

    public static int ZONE;

    private Gson gson;
    private Path yawaHome;
    private Path dataFile, settingsFile;
    private Settings settings;
    private OneCallContainer occ;
    private String json, key;
    private boolean networkAccess = true;

    private final static String GEOAPIURL = "http://api.openweathermap.org/geo/1.0/direct?";
    private final static String OCCAPIURL = "https://api.openweathermap.org/data/2.5/onecall?";


    public Yawa() throws IOException {
        
        gson = new Gson();
        settings = new Settings();
        yawaHome = Paths.get(System.getProperty("user.home")+"/.Yawa");
        
        if(!Files.isDirectory(yawaHome)) {
            Files.createDirectories(yawaHome);
        }

        dataFile = Paths.get(yawaHome+"/openweathermap_occ.json");
        settingsFile = Paths.get(yawaHome+"/yawa_settings.json");

        if(Files.exists(settingsFile)) {

            json = getSettingsFromFile();
            settings = gson.fromJson(json, Settings.class);

            if(Files.exists(dataFile)) {

                // TimeZone tz = Calendar.getInstance().getTimeZone();
                TimeZone tz = TimeZone.getDefault();
                long ft = Files.getLastModifiedTime(dataFile).to(TimeUnit.SECONDS);
                int offset = (tz.getRawOffset()+tz.getDSTSavings()) / 3600000;
                LocalDateTime fct = LocalDateTime.ofEpochSecond(ft, 0, ZoneOffset.ofHours(offset));

                if(LocalDateTime.now().isAfter(fct.plusMinutes(20))) {
                    json = fetchJsonFromApi();
                } else {
                    json = getJsonFromFile();
                }

            } else {
                json = fetchJsonFromApi();
            }

            occ = gson.fromJson(json, OneCallContainer.class);
            ZONE = occ.timezone_offset / 3600;
    
        }

    }


    public String fetchJsonFromApi() {

        try {
            
            key = settings.key;
            String urlstring = OCCAPIURL+"lat="+settings.lat+
                "&lon="+settings.lon+"&units=metric&lang=de&appid="+key;
            URL url = new URL(urlstring);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            System.out.println("[Yawa] making new API call");
            c.connect();
            InputStream is = c.getInputStream();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            json = br.readLine();
            br.close();
            System.out.println("[Yawa] received JSON from owm");

            FileWriter fw = new FileWriter(dataFile.toFile());
            fw.write(json);
            fw.flush();
            fw.close();
            System.out.println("[Yawa] wrote JSON to $yawaHOME/openweathermap_occ.json");
            networkAccess = true;

        } catch (MalformedURLException e) {
            // e.printStackTrace();
            System.out.println("[Yawa] malformed URL exception in Yawa.fetchJsonFromApi");
            System.exit(-1);
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("[Yawa] IO exception in Yawa.fetchJsonFromApi");
            networkAccess = false;
            json = getJsonFromFile();
        }

        return json;

    }


    public String getSettingsFromFile() {

        try {
            FileReader fr = new FileReader(settingsFile.toFile());
            BufferedReader br = new BufferedReader(fr);
            json = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Yawa] IO exception in Yawa.getSettingsFromFile()");
            System.exit(-1);
        }
        return json;

    }


    public String getJsonFromFile() {

        try {
            System.out.println("[Yawa] read weather data from file");
            FileReader fr = new FileReader(dataFile.toFile());
            BufferedReader br = new BufferedReader(fr);
            json = br.readLine();
            br.close();
            System.out.println("[Yawa] done");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Yawa] IO exception in Yawa.getJsonFromFile()");
            System.exit(-1);
        }
        return json;

    }


    public void saveSettings(Settings s) {

        settings = s;
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        String json = g.toJson(s);
        FileWriter fw;
        try {
            fw = new FileWriter(settingsFile.toFile());
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Yawa] IO exception in Yawa.saveSettings()");
            System.exit(-1);
        }
        json = fetchJsonFromApi();
        occ = gson.fromJson(json, OneCallContainer.class);
        ZONE = occ.timezone_offset / 3600;

    }


    public Location[] fetchLocations(String q) {

        if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
            try {
                q = new String(q.getBytes("UTF-8"), "Windows-1252");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        String json = "";
        try {
            URL url = new URL(GEOAPIURL+"q="+q+"&limit=5&appid="+settings.key);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.connect();
            InputStream is = c.getInputStream();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            json = br.readLine();
            br.close();
            networkAccess = true;
        } catch (MalformedURLException e) {
            // e.printStackTrace();
            System.out.println("[Yawa] malformed URL exception in Yawa.fetchLocations()");
        } catch (IOException e) {
            // e.printStackTrace();
            networkAccess = false;
            System.out.println("[Yawa] IO exception in Yawa.fetchLocations()");
        }
        Gson gson = new Gson();
        return gson.fromJson(json, Location[].class);

    }


    public OneCallContainer getOCC() {
        return occ;
    }


    public Settings getSettings() {
        return settings;
    }


    public boolean isOnline() {
        return networkAccess;
    }

}