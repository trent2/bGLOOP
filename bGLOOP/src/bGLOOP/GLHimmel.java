package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Sichtbarer Himmel in einer Szene. Die Darstellung erfolgt durch das
 * fortwährende Rendern einer Kugel um die Kamera.
 * 
 * @author R. Spillner
 */
public class GLHimmel extends GLObjekt {
	/**
	 * Erstellt ein Himmelobjekt mit weißer Hintergrundfarbe.
	 */
	public GLHimmel() {
		this(null);
	}

	public GLHimmel(GLTextur pTextur) {
		super(pTextur);
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		conf.displayMode = Darstellungsmodus.FUELLEN;
		aVisible = true;
	}

	@Override
	void doRenderGL_VBO(GL2 gl) {
		//		throw new UnsupportedOperationException("VBOs sind für diesen Objekttyp (noch) nicht implementiert.");
		doRenderGL(gl);
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		throw new AssertionError("Diese Methode dürfte nie aufgerufen worden sein.");
	}

	@Override
	void doRenderGL(GL2 gl) {
		if (!associatedCam.getWconf().aWireframe) {
			double radius = 100;
			int divs = 16;
			double quality = 180.0 / divs;
			double lX, lZ;

			// the GLHimmel sphere is permanently moved to the position of the
			// camera.
			// therefore it is displayed all the time

			gl.glDisable(GL2.GL_DEPTH_TEST);
			// gl.glDisable(GL2.GL_CULL_FACE);
			gl.glCullFace(GL2.GL_FRONT);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			gl.glTranslated(associatedCam.aPos[0], associatedCam.aPos[1], associatedCam.aPos[2]);
			gl.glRotatef(90, 1, 0, 0);

			for (int i = 0; i < divs; ++i) { // 16; i++) {
				double lY1 = Math.cos(Math.toRadians(i * quality));
				double lY2 = Math.cos(Math.toRadians((i + 1) * quality));

				double lRT1 = Math.sin(Math.toRadians(i * quality));
				double lRT2 = Math.sin(Math.toRadians((i + 1) * quality));

				gl.glBegin(GL2.GL_QUAD_STRIP);
				for (int j = 0; j <= divs; j++) {
					lX = Math.cos(Math.toRadians(j * 2 * quality));
					lZ = Math.sin(Math.toRadians(j * 2 * quality));

					gl.glNormal3d(-lX * lRT1, -lZ * lRT1, -lY1);
					gl.glTexCoord2d(1.0 * (divs - j) / divs, 1.0 * (divs - i) / divs);
					gl.glVertex3d(lX * radius * lRT1, lZ * radius * lRT1, lY1 * radius);
					gl.glNormal3d(-lX * lRT2, -lZ * lRT2, -lY2);
					gl.glTexCoord2d(1.0 * (divs - j) / divs, 1.0 * (divs - i - 1) / divs);
					gl.glVertex3d(lX * radius * lRT2, lZ * radius * lRT2, lY2 * radius);
				}
				gl.glEnd();
			}
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glCullFace(GL2.GL_BACK);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		}
	}
}
