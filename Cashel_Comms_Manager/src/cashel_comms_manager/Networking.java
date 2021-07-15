package cashel_comms_manager;

import java.util.regex.Pattern;

public class Networking {
	private final static String ipv4_regex = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
	public static boolean isValidIPV4Address(String ip)
	{
		return ip.matches(ipv4_regex); 
	}
}
