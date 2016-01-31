package bGLOOP;

/** Methoden, die alle beweglichen Objekte implementieren. Die beweglichen
 * Objekte sind alle von {@link GLObjekt} abgeleiteten Klassen außer
 * {@link GLHimmel} und {@link GLBoden}.
 * 
 * @author R. Spillner
 */
interface IGLTransformierbar {
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
	 * y-Achse um <code>pWinkelY</code>, danach um die z-Achse um
	 * <code>pWinkelZ</code> und schließlich um die y-Achse um
	 * <code>pWinkelY</code> gedreht.
	 * 
	 * @param pWinkelX
	 *            Winkel der Rotation um die x-Achse durch <code>P</code> in Grad
	 * @param pWinkelY
	 *            Winkel der Rotation um die y-Achse durch <code>P</code> in Grad
	 * @param pWinkelZ
	 *            Winkel der Rotation um die z-Achse durch <code>P</code> in Grad
	 * @param pX
	 *            x-Koordinate von <code>P</code>
	 * @param pY
	 *            y-Koordinate von <code>P</code>
	 * @param pZ
	 *            z-Koordinate von <code>P</code>
	 */
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ);

	/** Dreht Objekt um seinen Mittelpunkt. Dabei wird nacheinander um die in den
	 * Mittelpunkt <code>M</code> verschobenen Koordinatenachsen rotiert. <br>
	 * Rotationsreihenfolge: y-Achse, z-Achse, zuletzt x-Achse.
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

	/** Dreht Objekt um eine angegebene Achse im Raum.
	 * <p>Die Achse wird dabei durch die Vektorgleichung
	 * </p><blockquote><img src="doc-files/gerade-1.png" alt="Geradengleichung"></blockquote>
	 * <p>beschrieben.</p>
	 * <p>Das bedeutet: Stellt man sich eine Linie ausgehend vom Ursprung <code>(0,0,0)</code>
	 * zu <code>(pRichtX, pRichtY, pRichtZ)</code> vor, so verläuft die zu beschreibende
	 * Rotationsachse <em>parallel zu dieser Strecke</em> durch den Punkt
	 * <code>(pOrtX, pOrtY, pOrtZ)</code>. <code>(pRichtX, pRichtY, pRichtZ)</code> geben daher
	 * lediglich die <em>Richtung</em> der Achse an.
	 * </p>
	 * @param pWinkel Winkel der Rotation in Grad
	 * @param pOrtX x-Koordinate des Ortsvektors <code>pOrt</code>
	 * @param pOrtY y-Koordinate des Ortsvektors <code>pOrt</code>
	 * @param pOrtZ z-Koordinate des Ortsvektors <code>pOrt</code>
	 * @param pRichtX x-Koordinate des Richtungsvektors <code>pRicht</code>
	 * @param pRichtY y-Koordinate des Richtungsvektors <code>pRicht</code>
	 * @param pRichtZ z-Koordinate des Richtungsvektors <code>pRicht</code>
	 */
	public void drehe(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX, double pRichtY,
			double pRichtZ);

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

	/** 
	 * @deprecated Diese Methode ist veraltet, verwenden Sie bitte
	 *             {@link #drehe(double, double, double)}.
	 **/
	@Deprecated public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ);

	/**
	 * @deprecated Diese Methode ist veraltet, verwenden Sie bitte
	 *             {@link #drehe(double, double, double, double, double, double) drehe}
	 */
	@Deprecated public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ);

	/**
	 * @deprecated Diese Methode ist veraltet und nur aus Kompatibilitätsgründen zu GLOOP
	 * 				vorhanden. Verwenden Sie bitte
	 *             {@link #drehe(double, double, double, double, double, double, double) drehe}
	 */
	@Deprecated	public void rotiere(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX, double pRichtY,
			double pRichtZ);

	/** Setzt Skalierungen und Drehungen zurück auf die Standardwerte.
	 * Die bereits vollzogenen Verschiebungen (Translationen) des Objekts bleiben
	 * erhalten.
	 */
	void resetSkalierungUndRotation();
}