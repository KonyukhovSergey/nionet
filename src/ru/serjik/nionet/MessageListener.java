package ru.serjik.nionet;

public interface MessageListener
{
	void onMessage(ConnectionProvider client, String message);
}
