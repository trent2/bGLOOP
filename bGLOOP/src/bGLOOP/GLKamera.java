package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
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

	private GLWindowConfig wconf;

	GLRenderer renderer;

	double[] aPos = { 0, 0, 500 };
	double[] aLookAt = { 0, 0, 0 };
	double[] aUp = { 0, 1, 0 };

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
		this(GLWindowConfig.defaultWindowConfig.globalDefaultWidth,
				GLWindowConfig.defaultWindowConfig.globalDefaultHeight, pVollbild, pKeineDekoration);
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
		wconf = new GLWindowConfig();

		renderer = new GLRenderer(wconf, width, height, this, pVollbild, pKeineDekoration);
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

	GLRenderer getRenderer() {
		return renderer;
	}

	GLWindowConfig getWconf() {
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
		renderer.scheduleRender();
	}

	/** Setzt den Blickpunkt der Kamera. Der Blickpunkt ist der Punkt, der in der
	 * Mitte des Kamerafensters liegt, auf den die Kamera also zentriert blickt.
	 * 
	 * @param pX x-Koordinate des Blickpunkts
	 * @param pY y-Koordinate des Blickpunkts
	 * @param pZ z-Koordinate des Blickpunkts
	 */
	synchronized public void setzeBlickpunkt(double pX, double pY, double pZ) {
		aLookAt[0] = pX;
		aLookAt[1] = pY;
		aLookAt[2] = pZ;
		renderer.scheduleRender();
	}

	/** Setzt die Position der Kamera.
	 * 
	 * @param pX x-Koordinate der Position
	 * @param pY y-Koordinate der Position
	 * @param pZ z-Koordinate der Position
	 */
	synchronized public void setzePosition(double pX, double pY, double pZ) {
		aPos[0] = pX;
		aPos[1] = pY;
		aPos[2] = pZ;
		renderer.scheduleRender();
	}

	/** Dreht die Kamera um den angegebenen Winkel um die x-Achse im Koordinatensystem.
	 * @param pWinkel Drehwinkel in Grad
	 */
	synchronized public void dreheUmXAchse(double pWinkel) {
		double s, c, t;
		s = Math.sin(Math.toRadians(pWinkel));
		c = Math.cos(Math.toRadians(pWinkel));

		t = aPos[1];
		// rotate around x-axis
		aPos[1] = t * c - aPos[2] * s;
		aPos[2] = aPos[2] * c + t * s;

		t = aUp[1];
		aUp[1] = t * c - aUp[2] * s;
		aUp[2] = aUp[2] * c + t * s;

		renderer.scheduleRender();
	}

	/** Dreht die Kamera um den angegebenen Winkel um die y-Achse im Koordinatensystem.
	 * @param pWinkel Drehwinkel in Grad
	 */
	synchronized public void dreheUmYAchse(double pWinkel) {
		double s, c, t;
		s = Math.sin(Math.toRadians(pWinkel));
		c = Math.cos(Math.toRadians(pWinkel));

		t = aPos[0];
		// rotate around y-axis
		aPos[0] = t * c - aPos[2] * s;
		aPos[2] = aPos[2] * c + t * s;

		renderer.scheduleRender();
	}

	/** Dreht die Kamera um die angegebene Achse im Raum. Die Achse wird
	 * durch eine Gerade in Parameterform beschrieben. Daher muss insbesondere
	 * der Vektor <em>&lt;pRX, pRY, pRZ&gt;&ne;&lt;0, 0, 0&gt;</em> sein.
	 * @throws IllegalArgumentException Diese Ausnahme wird geworfen, wenn der Richtungsvektor
	 *   der Gerade der Nullvektor ist.
	 * @param pWinkel Drehwinkel in Grad
	 * @param pNX x-Koordinate des Ortsvektors der Geradendarstellung  
	 * @param pNY y-Koordinate des Ortsvektors der Geradendarstellung
	 * @param pNZ z-Koordinate des Ortsvektors der Geradendarstellung
	 * @param pRX x-Koordinate des Richtungsvektors der Geradendarstellung
	 * @param pRY y-Koordinate des Richtungsvektors der Geradendarstellung
	 * @param pRZ z-Koordinate des Richtungsvektors der Geradendarstellung
	 */
	synchronized public void drehe(double pWinkel, double pNX, double pNY, double pNZ, double pRX, double pRY,
			double pRZ) throws IllegalArgumentException {
		if(pRX == 0 && pRY == 0 && pRZ == 0)
			throw new IllegalArgumentException("Richtungsvektor darf nicht der Nullvektor sein");

		
	}

	void renderPreObjects(GL2 gl, GLU glu) {
		// camera position and look-at point
		glu.gluLookAt(aPos[0], aPos[1], aPos[2], aLookAt[0], aLookAt[1], aLookAt[2], aUp[0], aUp[1], aUp[2]);
	}

	void renderPostObjects(GL2 gl, GLU glu) {
		if (wconf.aDisplayAxes)
			drawAxes(gl);
	}

	/*
	 * public void rotiere_defekt(double pWinkel, double pPunktX, double
	 * pPunktY, double pPunktZ, double pRichtungX, double pRichtungY, double
	 * pRichtungZ) { double x = aPos[0], y = aPos[1], z = aPos[2]; double a =
	 * pPunktX, b = pPunktY, c = pPunktZ; double n = Math.sqrt(pRichtungX *
	 * pRichtungX + pRichtungY * pRichtungY + pRichtungZ * pRichtungZ); double u
	 * = pRichtungX / n, v = pRichtungY / n, w = pRichtungZ / n;
	 * 
	 * aPos[0] = (a * (v * v + w * w) - u (b * v + c * w - u * x - v * y - w *
	 * z)) (1 - Math.cos(pWinkel)) + x Math.cos(pWinkel) + (-c * v + b * w - w *
	 * y + v * z) * Math.sin(pWinkel); aPos[1] = (a * (u * u + w * w) - v (a * u
	 * + c * w - u * x - v * y - w * z)) (1 - Math.cos(pWinkel)) + y
	 * Math.cos(pWinkel) + (c * u - a * w + w * x - u * z) * Math.sin(pWinkel);
	 * aPos[2] = (v * (u * u + v * v) - w (a * u + b * v - u * x - v * y - w *
	 * z)) (1 - Math.cos(pWinkel)) + z Math.cos(pWinkel) + (-b * u + a * v - v *
	 * x + u * y) * Math.sin(pWinkel); x = aUp[0]; y = aUp[1]; z = aUp[2];
	 * aUp[0] = (a * (v * v + w * w) - u (b * v + c * w - u * x - v * y - w *
	 * z)) (1 - Math.cos(pWinkel)) + x Math.cos(pWinkel) + (-c * v + b * w - w *
	 * y + v * z) * Math.sin(pWinkel); aUp[1] = (a * (u * u + w * w) - v (a * u
	 * + c * w - u * x - v * y - w * z)) (1 - Math.cos(pWinkel)) + y
	 * Math.cos(pWinkel) + (c * u - a * w + w * x - u * z) * Math.sin(pWinkel);
	 * aUp[2] = (v * (u * u + v * v) - w (a * u + b * v - u * x - v * y - w *
	 * z)) (1 - Math.cos(pWinkel)) + z Math.cos(pWinkel) + (-b * u + a * v - v *
	 * x + u * y) * Math.sin(pWinkel); renderer.scheduleRender(); }
	 */

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
		renderer.getWindow().setFullscreen(pVollbild);
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
		renderer.scheduleRender();
	}

	/**
	 * Aktiviert oder deaktiviert die Darstellung der Koordinatenachsen.
	 * 
	 * @see #zeigeAchsen(double)
	 * @param pZeigeAchsen
	 *            Wenn <code>true</code>, dann wird die Achsendarstellung
	 *            aktiviert, wenn <code>false</code>, so wird sie deaktiviert.
	 */
	public void zeigeAchsen(boolean pZeigeAchsen) {
		wconf.aDisplayAxes = pZeigeAchsen;
		zeigeAchsen(wconf.axesLength);
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
		renderer.scheduleRender();
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
	 *            Schrittweite der Bewegung. Die Schrittweite entspricht den
	 *            Koordinaten der 3D-Welt.
	 */
	synchronized public void vor(double pSchrittweite) {
		float[] dir = new float[3];
		float[] pos = { (float) this.aPos[0], (float) this.aPos[1], (float) this.aPos[2] };
		float[] lookAt = { (float) this.aLookAt[0], (float) this.aLookAt[1], (float) this.aLookAt[2] };

		VectorUtil.subVec3(dir, pos, lookAt);
		VectorUtil.normalizeVec3(dir);
		VectorUtil.scaleVec3(dir, dir, (float) pSchrittweite);
		VectorUtil.addVec3(pos, pos, dir);
		VectorUtil.addVec3(lookAt, lookAt, dir);
		this.aPos[0] = pos[0];
		this.aPos[1] = pos[1];
		this.aPos[2] = pos[2];
		this.aLookAt[0] = lookAt[0];
		this.aLookAt[1] = lookAt[1];
		this.aLookAt[2] = lookAt[2];
		renderer.scheduleRender();
	}

	private void drawAxes(GL2 gl) {
		double axesLength = wconf.axesLength;
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glLineWidth(wconf.axesWidth);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < axesLength; i += 20) {
			gl.glColor3f(1, 1, 1);
			gl.glVertex3f(i, 0, 0);
			gl.glColor3f(1, 0, 0);
			gl.glVertex3f(i + 1, 0, 0);
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f((float) axesLength, 0, 0);
		gl.glVertex3f((float) axesLength - 20, -10, 0);
		gl.glVertex3f((float) axesLength, 0, 0);
		gl.glVertex3f((float) axesLength - 20, 10, 0);
		gl.glEnd();

		gl.glBegin(3);
		for (int i = 0; i < axesLength; i += 20) {
			gl.glColor3f(1, 1, 1);
			gl.glVertex3f(0, i, 0);
			gl.glColor3f(0, 1, 0);
			gl.glVertex3f(0, i + 1, 0);
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f(0, (float) axesLength, 0);
		gl.glVertex3f(10, (float) axesLength - 20, 0);
		gl.glVertex3f(0, (float) axesLength, 0);
		gl.glVertex3f(-10, (float) axesLength - 20, 0);
		gl.glEnd();

		gl.glBegin(3);
		for (int i = 0; i < axesLength; i += 20) {
			gl.glColor3f(1, 1, 1);
			gl.glVertex3f(0, 0, i);
			gl.glColor3f(0, 0, 1);
			gl.glVertex3f(0, 0, i + 1);
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f(0, 0, (float) axesLength);
		gl.glVertex3f(0, 10, (float) axesLength - 20);
		gl.glVertex3f(0, 0, (float) axesLength);
		gl.glVertex3f(0, -10, (float) axesLength - 20);
		gl.glEnd();

		if (wconf.globalLighting)
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glLineWidth(wconf.wireframeLineWidth);
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
}