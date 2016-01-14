package bGLOOP;

import java.util.logging.Logger;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Basisklasse aller bGLOOP-Objekte.
 * 
 * @author R. Spillner
 */
public abstract class GLObjekt extends DisplayItem implements IGLColorable {
	Logger log = Logger.getLogger("bGLOOP");
	float[] aDiffuse = { 1, 1, 1, 1 };
	final private float[] aAmbient = { 0.15f, 0.15f, 0.15f, 1 };
	private float[] aSpecular = { 0, 0, 0, 1 };
	float[] aEmission = { 0, 0, 0, 1 };
	private float aGlanz = 70; // between 0 and 128

	/**
	 * Der Darstellungsmodus beschreibt, wie ein Objekt gezeichnet wird. Dabei
	 * gibt es drei Möglichkeiten: als Punktgrafik, als Gitternetzgrafik oder
	 * als durchgehend gefüllte Struktur.
	 * 
	 * @author R. Spillner
	 */
	public static enum Darstellungsmodus {
		/**
		 * <code>Darstellungsmodus.PUNKT</code>: Das Objekt wird durch ein
		 * Punktnetz dargestellt.
		 */
		PUNKT(GL2.GL_POINT),
		/**
		 * <code>Darstellungsmodus.LINIE</code>: Das Objekt wird durch ein
		 * Gitternetz dargestellt.
		 */
		LINIE(GL2.GL_LINE),
		/**
		 * <code>Darstellungsmodus.FUELLEN</code>: Die Seitenflächen des Objekts
		 * sind durchgehend mit der Farbe gefüllt.
		 */
		FUELLEN(GL2.GL_FILL),
		/**
		 * <code>Darstellungsmodus.NA</code>: Darstellungsmodus nicht verfügbar.
		 * In diesem Fall wird der Standarddarstellungsmodus des Fensters gewählt. 
		 */
		NV(-1);
		private int dm;
		private Darstellungsmodus(int i) {
			dm = i;
		}

		int getMode() {
			return dm;
		}
	};

	static enum Rendermodus {
		RENDER_GLU, RENDER_GL, RENDER_VBOGL
	};

	/**
	 * Klasse eines Konfigurations-Objektes, dass jedem {@link GLObjekt}
	 * zugeordnet ist.
	 * 
	 * @author R. Spillner
	 */
	static class GLConfig {
		int xDivision;
		int yDivision;
		Rendermodus objectRenderMode;
		Darstellungsmodus displayMode;
	}

	final GLConfig conf;
	final WindowConfig wconf;

	GLUquadric quadric;
	// true if needed to be recomputed
	boolean needsRedraw = true;

	abstract void renderDelegate(GL2 gl, GLU glu);

	GLObjekt() {
		conf = new GLConfig();
		associatedCam = GLKamera.aktiveKamera();
		associatedRenderer = associatedCam.associatedRenderer;

		wconf = associatedCam.getWconf();
		conf.xDivision = wconf.xDivision;
		conf.yDivision = wconf.yDivision;

		conf.objectRenderMode = wconf.globalObjectRenderMode;
		conf.displayMode = Darstellungsmodus.NV;

		scheduleRender();
	}

	/**
	 * Legt das Oberflächenmaterial des Objektes fest. Vordefinierte Materialien
	 * findet man in der Klasse {@link GLMaterial}.
	 * 
	 * @param pMaterial
	 *            Ein Material aus der Aufzählungsklasse GLMaterial
	 */
	public synchronized void setzeMaterial(GLMaterial pMaterial) {
		System.arraycopy(pMaterial.getAmbient(), 0, aAmbient, 0, 4);
		System.arraycopy(pMaterial.getDiffuse(), 0, aDiffuse, 0, 4);
		System.arraycopy(pMaterial.getSpecular(), 0, aSpecular, 0, 4);
		aGlanz = pMaterial.getShininess();
		scheduleRender();
	}

	/** Farbe des Objekts.
	 * @return dreielementiges Array mit Rot-, Grün und Blauanteilen
	 */
	@Override
	public double[] gibFarbe() {
		return new double[] { aDiffuse[0], aDiffuse[1], aDiffuse[2] };
	}

	/** Setzt die Farbe des Objekts. Standardmäßig ist hier die Farbe weiß,
	 * also <code>(1,1,1)</code> voreingestellt.
	 * Die Parameterwerte müssen zwischen 0 und 1 liegen.
	 * 
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 */
	@Override
	public synchronized void setzeFarbe(double pR, double pG, double pB) {
		aDiffuse[0] = (float) pR;
		aDiffuse[1] = (float) pG;
		aDiffuse[2] = (float) pB;
		scheduleRender();
	}


	/**
	 * Setzt die Reflektivität und Farbe der Reflexion. Eine hohe Reflektivität
	 * ergibt einen z.T. großen Glanzfleck auf gekrümmten Flächen des Objekts.
	 * Die Farbanteile müssen zwischen 0 und 1 liegen, die Reflektivität muss
	 * zwischen 0 und 128 liegen.
	 * 
	 * @param pR
	 *            Rotanteil, zwischen 0 und 1
	 * @param pG
	 *            Grünanteil, zwischen 0 und 1
	 * @param pB
	 *            Blauanteil, zwischen 0 und 1
	 * @param pGlanz
	 *            Reflektivität, zwischen 0 und 128, standardmäßig 70
	 */
	public synchronized void setzeGlanz(double pR, double pG, double pB, int pGlanz) {
		aGlanz = pGlanz;
		aSpecular[0] = (float) pR;
		aSpecular[1] = (float) pG;
		aSpecular[2] = (float) pB;
		scheduleRender();
	}

	@Override
	public boolean isTransparent() {
		return aDiffuse[3] != 1 || aAmbient[3] != 1;
	}

	/**
	 * Bestimmt, ob das Objekt gezeichnet wird oder nicht. Wenn die Sichtbarkeit
	 * auf <code>false</code> gesetzt wird, wird keinerlei OpenGL-Code zum Rendern
	 * des Objekts ausgeführt.
	 * 
	 * @param pSichtbar
	 *            Wenn <code>true</code>, so wird das Objekt gezeichnet, bei
	 *            <code>false</code> wird es nicht gezeichnet.
	 */
	public synchronized void setzeSichtbarkeit(boolean pSichtbar) {
		aVisible = pSichtbar;
		scheduleRender();
	}

	/**
	 * Löscht das Objekt aus der Szene. Es ist danach nicht mehr
	 * wiederherstellbar.
	 */
	public synchronized void loesche() {
		associatedRenderer.removeObjectFromRenderMap(GLTextur.NULL_TEXTURE, this);
		scheduleRender();
	}

	@Override
	synchronized void render(GL2 gl, GLU glu) {
		if (conf.objectRenderMode == Rendermodus.RENDER_GLU)
			if (quadric == null)
				quadric = glu.gluNewQuadric();

		if (associatedCam.istDrahtgittermodell())
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, conf.displayMode.getMode());

		loadMaterial(gl);
		gl.glPushMatrix();
		renderDelegate(gl, glu);
		gl.glPopMatrix();
	}

	GLConfig getConf() {
		return conf;
	}

	void loadMaterial(GL2 gl) {
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, aAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, aDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, aSpecular, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, aEmission, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, aGlanz);
	}

	void loadMaterial(GL2 gl, float[] pAmb, float[] pDiff, float[] pSpec, float[] pEmiss, float pGlanz) {
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, pAmb, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, pDiff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, pSpec, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, pEmiss, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, pGlanz);
	}

	void scheduleRender() {
		associatedRenderer.scheduleRender();
	}
}
