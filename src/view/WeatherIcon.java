package view;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class WeatherIcon extends ImageIcon {

    public WeatherIcon(int id, boolean isDay, int size) {

        super();
        java.net.URL iconURL;

        String daylight= isDay?"day":"night";

        String path = "/res/png/";
        if(size>0) { path += "small/"; }
        
        switch(id) {
            /*
             *   Group 2xx: Thunderstorm
            */
            case 200:
            case 201:
            case 202:
            
            case 210:
            case 211:
            case 212:
            
            case 221:
            iconURL = getClass().getResource(path+"thunderstorm.png"); break;
            
            case 230:
            case 231:
            case 232:
            iconURL = getClass().getResource(path+"thundershower_"+daylight+".png"); break;

            /*
             *   Group 3xx: Drizzle
            */
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:

            /*
             *   Group 5xx: Rain
            */
            case 500:
            iconURL = getClass().getResource(path+"light_rain.png"); break;
            case 501:
            iconURL = getClass().getResource(path+"moderate_rain.png"); break;
            case 502:
            case 503:
            case 504:
            iconURL = getClass().getResource(path+"heavy_rain.png"); break;

            /*
             *   Group 6xx: Snow
            */
            case 600:
            iconURL = getClass().getResource(path+"light_snow.png"); break;
            case 601:
            iconURL = getClass().getResource(path+"snow.png"); break;
            case 616:
            iconURL = getClass().getResource(path+"rain_and_snow.png"); break;

            /*
             *   Group 7xx: Atmosphere
            */
            case 701:
            case 711:
            case 721:
            iconURL = getClass().getResource(path+"haze_"+daylight+".png"); break;
            case 741:
            iconURL = getClass().getResource(path+"mist.png"); break;
            case 771:
            iconURL = getClass().getResource(path+"squalls.png"); break;
            /*
             *   Group 800: Clear
            */
            case 800:
            iconURL = getClass().getResource(path+"clear_"+daylight+".png"); break;
            
            /*
             *   Group 80x: Clouds
            */
            case 801:
            iconURL = getClass().getResource(path+"few_clouds_"+daylight+".png"); break;
            
            case 802:
            iconURL = getClass().getResource(path+"scattered_clouds_"+daylight+".png"); break;

            case 803:
            iconURL = getClass().getResource(path+"broken_clouds_"+daylight+".png"); break;
            
            case 804:
            iconURL = getClass().getResource(path+"overcast_clouds.png"); break;
            
            /*
             *   default (should'nt ever take place)
            */
            default:
            iconURL = getClass().getResource(path+"na.png");
        
        }

        java.awt.image.BufferedImage img;
        try {
            img = ImageIO.read(iconURL);
            setImage(img);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-23);
        }

    }
    
}
