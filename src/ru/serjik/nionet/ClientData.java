package ru.serjik.nionet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public class ClientData
{
	private final static int BUFFER_SIZE = 4096;

	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	private ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private BufferReader reader = new BufferReader(BUFFER_SIZE);

	private SocketChannel socket;

	public MessageListener tag;

	public ClientData(SocketChannel socket)
	{
		this.socket = socket;
		sendBuffer.limit(0);
	}

	public void close()
	{
		messages.clear();

		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			// e.printStackTrace();
		}
	}

	public boolean isOpen()
	{
		return socket.isOpen();
	}

	public void send(String message)
	{
		if (message != null)
		{
			messages.add(message);
		}
	}

	public void send() throws IOException
	{
		if (sendBuffer.hasRemaining())
		{
			socket.write(sendBuffer);
		}

		if (sendBuffer.hasRemaining() == false && messages.size() > 0)
		{
			BufferWriter.write(sendBuffer, messages.poll());
			socket.write(sendBuffer);
		}
	}

	public boolean recv(MessageListener messageListener) throws IOException
	{
		buffer.clear();

		int count = socket.read(buffer);

		if (count == -1)
		{
			socket.close();
			return false;
		}

		buffer.flip();

		if (count > 0)
		{
			buffer.limit(count);

			int size;

			while ((size = reader.read(buffer)) > 0)
			{
				messageListener.onMessage(this, new String(reader.data(), 0, size, charset));
			}
		}

		return true;
	}
}
