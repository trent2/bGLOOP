package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Sichtbare Bodenfläche in einer Szene.
 * 
 * @author R. Spillner
 *
 */
public class GLBoden extends GLObjekt implements IGLSurface {
	private GLTextur aTex;

	/**
	 * Erstellt eine weiße Bodenfläche.
	 */
	public GLBoden() {
		this(null);
	}

	public GLBoden(GLTextur pTextur) {
		super();
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		conf.displayMode = Darstellungsmodus.FUELLEN;

		aTex = (pTextur == null ? GLTextur.NULL_TEXTURE : pTextur);

		associatedRenderer.addObjectToRenderMap(aTex, this);

		aVisible = true;
	}

	@Override
	void renderDelegate(GL2 gl, GLU glu) {
		// don't render wireframe
		if (!associatedCam.getWconf().aWireframe) {
			double lTX = associatedCam.aPos[0] / 1000;
			double lTY = associatedCam.aPos[1] / 10;
			double lTZ = associatedCam.aPos[2] / 1000;
			double lG = 1000;

			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, 1, 0);

			gl.glTexCoord2d(lTX, lTZ);
			gl.glVertex3d(0, -10, 0);

			gl.glTexCoord2d(lTX + lTY * 1.42, lTZ);
			gl.glVertex3d(1.42 * lG, -10, 0);

			gl.glTexCoord2d(lTX + lTY, lTZ - lTY);
			gl.glVertex3d(lG, -10, -lG);

			gl.glTexCoord2d(lTX, lTZ - lTY * 1.42);
			gl.glVertex3d(0, -10, -1.42 * lG);

			gl.glTexCoord2d(lTX - lTY, lTZ - lTY);
			gl.glVertex3d(-lG, -10, -lG);

			gl.glTexCoord2d(lTX - lTY * 1.42, lTZ);
			gl.glVertex3d(-lG, -10, 0);

			gl.glTexCoord2d(lTX - lTY, lTZ + lTY);
			gl.glVertex3d(-lG, -10, lG);

			gl.glTexCoord2d(lTX, lTZ + lTY * 1.42);
			gl.glVertex3d(0, -10, 1.42 * lG);

			gl.glTexCoord2d(lTX + lTY, lTZ + lTY);
			gl.glVertex3d(1 * lG, -10, lG);

			gl.glTexCoord2d(lTX + lTY * 1.42, lTZ);
			gl.glVertex3d(1.42 * lG, -10, 0);

			gl.glEnd();
		}
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
	public synchronized void loesche() {
		associatedRenderer.removeObjectFromRenderMap(aTex, this);
		scheduleRender();
	}

	@Override
	public synchronized void setzeDurchsichtigkeit(double pAlpha) {
		aDiffuse[3] = (float)pAlpha;
		scheduleRender();
	}

	@Override
	public synchronized void setzeSelbstleuchten(double pR, double pG, double pB) {
		aEmission[0] = (float)pR;
		aEmission[1] = (float)pG;
		aEmission[2] = (float)pB;
		aEmission[3] = 1;
		scheduleRender();		
	}
}
