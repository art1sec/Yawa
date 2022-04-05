package model;

import java.time.*;
import java.time.format.DateTimeFormatter;

import controller.Yawa;

public class OneCallContainer {

    public int timezone_offset;
    public CurrentWeather current;
    public Minutely[] minutely;
    public Hourly[] hourly;
    public Daily[] daily;
    public Alerts[] alerts;

    public CurrentWeather getCurrentWeather() {
        return current;
    }
    
    public String toString() {

        LocalDateTime dt = LocalDateTime.ofEpochSecond(current.dt, 0, ZoneOffset.ofHours(1));
        LocalDateTime sunrise = LocalDateTime.ofEpochSecond(current.sunrise, 0, ZoneOffset.ofHours(1));
        LocalDateTime sunset = LocalDateTime.ofEpochSecond(current.sunset, 0, ZoneOffset.ofHours(1));
        
        String output = 
            "DT: "+dt+"\n"+
            current.weather[0].description+", "+Math.round(current.temp)+"° ("+
            Math.round(current.feels_like)+"°), "+current.humidity+"%, "+
            sunrise.format(DateTimeFormatter.ofPattern("hh:mm"))+" - "+
            sunset.format(DateTimeFormatter.ofPattern("HH:mm"))+"\n";

        for(Daily day: daily) {
            LocalDateTime d = LocalDateTime.ofEpochSecond(day.dt, 0, ZoneOffset.ofHours(1));
            System.out.println(d.format(DateTimeFormatter.ofPattern("dd.MM.")));
        }

        for(Hourly hour: hourly) {
            LocalDateTime d = LocalDateTime.ofEpochSecond(hour.dt, 0, ZoneOffset.ofHours(1));
            System.out.println(d.format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        return output;
    }

    public class CurrentWeather {
        
        private long dt;
        private long sunrise;
        private long sunset;
        private double temp;
        private double feels_like;
        private double wind_speed;
        private int wind_deg;
        private double wind_gust;
        private int humidity;

        Weather[] weather;

        public class Weather {

            private int id;
            private String description;
            private String icon;

            public String getIcon() { return icon; }
            public String getDescription() { return description; }
            public int getId() { return id; }
            
        }

        public int getTemp() { return (int)Math.round(temp); }
        public int getFeelsLike() { return (int)Math.round(feels_like); }
        public int getHumidity() { return humidity; }
        public int getWindSpeed() { return (int)Math.round(wind_speed*3.6); }
        public int getWindDeg() { return wind_deg; }
        public int getWindGust() { return (int)Math.round(wind_gust*3.6); }
        public Weather[] getWeather() { return weather; }
        public LocalDateTime getDt() {
            return LocalDateTime.ofEpochSecond(dt , 0, ZoneOffset.ofHours(Yawa.ZONE));
        }
        public LocalDateTime getSunrise() {
            return LocalDateTime.ofEpochSecond(sunrise, 0, ZoneOffset.ofHours(Yawa.ZONE));
        }
        public LocalDateTime getSunset() {
            return LocalDateTime.ofEpochSecond(sunset , 0, ZoneOffset.ofHours(Yawa.ZONE));
        }
    }

    public class Daily {
        private long dt;
        private Weather[] weather;
        
        public Weather[] getWeather() { return weather; }
        public LocalDateTime getDt() {
            return LocalDateTime.ofEpochSecond(dt , 0, ZoneOffset.ofHours(Yawa.ZONE));
        }

        public class Weather {
            private String description;
            private String icon;
            private int id;
            public String getDescription() { return description; }
            public String getIcon() { return icon; }
            public int getId() { return id; }
        }

    }

    public class Alerts  {
        private String event;
        private String description;
        private long start;
        private long end;
        public String getEvent() { return event; }
        public String getDescription() { return description; }
        public LocalDateTime getStart() {
            return LocalDateTime.ofEpochSecond(start , 0, ZoneOffset.ofHours(Yawa.ZONE));
        }
        public LocalDateTime getEnd() {
            return LocalDateTime.ofEpochSecond(end , 0, ZoneOffset.ofHours(Yawa.ZONE));
        }
    }

    public class Minutely {
        private double precipitation;
        public double getPrecipitation() { return precipitation; }
    }

    public class Hourly {

        private long dt;
        private double temp;
        private double feels_like;
        private int humidity;
        private double wind_speed;
        private int wind_deg;
        private double wind_gust;
        private Weather[] weather;
        private double pop;
        private Rain rain;

        public LocalDateTime getHour() {
            return LocalDateTime.ofEpochSecond(dt , 0, ZoneOffset.ofHours(Yawa.ZONE));
        }
        public int getTemp() { return (int)Math.round(temp); }
        public int getFeelsLike() { return (int)Math.round(feels_like); }
        public int getHumidity() { return humidity; }
        public int getWindSpeed() { return (int)Math.round(wind_speed*3.6); }
        public int getWindDeg() { return wind_deg; }
        public int getWindGust() { return (int)Math.round(wind_gust*3.6); }
        public int getPop() { return (int)Math.round(pop*100.0); }
        public Weather[] getWeather() { return weather; }
        public Rain getRain() { return rain; }

        public class Rain {

        }

        public class Weather {
            private String description;
            private String icon;
            private int id;
            public String getDescription() { return description; }
            public String getIcon() { return icon; }
            public int getId() { return id; }
        }

    }

}