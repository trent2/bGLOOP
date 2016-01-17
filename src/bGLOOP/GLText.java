package bGLOOP;

import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.awt.TextRenderer;

import bGLOOP.linalg.Matrix4;

/** Ermöglicht die Darstellung von Texten. Im Moment steht nur eine Schrift mit Serifen
 * zur Verfügung. Die Darstellung wird dabei mit den Bordmitteln von JOGL realisiert,
 * insbesondere ist es nicht notwendig, eine weitere Datei mit Zeichensätzen oder
 * Bildern in das Projektverzeichnis zu legen.
 * @author R. Spillner
 */
public class GLText extends TransformableObject implements IGLColorable {
	private String aText;
	private boolean aAutoRotation = false, aBgr = false, aCenter = false;
	// matrix for centering the text and auto rotating
	private Matrix4 autoPositionMatrix = new Matrix4();
	// private Logger log = Logger.getLogger(getClass().getName());
	private double aFSize, aBorderSize = 3;
	private static final int MAX_FONT_RENDER_SIZE = 72;
	private static final TextRenderer textWriter =
		new TextRenderer(new Font("Serif", Font.PLAIN, MAX_FONT_RENDER_SIZE), true, true);
	private int bufferName = -1;
	private float[] aTextColor = {1, 1, 1, 1};

	/** Erzeugt einen {@link GLText}-Objekt. Die Schriftgröße ist dabei ein Näherungswert an
	 * die Schriftgröße eines Textverarbeitungsprogramm (in Punkten), wenn der
	 * Text in der Standard-Ansicht (also <code>new GLKamera()</code> ohne weitere
	 * Transformation) im Ursprung gezeichnet wird.
	 * @param pX x-Koordinate der linken unteren Ecke des Textes
	 * @param pY y-Koordinate der linken unteren Ecke des Textes
	 * @param pZ z-Koordinate der linken unteren Ecke des Textes
	 * @param pSchriftgroesse Näherungswert an Schriftgröße in Punkten (s. Beschreibung oben)
	 * @param pText der darzustellende Text 
	 */
	public GLText(double pX, double pY, double pZ, double pSchriftgroesse, String pText) {
		aText = pText;
		verschiebe(pX, pY, pZ);
		aFSize = pSchriftgroesse;
		associatedRenderer.addObjectToRenderMap(GLTextur.NULL_TEXTURE, this);
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		conf.displayMode = Darstellungsmodus.FUELLEN;

		// color of background board, if enabled
		aDiffuse = new float[] { .3f, .3f, .3f, 1};

		aVisible = true;
	}

	/** Erzeugt einen {@link GLText}-Objekt. Die Schriftgröße wird dabei auf
	 * 12pt gesetzt (vgl. {@link #GLText(double, double, double, double, String) Beschreibung des
	 * obigen Konstruktors}).
	 * @param pX x-Koordinate der linken unteren Ecke des Textes
	 * @param pY y-Koordinate der linken unteren Ecke des Textes
	 * @param pZ z-Koordinate der linken unteren Ecke des Textes
	 * @param pText der darzustellende Text 
	 */
	public GLText(double pX, double pY, double pZ, String pText) {
		this(pX, pY, pZ, 12, pText);
	}

	/** Legt den darzustellenden Text fest.
	 * @param pText der darzustellende Text
	 */
	public synchronized void setzeText(String pText) {
		aText = pText;
		needsRedraw = true;
		scheduleRender();
	}

	/** Legt die Größe des Randes der Hintergrundtafel fest.
	 * Standardmäßig ist dies 3. Dabei ist der Abstand vom Text zum
	 * Tafelrand gemeint.
	 * @param pRand Abstand des Textes vom Rand der Hintergrundtafel
	 */
	public synchronized void setzeTafelrand(double pRand) {
		if(aBorderSize != (aBorderSize = pRand)) {
			needsRedraw = true;
			scheduleRender();
		}
	}

	/** Aktiviert die automatische Drehung des Textes. Dabei wird der Text in jeder
	 * Kameraposition optimal zur Kamera rotiert.
	 * @param pAutoDrehung Automatische Drehung wird aktiviert, wenn dieser Wert
	 * auf <code>true</code> gesetzt wird, sonst deaktiviert
	 */
	public synchronized void setzeAutodrehung(boolean pAutoDrehung) {
		if(!pAutoDrehung)
			autoPositionMatrix.loadIdentity();

		if (aAutoRotation != (aAutoRotation = pAutoDrehung))
			scheduleRender();
	}

	/** Aktiviert die automatische Zentrierung des Textes. Dies führt dazu, dass
	 * der Positionsreferenzpunkt des Objektes nicht die linke untere Ecke des
	 * Schriftzuges ist, sondern (wie in allen anderen positionierbaren von {@link GLObjekt}
	 * angeleiteten bGLOOP-Klassen üblich) der <em>Mittelpunkt</em>.
	 * @param pZentrierung Wenn <code>true</code>, dann ist der Mittelpunkt des Textes
	 * Referenzpunkt für die Darstellung, sonst die linke untere Ecke.
	 */
	public synchronized void setzeZentrierung(boolean pZentrierung) {
		if(aCenter != (aCenter = pZentrierung))
			scheduleRender();
	}

	/** Legt fest, ob ein rechteckigen Hintergrund für die Schrift gezeichnet wird.
	 * Die Farbe der Tafel wird mit {@link #setzeTafelFarbe(double, double, double)}
	 * festgelegt.
	 * @param pTafelAnzeigen <code>true</code>, um den Hintergrund anzuzeigen,
	 * <code>false</code>, um ihn zu deaktivieren
	 */
	public synchronized void setzeTafel(boolean pTafelAnzeigen) {
		aBgr = pTafelAnzeigen;
		needsRedraw = true;
		scheduleRender();
	}

	/** Setzt die Farbe der Hintergrundtafel für den Text. Der Hintergrund
	 * wird nur gezeichnet, wenn dies mittels {@link #setzeTafel(boolean)} aktiviert
	 * wurde. Die Standardeinstellung ist <code>(0.3,0.3,0.3)</code>, ein dunkles
	 * Grau. Die Parameterwerte müssen zwischen 0 und 1 liegen.
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 */
	public void setzeTafelFarbe(double pR, double pG, double pB) {
		super.setzeFarbe(pR, pG, pB);
	}

	/** Setzt die Textfarbe. Der Befehl legt die Farbe des darzustellenden Textes
	 * fest, standardmäßig ist das <code>(1,1,1)</code>, also weiß.
	 * Die Parameterwerte müssen zwischen 0 und 1 liegen.
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 */
	@Override
	public synchronized void setzeFarbe(double pR, double pG, double pB) {
		aTextColor[0] = (float) pR;
		aTextColor[1] = (float) pG;
		aTextColor[2] = (float) pB;
		scheduleRender();
	}

	/** Farbe des Textes.
	 * @return dreielementiges Array mit Rot-, Grün und Blauanteilen
	 */
	@Override
	public double[] gibFarbe() {
		return new double[] { aTextColor[0], aTextColor[1], aTextColor[2] };
	}

	/** Legt die Schriftgröße des darzustellenden Textes fest. Die Schriftgröße ist dabei ein Näherungswert an
	 * die Schriftgröße eines Textverarbeitungsprogramm (in Punkten), wenn der
	 * Text in der Standard-Ansicht (also <code>new GLKamera()</code> ohne weitere
	 * Transformation) im Ursprung gezeichnet wird.
	 * @param pSchriftgroesse Näherungswert an Schriftgröße in Punkten (s. Beschreibung oben)
	 */
	public synchronized void setzeSchriftgroesse(double pSchriftgroesse) {
		aFSize = pSchriftgroesse;
		needsRedraw = true;
		scheduleRender();
	}

	/** Gibt die Schriftgröße des dargestellten Textes zurück.
	 * @return Schriftgröße in Punkten
	 */
	public double gibSchriftgroesse() {
		return aFSize;
	}

	/** Gibt den dargestellenten Text zurück.
	 * @return der im Fenster dargestellte Text
	 */
	public String gibText() {
		return aText;
	}

	
	/* TODO The following two methods are private because they need a GLContext
	 * or they will crash. We need to ensure this somehow for the user thread
	 * to make them public.
	 */
	/** Berechnet die Breite des Textes.
	 * @return die Breite des Textes
	 */
	private double gibBreite() {
		return textWriter.getBounds(aText).getWidth() * aFSize / MAX_FONT_RENDER_SIZE;
	}

	/** Berechnet die Höhe des Textes.
	 * @return die Höhe des Textes
	 */
	private double gibHoehe() {
		return textWriter.getBounds(aText).getHeight() * aFSize / MAX_FONT_RENDER_SIZE;
	}

	@Override
	void renderDelegate(GL2 gl, GLU glu) {
		if(needsRedraw) {
			if (bufferName != -1)
				gl.glDeleteLists(bufferName, 1);

			bufferName = gl.glGenLists(1);

			renderText(gl);
			needsRedraw = false;
		}

		// the order of the next three lines is important
		centerText(aCenter);
		if(aAutoRotation)
			computeAutoRotation();

		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glMultMatrixf(transformationMatrix.getMatrix(), 0);
		if(aAutoRotation || aCenter)  // don't do any unnecessary work with the identity matrix
			gl.glMultMatrixf(autoPositionMatrix.getMatrix(), 0);

		if(!aAutoRotation)
			gl.glDisable(GL2.GL_CULL_FACE);

		gl.glCallList(bufferName);

		if(!aAutoRotation)
			gl.glEnable(GL2.GL_CULL_FACE);
	}

	@Override
	void loadMaterial(GL2 gl) {
		textWriter.setColor(aTextColor[0], aTextColor[1], aTextColor[2], aTextColor[3]);
		super.loadMaterial(gl);
	}

	private void computeAutoRotation() {
		Matrix4 rM = new Matrix4();
		float[] rm = rM.getMatrix();

		/*
		 * do an orthonormal basis transformation: - the vector from the
		 * camera's view point to the cameras position is the third base vector
		 * --> v3 - project the camera's up vector onto the plane where v3 is
		 * normal, normalize --> v2 - compute the first base vector by taking
		 * the genuine normalized vector which is orthogonal to v3 and v2 --> v1
		 *
		 * This basis is used for the transformation
		 */

		/*
		 * alternative for v3, see above for tM float[] v3 = {
		 * (float)(associatedCam.aPos[0]-tM[12]),
		 * (float)(associatedCam.aPos[1]-tM[13]),
		 * (float)(associatedCam.aPos[2]-tM[14]) };
		 */
		float[] v3 = new float[3];
		VectorUtil.subVec3(v3, associatedCam.aPos, associatedCam.aLookAt);
		VectorUtil.normalizeVec3(v3);

		float[] v2 = associatedCam.aUp.clone();

		float[] t = new float[3];
		VectorUtil.scaleVec3(t, v3, VectorUtil.dotVec3(v2, v3));
		VectorUtil.subVec3(v2, v2, t);
		VectorUtil.normalizeVec3(v2);

		float[] v1 = new float[3];
		VectorUtil.crossVec3(v1, v2, v3);
		VectorUtil.normalizeVec3(v1);

		rm[0] = v1[0];
		rm[1] = v1[1];
		rm[2] = v1[2];
		rm[4] = v2[0];
		rm[5] = v2[1];
		rm[6] = v2[2];
		rm[8] = v3[0];
		rm[9] = v3[1];
		rm[10] = v3[2];

		autoPositionMatrix.multMatrixFromLeft(rm);
	}

	private void renderText(GL2 gl) {
		gl.glNewList(bufferName, GL2.GL_COMPILE);

		if (aBgr) {
			double w = gibBreite();
			double h = gibHoehe();
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glColor3f(aDiffuse[0], aDiffuse[1], aDiffuse[2]);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-aBorderSize, -aBorderSize, 0);
			gl.glVertex3d(w+aBorderSize,  -aBorderSize, 0);
			gl.glVertex3d(w+aBorderSize, h+aBorderSize, 0);
			gl.glVertex3d( -aBorderSize, h+aBorderSize, 0);
			gl.glEnd();
			if(wconf.globalLighting)
				gl.glEnable(GL2.GL_LIGHTING);
		}
		textWriter.begin3DRendering();
		textWriter.draw3D(aText, 0, 0, 0, (float)aFSize/MAX_FONT_RENDER_SIZE);
		textWriter.end3DRendering();

		gl.glEndList();
	}

	private void centerText(boolean pDoCenter) {
		autoPositionMatrix.loadIdentity();
		float[] mat = autoPositionMatrix.getMatrix();
		if(pDoCenter) {
			mat[12] = -(float)gibBreite()/2;
			mat[13] = -(float)gibHoehe()/2;
		} else {
			mat[12] = 0;
			mat[13] = 0;
		}		
	}
}