package bGLOOP;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Sichtbarer Himmel in einer Szene. Die Darstellung erfolgt durch das
 * fortwährende Rendern einer Kugel um die Kamera.
 * 
 * @author R. Spillner
 */
public class GLHimmel extends GLObjekt implements IGLSurface {
	private GLTextur aTex;

	/**
	 * Erstellt ein Himmelobjekt mit weißer Hintergrundfarbe.
	 */
	public GLHimmel() {
		this(null);
	}

	public GLHimmel(GLTextur pTextur) {
		super();
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		conf.displayMode = Darstellungsmodus.FUELLEN;

		aTex = (pTextur == null ? GLTextur.NULL_TEXTURE : pTextur);
		associatedRenderer.addObjectToRenderMap(aTex, this);
		log.fine("sky created");

		aVisible = true;
	}

	@Override
	void renderDelegate(GL2 gl, GLU glu) {
		if (!associatedCam.getWconf().aWireframe) {
			double radius = 100;
			int divs = 16;
			double quality = PI / divs;
			double lX, lZ;

			// the GLHimmel sphere is permanently moved to the position of the
			// camera.
			// therefore it is displayed all the time
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glCullFace(GL2.GL_FRONT);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			synchronized (associatedCam) {
				gl.glTranslated(associatedCam.aPos[0], associatedCam.aPos[1], associatedCam.aPos[2]);				
			}
			gl.glRotatef(90, 1, 0, 0);

			double lY1 = 1, lRT1 = 0, lY2, lRT2;
			for (int i = 0; i < divs; ++i) { // 16; i++) {
				lY2 = cos((i + 1) * quality);
				lRT2 = sin((i + 1) * quality);

				gl.glBegin(GL2.GL_QUAD_STRIP);
				for (int j = 0; j <= divs; j++) {
					lX = cos(j * 2 * quality);
					lZ = sin(j * 2 * quality);

					gl.glNormal3d(-lX * lRT1, -lZ * lRT1, -lY1);
					gl.glTexCoord2d(1.0 * (divs - j) / divs, 1.0 * (divs - i) / divs);
					gl.glVertex3d(lX * radius * lRT1, lZ * radius * lRT1, lY1 * radius);
					gl.glNormal3d(-lX * lRT2, -lZ * lRT2, -lY2);
					gl.glTexCoord2d(1.0 * (divs - j) / divs, 1.0 * (divs - i - 1) / divs);
					gl.glVertex3d(lX * radius * lRT2, lZ * radius * lRT2, lY2 * radius);
				}
				gl.glEnd();
				lY1 = lY2;
				lRT1 = lRT2;
			}
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glCullFace(GL2.GL_BACK);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
			log.fine("redrawing " + getClass().getName() + ":" + hashCode() );
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
