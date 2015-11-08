package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import bGLOOP.linalg.Matrix4;

/** Basisklasse aller bGLOOP-Objekte.
 * 
 * @author R. Spillner
 */
public abstract class GLObjekt extends GLDisplayItem implements IGLSurface {
	final private float[] aAmbient = { 0.15f, 0.15f, 0.15f, 1 };
	float[] aDiffuse = { 1, 1, 1, 1 };
	private float[] aSpecular = { 0, 0, 0, 1 };
	float[] aEmission = { 0, 0, 0, 1 };
	private float aGlanz = 70; // between 0 and 128
	int bufferName = -1;

	/** Der Darstellungsmodus beschreibt, wie ein Objekt gezeichnet wird. Dabei
	 * gibt es drei Möglichkeiten: als Punktgrafik, als Gitternetzgrafik oder
	 * als durchgehend gefüllte Struktur.
	 * 
	 * @author R. Spillner
	 */
	public static enum Darstellungsmodus {
		/** <code>Darstellungsmodus.PUNKT</code>: Das Objekt wird durch ein
		 * Punktnetz dargestellt.
		 */
		PUNKT,
		/** <code>Darstellungsmodus.LINIE</code>: Das Objekt wird durch ein
		 * Gitternetz dargestellt.
		 */
		LINIE,
		/** <code>Darstellungsmodus.FUELLEN</code>: Die Seitenflächen des Objekts
		 * sind durchgehend mit der Farbe gefüllt.
		 */
		FUELLEN
	};

	static enum Rendermodus {
		RENDER_GLU, RENDER_GL, RENDER_VBOGL
	};

	/** Klasse eines Konfigurations-Objektes, dass jedem {@link GLObjekt}
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
	Matrix4 transformationMatrix;
	GLUquadric quadric;

	abstract void doRenderGL(GL2 gl);
	abstract void doRenderGL_VBO(GL2 gl);
	abstract void doRenderGLU(GL2 gl, GLU glu);

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

		// this must be initialized BEFORE adding to any display lists
		transformationMatrix = new Matrix4();

		if(aTex != null)
			associatedRenderer.addObjectToTextureMap(aTex.aTexturImpl, this, null);
		else
			associatedRenderer.getNoTextureItemList().add(this);

		scheduleRender();
	}

	/**
	 * Setzt die Farbe des Objekts. Die Parameterwerte müssen zwischen 0 und 1
	 * liegen.
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

	/** Farbe des Objekts.
	 * @return Dreielementiges Array mit Rot-, Grün und Blauanteilen.
	 */
	@Override
	public double[] gibFarbe() {
		return new double[] { aDiffuse[0], aDiffuse[1], aDiffuse[2] };
	}

	/** Legt das Oberflächenmaterial des Objektes fest. Vordefinierte Materialien
	 * findet man in der Klasse {@link GLMaterial}.
	 * 
	 * @param pMaterial Ein Material aus der Aufzählungsklasse GLMaterial
	 */
	public synchronized void setzeMaterial(GLMaterial pMaterial) {
		System.arraycopy(pMaterial.getAmbient(), 0, aAmbient, 0, 4);
		System.arraycopy(pMaterial.getDiffuse(), 0, aDiffuse, 0, 4);
		System.arraycopy(pMaterial.getSpecular(), 0, aSpecular, 0, 4);
		aGlanz = pMaterial.getShinyness();
		scheduleRender();
	}

	/** Setzt die Reflektivität und Farbe der Reflexion. Eine hohe Reflektivität
	 * ergibt einen z.T. großen Glanzfleck auf gekrümmten Flächen des Objekts.
	 * Die Farbanteile müssen zwischen 0 und 1 liegen, die Reflektivität muss
	 * zwischen 0 und 128 liegen.
	 * 
	 * @param pR Rotanteil, zwischen 0 und 1
	 * @param pG Grünanteil, zwischen 0 und 1
	 * @param pB Blauanteil, zwischen 0 und 1
	 * @param pGlanz Reflektivität, zwischen 0 und 128, standardmäßig 70
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

	/** Löscht das Objekt aus der Szene. Es ist danach nicht mehr
	 * wiederherstellbar.
	 */
	public synchronized void loesche() {
		associatedRenderer.getNoTextureItemList().remove(this);
		scheduleRender();
	}

	/** Legt die übergebene Textur auf das Objekt.
	 * 
	 * @param pTextur Ein {@link GLTextur}-Objekt
	 */
	@Override
	public synchronized void setzeTextur(GLTextur pTextur) {
		if (aTex != null)
			associatedRenderer.addObjectToTextureMap(pTextur.aTexturImpl, this,
					aTex.aTexturImpl);
		else {
			associatedRenderer.getNoTextureItemList().remove(this);
			associatedRenderer.addObjectToTextureMap(pTextur.aTexturImpl, this, null);
		}
		aTex = pTextur;
		scheduleRender();
	}

	/** Legt die übergebene Textur auf das Objekt.
	 * 
	 * @param pTexturBilddatei Ein Dateiname einer Bilddatei (.jpg 
	 * oder .png)
	 */
	@Override
	public synchronized void setzeTextur(String pTexturBilddatei) {
		setzeTextur(new GLTextur(pTexturBilddatei));
	}

	@Override
	synchronized void render(GL2 gl, GLU glu) {
		loadMaterial(gl);

		gl.glPushMatrix();
		gl.glMultMatrixf(transformationMatrix.getMatrix(), 0);
		if (conf.objectRenderMode == Rendermodus.RENDER_GLU) {
			if (quadric == null)
				quadric = glu.gluNewQuadric();

			if (associatedCam.istDrahtgittermodell())
				glu.gluQuadricDrawStyle(quadric, GLU.GLU_LINE);
			else
				glu.gluQuadricDrawStyle(quadric, computeDrawMode(conf.displayMode, conf.objectRenderMode));
			doRenderGLU(gl, glu);
		} else if (conf.objectRenderMode == Rendermodus.RENDER_GL) {
			if (associatedCam.istDrahtgittermodell())
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			else
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, computeDrawMode(conf.displayMode, conf.objectRenderMode));
			doRenderGL(gl);
		} else if (conf.objectRenderMode == Rendermodus.RENDER_VBOGL) {
			if (associatedCam.istDrahtgittermodell())
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			else
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, computeDrawMode(conf.displayMode, conf.objectRenderMode));
			doRenderGL_VBO(gl);
		}
		gl.glPopMatrix();
	}

	GLConfig getConf() {
		return conf;
	}

	private void loadMaterial(GL2 gl) {
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, this.aAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, aDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, aSpecular, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, aEmission, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, aGlanz);
	}

	void scheduleRender() {
		associatedRenderer.scheduleRender();
	}

	static int computeDrawMode(final Darstellungsmodus dm, final Rendermodus rm) {
		int r = -1;
		switch (dm) {
		case PUNKT:
			if (rm == Rendermodus.RENDER_GLU)
				r = GLU.GLU_POINT;
			else if (rm == Rendermodus.RENDER_GL || rm == Rendermodus.RENDER_VBOGL)
				r = GL2.GL_POINT;
			break;
		case LINIE:
			if (rm == Rendermodus.RENDER_GLU)
				r = GLU.GLU_LINE;
			else if (rm == Rendermodus.RENDER_GL || rm == Rendermodus.RENDER_VBOGL)
				r = GL2.GL_LINE;
			break;
		case FUELLEN:
		default:
			if (rm == Rendermodus.RENDER_GLU)
				r = GLU.GLU_FILL;
			else if (rm == Rendermodus.RENDER_GL || rm == Rendermodus.RENDER_VBOGL)
				r = GL2.GL_FILL;
		}
		if (r == -1)
			throw new IllegalStateException("No appropriate draw mode found.");
		return r;
	}
}
