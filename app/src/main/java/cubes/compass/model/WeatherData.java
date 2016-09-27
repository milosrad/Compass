package cubes.compass.model;

import java.sql.Array;

/**
 * Created by User on 17.7.2016.
 */
public class WeatherData {

    public Object coord;
    public Object sys;
    public Array weather;
    public Object main;
    public Object wind;
    public String name;

    public WeatherData(){}
}
