package Hardware.sensors;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class FXAS21002C extends Sensor {
	public int address;
	private I2CDevice i2cDevice;
	public int bus;
	private EGyroRange range;
	private int sensorId;
	
	// Raw values from last sensor read */
	public GyroRawData raw;
	
	/*=========================================================================
    I2C ADDRESS/BITS AND SETTINGS
    -----------------------------------------------------------------------*/
    public final int FXAS21002C_ADDRESS = 0x21;       // 0100001
    public final int FXAS21002C_ID = 0xD7;       // 1101 0111
    
    public enum EGyroSensitivity {
    	GYRO_SENSITIVITY_250DPS(0.0078125F), // Table 35 of datasheet
    	GYRO_SENSITIVITY_500DPS(0.015625F), // ..
    	GYRO_SENSITIVITY_1000DPS(0.03125F),   // ..
    	GYRO_SENSITIVITY_2000DPS(0.0625F);    // ..
    	
    	private float sensitivity;
        
        public float getSensitivity() {
      	  return sensitivity;
        }
        
        private EGyroSensitivity(float sensitivity) {
      	  this.sensitivity = sensitivity;
        }
    }
   
	public enum EGyroRegisters
    {                                             // DEFAULT    TYPE
      GYRO_REGISTER_STATUS(0x00),
      GYRO_REGISTER_OUT_X_MSB(0x01),
      GYRO_REGISTER_OUT_X_LSB(0x02),
      GYRO_REGISTER_OUT_Y_MSB(0x03),
      GYRO_REGISTER_OUT_Y_LSB(0x04),
      GYRO_REGISTER_OUT_Z_MSB(0x05),
      GYRO_REGISTER_OUT_Z_LSB(0x06),
      GYRO_REGISTER_WHO_AM_I(0x0C),   // 11010111   r
      GYRO_REGISTER_CTRL_REG0(0x0D),   // 00000000   r/w
      GYRO_REGISTER_CTRL_REG1(0x13),   // 00000000   r/w
      GYRO_REGISTER_CTRL_REG2(0x14);	  // 00000000   r/w
      
      private int registerAddress;
      
      public int getAddress() {
    	  return registerAddress;
      }
      
      private EGyroRegisters(int address) {
    	  this.registerAddress = address;
      }
    }
	
	public enum EGyroRange
    {
	      GYRO_RANGE_250DPS(250),
	      GYRO_RANGE_500DPS(500),
	      GYRO_RANGE_1000DPS(1000),
	      GYRO_RANGE_2000DPS(2000);
	      
	      private int range;
	      
	      public int getRange() {
	    	  return range;
	      }
	      
	      private EGyroRange(int range) {
	    	  this.range = range;
	      }
	}
	
	/*=========================================================================
    RAW GYROSCOPE DATA TYPE
    -----------------------------------------------------------------------*/
    public class GyroRawData
    {
      public short x;
      public short y;
      public short z;
    }
	
	public void FXAS210002C(EGyroRange range, int bus, int address)  throws IOException, InterruptedException, UnsupportedBusNumberException {
		this.bus = bus;
		this.address = address;
		i2cDevice = I2CFactory.getInstance(bus).getDevice(address);
		
		// Store the range
		this.range = range;
		
		// Clear the raw sensor data
		raw.x = 0;
		raw.y = 0;
		raw.z = 0;
		
		// Make sure we have the correct chip ID since this checks
		// for correct address and that the IC is properly connected
		int id = i2cDevice.read(EGyroRegisters.GYRO_REGISTER_WHO_AM_I.getAddress());
		// Serial.print("WHO AM I? 0x"); Serial.println(id, HEX);
		if (id != FXAS21002C_ID)
		{
		  throw new IOException("Talking to incorrect hardware!");
		}
		
		/* Set CTRL_REG1 (0x13)
		 ====================================================================
		 BIT  Symbol    Description                                   Default
		 ---  ------    --------------------------------------------- -------
		   6  RESET     Reset device on 1                                   0
		   5  ST        Self test enabled on 1                              0
		 4:2  DR        Output data rate                                  000
		                000 = 800 Hz
		                001 = 400 Hz
		                010 = 200 Hz
		                011 = 100 Hz
		                100 = 50 Hz
		                101 = 25 Hz
		                110 = 12.5 Hz
		                111 = 12.5 Hz
		   1  ACTIVE    Standby(0)/Active(1)                                0
		   0  READY     Standby(0)/Ready(1)                                 0
		/* Reset then switch to active mode with 100Hz output */
		i2cDevice.write(EGyroRegisters.GYRO_REGISTER_CTRL_REG1.getAddress(), (byte) 0x00);
		i2cDevice.write(EGyroRegisters.GYRO_REGISTER_CTRL_REG1.getAddress(), (byte)(1<<6));
		i2cDevice.write(EGyroRegisters.GYRO_REGISTER_CTRL_REG1.getAddress(), (byte) 0x0E);
		Thread.sleep(100); // 60 ms + 1/ODR
		/* ------------------------------------------------------------------ */

	}
	
	public void FXAS210002C(EGyroRange range)  throws IOException, InterruptedException, UnsupportedBusNumberException {
		FXAS210002C(range, I2CBus.BUS_1, 0x40);
	}
	
	public void enableAutoRange(boolean enabled) {
		// TODO Auto-generated method stub
		
	}
	
	/**************************************************************************/
	/*!
	  @brief  Gets the most recent sensor event
	 @throws IOException 
	 
	 ************************************************************************/
	public SensorEvent getEvent() throws IOException
	{
	// Create a new SensorEvent
	SensorEvent event = new SensorEvent();
	
	// Clear the raw data placeholder
	raw.x = 0;
	raw.y = 0;
	raw.z = 0;

	event.version   = 1;
	event.sensor_id = sensorId;
	event.type      = ESensorType.SENSOR_TYPE_GYROSCOPE;
	event.timestamp = System.currentTimeMillis();

	// Write to the sensor, something about the status register
	i2cDevice.write(FXAS21002C_ADDRESS, (byte) (EGyroRegisters.GYRO_REGISTER_STATUS.getAddress() | 0x80));
	//Wire.beginTransmission((byte)FXAS21002C_ADDRESS);
	//  Wire.write(GYRO_REGISTER_STATUS | 0x80);

	//Wire.endTransmission();
	
	// Read 7 bytes from the sensor
	byte[] bytes = new byte[7];
	//Wire.requestFrom((byte)FXAS21002C_ADDRESS, (byte)7);
	i2cDevice.read(bytes, 0, 7);

	@SuppressWarnings("unused")
	byte status = bytes[0];
	byte xhi = bytes[1];
	byte xlo = bytes[2];
	byte yhi = bytes[3];
	byte ylo = bytes[4];
	byte zhi = bytes[5];
	byte zlo = bytes[6];
	

	/* Shift values to create properly formed integer */
	raw.x = (short) ((xhi << 8) | xlo);
	raw.y = (short) ((yhi << 8) | ylo);
	raw.z = (short) ((zhi << 8) | zlo);

	/* Assign raw values in case someone needs them */
	event.gyro.x = raw.x;
	event.gyro.y = raw.y;
	event.gyro.z = raw.z;

	/* Compensate values depending on the resolution */
	float sensitivity = 1.0f;
	switch(range)
	{
	  case GYRO_RANGE_250DPS:
		sensitivity = EGyroSensitivity.GYRO_SENSITIVITY_250DPS.getSensitivity();
	    event.gyro.x *= sensitivity;
	    event.gyro.y *= sensitivity;
	    event.gyro.z *= sensitivity;
	    break;
	  case GYRO_RANGE_500DPS:
		  sensitivity = EGyroSensitivity.GYRO_SENSITIVITY_500DPS.getSensitivity();
		    event.gyro.x *= sensitivity;
		    event.gyro.y *= sensitivity;
		    event.gyro.z *= sensitivity;
	    break;
	  case GYRO_RANGE_1000DPS:
		  sensitivity = EGyroSensitivity.GYRO_SENSITIVITY_1000DPS.getSensitivity();
		    event.gyro.x *= sensitivity;
		    event.gyro.y *= sensitivity;
		    event.gyro.z *= sensitivity;
		    break;
	  case GYRO_RANGE_2000DPS:
		  sensitivity = EGyroSensitivity.GYRO_SENSITIVITY_2000DPS.getSensitivity();
		    event.gyro.x *= sensitivity;
		    event.gyro.y *= sensitivity;
		    event.gyro.z *= sensitivity;
		    break;
	}

	/* Convert values to rad/s */
	event.gyro.x *= SENSORS_DPS_TO_RADS;
	event.gyro.y *= SENSORS_DPS_TO_RADS;
	event.gyro.z *= SENSORS_DPS_TO_RADS;
	
	return event;
	}

	/**************************************************************************/
	/*!
	  @brief  Gets the sensor_t data
	*/
	/**************************************************************************/
	public SensorDetails getSensor()
	{
		SensorDetails sensor = new SensorDetails();
		
		sensor.name = "FXAS21002C";
		sensor.version     = 1;
		sensor.sensor_id   = sensorId;
		sensor.type        = ESensorType.SENSOR_TYPE_GYROSCOPE;
		sensor.min_delay   = 0;
		sensor.max_value   = range.getRange() * SENSORS_DPS_TO_RADS;
		sensor.min_value   = (range.getRange() * -1.0f) * SENSORS_DPS_TO_RADS;
		sensor.resolution  = 0.0F; // TBD
		
		return sensor;
	}
}
