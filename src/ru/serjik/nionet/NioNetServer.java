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

	public void tick()
	{
		try
		{
			SocketChannel socketChannel = connectionAcceptor.accept();

			if (socketChannel != null)
			{
				ConnectionProvider client = new ConnectionProvider(socketChannel);
				connectionListener.onConnect(client);
				connections.add(client);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			// TODO: it is need the decision to be or not to be
		}

		for (Iterator<ConnectionProvider> iterator = connections.iterator(); iterator.hasNext();)
		{
			ConnectionProvider client = iterator.next();

			try
			{
				client.tick(connectionListener);
			}
			catch (IOException e)
			{
				iterator.remove();
				connectionListener.onDisconnect(client);
			}
		}
	}

	public List<ConnectionProvider> connections()
	{
		return connections;
	}

	public void broadcast(String message)
	{
		for (ConnectionProvider connection : connections)
		{
			connection.send(message);
		}
	}
}
