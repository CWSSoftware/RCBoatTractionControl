package Hardware.sensors;

public class SensorDetails {
	public String name; // sensor name
    public int version; //version of the hardware + driver
    public int sensor_id; // unique sensor identifier
    public ESensorType type; // this sensor's type (ex. SENSOR_TYPE_LIGHT)
    public float max_value; // maximum value of this sensor's value in SI units
    public float min_value; // minimum value of this sensor's value in SI units
    public float resolution; // smallest difference between two values reported by this sensor
	public int min_delay; // min delay in microseconds between events. zero = not a constant rate
}
