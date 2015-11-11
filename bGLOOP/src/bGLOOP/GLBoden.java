package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Sichtbare Bodenfläche in einer Szene.
 * 
 * @author R. Spillner
 *
 */
public class GLBoden extends GLObjekt {

	/**
	 * Erstellt eine weiße Bodenfläche.
	 */
	public GLBoden() {
		this(null);
	}

	public GLBoden(GLTextur pTextur) {
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
}
