package ru.serjik.nionet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BufferWriter
{
	private static final Charset utf8 = Charset.forName("UTF-8");

	public static final int write(ByteBuffer buffer, String line)
	{
		byte[] data = line.getBytes(utf8);

		int position = 0;
		int xor;
		
		buffer.clear();

		while (position < data.length)
		{
			int blockLength = data.length - position;

			if (blockLength > 255)
			{
				blockLength = 255;
			}

			buffer.put((byte) blockLength);

			xor = 0;

			while (blockLength > 0)
			{
				buffer.put(data[position]);
				xor = (xor ^ (int) (data[position] & 0xff));

				position++;
				blockLength--;
			}

			buffer.put((byte) xor);
		}

		buffer.put((byte) 0);
		buffer.limit(buffer.position());
		buffer.position(0);
		
		return buffer.position();
		
	}
}
