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
	void setzeDurchsichtigkeit(double pAlpha);
}
