package ru.serjik.nionet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ConnectionProvider
{
	private final static int BUFFER_SIZE = 4096;

	private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private BufferReader reader = new BufferReader(BUFFER_SIZE);

	private SocketChannel socket;

	private LoopBuffer messages = new LoopBuffer(BUFFER_SIZE * 8);

	public MessageListener messageListener;

	public ConnectionProvider(SocketChannel socket)
	{
		this.socket = socket;
		sendBuffer.limit(0);
	}

	public void close()
	{
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
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
			messages.enqueue(message.getBytes());
		}
	}

	public void tick(ConnectionListener messageListener) throws IOException
	{
		if (false == recv(messageListener))
		{
			throw new IOException();
		}
		
		send();
	}

	private void send() throws IOException
	{
		if (sendBuffer.hasRemaining())
		{
			socket.write(sendBuffer);
		}

		if (sendBuffer.hasRemaining() == false && messages.hasData())
		{
			messages.dequeue(sendBuffer);
			socket.write(sendBuffer);
		}
	}

	private boolean recv(ConnectionListener messageListener) throws IOException
	{
		readBuffer.clear();

		int count = socket.read(readBuffer);

		if (count == -1)
		{
			socket.close();
			return false;
		}

		readBuffer.flip();

		if (count > 0)
		{
			readBuffer.limit(count);

			int size;

			while ((size = reader.read(readBuffer)) > 0)
			{
				messageListener.onMessage(this, new String(reader.data(), 0, size));
			}
		}

		return true;
	}
}
