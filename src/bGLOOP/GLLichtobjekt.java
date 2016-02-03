package bGLOOP;

import java.util.logging.Logger;

import bGLOOP.GLObjekt.Darstellungsmodus;

/** <p>Ein <code>GLLichtobjekt</code> ein normales bGLOOP-Objekt, das leuchtet. Dies kann
 * im einzelnen z.B. eine {@link GLKugel Kugel}, ein {@link GLZylinder Zylinder}, ein {@link GLKegel Kegel},
 * ein {@link GLKegelstumpf Kegelstumpf} usw. sein. Die Klasse kann dazu verwendet werden, um
 * etwa Kerzenflammen oder Lampen darzustellen.</p>
 * <p>Das an den {@link #GLLichtobjekt(TransformableSurfaceObject) Konstruktor} übergebene
 * Objekt sollte nicht mehr verwendet werden, d.h. es sollten keine Methoden auf
 * ihm ausgeführt werden. Dies sollte nun auf dem Lichtobjekt passieren.</p><p>
 * <em>Beispiel:</em>
 * <pre>
 *   GLLichtobjekt lobj;
 *   lobj = new GLLichtobjekt(new GLKugel(100, 200, 50, 10));
 *   lobj.setzeFarbe(.4, .5, .2);
 *   lobj.skaliere(1.5);
 * </pre>
 * <p>In der Implementierung dieser Klasse wird für jedes Objekt eine Lichtquelle erzeugt.
 * Dabei ist zu beachten, dass pro Szene von OpenGL in der Regel nur 8 Lichtquellen
 * unterstützt werden. Werden weitere Lichter benötigt, so sollte die Methode
 * {@link GLObjekt#setzeSelbstleuchten(double, double, double)} verwendet werden. Der
 * Nachteil eines lediglich selbstleuchtendes Objekt besteht darin, dass es keine Objekte
 * in seiner Umgebung beleuchten kann und die Szene ohne weitere Lichtquellen dunkel
 * bleibt. 
 * </p> 
 * @author R. Spillner
 */
public class GLLichtobjekt
 implements IGLTransformierbar, IGLDisplayable, IGLSubdivisable, IGLSurface, IGLColorable {
	private Logger log = Logger.getLogger("bGLOOP");
	// designed after the Decorator pattern, see
	// GoF, Design Patterns, Addison Wesley
	private TransformableSurfaceObject aLichtobjekt;
	private GLLicht aLicht;

	/** Erstellt ein Lichtobjekt. Der Übergabeparameter muss eine {@link GLKugel Kugel}-,
	 * ein {@link GLKegel Kegel}-, ein {@link GLKegelstumpf Kegelstumpf}- oder ein
	 * {@link GLZylinder Zylinder}-Objekt sein. Dieses wird dann beleuchtet. Alle
	 * weiteren Methodenaufrufe müssen anschließend an das neu erstellte Lichtobjekt und
	 * <em>nicht mehr</em> an das ursprüngliche Objekt gegeben werden &mdash; dieses 
	 * sollte nach Möglichkeit nicht mehr verwendet werden (s. {@link GLLichtobjekt}). 
	 * @param pLichtobjekt Objekt, welches beleuchtet wird.
	 */
	public GLLichtobjekt(TransformableSurfaceObject pLichtobjekt) {
		super();
		aLichtobjekt = pLichtobjekt;
		aLicht = new GLLicht(aLichtobjekt.gibX(), aLichtobjekt.gibY(), aLichtobjekt.gibZ());

		double[] col = aLichtobjekt.gibFarbe();
		aLicht.setzeFarbe(col[0], col[1], col[2]);

		// make aLichtobjekt emitting light
		aLichtobjekt.aEmission = aLichtobjekt.aDiffuse;
		log.fine("light object created");
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
	public void drehe(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX, double pRichtY,
			double pRichtZ) {
		aLichtobjekt.drehe(pWinkel, pOrtX, pOrtY, pOrtZ, pRichtX, pRichtY, pRichtZ);
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
	public GLTextur gibTextur() {
		return aLichtobjekt.gibTextur();
	}

	@Override
	public void setzeSichtbarkeit(boolean pSichtbar) {
		aLichtobjekt.setzeSichtbarkeit(pSichtbar);
		aLicht.lichtAn(pSichtbar);
	}

	/**  Aktiviert oder deaktiviert die Lichtquelle des Leuchtobjekts.
	 * @param an
	 *            Wenn <code>true</code>, dann wird die Lichtquelle aktiviert,
	 *            wenn <code>false</code>, dann wird sie deaktiviert.
	 */
	public void lichtAn(boolean an) {
		aLicht.lichtAn(an);
	}

	/** Setzt die Farbe des Anteils des vom Lichtobjekt emittierten Streulichts.
	 * Die Parameterwerte müssen zwischen 0 und 1 liegen.
	 * 
	 * @param pR Rotanteil
	 * @param pG Grünanteil
	 * @param pB Blauanteil
	 */
	public void setzeAmbientBeleuchtung(double pR, double pG, double pB) {
		aLicht.setzeAmbientBeleuchtung(pR, pG, pB);
	}

	/** Setzt den die Hauptfarbe des vom Lichtobjekt emittierten Lichts.
	 * Die Parameterwerte müssen zwischen 0 und 1 liegen.
	 * 
	 * @param pR Rotanteil
	 * @param pG Grünanteil
	 * @param pB Blauanteil
	 */
	public void setzeLichtFarbe(double pR, double pG, double pB) {
		aLicht.setzeFarbe(pR, pG, pB);
	}

	/** Legt fest, wie das emittierte Licht abgeschwächt mit zunehmender
	 * Entfernung vom Lichtobjekt abgeschwächt wird. Dabei werden
	 * eine konstante, eine lineare und eine quadratische Komponente unterschieden.
	 * @param pKonstant konstanter Faktor. Der Einfluss dieses Parameters ist
	 * unabhängig von der Entfernung und gibt somit eine globale (d.h. 
	 * positionsunabhängige) Abschwächung des Lichts an.
	 * @param pLinear linearer Faktor der Abschwächung abhängig von der
	 * 	Entfernung eines Pixels vom Lichtobjekt
	 * @param pQuadratisch quadratischer Faktor der Abschwächung abhängig vom
	 * 	Quadrat der Entfernung eines Pixels vom Lichtobjekt.
	 */
	public void setzeAbschwaechung(double pKonstant, double pLinear, double pQuadratisch) {
		aLicht.setzeAbschwaechung(pKonstant, pLinear, pQuadratisch);
	}

	@Override
	@Deprecated public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ) {
		drehe(pWinkelX, pWinkelY, pWinkelZ);
	}

	@Override
	@Deprecated public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ) {
		drehe(pWinkelX, pWinkelY, pWinkelZ, pX, pY, pZ);
	}

	@Override
	@Deprecated public void rotiere(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX, double pRichtY,
			double pRichtZ) {
		drehe(pWinkel, pOrtX, pOrtY, pOrtZ, pRichtX, pRichtY, pRichtZ);
	}

	@Override
	public void setzePosition(double pX, double pY, double pZ) {
		aLichtobjekt.setzePosition(pX, pY, pZ);
	}

	@Override
	public void resetSkalierungUndRotation() {
		aLichtobjekt.resetSkalierungUndRotation();
	}

	@Override
	public void setzeDurchsichtigkeit(double pAlpha) {
		aLichtobjekt.setzeDurchsichtigkeit(pAlpha);
	}

	@Override
	public void setzeSelbstleuchten(double pR, double pG, double pB) {
		aLichtobjekt.setzeSelbstleuchten(pR, pG, pB);
	}
}