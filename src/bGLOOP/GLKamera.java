package bGLOOP;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.util.logging.Logger;

import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;

/** Klasse, die eine virtuelle Kamera beschreibt, die die 3D-Szene betrachtet.
 * Sie bietet eine Reihe von Diensten zur Manipultion der Kamera (wie etwas
 * Drehen und Verschieben). Eine Kamera öffnet automatisch ein Fenster, in dem
 * dargestellt wird, was die Kamera sieht.
 * 
 * @author R. Spillner
 *
 */
public class GLKamera {

	// false uses NEWT
	private static GLKamera activeCamera;
    Logger log = Logger.getLogger("bGLOOP");

	private WindowConfig wconf;

	GLRenderer associatedRenderer;

	float[] aPos = { 0, 0, 500 };
	float[] aLookAt = { 0, 0, 0 };
	float[] aUp = { 0, 1, 0 };

	GLObjekt selectedObj = null;

	/**
	 * Erstellt eine bGLOOP-Kamera. Die Kamera öffnet ein Fenster mit den
	 * Abmessungen aus der in der bGLOOP-Konfigurationsdatei eingetragenen
	 * Standardgrößen.
	 */
	public GLKamera() {
		this(false, false);
	}

	public GLKamera(boolean pVollbild) {
		this(pVollbild, false);
	}

	/**
	 * Erstellt eine bGLOOP-Kamera mit zusätzlichen Fenster-Parametern. Das
	 * Fenster kann dabei im Vollbildmodus und/oder mit/ohne Fenstermanager-
	 * Dekorationen angezeigt werden.
	 * 
	 * @param pVollbild
	 *            Wenn <code>true</code>, dann wird die Anzeige direkt in den
	 *            Vollbildmodus geschaltet. Wenn <code>false</code>, dann nicht
	 * @param pKeineDekoration
	 *            Wenn <code>true</code>, dann wird das Anzeigefenster ohne
	 *            Dekoration des Fenstermanagers gezeichnet, wenn
	 *            <code>false</code>, dann nicht.
	 */
	public GLKamera(boolean pVollbild, boolean pKeineDekoration) {
		this(WindowConfig.defaultWindowConfig.globalDefaultWidth,
				WindowConfig.defaultWindowConfig.globalDefaultHeight, pVollbild, pKeineDekoration);
	}

	/**
	 * Erstellt eine bGLOOP-Kamera mit vorgegebener Breite und Höhe. Das Fenster
	 * wird nicht im Vollbild-Modus geöffnet.
	 * 
	 * @param width
	 *            Die Breite des Anzeigefensters
	 * @param height
	 *            Die Höhe des Anzeigefensters
	 */
	public GLKamera(int width, int height) {
		this(width, height, false, false);
	}

	private GLKamera(int width, int height, boolean pVollbild, boolean pKeineDekoration) {
		activeCamera = this;
		wconf = new WindowConfig();
		log.setLevel(wconf.loggingLevel);

		associatedRenderer = new GLRenderer(wconf, width, height, this, pVollbild, pKeineDekoration);
	}

	/** Rückgabe der aktiven Kamera. Die aktive Kamera ist die zuletzt erstellte
	 * bzw. mit {@link #setzeAktiveKamera(GLKamera)} aktiviert Kamera.
	 * 
	 * @return die aktive Kamera
	 */
	public static GLKamera aktiveKamera() {
		return activeCamera;
	}

	/** Setzt die momentan aktive Kamera.
	 * 
	 * @param activeCamera
	 *            Kameraobjekt, das als aktive Kamera gesetzt werden soll.
	 */
	public static void setzeAktiveKamera(GLKamera activeCamera) {
		GLKamera.activeCamera = activeCamera;
	}

	WindowConfig getWconf() {
		return wconf;
	}

	/** Aktiviert die Beleuchtung der Szene. Ist dies deaktiviert, werden alle
	 * Objekte in einem gleichmäßigen Licht dargestellt, alle vorhandenen
	 * Lichtquellen werden also ignoriert.
	 * 
	 * @param pBeleuchtungAn Wenn <code>true</code>, dann wird die Beleuchtung
	 *       angeschaltet, wenn <code>false</code>, dann wird sie deaktiviert.
	 */
	synchronized public void beleuchtungAktivieren(boolean pBeleuchtungAn) {
		wconf.globalLighting = pBeleuchtungAn;
		associatedRenderer.scheduleRender();
	}

	/** Setzt den Blickpunkt der Kamera. Der Blickpunkt ist der Punkt, der in der
	 * Mitte des Kamerafensters liegt, auf den die Kamera also zentriert blickt.
	 * 
	 * @param pX x-Koordinate des Blickpunkts
	 * @param pY y-Koordinate des Blickpunkts
	 * @param pZ z-Koordinate des Blickpunkts
	 */
	synchronized public void setzeBlickpunkt(double pX, double pY, double pZ) {
		aLookAt[0] = (float)pX;
		aLookAt[1] = (float)pY;
		aLookAt[2] = (float)pZ;
		computeUpVector();
		associatedRenderer.scheduleRender();
	}

	/** Richtet die Kamera so aus, dass "oben" auf dem Bildschirm der positiven
	 * y-Achsen-Richtung entspricht. Dabei wird außerdem die y-Kameraposition auf die
	 * y-Position der Kamera auf die des Kamera-Blickpunktes gesetzt. 
	 */
	synchronized public void setzeStandardOben() {
		aPos[1] = aLookAt[1];
		aUp[0] = 0;
		aUp[1] = 1;
		aUp[2] = 0;
		computeUpVector();
		associatedRenderer.scheduleRender();
	}

	/** Setzt die Position der Kamera.
	 * 
	 * @param pX x-Koordinate der Position
	 * @param pY y-Koordinate der Position
	 * @param pZ z-Koordinate der Position
	 */
	synchronized public void setzePosition(double pX, double pY, double pZ) {
		aPos[0] = (float)pX;
		aPos[1] = (float)pY;
		aPos[2] = (float)pZ;
		computeUpVector();
		associatedRenderer.scheduleRender();
	}

	/** Dreht die Kamera um den angegebenen Winkel um die x-Achse im Koordinatensystem.
	 * @param pWinkel Drehwinkel in Grad
	 */
	synchronized public void dreheUmXAchse(double pWinkel) {
		double s, c, t;
		s = sin(toRadians(pWinkel));
		c = cos(toRadians(pWinkel));

		t = aPos[1];
		// rotate around x-axis
		aPos[1] = (float)(t * c - aPos[2] * s);
		aPos[2] = (float)(aPos[2] * c + t * s);

		t = aUp[1];
		aUp[1] = (float)(t * c - aUp[2] * s);
		aUp[2] = (float)(aUp[2] * c + t * s);

		associatedRenderer.scheduleRender();
	}

	/** Dreht die Kamera um den angegebenen Winkel um die y-Achse im Koordinatensystem.
	 * @param pWinkel Drehwinkel in Grad
	 */
	synchronized public void dreheUmYAchse(double pWinkel) {
		double s, c, t;
		s = sin(toRadians(pWinkel));
		c = cos(toRadians(pWinkel));

		t = aPos[0];
		// rotate around y-axis
		aPos[0] = (float)(t * c - aPos[2] * s);
		aPos[2] = (float)(aPos[2] * c + t * s);

		associatedRenderer.scheduleRender();
	}

	/** Dreht die Kamera um eine angegebene Achse im Raum.
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
	synchronized public void drehe(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX,
			double pRichtY, double pRichtZ) throws IllegalArgumentException {
		if (pRichtX == 0 && pRichtY == 0 && pRichtZ == 0)
			throw new IllegalArgumentException("Richtungsvektor darf nicht der Nullvektor sein");

		float[] axis = new float[] { (float)pRichtX, (float)pRichtY, (float)pRichtZ };
		VectorUtil.normalizeVec3(axis);
		Quaternion rot = new Quaternion().rotateByAngleNormalAxis((float) toRadians(pWinkel), axis[0], axis[1], axis[2]);

		aPos[0] -= pOrtX; aPos[1] -= pOrtY; aPos[2] -= pOrtZ;
		aUp[0] -= pOrtX; aUp[1] -= pOrtY; aUp[2] -= pOrtZ;
		rot.rotateVector(aPos, 0, aPos, 0);
		rot.rotateVector(aUp, 0, aUp, 0);
		aPos[0] += pOrtX; aPos[1] += pOrtY; aPos[2] += pOrtZ;
		aUp[0] += pOrtX; aUp[1] += pOrtY; aUp[2] += pOrtZ;

		associatedRenderer.scheduleRender();
	}

	/**
	 * Aktiviert oder deaktiviert den Vollbildmodus des Anzeigefensters. <br>
	 * Mit der Einstellung <code>DEFAULT_WINDOW_MODE=NEWT</code> gibt es einen
	 * echten Vollbildmodus. Ist die Einstellung auf <code>AWT</code> gesetzt,
	 * so wird das Fenster maximiert angezeigt, aber mit Fensterdekoration des
	 * Fenstermanagers. Es bleibt dann verschiebbar.
	 * 
	 * @param pVollbild
	 *            Wenn <code>true</code>, so wird der Vollbildmodus aktiviert,
	 *            wenn <code>false</code>, dann wird auf Fenstermodus
	 *            geschaltet.
	 */
	public void setzeVollbildmodus(boolean pVollbild) {
		associatedRenderer.getWindow().setFullscreen(pVollbild);
	}

	/**
	 * Aktiviert die Darstellung der Koordinatenachsen. Die Länge der Achsen
	 * kann dabei angegeben werden.
	 * 
	 * @param pAchsenlaenge
	 *            Länge für die Koordinatenachsen
	 */
	synchronized public void zeigeAchsen(double pAchsenlaenge) {
		wconf.axesLength = pAchsenlaenge;
		associatedRenderer.scheduleRender();
	}

	/**
	 * Aktiviert oder deaktiviert die Darstellung der Koordinatenachsen.
	 * 
	 * @see #zeigeAchsen(double)
	 * @param pZeigeAchsen
	 *            Wenn <code>true</code>, dann wird die Achsendarstellung
	 *            aktiviert, wenn <code>false</code>, so wird sie deaktiviert.
	 */
	public synchronized void zeigeAchsen(boolean pZeigeAchsen) {
		wconf.aDisplayAxes = pZeigeAchsen;
		associatedRenderer.scheduleRender();
	}

	/**
	 * Zeichnet den Blickpunkt ins Kamerafenster.
	 * @param pBlickpunktZeichnen
	 *            Wenn <code>true</code>, dann wird der Blickpunkt im Kamerafenster
	 *            angezeigt, wenn <code>false</code>, dann nicht.
	 */
	public synchronized void zeigeBlickpunkt(boolean pBlickpunktZeichnen) {
		wconf.aDrawLookAt = pBlickpunktZeichnen;
		associatedRenderer.scheduleRender();
	}
	

	/**
	 * Zeigt alle beweglichen bGLOOP-Objekte als Drahtgittermodelle an.
	 * Ausgenommen davon sind Himmel und Boden &mdash; diese werden im
	 * Drahtgittermodus gar nicht dargestellt.
	 * 
	 * @param pZeigeGitter
	 *            Wenn <code>true</code>, so werden alle beweglichen Objekte als
	 *            Drahtgittermodell dargestellt, wenn <code>false</code> werden
	 *            sie gemäß ihrer eigenen Konfiguration dargestellt.
	 */
	synchronized public void setzeGittermodelldarstellung(boolean pZeigeGitter) {
		wconf.aWireframe = pZeigeGitter;
		associatedRenderer.scheduleRender();
	}

	/**
	 * Gibt an, ob im Moment die Drahtgitterdarstellung für alle bGLOOP-Objekte
	 * gewählt ist.
	 * 
	 * @return <code>true</code>, wenn die Drahtgitterdarstellung für alle
	 *         Objekte gewählt ist, sonst <code>false</code>.
	 */
	public boolean istDrahtgittermodell() {
		return wconf.aWireframe;
	}

	/**
	 * Bewegt die Kamera auf ihren Blickpunkt zu.
	 * 
	 * @param pSchrittweite
	 *            Schrittweite der Bewegung. Die Schrittweite ist in der Maßeinheit
	 *            des Koordinatensystems der 3D-Welt.
	 */
	synchronized public void vor(double pSchrittweite) {
		float[] dir = new float[3];

		VectorUtil.subVec3(dir, aLookAt, aPos);
		VectorUtil.normalizeVec3(dir);
		VectorUtil.scaleVec3(dir, dir, (float) pSchrittweite);
		VectorUtil.addVec3(aPos, aPos, dir);
		VectorUtil.addVec3(aLookAt, aLookAt, dir);

		associatedRenderer.scheduleRender();
	}

	/** Gibt das {@link GLObjekt} zurück, was im Kamerafenster an der gegebenen
	 * Position ist.
	 * <p>
	 * Dabei beziehen sich die Koordinaten der Parameter auf
	 * das Kamerafenster &mdash; hier hat die linke obere Ecke die Koordinaten
	 * <code>(0|0)</code>. {@link GLMaus Maus}-Objekte liefern ebenfalls Kamerafenster-
	 * Koordinaten mittels der Methoden {@link GLMaus#gibX() gibX} und {@link GLMaus#gibY()
	 * gibY} zurück, so dass man auf diese Art und Weise einen einfachen Objekt-
	 * Selektionsmechanismus mit der Maus realisieren kann:</p>
	 * <pre>
	 *   <b>if</b>(maus.linksklick())
	 *     GLObjekt selectedObject = kamera.gibObjekt(maus.gibX(), maus.gibY());
	 * </pre>
	 * <p>
	 * Liegen an der Mausposition mehrere Objekte hintereinander, so wird das vorderste
	 * Objekt zurückgegeben. Die Methode liefert <code>null</code>, wenn kein Objekt
	 * an der Stelle liegt.
	 * </p>
	 * @param pFensterX x-Koordinate, an der nach einem GLObjekt gesucht wird
	 * @param pFensterY y-Koordinate, an der nach einem GLObjekt gesucht wird
	 * @return {@link GLObjekt}-Objekt oder <code>null</code>
	 */
	synchronized public GLObjekt gibObjekt(int pFensterX, int pFensterY) {
		GLObjekt r;
		log.info("scheduling an object selection run");
		associatedRenderer.scheduleSelection(pFensterX, pFensterY);
		try { // wait for selection mechanism to finish
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		r = selectedObj;
		selectedObj = null;
		return r;
	}
	
	/** Gibt die x-Koordinate des Blickpunkts der Kamera zurück.
	 * @return x-Koordinate des Blickpunkts
	 */
	public double gibBlickpunktX() {
		return aLookAt[0];
	}

	/** Gibt die y-Koordinate des Blickpunkts der Kamera zurück.
	 * @return y-Koordinate des Blickpunkts
	 */
	public double gibBlickpunktY() {
		return aLookAt[1];
	}

	/** Gibt die z-Koordinate des Blickpunkts der Kamera zurück.
	 * @return z-Koordinate des Blickpunkts
	 */
	public double gibBlickpunktZ() {
		return aLookAt[2];
	}

	/** Liefert die Breite des Kamerafensters.
	 * @return Breite des Kamerafensters
	 */
	public int gibBreite() {
		return wconf.globalDefaultWidth;
	}

	/** Liefert die Höhe des Kamerafensters.
	 * @return Höhe des Kamerafensters
	 */
	public int gibHoehe() {
		return wconf.globalDefaultHeight;
	}

	/** Gibt die x-Position der Kamera.
	 * @return x-Position der Kamera
	 */
	public double gibX() {
		return aPos[0];
	}

	/** Gibt die y-Position der Kamera.
	 * @return y-Position der Kamera
	 */
	public double gibY() {
		return aPos[1];
	}

	/** Gibt die z-Position der Kamera.
	 * @return z-Position der Kamera
	 */
	public double gibZ() {
		return aPos[2];
	}

	/** Erstellt ein Bildschirmfoto und speichert es unter dem in der
	 * bGLOOP-Konfigurationsdatei festgelegten Standardnamen.
	 */
	public void bildschirmfoto() {
		associatedRenderer.scheduleScreenshot(null);
	}

	/** Erstellt ein Bildschirmfoto und speichert es unter dem übergebenen
	 * Dateinamen.
	 * 
	 * @param pDateiname Dateiname des Screenshots
	 */
	public void bildschirmfoto(String pDateiname) {
		associatedRenderer.scheduleScreenshot(pDateiname);
	}

	/* The method the scalar product of the lookAt vector with the up vector. If all is
	 * good, this should be very close to 0. 
	 * If checkCameraVectors() > 2 * FloatUtil.EPSILON, the camera vectors
	 * are considered to be faulty. The float's epsilon is used, because the
	 * aPos, aLookAt, and aUp are float vectors. 
	 */
	double checkCameraVectors() {
		return Math.abs(aUp[0] * (aPos[0] - aLookAt[0]) + aUp[1] * (aPos[1] - aLookAt[1]) + aUp[2] * (aPos[2] - aLookAt[2]));
	}

	private void computeUpVector() {
		final float res[] = new float[3];
		VectorUtil.subVec3(aPos, aPos, aLookAt);
		VectorUtil.crossVec3(res, aUp, aPos);
		VectorUtil.crossVec3(aUp, aPos, res);
		VectorUtil.normalizeVec3(aUp);
		VectorUtil.addVec3(aPos, aPos, aLookAt);
	}
}