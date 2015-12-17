package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

/**
 * Basisklasse aller bGLOOP-Objekte.
 * 
 * @author R. Spillner
 */
public abstract class GLObjekt extends GLDisplayItem implements IGLSurface {
	final private float[] aAmbient = { 0.15f, 0.15f, 0.15f, 1 };
	float[] aDiffuse = { 1, 1, 1, 1 };
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
		FUELLEN(GL2.GL_FILL);
		private int dm;
		private Darstellungsmodus(int i) {
			dm = i;
		}

		private int getMode() {
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
	final GLWindowConfig wconf;
	GLTextur aTex;
	int bufferName = -1;

	GLUquadric quadric;
	// true if needed to be recomputed
	boolean needsRedraw = true;

	abstract void renderDelegate(GL2 gl, GLU glu);

	GLObjekt() {
		this(null);
	}

	GLObjekt(GLTextur pTextur) {
		conf = new GLConfig();
		aTex = pTextur;
		associatedCam = GLKamera.aktiveKamera();
		associatedRenderer = associatedCam.getRenderer();

		wconf = associatedCam.getWconf();
		conf.xDivision = wconf.xDivision;
		conf.yDivision = wconf.yDivision;

		conf.objectRenderMode = wconf.globalObjectRenderMode;

		if (aTex != null)
			associatedRenderer.addObjectToTextureMap(aTex.aTexturImpl, this, null);
		else
			associatedRenderer.getNoTextureItemList().add(this);

		scheduleRender();
	}

	/**
	 * Setzt die Farbe des Objekts. Die Parameterwerte müssen zwischen 0 und 1
	 * liegen.
	 * 
	 * @param pR
	 *            Rotanteil, zwischen 0 und 1
	 * @param pG
	 *            Grünanteil, zwischen 0 und 1
	 * @param pB
	 *            Blauanteil, zwischen 0 und 1
	 */
	@Override
	public synchronized void setzeFarbe(double pR, double pG, double pB) {
		aDiffuse[0] = (float) pR;
		aDiffuse[1] = (float) pG;
		aDiffuse[2] = (float) pB;
		scheduleRender();
	}

	/**
	 * Farbe des Objekts.
	 * 
	 * @return Dreielementiges Array mit Rot-, Grün und Blauanteilen.
	 */
	@Override
	public double[] gibFarbe() {
		return new double[] { aDiffuse[0], aDiffuse[1], aDiffuse[2] };
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

	public synchronized void setzeSichtbarkeit(boolean pSichtbar) {
		aVisible = pSichtbar;
		associatedCam.getRenderer().scheduleRender();
	}

	/**
	 * Löscht das Objekt aus der Szene. Es ist danach nicht mehr
	 * wiederherstellbar.
	 */
	public synchronized void loesche() {
		associatedRenderer.getNoTextureItemList().remove(this);
		scheduleRender();
	}

	/**
	 * Legt die übergebene Textur auf das Objekt.
	 * 
	 * @param pTextur
	 *            Ein {@link GLTextur}-Objekt
	 */
	@Override
	public synchronized void setzeTextur(GLTextur pTextur) {
		if (aTex != null)
			associatedRenderer.addObjectToTextureMap(pTextur.aTexturImpl, this, aTex.aTexturImpl);
		else {
			associatedRenderer.getNoTextureItemList().remove(this);
			associatedRenderer.addObjectToTextureMap(pTextur.aTexturImpl, this, null);
		}
		aTex = pTextur;
		scheduleRender();
	}

	/**
	 * Legt die übergebene Textur auf das Objekt.
	 * 
	 * @param pTexturBilddatei
	 *            Ein Dateiname einer Bilddatei (.jpg oder .png)
	 */
	@Override
	public synchronized void setzeTextur(String pTexturBilddatei) {
		setzeTextur(new GLTextur(pTexturBilddatei));
	}

	@Override
	synchronized void render(GL2 gl, GLU glu) {
		loadMaterial(gl, aAmbient, aDiffuse, aSpecular, aEmission, aGlanz);

		gl.glPushMatrix();

		if (conf.objectRenderMode == Rendermodus.RENDER_GLU) {
			if (quadric == null)
				quadric = glu.gluNewQuadric();

			if (associatedCam.istDrahtgittermodell()) {
//				glu.gluQuadricDrawStyle(quadric, GLU.GLU_LINE);
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			} else {
//				glu.gluQuadricDrawStyle(quadric, computeDrawMode(conf.displayMode, conf.objectRenderMode));
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, conf.displayMode.getMode());
			}
		} else {
			if (associatedCam.istDrahtgittermodell())
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			else
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, conf.displayMode.getMode());
		}
		renderDelegate(gl, glu);
		gl.glPopMatrix();
	}

	GLConfig getConf() {
		return conf;
	}

	void loadMaterial(GL2 gl, float[] amb, float[] diff, float[] spec, float[] emiss, float glanz) {
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, amb, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diff, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, spec, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emiss, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, glanz);
	}

	void scheduleRender() {
		associatedRenderer.scheduleRender();
	}
}
