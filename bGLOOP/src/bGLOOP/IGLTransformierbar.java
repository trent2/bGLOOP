package bGLOOP;

/** Methoden, die alle beweglichen Objekte implementieren. Die beweglichen
 * Objekte sind alle von {@link GLObjekt} abgeleiteten Klassen außer
 * {@link GLHimmel} und {@link GLBoden}.
 * 
 * @author R. Spillner
 */
public interface IGLTransformierbar {
	/** Verschiebt das Objekt um den Vektor <code>(pX, pY, pZ)</code>
	 * 
	 * @param pX
	 *            Objekt wird um <code>pX</code> parallel zur x-Achse verschoben
	 * @param pY
	 *            Objekt wird um <code>pY</code> parallel zur y-Achse verschoben
	 * @param pZ
	 *            Objekt wird um <code>pZ</code> parallel zur z-Achse verschoben
	 */
	public void verschiebe(double pX, double pY, double pZ);

	/** Verschiebt das Objekts mit seinem Bezugspunkt (meistens der Mittelpunkt)
	 * auf den Punkt <code>(pX, pY, pZ)</code>.
	 * 
	 * @param pX x-Koordinate des Zielpunkts
	 * @param pY y-Koordinate des Zielpunkts
	 * @param pZ z-Koordinate des Zielpunkts
	 */
	void setzePosition(double pX, double pY, double pZ);

	/** Dreht Objekt um die Koordinatenachsen, die in Punkt
	 * <code>P(pX, pY, pZ)</code> verschoben wurden. Dabei wird zuerst um die
	 * x-Achse um <code>pWinkelX</code>, danach um die y-Achse um
	 * <code>pWinkelY</code> und schließlich um die z-Achse um
	 * <code>pWinkelZ</code> gedreht.
	 * 
	 * @param pWinkelX
	 *            Winkel der Rotation um die x-Achse durch P in Grad
	 * @param pWinkelY
	 *            Winkel der Rotation um die y-Achse durch P in Grad
	 * @param pWinkelZ
	 *            Winkel der Rotation um die z-Achse durch P in Grad
	 * @param pX
	 *            x-Koordinate von P
	 * @param pY
	 *            y-Koordinate von P
	 * @param pZ
	 *            z-Koordinate von P
	 */
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ);

	/** Dreht Objekt um seinen Mittelpunkt. Dabei wird nacheinander um die in den
	 * Mittelpunkt verschobenen Koordinatenachsen rotiert. <br>
	 * Rotationsreihenfolge: x-Achse, y-Achse, zuletzt z-Achse. dann y-Achse,
	 * dann z-Achse.
	 * 
	 * @param pWinkelX
	 *            Winkel der Rotation um die x-Achse durch <code>M</code> in
	 *            Grad
	 * @param pWinkelY
	 *            Winkel der Rotation um die y-Achse durch <code>M</code> in
	 *            Grad
	 * @param pWinkelZ
	 *            Winkel der Rotation um die z-Achse durch <code>M</code> in
	 *            Grad
	 */
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ);

	/** Verzerrt das Objekt in alle drei Dimensionen mit unterschiedlichen
	 * Skalierungsfaktoren. D.h., dass alle Längen in x-Richtung mit dem Faktor
	 * <code>pFaktorX</code>, in y-Richtung mit dem Faktor <code>pFaktorY</code>
	 * und in z-Richtung mit dem Faktor <code>pFaktorZ</code> multipliziert
	 * werden.
	 * 
	 * @param pFaktorX
	 *            Skalierungsfaktor in x-Richtung
	 * @param pFaktorY
	 *            Skalierungsfaktor in y-Richtung
	 * @param pFaktorZ
	 *            Skalierungsfaktor in z-Richtung
	 */
	public void skaliere(double pFaktorX, double pFaktorY, double pFaktorZ);

	/** Skaliert das Objekt in alle drei Dimensionen mit dem gegebenen Faktor.
	 * 
	 * @see #skaliere(double, double, double)
	 * @param pFaktor
	 *            Skalierungsfaktor in x-,y- und z-Richtung
	 */
	public void skaliere(double pFaktor);

	/** Gibt die x-Koordinate des Objekts zurück.
	 * @return x-Koordinate des Objekts
	 */
	public double gibX();

	/** Gibt die y-Koordinate des Objekts zurück.
	 * @return y-Koordinate des Objekts
	 */
	public double gibY();

	/** Gibt die z-Koordinate des Objekts zurück.
	 * @return z-Koordinate des Objekts
	 */
	public double gibZ();

	/** @see #drehe(double, double, double)
	 * @deprecated Diese Methode ist veraltet, verwenden Sie bitte
	 *             {@link #drehe(double, double, double)}.
	 */
	@Deprecated public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ);

	/** @see #drehe(double, double, double, double, double, double) 
	 * @deprecated Diese Methode ist veraltet, verwenden Sie bitte
	 *             {@link #drehe(double, double, double, double, double, double)}
	 */
	@Deprecated public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ);

	/** Setzt Skalierung und Drehungen zurück auf die Standardwerte.
	 * Die bereits vollzogenen Verschiebungen (Translationen) des Objekts bleiben
	 * erhalten.
	 */
	void resetSkalierungUndRotation();
}