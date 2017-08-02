package de.timmyrs.oneroute.main;

import de.timmyrs.oneroute.utils.OneRouteUtils;
import org.junit.Assert;
import org.junit.Test;

public class UnitTests
{
	@Test
	public void isIPallowed()
	{
		Assert.assertTrue("IPv4 should be allowed (Direct)", OneRouteUtils.isIPallowed("127.0.0.1", new String[] { "127.0.0.1" }, new String[] { "127.0.0.1" }));
		Assert.assertFalse("IPv4 should not be allowed (Direct)", OneRouteUtils.isIPallowed("127.0.0.1", new String[] { "127.0.0.1" }, new String[] { "127.0.0.2" }));
		Assert.assertTrue("IPv4 should be allowed (CIDR)", OneRouteUtils.isIPallowed("127.0.0.1", new String[] { "0.0.0.0/0" }, new String[] { "127.0.0.0/31" }));
		Assert.assertFalse("IPv4 should not be allowed (CIDR)", OneRouteUtils.isIPallowed("127.0.0.1", new String[] { "0.0.0.0/0" }, new String[] { "127.0.1.0/24" }));
		Assert.assertTrue("IPv6 should be allowed (Direct)", OneRouteUtils.isIPallowed("0:0:0:0:0:0:0:1", new String[] { "0:0:0:0:0:0:0:1" }, new String[] { "127.0.0.1", "0:0:0:0:0:0:0:1" }));
		Assert.assertFalse("IPv6 should not be allowed (Direct)", OneRouteUtils.isIPallowed("0:0:0:0:0:0:0:1", new String[] { "0:0:0:0:0:0:0:1" }, new String[] { "127.0.0.1", "0:0:0:0:0:0:0:2" }));
		Assert.assertTrue("IPv6 should be allowed (CIDR)", OneRouteUtils.isIPallowed("0:0:0:0:0:0:0:1", new String[] { "0:0:0:0:0:0:0:0/0" }, new String[] { "0:0:0:0:0:0:0:0/127" }));
		Assert.assertFalse("IPv6 should not be allowed (CIDR)", OneRouteUtils.isIPallowed("0:0:0:0:0:0:0:1", new String[] { "0:0:0:0:0:0:0:0/0" }, new String[] { "0:0:0:0:0:0:1:0/127" }));
	}
}