package bGLOOP;

interface IGLColorable {
	/** Farbe des Objekts.
	 * @return Dreielementiges Array mit Rot-, Grün und Blauanteilen.
	 */
	public double[] gibFarbe();

	/** Setzt die Farbe des Objekts. Die Parameterwerte müssen zwischen 0 und 1
	 * liegen.
	 * 
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 */
	public void setzeFarbe(double pR, double pG, double pB);
}
