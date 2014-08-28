package applications;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EchoWithHelloMain {

	public static void main(final String... strings) throws Exception {
		out.println("Hello");
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if ("exit".equals(line)) {
					System.exit(10);
				} else if (line.startsWith("err ")) {
					err.println(line);
				} else {
					out.println(line);
				}
			}
		}
	}
}
