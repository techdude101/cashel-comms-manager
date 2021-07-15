package cashel_comms_manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileIO {
	/**
	 * Read a text file
	 * @param fname File path and filename e.g. C:\\myfile.txt
	 * @return
	 */
	protected static String readTextFile(String fname)
	{
		StringBuffer lines = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null)
			{
				lines.append(currentLine);
			}
		}
		catch (IOException e)
		{
			lines.append(e.getMessage());
		}
		return lines.toString();
	}
	
	/**
	 * Reads a text file and returns the contents as an html ordered list
	 * @param fname
	 * @return
	 */
	protected static String readTextFileOrderedList(String fname)
	{
		StringBuffer lines = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
			String currentLine;
			lines.append("<ol>");
			while ((currentLine = br.readLine()) != null)
			{
				lines.append("<li>");
				lines.append(currentLine);
				lines.append("</li>");
			}
			lines.append("</ol>");
		}
		catch (IOException e)
		{
			lines.append(e.getMessage());
		}
		return lines.toString();
	}
}
