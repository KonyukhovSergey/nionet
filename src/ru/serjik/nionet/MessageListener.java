package ru.serjik.nionet;

public interface MessageListener
{
	void onMessage(ClientData client, String message);
}
