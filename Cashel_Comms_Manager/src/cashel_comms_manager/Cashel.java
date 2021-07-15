package cashel_comms_manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Cashel {
	private static final String ssh_username = "root";
	private static final String ssh_password = "xxxx";
	private static final String web_username = "xxxx";
	private static final String web_password = "xxxx";
	private static final String trigger_threshold = "51";
	private static final String channel_gain = "56";
	
	private static final String url_cashelconfig = "/xxxx/cashelconfig.cgi?instance=tod";
	private static final String url_cashelview = "/xxxx/cashelview.cgi?instance=tod";
	
	private static final String url_cashelconfig_scm = "/xxxx/cashelconfig.cgi?instance=scm";
	private static final String url_cashelview_scm = "/xxxx/cashelview.cgi?instance=scm";
	
	private static final String url_cashelconfig_mmi = "/xxxx/cashelconfig.cgi?instance=mmi";
	private static final String url_cashelview_mmi = "/xxxx/cashelview.cgi?instance=mmi";

	private static final String url_cashelconfig_flr = "/xxxx/cashelconfig.cgi?instance=flr";
	private static final String url_cashelview_flr = "/xxxx/cashelview.cgi?instance=flr";
	
	/**
	 * Opens an SSH session using putty
	 * @param ip IP address as a string
	 * @return Returns false on error
	 */
	public static boolean openPutty(String ip)
	{
		boolean status = false;
		try {
			String path = new File(".").getCanonicalPath();
			String putty_args = "-ssh " + "-pw " + ssh_password + " " + ssh_username + "@" + ip;
			String putty_command = path + "\\putty.exe " + putty_args;
			Runtime.getRuntime().exec(putty_command);
			status = true;
		} catch (IOException e) {
			e.printStackTrace();
			status = false;
		}
		return status;
	}
	
	/**
	 * Sends an HTTP GET request - http://<ip><url>
	 * @param ip
	 * @param url
	 * @return HTTPClient response as a string
	 */
	public static String sendGet(String ip, String url)
	{
		String return_response = "";
		HttpGet httpGet = new HttpGet("http://" + ip + url);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse httpResponse;
		
		try {
			httpResponse = client.execute(httpGet);
			return_response = EntityUtils.toString(httpResponse.getEntity());
		} catch (ClientProtocolException e) {
			return_response = e.getMessage();
		} catch (IOException e) {
			return_response = e.getMessage();
		}	
		return return_response;
	}
	
	/**
	 * @param ip IP address as a string e.g. 192.168.1.11
	 * @param url URL e.g. /cgi-bin/cashelview.cgi
	 * @param data Form data to post
	 * @param user Username
	 * @param pass Password
	 * @return HTTPClient response
	 */
	public static String sendPost(String ip, String url, List<NameValuePair> data, String user, String pass)
	{
		int status = 0;
		String return_response = "";
		final int timeout = 1000;

		if (ip == null) {
			System.out.println("ip is null!"); 
		}
		String encoding = Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));

		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
		provider.setCredentials(AuthScope.ANY, credentials);

		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder.setConnectTimeout(timeout);
		requestBuilder.setSocketTimeout(timeout);
		requestBuilder.setConnectionRequestTimeout(timeout);
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultRequestConfig(requestBuilder.build());
		builder.setDefaultCredentialsProvider(provider);

		HttpClient client = builder.build();

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, Consts.UTF_8);

		HttpPost httpPost = new HttpPost("http://" + ip + url);
		httpPost.setEntity(entity);
		httpPost.setHeader("Authorization", "Basic " + encoding);

		// Create a custom response handler
		ResponseHandler<String> responseHandler = null;
		try
		{
			responseHandler = response -> {
				int response_status = response.getStatusLine().getStatusCode();
				if (response_status >= 200 && response_status < 300) {
					HttpEntity responseEntity = response.getEntity();
					return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + response_status);
				}
			};
		}
		catch (Exception e)
		{
			status = -1;
		}

		String responseBody;

		try 
		{
			if (responseHandler != null)
			{
				responseBody = client.execute(httpPost, responseHandler);
				return_response = responseBody;
			}
		} catch (ClientProtocolException e) {
			status = -1;
			return_response = e.getMessage();
		} catch (IOException e) {
			status = -1;
			return_response = e.getMessage();
		} catch (Exception e) {
			status = -1;
			return_response = e.getMessage();
		}
		return return_response;
	}
	
	/**
	 * Sets the time on a cashel device
	 * @param ip IP address as a string e.g. 192.168.1.1
	 * @return boolean
	 */
	public static boolean setTOD(String ip)
	{
		String response = "";
		
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("ipcchanges", "tod:tod/config/timezone_filepath=Europe/Belfast&tod:tod/config/time_config/dst=1"));
		response = sendPost(ip, url_cashelconfig, data, web_username, web_password);
		
		String epoch_now = getEpochTime();
		
		data.clear();
		
		data.add(new BasicNameValuePair("ipcchanges", "tod:tod/control/set_rtc=" + epoch_now));
		response = sendPost(ip, url_cashelview, data, web_username, web_password);

		String regexp_xml = "(/tmp/[\\w+].xml)";
		String pattern = "/tmp/(\\w+).xml";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(response);
		if (m.find())
		{
			System.out.println(m.group(0));
			String xml_url = "http://" + ip + m.group(0); 
			System.out.println(sendGet(ip, xml_url));
		}
		else
		{
			System.out.println("No match found");
		}
		
		return false;
	}
	
	/**
	 * Returns the number of seconds since 1 Jan 1970
	 * @return Number of seconds since 1 Jan 1970 
	 */
	public static String getEpochTime()
	{
		Instant i = Instant.now();
		
		return String.format("%d", i.toEpochMilli() / 1000);
	}
	
	/**
	 * Sends the command to activate the XML alarm file on a cashel device
	 * @param ip IP address as a string of the cashel device
	 */
	public static void activateXML(String ip)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("ipcchanges", "mmi:mmi/control/ser_event_xml_update=1"));
		sendPost(ip, url_cashelview_mmi, data, web_username, web_password);
		return;
	}
	
	/**
	 * Sends the shutdown command to a cashel device
	 * @param ip String IP address of the device to send the shutdown command to
	 */
	public static void turnOffDevice(String ip)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("ipcchanges", "mmi:mmi/control/system_shutdown=1"));
		sendPost(ip, url_cashelview_mmi, data, web_username, web_password);
		return;
	}
	
	/**
	 * @param currentIP IP address of the device
	 * @param frontIP New front IP address (eth0)
	 * @param rearIP New rear IP address (eth1)
	 */
	public static void setIP(String currentIP, String frontIP, String rearIP)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("ipcchanges", 
				"scm:scm/config/ethernet_settings[1]/cashelip=" + rearIP + 
				"&scm:scm/config/ethernet_settings[0]/cashelip=" + frontIP));
		sendPost(currentIP, url_cashelconfig_scm, data, web_username, web_password);
		return;
	}
	
	/**
	 * @param currentIP IP address of the device
	 * @param rearIP New rear IP address (eth1)
	 */
	public static void setIP(String currentIP, String rearIP)
	{
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("ipcchanges", 
				"scm:scm/config/ethernet_settings[1]/cashelip=" + rearIP));
		sendPost(currentIP, url_cashelconfig_scm, data, web_username, web_password);
		return;
	}
	
	/**
	 * Sends commands to automatically setup each FL line module
	 * @param ip IP address as a string of the cashel device
	 */
	public static void setupFL(String ip)
	{
		String lm_data = "";
		String lockout_data = "&";
		String trigger_data = "";
		List<NameValuePair> data = new ArrayList<>();
		
		// Loop through all 8 line modules
		for (int i = 0; i < 8; i++)
		{
			lm_data += "flr:flr/config/line_module[" + i + "]/Digital_Input1/label=D" + 
					(i + 1) + "%20-%20CH01&flr:flr/config/line_module[" + 
					i + "]/Digital_Input2/label=D" + (i + 1) + "%20-%20CH02";
			lockout_data += "flr:flr/config/line_module[" + 
					i + "]/lockout_duration=1&flr:flr/config/line_module[" + 
					i + "]/lockout_trigger=120";
			trigger_data += "flr:flr/config/line_module[" + i + "]/trigger_threshold/channel_1=" + trigger_threshold + "&" + 
				    "flr:flr/config/line_module[" + i + "]/trigger_threshold/channel_2=" + trigger_threshold + "&" + 
				    "flr:flr/config/line_module[" + i + "]/trigger_threshold/channel_3=" + trigger_threshold + "&" + 
				    "flr:flr/config/line_module[" + i + "]/trigger_gain/channel_1=" + channel_gain + "&" + 
				    "flr:flr/config/line_module[" + i + "]/trigger_gain/channel_2=" + channel_gain + "&" + 
				    "flr:flr/config/line_module[" + i + "]/trigger_gain/channel_3=" + channel_gain;
			if (i != 7) {
				lm_data += "&";
				lockout_data += "&";
				trigger_data += "&";
			}
		}
		
		data.add(new BasicNameValuePair("ipcchanges", lm_data + lockout_data));
		sendPost(ip, url_cashelconfig_flr, data, web_username, web_password);
		data.clear();
	
		data.add(new BasicNameValuePair("ipcchanges", trigger_data));
		sendPost(ip, url_cashelconfig_flr, data, web_username, web_password);
		return;
	}
}
