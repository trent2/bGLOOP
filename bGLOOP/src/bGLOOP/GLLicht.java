package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Punktlichtquelle mit unendlicher Reichweite.
 * 
 * @author R. Spillner
 */
public class GLLicht extends GLDisplayItem {
	private final static int LIGHT_NUMS[] = { GL2.GL_LIGHT0, GL2.GL_LIGHT1, GL2.GL_LIGHT2, GL2.GL_LIGHT3, GL2.GL_LIGHT4,
			GL2.GL_LIGHT5, GL2.GL_LIGHT6, GL2.GL_LIGHT7 };
	private static int lightCount = 0;
	private int id;
	private float[] lightAmbientValue = { 0.15f, 0.15f, 0.15f, 1.0f };
	private float[] lightDiffuseValue = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightSpecularValue = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightDiffusePosition;
	private boolean isOn = true;

	/** Eine Lichtquelle an der Position <code>L(pX, pY, pZ)</code>
	 * 
	 * @param pX x-Koordinate der Lichtquelle
	 * @param pY y-Koordinate der Lichtquelle
	 * @param pZ z-Koordinate der Lichtquelle
	 */
	public GLLicht(double pX, double pY, double pZ) {
		lightDiffusePosition = new float[] { (float) pX, (float) pY, (float) pZ, 1.0f };
		id = lightCount++;
		associatedCam = GLKamera.aktiveKamera();
		(associatedRenderer = associatedCam.getRenderer()).getNoTextureItemList().add(this);

	}

	/** Eine Lichtquelle an der Position <code>L(-10000, 10000, 10000)</code>.
	 */
	public GLLicht() {
		this(-10000, 10000, 10000);
	}

	/** Setzt den Anteil des Streulichts der Lichtquelle. Die Parameterwerte
	 * müssen zwischen 0 und 1 liegen.
	 * 
	 * @param pR Rotanteil
	 * @param pG Grünanteil
	 * @param pB Blauanteil
	 */
	synchronized public void setzeAmbientBeleuchtung(double pR, double pG, double pB) {
		lightAmbientValue[0] = (float)pR;
		lightAmbientValue[1] = (float)pG;
		lightAmbientValue[2] = (float)pB;
		associatedRenderer.scheduleRender();
	}

	public void setzeFarbe(double pR, double pG, double pB) {
		lightDiffuseValue[0] = (float)pR;
		lightDiffuseValue[1] = (float)pG;
		lightDiffuseValue[2] = (float)pB;
		associatedRenderer.scheduleRender();
	}

	void setzeFarbe(double[] pCol) {
		setzeFarbe(pCol[0], pCol[1], pCol[2]);
	}

	/**
	 * Aktiviert oder deaktiviert die Lichtquelle.
	 * 
	 * @param an
	 *            Wenn <code>true</code>, dann wird die Lichtquelle aktiviert,
	 *            wenn <code>false</code>, dann wird sie deaktiviert.
	 */
	public synchronized void lichtAn(boolean an) {
		isOn = an;
		associatedRenderer.scheduleRender();
	}

	@Override
	void render(GL2 gl, GLU glu) {
		if (isOn && associatedCam.getWconf().globalLighting) {
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_AMBIENT, lightAmbientValue, 0);
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_DIFFUSE, lightDiffuseValue, 0);
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_SPECULAR, lightSpecularValue, 0);
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_POSITION, lightDiffusePosition, 0);
			gl.glEnable(LIGHT_NUMS[id]);
		} else
			gl.glDisable(LIGHT_NUMS[id]);
	}

	/** Setzt die Position der Lichtquelle im Koordinatensystem.
	 * @param pX x-Koordinate der Position
	 * @param pY y-Koordinate der Position
	 * @param pZ z-Koordinate der Position
	 */
	public synchronized void setzePosition(double pX, double pY, double pZ) {
		lightDiffusePosition[0] = (float)pX;
		lightDiffusePosition[1] = (float)pY;
		lightDiffusePosition[2] = (float)pZ;
		associatedRenderer.scheduleRender();
	}

	/** Verschiebt die Lichtquelle im Koordinatensystem.
	 * @param pX x-Koordinate der Position
	 * @param pY y-Koordinate der Position
	 * @param pZ z-Koordinate der Position
	 */	public synchronized void verschiebe(double pX, double pY, double pZ) {
		lightDiffusePosition[0] += (float)pX;
		lightDiffusePosition[1] += (float)pY;
		lightDiffusePosition[2] += (float)pZ;
		associatedRenderer.scheduleRender();
	}
}
