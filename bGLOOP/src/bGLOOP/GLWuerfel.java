package bGLOOP;

/** Ein Würfelmodell.
 *  <img alt="Abbildung Würfel" src="./doc-files/Wuerfel-1.png">
 * @author R. Spillner
 *
 */
public class GLWuerfel extends GLQuader {
	/** Erzeugt einen Würfel mit Mittelpunkt <code>M(pMX, pMY, pMZ)</code> und
	 * Kantenlänge <code>pL</code>.
	 * <div style="float:right">
	 * <img alt="Abbildung Würfel" src="./doc-files/Wuerfel-1.png">
	 * </div><div>
	 * <p>
	 * <em>Abbildung:</em> Lage eines Würfels mit Mittelpunkt
	 * <code>M(0,0,0)</code> und Kantenlänge <code>1</code>.
	 * </p>
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts des Würfels
	 * @param pMY y-Koordinate des Mittelpunkts des Würfels
	 * @param pMZ z-Koordinate des Mittelpunkts des Würfels
	 * @param pL Seitenlänge in alle Richtung des Würfels
	 * </div>
	 * <div style="clear:right"></div>
	 */
	public GLWuerfel(double pMX, double pMY, double pMZ, double pL) {
		super(pMX, pMY, pMZ, pL, pL, pL, null);
	}

	/** Erzeugt einen Würfel mit Textur.
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts des Würfels
	 * @param pMY y-Koordinate des Mittelpunkts des Würfels
	 * @param pMZ z-Koordinate des Mittelpunkts des Würfels
	 * @param pL Seitenlänge in alle Richtung des Würfels
	 * @param pTextur Textur-Objekt für die Oberfläche des Würfels
	 */
	public GLWuerfel(double pMX, double pMY, double pMZ, double pL, GLTextur pTextur) {
		super(pMX, pMY, pMZ, pL, pL, pL, pTextur);
	}

}
