package bGLOOP;

/** Klasse mit einigen Hilfsfunktionen für das Entwickeln mit bGLOOP.
 * @author R. Spillner
 */
public class Sys {

	/** Warte die angegebene Zeitspanne in Millisekunden.
	 * @param pMilliseconds Zeitspanne in Millisekundne, die der aktuelle
	 * 	Thread angehalten wird.
	 */
	public static void warte(long pMilliseconds) {
		try {
			Thread.sleep(pMilliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
/*  // doesn't work correctly, at least in Linux
	public static void warte(double pMilliseconds) {
		long mil = new Double(pMilliseconds).longValue();
		int nan = new Double((pMilliseconds - mil) * 1e6).intValue();
		try {
			Thread.sleep(mil, nan);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
*/
	/** Wartet eine Millisekunde.
	 */
	public static void warte() {
		warte(1);
	}

	public static void beenden() {
		GLKamera.aktiveKamera().associatedRenderer.getWindow().closeDisplay();
	}

}
