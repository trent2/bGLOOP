package bGLOOP;

/**
 * Ein Zylindermodell.<br>
 * <img alt="Abbildung Zylinder" src="./doc-files/Zylinder-1.png">
 * 
 * @author R. Spillner
 */
public class GLZylinder extends GLKegelstumpf {

	/**
	 * Erzeugt einen Zylinder längs zur z-Achse.
	 * <div style="float:right"> <img alt=
	 * "Abbildung Zylinder" src="./doc-files/Zylinder-1.png"> </div> <div>
	 * <p>
	 * Der Zylinder liegt in der Höhe parallel zur z-Achse. und seine Der
	 * Mittelpunkt <code>M(pMX, pMY, pMT,)</code>des Kegels ist der Punkt auf
	 * halber Höhe <code>pHoehe</code>, der Radius der Deckelflächen ist
	 * <code>pRadius</code>.
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
	 * @param pRadius Radius der Deckelflächen
	 * @param pHoehe  Höhe des Zylinders </div><div style="clear:right"></div>
	 */
	public GLZylinder(double pMX, double pMY, double pMZ, double pRadius, double pHoehe) {
		super(pMX, pMY, pMZ, pRadius, pRadius, pHoehe, null);
	}

	/**
	 * Erstellt einen Zylinder mit Textur.
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts
	 * @param pMY y-Koordinate des Mittelpunkts
	 * @param pMZ z-Koordinate des Mittelpunkts
	 * @param pRadius Radius der Deckelflächen
	 * @param pHoehe Höhe des Zylinders
	 * @param pTextur Textur-Objekt des Kegels.
	 */
	public GLZylinder(double pMX, double pMY, double pMZ, double pRadius, double pHoehe, GLTextur pTextur) {
		super(pMX, pMY, pMZ, pRadius, pRadius, pHoehe, pTextur);
	}
}