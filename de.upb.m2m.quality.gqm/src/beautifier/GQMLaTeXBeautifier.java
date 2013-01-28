package beautifier;

import java.util.Locale;
import java.util.Scanner;

import org.eclipse.xpand2.output.FileHandle;
import org.eclipse.xpand2.output.PostProcessor;

/**
 * A simple beautifier for the GQMLaTeXTemplate xPand template. It formats
 * every number to "2 digits after the dot".
 * 
 * @version 0.1
 * @author Sebastian Lehrig (lehrig@mail.upb.de)
 */
public class GQMLaTeXBeautifier implements PostProcessor {

	/**
	 * Aligns the columns of the GQM table.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void beforeWriteAndClose(FileHandle info) {
		if (info.getAbsolutePath() != null
				&& info.getAbsolutePath().endsWith(".tex")) {
			String text = info.getBuffer().toString();
			String format = "%.2f"; // width == 10 and 2 digits after the dot            
			Scanner scanner = new Scanner(text).useLocale(Locale.ENGLISH);
			while (scanner.hasNext()) {
				if (scanner.hasNextInt()) {
					int i = scanner.nextInt();
					String intText = "{ " + i + " }";
					String formatedIntText = "{"
							+ String.format(format, (double) i) + "}";
					formatedIntText = formatedIntText.replace(",", ".");
					text = text.replace(intText, formatedIntText);
				} else if (scanner.hasNextDouble()) {
					double d = scanner.nextDouble();
					String doubleText = "{ " + d + " }";
					String formatedDoubleText = "{" + String.format(format, d)
							+ "}";
					formatedDoubleText = formatedDoubleText.replace(",", ".");
					text = text.replace(doubleText, formatedDoubleText);
				} else {
					scanner.next();
				}
			}

			info.setBuffer(new StringBuffer(text));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterClose(FileHandle impl) {
		// not needed
	}

}
