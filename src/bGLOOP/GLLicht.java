package bGLOOP;

import java.util.logging.Logger;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;

/**
 * Punktlichtquelle mit unendlicher Reichweite. OpenGL unterstützt in den meisten
 * Implementationen nur 8 unterschiedliche Lichtquellen. Sollte versucht werden,
 * eine neunte zu generieren, wird 
 * 
 * @author R. Spillner
 */
public class GLLicht extends DisplayItem implements IGLColorable {
	private final static int LIGHT_NUMS[] = { GL2.GL_LIGHT0, GL2.GL_LIGHT1, GL2.GL_LIGHT2, GL2.GL_LIGHT3, GL2.GL_LIGHT4,
			GL2.GL_LIGHT5, GL2.GL_LIGHT6, GL2.GL_LIGHT7 };
	private final static int MAX_LIGHTS = 8;
	private static int lightCount = 0;
	private int id;
	private float[] lightAmbientValue = { 0.15f, 0.15f, 0.15f, 1.0f };
	private float[] lightDiffuseValue = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightSpecularValue = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] attenuation = { 1, 0, 0 };
	private float[] lightDiffusePosition;
	private boolean isOn = true;
	private Logger log = Logger.getLogger("bGLOOP");

	/** Eine Lichtquelle an der Position <code>L(pX, pY, pZ)</code>
	 * 
	 * @param pX x-Koordinate der Lichtquelle
	 * @param pY y-Koordinate der Lichtquelle
	 * @param pZ z-Koordinate der Lichtquelle
	 * @throws GLException wird geworfen, wenn mehr als die maximal verfügbare Anzahl
	 *   an Lichtquellen pro Szene (meistens 8) erstellt werden sollen
	 */
	public GLLicht(double pX, double pY, double pZ) throws GLException {
		lightDiffusePosition = new float[] { (float) pX, (float) pY, (float) pZ, 1.0f };
		if(lightCount < MAX_LIGHTS) {
			id = lightCount++;
			associatedCam = GLKamera.aktiveKamera();

			(associatedRenderer = associatedCam.associatedRenderer).addObjectToRenderMap(GLTextur.NULL_TEXTURE, this);
			log.fine("light created");
			aVisible = true;
		} else {
			throw new GLException("Es sind nur maximal "+ MAX_LIGHTS +" Lichtquellen in einer "
					+ "Szene erlaubt!");
		}
	}

	@Override
	boolean isTransparent() {
		return false;
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
	public synchronized void setzeAmbientBeleuchtung(double pR, double pG, double pB) {
		lightAmbientValue[0] = (float)pR;
		lightAmbientValue[1] = (float)pG;
		lightAmbientValue[2] = (float)pB;
		associatedRenderer.scheduleRender();
	}

	/** Setzt den die Hauptfarbe der Lichtquelle. Die Parameterwerte
	 * müssen zwischen 0 und 1 liegen.
	 * 
	 * @param pR Rotanteil
	 * @param pG Grünanteil
	 * @param pB Blauanteil
	 */
	@Override
	public synchronized void setzeFarbe(double pR, double pG, double pB) {
		lightDiffuseValue[0] = (float)pR;
		lightDiffuseValue[1] = (float)pG;
		lightDiffuseValue[2] = (float)pB;
		associatedRenderer.scheduleRender();
	}

	/** Farbe der Lichtquelle.
	 * @return Dreielementiges Array mit Rot-, Grün und Blauanteilen.
	 */
	@Override
	public double[] gibFarbe() {
		return new double[] { lightDiffuseValue[0], lightDiffuseValue[1], lightDiffuseValue[2] }; 
	}

	/**
	 * Aktiviert oder deaktiviert die Lichtquelle.
	 * 
	 * @param pAn
	 *            Wenn <code>true</code>, dann wird die Lichtquelle aktiviert,
	 *            wenn <code>false</code>, dann wird sie deaktiviert.
	 */
	public synchronized void lichtAn(boolean pAn) {
		isOn = pAn;
		associatedRenderer.scheduleRender();
	}

	@Override
	void render(GL2 gl, GLU glu) {
		if (isOn && associatedCam.getWconf().globalLighting) {
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_AMBIENT, lightAmbientValue, 0);
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_DIFFUSE, lightDiffuseValue, 0);
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_SPECULAR, lightSpecularValue, 0);
			gl.glLightfv(LIGHT_NUMS[id], GL2.GL_POSITION, lightDiffusePosition, 0);
			gl.glLightf(LIGHT_NUMS[id], GL2.GL_CONSTANT_ATTENUATION, attenuation[0]);
			gl.glLightf(LIGHT_NUMS[id], GL2.GL_LINEAR_ATTENUATION, attenuation[1]);
			gl.glLightf(LIGHT_NUMS[id], GL2.GL_QUADRATIC_ATTENUATION, attenuation[2]);
			gl.glEnable(LIGHT_NUMS[id]);
			log.fine("rendering light " + id);
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
	 */
	public synchronized void verschiebe(double pX, double pY, double pZ) {
		lightDiffusePosition[0] += (float)pX;
		lightDiffusePosition[1] += (float)pY;
		lightDiffusePosition[2] += (float)pZ;
		associatedRenderer.scheduleRender();
	}

	/** Legt fest, wie das emittierte Licht abgeschwächt mit zunehmender
	 * Entfernung von der Lichtquelle abgeschwächt wird. Dabei werden
	 * eine konstante, eine lineare und eine quadratische Komponente unterschieden.
	 * @param pKonstant konstanter Faktor. Der Einfluss dieses Parameters ist
	 * unabhängig von der Entfernung und gibt somit eine globale (d.h. 
	 * positionsunabhängige) Abschwächung des Lichts an.
	 * @param pLinear linearer Faktor der Abschwächung abhängig von der
	 * 	Entfernung eines Pixels von der Lichtquelle
	 * @param pQuadratisch quadratischer Faktor der Abschwächung abhängig vom
	 * 	Quadrat der Entfernung eines Pixels von der Lichtquelle.
	 */
	public synchronized void setzeAbschwaechung(double pKonstant, double pLinear, double pQuadratisch) {
		attenuation[0] = (float)pKonstant;
		attenuation[1] = (float)pLinear;
		attenuation[2] = (float)pQuadratisch;
		associatedRenderer.scheduleRender();
	}
}
