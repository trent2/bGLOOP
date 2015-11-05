package bGLOOP;

/**
 * Klasse mit einigen Hilfsfunktionen für das Entwickeln mit bGLOOP.
 * 
 * @author R. Spillner
 *
 */
public class Sys {
	public static void warte(long pMilliseconds) {
		try {
			Thread.sleep(pMilliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void warte() {
		warte(1);
	}
}
