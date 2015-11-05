package bGLOOP;

/**
 * Ein Kegelmodell. <br>
 * <img alt="Abbildung Kegel" src="./doc-files/Kegel-1.png">
 * 
 * @author R. Spillner
 */
public class GLKegel extends GLKegelstumpf {

	/** Erzeugt einen Kegel längs zur z-Achse.
	 * <div style="float:right"> <img alt=
	 * "Abbildung Kegel" src="./doc-files/Kegel-1.png"> </div> <div>
	 * <p>
	 * Der Kegel liegt in der Höhe parallel zur z-Achse und seine Spitze zeigt
	 * in die negative z-Richtung. Der Mittelpunkt <code>M(pMX, pMY, pMZ)</code>
	 * des Kegels ist der Punkt auf halber Höhe <code>pHoehe</code>, der Radius
	 * der Bodenfläche ist <code>pRadius</code>.
	 * </p>
	 * <p>
	 * <em>Abbildung:</em> Lage eines Kegels mit Mittelpunkt
	 * <code>M(0,0,0)</code> und Höhe 2. Der Mittelpunkt des Bodenkreises liegt
	 * folglich in <code>(0,0,1)</code>, die Spitze in <code>(0,0,-1)</code>.
	 * </p>
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts
	 * @param pMY y-Koordinate des Mittelpunkts
	 * @param pMZ z-Koordinate des Mittelpunkts
	 * @param pRadius Radius der Bodenfläche
	 * @param pHoehe Höhe des Kegels </div><div style="clear:right"></div>
	 */
	public GLKegel(double pMX, double pMY, double pMZ, double pRadius, double pHoehe) {
		super(pMX, pMY, pMZ, pRadius, 0, pHoehe, null);
	}

	/**
	 * Erzeugt einen Kegel mit Textur.
	 * 
	 * @param pMX
	 *            x-Koordinate des Mittelpunkts
	 * @param pMY
	 *            y-Koordinate des Mittelpunkts
	 * @param pMZ
	 *            z-Koordinate des Mittelpunkts
	 * @param pRadius
	 *            Radius der Bodenfläche
	 * @param pHoehe
	 *            Höhe des Kegels
	 * @param pTextur
	 *            Textur-Objekt des Kegels.
	 */
	public GLKegel(double pMX, double pMY, double pMZ, double pRadius, double pHoehe, GLTextur pTextur) {
		super(pMX, pMY, pMZ, pRadius, 0, pHoehe, pTextur);
	}
}
