package applications;

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.common.base.Joiner;

public class EchoMain {

	public static void main(final String... strings) throws Exception {
		out.println(Joiner.on(" ")
				.join(strings));
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line;
			while ((line = reader.readLine()) != null) {
				out.println(line);
			}
		}
	}
}
