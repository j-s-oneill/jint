package applications;

public class SleepMain {

	public static void main(final String... strings) throws Exception {
		final long ms = Long.parseLong(strings[0]);
		Thread.sleep(ms);
	}
}
