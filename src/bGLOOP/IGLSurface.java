package bGLOOP;

/** Methoden, die GLObjekte mit Oberflächen unterstützen.
 * @author R. Spillner
 */
public interface IGLSurface {
	/** Setzt die Farbe des Objekts. Die Parameterwerte müssen zwischen 0 und 1
	 * liegen.
	 * 
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 */
	public void setzeFarbe(double pR, double pG, double pB);

	/** Farbe des Objekts.
	 * @return Dreielementiges Array mit Rot-, Grün und Blauanteilen.
	 */
	public double[] gibFarbe();

	/** Legt die übergebene Textur auf das Objekt.
	 * 
	 * @param pTextur Ein {@link GLTextur}-Objekt
	 */
	public void setzeTextur(GLTextur pTextur);

	/** Legt die übergebene Textur auf das Objekt.
	 * 
	 * @param pTexturBilddatei Ein Dateiname einer Bilddatei (.jpg 
	 * oder .png)
	 */
	public void setzeTextur(String pTexturBilddatei);
}
