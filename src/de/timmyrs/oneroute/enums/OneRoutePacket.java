package de.timmyrs.oneroute.enums;

public enum OneRoutePacket
{
	AUTH(0),
	PORTS_TO_OPEN(1),
	PORTS_TO_CLOSE(2),
	AUTH_RESPONSE(0),
	UNACCEPTED_PORTS(1),
	CONNECT(2);

	public final byte id;

	OneRoutePacket(int id)
	{
		this.id = (byte) id;
	}
}