package Hardware.sensors;

import java.awt.Color;

public class SensorEvent {
	public int version;                          // must be sizeof(struct sensors_event_t) 
    public int sensor_id;                        // unique sensor identifier 
    public ESensorType type;                     // sensor type 
    public int reserved0;                        // reserved 
    public long timestamp;                        // time is in milliseconds 
    
    public SensorVector acceleration;         // acceleration values are in meter per second per second (m/s^2) 
    public SensorVector magnetic;             // magnetic vector values are in micro-Tesla (uT) 
    public SensorVector orientation;          // orientation values are in degrees 
    public SensorVector gyro;                 // gyroscope values are in rad/s 
    public float temperature;          // temperature is in degrees centigrade (Celsius) 
    public float distance;             // distance in centimeters 
    public float light;                // light in SI lux units 
    public float pressure;             // pressure in hectopascal (hPa) 
    public float relative_humidity;    // relative humidity in percent 
    public float current;              // current in milliamps (mA) 
    public float voltage;              // voltage in volts (V) 
    public Color color;                // color in RGB component values 
}
