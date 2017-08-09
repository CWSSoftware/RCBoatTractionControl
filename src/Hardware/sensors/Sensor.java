package Hardware.sensors;

import java.io.IOException;

public abstract class Sensor {
	public final static float SENSORS_GRAVITY_EARTH = 9.80665F; // Earth's
																// gravity in
																// m/s^2
	public final static float SENSORS_GRAVITY_MOON = 1.6F; // The moon's gravity
															// in m/s^2
	public final static float SENSORS_GRAVITY_SUN = 275.0F; // The sun's gravity
															// in m/s^2
	public final static float SENSORS_GRAVITY_STANDARD = SENSORS_GRAVITY_EARTH;
	public final static float SENSORS_MAGFIELD_EARTH_MAX = 60.0F; // Maximum
																	// magnetic
																	// field on
																	// Earth's
																	// surface
	public final static float SENSORS_MAGFIELD_EARTH_MIN = 30.0F; // Minimum
																	// magnetic
																	// field on
																	// Earth's
																	// surface
	public final static float SENSORS_PRESSURE_SEALEVELHPA = 1013.25F; // Average
																		// sea
																		// level
																		// pressure
																		// is
																		// 1013.25
																		// hPa
	public final static float SENSORS_DPS_TO_RADS = 0.017453293F; // Degrees/s
																	// to rad/s
																	// multiplier
	public final static int SENSORS_GAUSS_TO_MICROTESLA = 100; // Gauss to
																// micro-Tesla
																// multiplier

	public abstract void enableAutoRange(boolean enabled);

	public abstract SensorEvent getEvent() throws IOException;

	public abstract SensorDetails getSensor();

	protected boolean _autoRange;
}