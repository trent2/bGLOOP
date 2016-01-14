package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

abstract class TransformableSurfaceObject extends TransformableObject implements IGLSurface, IGLSubdivisable {
	private GLTextur aTex;
	int bufferName = -1;

	TransformableSurfaceObject() {
		this(null);
	}

	TransformableSurfaceObject(GLTextur pTex) {
		super();
		aTex = (pTex == null ? GLTextur.NULL_TEXTURE : pTex);
		associatedRenderer.addObjectToRenderMap(aTex, this);
	}

	@Override
	public synchronized void setzeQualitaet(int pBreitengrade, int pLaengengrade) {
		if (pBreitengrade != conf.xDivision || pLaengengrade != conf.yDivision) {
			conf.xDivision = pBreitengrade;
			conf.yDivision = pLaengengrade;
			needsRedraw = true;
			scheduleRender();
		}
	}

	@Override
	public void setzeQualitaet(int pUnterteilungen) {
		setzeQualitaet(pUnterteilungen, pUnterteilungen);
	}

	@Override
	public synchronized void setzeTextur(GLTextur pTextur) {
		associatedRenderer.updateObjectRenderMap(pTextur, this, aTex);
		aTex = (pTextur == null ? GLTextur.NULL_TEXTURE : pTextur);
		scheduleRender();
	}

	@Override
	public void setzeTextur(String pTexturBilddatei) {
		setzeTextur(new GLTextur(pTexturBilddatei));
	}

	@Override
	public GLTextur gibTextur() {
		return aTex.getObjectOrNull();
	}

	@Override
	public synchronized void setzeDurchsichtigkeit(double pAlpha) {
		aDiffuse[3] = (float)pAlpha;
		scheduleRender();
	}

	abstract void generateDisplayList_GL(GL2 gl);

	abstract void generateDisplayList_GLU(GL2 gl, GLU glu);

	abstract void generateVBO(GL2 gl);

	abstract void drawVBO(GL2 gl);

	@Override
	void renderDelegate(GL2 gl, GLU glu) {
		// now transform the object accordingly
		gl.glMultMatrixf(transformationMatrix.getMatrix(), 0);
		if (needsRedraw) {
			log.info("redrawing " + getClass().getName() + ":" + hashCode() );

			switch (conf.objectRenderMode) {
			case RENDER_GLU:
			case RENDER_GL:
				if (bufferName != -1)
					gl.glDeleteLists(bufferName, 1);

				bufferName = gl.glGenLists(1);
				if (conf.objectRenderMode == Rendermodus.RENDER_GL)
					generateDisplayList_GL(gl);
				else
					generateDisplayList_GLU(gl, glu);
				break;
			case RENDER_VBOGL:
				generateVBO(gl);
				break;
			}

			needsRedraw = false;
		}
		log.fine("rendering " + getClass().getName() + ":" + hashCode() );
		switch (conf.objectRenderMode) {
		case RENDER_GL:
		case RENDER_GLU:
			gl.glCallList(bufferName);
			break;
		case RENDER_VBOGL:
			drawVBO(gl);
			break;
		}

	}

	@Override
	public synchronized void loesche() {
		associatedRenderer.removeObjectFromRenderMap(aTex, this);
		scheduleRender();
	}
}
