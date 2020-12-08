package com.edu.formula1.bolid;


import java.io.Serializable;
import java.sql.Timestamp;


public class BolidState implements Serializable, BolidStateInterface {

    public final static Double[] TEMPERATURE_RANGE = new Double[]{ 30.00, 300.00 };
    public final static Double[] TIRE_PRESSURE_RANGE = new Double[]{ 100.0, 1000.0 };
    public final static Double[] OIL_PRESSURE_RANGE = new Double[]{ 200.0, 2000.0 };

    public final String name = "McLaren-SRI03";

    public Double temperature  = 30.00;
    public Double tirePressure = 100.00;
    public Double oilPressure = 200.00;
    public Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    private Integer pitStopCount = 0;
    private Boolean pitStopAllowed = false;

    private Integer emergencyLevel = 0;

    public BolidState(){

    }

    public BolidState(Double temperature, Double tirePressure, Double oilPressure){
        this.temperature = temperature;
        this.tirePressure = tirePressure;
        this.oilPressure = oilPressure;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public BolidState(Double temperature, Double tirePressure, Double oilPressure, Integer pitStopCount, Integer emergencyLevel){
        this.temperature = temperature;
        this.tirePressure = tirePressure;
        this.oilPressure = oilPressure;
        this.pitStopCount = pitStopCount;
        this.emergencyLevel = emergencyLevel;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "BolidState-"+name+"{" +
                "temperature='" + formaDouble(temperature) + ';' +
                "tirePressure='" + formaDouble(tirePressure) + ';' +
                "oilPressure='" + formaDouble(oilPressure) + ';' +
                "pitStopAllowed='" + pitStopAllowed.toString() + ';' +
                "pitStopCount='" + pitStopCount + ';' +
                "emergencyLevel='" + emergencyLevel + ';' +
                "timestamp=" + timestamp.toString() +
                '}';
    }

    private String formaDouble(Double val){
        return val.toString().format("%.2f", val);
    }

    public String getName() {
        return name;
    }

    public Double getTemperature() {
        return temperature;
    }

    public BolidState setTemperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public Double getTirePressure() {
        return tirePressure;
    }

    public void setTirePressure(Double tirePressure) {
        this.tirePressure = tirePressure;
    }

    public Double getOilPressure() {
        return oilPressure;
    }

    public void setOilPressure(Double oilPressure) {
        this.oilPressure = oilPressure;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPitStopCount() {
        return pitStopCount;
    }

    public BolidState setPitStopCount(Integer pitStopCount) {
        this.pitStopCount = pitStopCount;
        return this;
    }

    public Boolean getPitStopAllowed() {
        return pitStopAllowed;
    }

    public BolidState setPitStopAllowed(Boolean pitStopAllowed) {
        this.pitStopAllowed = pitStopAllowed;
        return this;
    }

    public Integer getEmergencyLevel() {
        return emergencyLevel;
    }

    public BolidState setEmergencyLevel(Integer emergencyLevel) {
        this.emergencyLevel = emergencyLevel;
        return this;
    }
}
