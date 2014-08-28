package applications;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

public class JmxServerMain {

	public static void main(final String... strings) throws Exception {
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		while (true) {
			Thread.sleep(Long.MAX_VALUE);
		}
	}
}