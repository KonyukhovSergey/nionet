package ru.serjik.nionet;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioNetServer
{
	private List<ConnectionProvider> connections = new ArrayList<ConnectionProvider>();
	private ConnectionAcceptor connectionAcceptor;
	private ConnectionListener connectionListener;

	public NioNetServer(int port, ConnectionListener connectionListener) throws IOException
	{
		this.connectionListener = connectionListener;
		connectionAcceptor = new ConnectionAcceptor(port);
	}

	public void stop() throws IOException
	{
		connectionAcceptor.close();
	}

	public boolean tick()
	{
		try
		{
			SocketChannel socketChannel = connectionAcceptor.accept();

			if (socketChannel != null)
			{
				ConnectionProvider client = new ConnectionProvider(socketChannel, connectionListener);
				connectionListener.onConnect(client);
				connections.add(client);
			}
		}
		catch (IOException e)
		{
			return false;
		}

		for (Iterator<ConnectionProvider> iterator = connections.iterator(); iterator.hasNext();)
		{
			ConnectionProvider connection = iterator.next();

			try
			{
				connection.tick();
			}
			catch (IOException e)
			{
				iterator.remove();
				connectionListener.onDisconnect(connection);
			}
		}
		return true;
	}
}
