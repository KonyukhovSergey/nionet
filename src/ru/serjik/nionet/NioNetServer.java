package ru.serjik.nionet;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioNetServer
{
	private List<ClientData> clients = new ArrayList<ClientData>();
	private ClientAcceptor clientAcceptor;
	private NioNetServerListener serverListener;

	public NioNetServer(int port, NioNetServerListener serverListener) throws IOException
	{
		this.serverListener = serverListener;
		clientAcceptor = new ClientAcceptor(port);
	}

	public void stop() throws IOException
	{
		clientAcceptor.close();
	}

	public void tick()
	{
		try
		{
			SocketChannel socketChannel = clientAcceptor.accept();

			if (socketChannel != null)
			{
				ClientData client = new ClientData(socketChannel);
				serverListener.onAccept(client);
				clients.add(client);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			// TODO: it is need the decision to be or not to be
		}

		for (Iterator<ClientData> iterator = clients.iterator(); iterator.hasNext();)
		{
			ClientData client = iterator.next();

			try
			{
				client.recv(serverListener);
				client.send();
			}
			catch (IOException e)
			{
				iterator.remove();
				serverListener.onDisconnect(client);
			}
		}
	}

	public List<ClientData> clients()
	{
		return clients;
	}

	public void broadcast(String message)
	{
		for (ClientData client : clients)
		{
			client.send(message);
		}
	}
}
