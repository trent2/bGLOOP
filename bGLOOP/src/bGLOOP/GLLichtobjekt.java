package bGLOOP;

import bGLOOP.GLObjekt.Darstellungsmodus;

/** <p>Ein GLLichtobjekt ein normales bGLOOP-Objekt, das leuchtet. Dies kann
 * im einzelnen z.B. eine Kugel, ein {@link GLZylinder Zylinder}, ein {@link GLKegel Kegel},
 * ein {@link GLKegelstumpf Kegelstumpf} usw. sein. Die Klasse kann dazu verwendet werden, um
 * etwa Kerzenflammen oder Lampen darzustellen.</p>
 * <p>Das an den {@link #GLLichtobjekt(GLTransformableObject) Konstruktor} übergebene
 * Objekt sollte nicht mehr verwendet werden, d.h. es sollten keine Methoden auf
 * ihm ausgeführt werden. Dies sollte nun auf dem Lichtobjekt passieren.</p><p>
 * <em>Beispiel:</em>
 * <pre>
 *   GLLichtobjekt lobj;
 *   lobj = new GLLichtobjekt(new GLKugel(100, 200, 50, 10));
 *   lobj.setzeFarbe(.4, .5, .2);
 *   lobj.skaliere(1.5);
 * </pre> 
 * @author R. Spillner
 */
public class GLLichtobjekt
		implements IGLTransformierbar, IGLDisplayable, IGLSubdivisable, IGLSurface {
	// designed after the Decorator pattern, see
	// GoF, Design Patterns, Addison Wesley
	private GLTransformableObject aLichtobjekt;
	private GLLicht aLicht;

	/** Erstellt ein Lichtobjekt. Der Übergabeparameter muss eine {@link GLKugel Kugel}-,
	 * ein {@link GLKegel Kegel}-, ein {@link GLKegelstumpf Kegelstumpf}- oder ein
	 * {@link GLZylinder Zylinder}-Objekt sein. Dieses wird dann beleuchtet. Alle
	 * weiteren Methodenaufrufe müssen anschließend an das neu erstellte Lichtobjekt und
	 * <em>nicht mehr</em> an das ursprüngliche Objekt gegeben werden &mdash; dieses 
	 * sollte nach Möglichkeit nicht mehr verwendet werden (s. {@link GLLichtobjekt}). 
	 * @param pLichtobjekt Objekt, welches beleuchtet wird.
	 */
	public GLLichtobjekt(GLTransformableObject pLichtobjekt) {
		super();
		aLichtobjekt = pLichtobjekt;
		aLicht = new GLLicht(aLichtobjekt.gibX(), aLichtobjekt.gibY(), aLichtobjekt.gibZ());

		aLicht.setzeFarbe(aLichtobjekt.gibFarbe());

		// make aLichtobjekt emitting light
		aLichtobjekt.aEmission = aLichtobjekt.aDiffuse;
	}

	@Override
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ) {
		aLichtobjekt.drehe(pWinkelX, pWinkelY, pWinkelZ, pX, pY, pZ);
	}

	@Override
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ) {
		aLichtobjekt.drehe(pWinkelX, pWinkelY, pWinkelZ);
	}

	@Override
	public void verschiebe(double pX, double pY, double pZ) {
		aLichtobjekt.verschiebe(pX, pY, pZ);
		aLicht.verschiebe(pX, pY, pZ);
	}

	@Override
	public void skaliere(double pFaktorX, double pFaktorY, double pFaktorZ) {
		aLichtobjekt.skaliere(pFaktorX, pFaktorY, pFaktorZ);
	}

	@Override
	public void skaliere(double pFaktor) {
		aLichtobjekt.skaliere(pFaktor);
	}

	@Override
	public void setzeDarstellungsModus(Darstellungsmodus dm) {
		aLichtobjekt.setzeDarstellungsModus(dm);
	}

	@Override
	public void setzeQualitaet(int pBreitengrade, int pLaengengrade) {
		aLichtobjekt.setzeQualitaet(pBreitengrade, pLaengengrade);
	}

	@Override
	public void setzeQualitaet(int pUnterteilungen) {
		aLichtobjekt.setzeQualitaet(pUnterteilungen);
	}

	@Override
	public double gibX() {
		return aLichtobjekt.gibX();
	}

	@Override
	public double gibY() {
		return aLichtobjekt.gibY();
	}

	@Override
	public double gibZ() {
		return aLichtobjekt.gibZ();
	}

	@Override
	public void setzeFarbe(double pR, double pG, double pB) {
		aLichtobjekt.setzeFarbe(pR, pG, pB);
	}

	@Override
	public double[] gibFarbe() {
		return aLichtobjekt.gibFarbe();
	}

	@Override
	public void setzeTextur(GLTextur pTextur) {
		aLichtobjekt.setzeTextur(pTextur);
	}

	@Override
	public void setzeTextur(String pTexturBilddatei) {
		aLichtobjekt.setzeTextur(pTexturBilddatei);
	}

	@Override
	public void setzeSichtbarkeit(boolean pSichtbar) {
		aLichtobjekt.setzeSichtbarkeit(pSichtbar);
		aLicht.lichtAn(pSichtbar);
	}
}