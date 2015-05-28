package ru.serjik.nionet.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ru.serjik.nionet.ConnectionListener;
import ru.serjik.nionet.ConnectionProvider;
import ru.serjik.nionet.NioNetClient;
import ru.serjik.nionet.NioNetServer;

public class NetworkTest
{
	@Test
	public void testCreate() throws IOException
	{
		TestConnectionListener connectionListener = new TestConnectionListener();
		NioNetServer server = new NioNetServer(11001, connectionListener);
		server.stop();
	}

	@Test
	public void testConnection() throws Exception
	{
		TestConnectionListener serverConnectionListener = new TestConnectionListener();
		TestConnectionListener clientConnectionListener = new TestConnectionListener();
		NioNetServer server = new NioNetServer(11001, serverConnectionListener);
		NioNetClient client = new NioNetClient("127.0.0.1", 11001, clientConnectionListener);

		client.tick();
		server.tick();
		client.tick();

		Assert.assertEquals(1, serverConnectionListener.OnConnectCount);
		Assert.assertEquals(1, clientConnectionListener.OnConnectCount);

		client.close();
		server.stop();
		
		Assert.assertEquals(1, clientConnectionListener.OnDisconnectCount);
	}

	private String message = "this is a test string";

	@Test
	public void testMessages() throws Exception
	{
		TestConnectionListener serverConnectionListener = new TestConnectionListener();
		TestConnectionListener clientConnectionListener = new TestConnectionListener();
		NioNetServer server = new NioNetServer(11001, serverConnectionListener);
		NioNetClient client = new NioNetClient("127.0.0.1", 11001, clientConnectionListener);

		client.tick();
		server.tick();
		client.tick();

		clientConnectionListener.connection.send(message.getBytes());

		for (int i = 0; i < 5; i++)
		{
			client.tick();
			server.tick();
			Thread.sleep(10);
		}

		Assert.assertEquals(1, serverConnectionListener.OnMessageCount);
		Assert.assertEquals(message, serverConnectionListener.messages.get(0));

		serverConnectionListener.connection.send(message.getBytes());

		for (int i = 0; i < 5; i++)
		{
			client.tick();
			server.tick();
			Thread.sleep(10);
		}

		Assert.assertEquals(1, clientConnectionListener.OnMessageCount);
		Assert.assertEquals(message, clientConnectionListener.messages.get(0));

		client.close();
		server.stop();
	}

	public class TestConnectionListener implements ConnectionListener
	{
		public int OnConnectCount = 0;
		public int OnDisconnectCount = 0;
		public int OnMessageCount = 0;
		public List<String> messages = new ArrayList<String>();
		public ConnectionProvider connection;

		@Override
		public void onConnect(ConnectionProvider connection)
		{
			this.connection = connection;
			OnConnectCount++;
		}

		@Override
		public void onDisconnect(ConnectionProvider connection)
		{
			OnDisconnectCount++;
		}

		@Override
		public void onMessage(ConnectionProvider connection, byte[] data, int size)
		{
			messages.add(new String(data, 0, size));
			OnMessageCount++;
		}
	}
}
