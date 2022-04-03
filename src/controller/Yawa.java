package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private Path yawaHome;
    private Path lastOwmCall, settingsFile;
    private Settings settings;
    private OneCallContainer occ;
    private String json, key;
    private Gson gson;
    private boolean isReady = false;

    public static int ZONE;
    private final static String GEOAPIURL = "http://api.openweathermap.org/geo/1.0/direct?";
    private final static String OCCAPIURL = "https://api.openweathermap.org/data/2.5/onecall?";

    public Yawa() throws IOException {
        
        gson = new Gson();
        settings = new Settings();
        yawaHome = Paths.get(System.getProperty("user.home")+"/.Yawa");
        
        if(!Files.isDirectory(yawaHome)) {
            Files.createDirectories(yawaHome);
        }

        lastOwmCall = Paths.get(yawaHome+"/openweatherOCC.json");
        settingsFile = Paths.get(yawaHome+"/settings.json");

        if(Files.exists(settingsFile)) {

            json = getSettingsFromFile();
            settings = gson.fromJson(json, Settings.class);

            if(Files.exists(lastOwmCall)) {

                // TimeZone tz = Calendar.getInstance().getTimeZone();
                TimeZone tz = TimeZone.getDefault();
                long ft = Files.getLastModifiedTime(lastOwmCall).to(TimeUnit.SECONDS);
                int offset = (tz.getRawOffset()+tz.getDSTSavings()) / 3600000;
                LocalDateTime fct = LocalDateTime.ofEpochSecond(ft, 0, ZoneOffset.ofHours(offset));

                if(LocalDateTime.now().isAfter(fct.plusMinutes(20))) {
                    json = callOwmApi();
                } else {
                    json = getOwmFromFile();
                }

            } else {
                json = callOwmApi();
            }

            occ = gson.fromJson(json, OneCallContainer.class);
            ZONE = occ.timezone_offset / 3600;
            isReady = true;
    
        } else {
            settings = new Settings();
            settings.name = "";
        }

    }


    public String callOwmApi() {

        try {
            
            key = settings.key;
            String urlstring = OCCAPIURL+"lat="+settings.lat+
                "&lon="+settings.lon+"&units=metric&lang=de&appid="+key;
            URL url = new URL(urlstring);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            System.out.println("[Yawa] making new API call");
            c.connect();
            InputStream is = c.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            json = br.readLine();
            br.close();
            System.out.println("[Yawa] received JSON from owm");

            FileWriter fw = new FileWriter(lastOwmCall.toFile());
            fw.write(json);
            fw.flush();
            fw.close();
            System.out.println("[Yawa] wrote JSON to $yawaHOME/openweatherOCC.json");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(-2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-3);
        }

        return json;

    }


    public String getSettingsFromFile() {

        try {
            FileReader fr = new FileReader(yawaHome.toFile()+"/settings.json");
            BufferedReader br = new BufferedReader(fr);
            json = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return json;

    }


    public String getOwmFromFile() {

        try {
            FileReader fr = new FileReader(lastOwmCall.toFile());
            BufferedReader br = new BufferedReader(fr);
            json = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return json;

    }


    public void saveSettings(Settings s) throws IOException {

        settings = s;
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        String json = g.toJson(s);
        FileWriter fw = new FileWriter(new File(yawaHome.toFile()+"/settings.json"));
        fw.write(json);
        fw.flush();
        fw.close();
        json = callOwmApi();
        occ = gson.fromJson(json, OneCallContainer.class);
        ZONE = occ.timezone_offset / 3600;
        isReady = true;

    }


    public Location[] fetchLocations(String q) {
        String json = "";
        try {
            URL url = new URL(GEOAPIURL+"q="+q+"&limit=5&appid="+settings.key);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.connect();
            InputStream is = c.getInputStream();
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);
            json = br.readLine();
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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


    public boolean isReady() {
        return isReady;
    }

}