package ru.serjik.nionet;

import java.io.IOException;
import java.io.InputStream;

public class ConsoleLineReader
{
	public static final String read(InputStream stream)
	{
		try
		{
			String ls = System.getProperty("line.separator");

			if (stream.available() > ls.length())
			{
				byte[] data = new byte[stream.available()];
				int pos = 0;

				while (stream.available() > 0)
				{
					int value = stream.read();
					if (value != 10 && value != 13)
					{
						data[pos] = (byte) value;
						pos++;
					}
				}

				return new String(data, 0, pos);
			}
			else
			{
				stream.skip(stream.available());
			}
		}
		catch (IOException e)
		{

		}

		return "";
	}
}
