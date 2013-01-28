package de.upb.m2m.quality.languages.qvto;

import de.upb.m2m.quality.languages.AbstractLanguageConfiguration;

/**
 * Provides blackbox operations for the QVT-O transformations in the "script"
 * folder of this plug-in.
 * 
 * @version 0.1
 * @author Sebastian Lehrig (lehrig@mail.upb.de)
 */
public class MetricsBlackboxLibrary {
	/**
	 * Default constructor.
	 */
	public MetricsBlackboxLibrary() {
		super();
	}

	/**
	 * Currently, returns the value as given. Originally, was intended to round
	 * the value. However, it needs to be evaluate whether this makes sense at
	 * all (and in which cases).
	 * 
	 * // TODO maybe round here (how many digits for average values? ...)
	 * 
	 * @param value
	 *            The value to be rounded
	 * @return The rounded value
	 */
	public double getRound(double value) {
		return value;
	}

	/**
	 * Returns the square root of the given value.
	 * 
	 * @param value
	 *            The given value
	 * @return The square root of the value
	 */
	public double getSqrt(double value) {
		return Math.sqrt(value);
	}

	/**
	 * Returns the measurement for the "lines of code" (LOC) metric available
	 * within the current language configuration.
	 * 
	 * @return The lines of code metric measurement.
	 */
	public int getMetricLOC() {
		return AbstractLanguageConfiguration.getLinesOfCode().getLOC();
	}

	/**
	 * Returns the measurement for the "blank lines of code" (BLOC) metric
	 * available within the current language configuration.
	 * 
	 * @return The blank lines of code metric measurement.
	 */
	public int getMetricBLOC() {
		return AbstractLanguageConfiguration.getLinesOfCode().getBLOC();
	}

	/**
	 * Returns the measurement for the "comment lines of code" (CLOC) metric
	 * available within the current language configuration.
	 * 
	 * @return The comment lines of code metric measurement.
	 */
	public int getMetricCLOC() {
		return AbstractLanguageConfiguration.getLinesOfCode().getCLOC();
	}

	/**
	 * Returns the measurement for the "source lines of code" (SLOC) metric
	 * available within the current language configuration.
	 * 
	 * @return The source lines of code metric measurement.
	 */
	public int getMetricSLOC() {
		return AbstractLanguageConfiguration.getLinesOfCode().getSLOC();
	}
}
