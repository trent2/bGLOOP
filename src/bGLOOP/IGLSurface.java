package bGLOOP;

/** Methoden, die GLObjekte mit Oberflächen unterstützen.
 * @author R. Spillner
 */
interface IGLSurface {

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

	/** Gibt das aktuelle Texturobjekt zurück. Ist keine Textur festgelegt,
	 * wird <code>null</code> zurück gegeben.
	 * @return Das aktuelle {@link GLTextur}-Objekt
	 */
	public GLTextur gibTextur();

	/** Setzt den Durchsichtigkeitsfaktor des Objekts.
	 * @param pAlpha Ein Wert zwischen 0 (komplett durchsichtig) und 1
	 * (vollständig opak)
	 */
	public void setzeDurchsichtigkeit(double pAlpha);

	/** Setzt die Farbwerte der Farbe, in der das Objekt "leuchtet". Ein selbstleuchtendes
	 * Objekt wird in einer Szene angezeigt, ohne dass dafür eine weitere Lichtquelle
	 * notwendig ist. Das Licht ist jedoch nicht räumlich und kann <em>insbesondere keine
	 * weiteren Objekte beleuchten</em>. Wenn ein solches Verhalten gewünscht ist, empfiehlt
	 * sich die Benutzung der Klasse {@link GLLichtobjekt}. Diese verwendet jedoch eine
	 * Lichtquelle, von der in OpenGL in der Regel nur insgesamt 8 pro Szene zur Verfügung
	 * stehen.
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 */
	public void setzeSelbstleuchten(double pR, double pG, double pB);
}
