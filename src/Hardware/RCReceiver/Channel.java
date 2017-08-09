package Hardware.RCReceiver;

import com.pi4j.io.gpio.GpioPinDigitalInput;

public class Channel {
	public int channelId; // Channel Number, 1 indexed
	public GpioPinDigitalInput pin; // Gpio Pin Assigned
	public boolean invert; // Whether or not to invert the signal, optional
	public int lowValue;	// Optional
	public int highValue;	// Optional
}
