package bGLOOP;

import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import bGLOOP.linalg.Matrix4;

/** Ermöglicht die Darstellung von Texten. Im Moment steht nur eine (Serifen-)Schrift
 * zur Verfügung.
 * @author R. Spillner
 */
public class GLText extends TransformableObject {
	private String aText;
	private boolean aAutoRotation = false;
	private Matrix4 autoRotateMatrix;
//    private Logger log = Logger.getLogger(getClass().getName());
    private double aFSize;
    private static final int MAX_FONT_RENDER_SIZE = 72;
	private static final TextRenderer textWriter = 
			new TextRenderer(new Font("Serif", Font.PLAIN, MAX_FONT_RENDER_SIZE), true, true);
	private int bufferName = -1;
	private float[] aRMs;

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
		autoRotateMatrix = new Matrix4();
		aFSize = pSchriftgroesse;
		associatedRenderer.getNoTextureItemList().add(this);
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		conf.displayMode = Darstellungsmodus.FUELLEN;

		aVisible = true;
	}

	/** Erzeugt einen {@link GLText}-Objekt. Die Schriftgröße wird dabei auf
	 * 12pt gesetzt (vgl. {@link #GLText(double, double, double, double, String) Beschreibung des
	 * obigen Konstruktors}.
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
	public void setzeText(String pText) {
		aText = pText;
		associatedRenderer.scheduleRender();
	}

	/** Aktiviert die automatische Drehung des Textes. Dabei wird der Text in jeder
	 * Kameraposition optimal zur Kamera rotiert.
	 * @param pAutoDrehung Automatische Drehung wird aktiviert, wenn dieser Wert
	 * auf <code>true</code> gesetzt wird, sonst deaktiviert
	 */
	public void setzeAutodrehung(boolean pAutoDrehung) {
		if (aAutoRotation != (aAutoRotation = pAutoDrehung))
			scheduleRender();
	}

	/** Legt die Schriftgröße des darzustellenden Textes fest. Die Schriftgröße ist dabei ein Näherungswert an
	 * die Schriftgröße eines Textverarbeitungsprogramm (in Punkten), wenn der
	 * Text in der Standard-Ansicht (also <code>new GLKamera()</code> ohne weitere
	 * Transformation) im Ursprung gezeichnet wird.
	 * @param pSchriftgroesse Näherungswert an Schriftgröße in Punkten (s. Beschreibung oben)
	 */
	public void setzeSchriftgroesse(double pSchriftgroesse) {
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

	@Override
	void renderDelegate(GL2 gl, GLU glu) {
		if(needsRedraw) {
			if (bufferName != -1)
				gl.glDeleteLists(bufferName, 1);

			bufferName = gl.glGenLists(1);

			renderText(gl);
			needsRedraw = false;
		}

		computeAutoRotation();

		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glMultMatrixf(transformationMatrix.getMatrix(), 0);
		gl.glMultMatrixf(aRMs, 0);

		gl.glCallList(bufferName);
	}

	private void computeAutoRotation() {
		aRMs = autoRotateMatrix.getMatrix();

		if(aAutoRotation) {
			/* do an orthonormal basis transformation:
			 * - the vector from the camera's view point to the cameras position
			 *   is the third base vector --> v3
			 * - project the camera's up vector onto the plane where v3
			 *   is normal, normalize --> v2
			 * - compute the first base vector by taking the genuine
			 *   normalized vector which is orthogonal to v3 and v2 --> v1
			 *
			 * This basis is used for the transformation
			 */

			/* alternative for v3, see above for tM
			  float[] v3 = {  (float)(associatedCam.aPos[0]-tM[12]),
			 				(float)(associatedCam.aPos[1]-tM[13]),
			 				(float)(associatedCam.aPos[2]-tM[14]) };
			 */
			float[] v3 = {  (float)(associatedCam.aPos[0]-associatedCam.aLookAt[0]),
					(float)(associatedCam.aPos[1]-associatedCam.aLookAt[1]),
					(float)(associatedCam.aPos[2]-associatedCam.aLookAt[2]) };

			VectorUtil.normalizeVec3(v3);

			float[] v2 = { (float) associatedCam.aUp[0], (float) associatedCam.aUp[1], (float) associatedCam.aUp[2] };
			float[] t = new float[3];
			VectorUtil.scaleVec3(t, v3, VectorUtil.dotVec3(v2, v3));
			VectorUtil.subVec3(v2, v2, t);
			VectorUtil.normalizeVec3(v2);

			float[] v1 = new float[3];
			VectorUtil.crossVec3(v1, v2, v3);
			VectorUtil.normalizeVec3(v1);

			aRMs[0] = v1[0]; aRMs[1] = v1[1];  aRMs[2] = v1[2];
			aRMs[4] = v2[0]; aRMs[5] = v2[1];  aRMs[6] = v2[2];
			aRMs[8] = v3[0]; aRMs[9] = v3[1]; aRMs[10] = v3[2];
		}
	}

	private void renderText(GL2 gl) {
		if(!aAutoRotation)
			gl.glDisable(GL2.GL_CULL_FACE);
		/*  // Draw test plane
		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3d(0,0,1);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(20, 0, 0);
		gl.glVertex3d(20, 10, 0);
		gl.glVertex3d(0, 10, 0);
		gl.glEnd();
		*/

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		textWriter.begin3DRendering();
		textWriter.draw3D(aText, 0, 0, 0, (float)aFSize/MAX_FONT_RENDER_SIZE);
		textWriter.end3DRendering();

		if(!aAutoRotation)
			gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEndList();
	}

	@Override
	void loadMaterial(GL2 gl) {
		textWriter.setColor(aDiffuse[0], aDiffuse[1], aDiffuse[2], aDiffuse[3]);
		super.loadMaterial(gl);
	}
}
