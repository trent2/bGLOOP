package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Ein Quadermodell.<br>
 * <img alt="Abbildung Quader" src="./doc-files/Quader-1.png">
 * 
 * @author R. Spillner
 */
public class GLQuader extends GLTransformableObject {

	/**
	 * Erzeugt einen Quader mit Mittelpunkt <code>M(pMX, pMY, pMZ)</code> und
	 * Seitenlängen <code>pLX</code>, <code>pLY</code> und <code>pLZ</code>.
	 * <div style="float:right">
	 * <img alt="Abbildung Quader" src="./doc-files/Quader-1.png">
	 * </div><div>
	 * <p>
	 * <em>Abbildung:</em> Lage eines Quaders mit Mittelpunkt
	 * <code>M(0,0,0)</code> und Seitenlängen <code>1</code>.
	 * </p>
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts des Quaders
	 * @param pMY y-Koordinate des Mittelpunkts des Quaders
	 * @param pMZ z-Koordinate des Mittelpunkts des Quaders
	 * @param pLX Seitenlänge in x-Richtung des Quaders
	 * @param pLY Seitenlänge in y-Richtung des Quaders
	 * @param pLZ Seitenlänge in z-Richtung des Quaders
	 * </div>
	 * <div style="clear:right"></div>
	 */
	public GLQuader(double pMX, double pMY, double pMZ, double pLX, double pLY, double pLZ) {
		this(pMX, pMY, pMZ, pLX, pLY, pLZ, null);
	}

	/**
	 * Erzeugt einen Quader mit Textur.
	 * 
	 * @param pMX
	 *            x-Koordinate des Mittelpunkts des Quaders
	 * @param pMY
	 *            y-Koordinate des Mittelpunkts des Quaders
	 * @param pMZ
	 *            z-Koordinate des Mittelpunkts des Quaders
	 * @param pLX
	 *            Seitenlänge in x-Richtung des Quaders
	 * @param pLY
	 *            Seitenlänge in y-Richtung des Quaders
	 * @param pLZ
	 *            Seitenlänge in z-Richtung des Quaders
	 * @param pTextur
	 *            Textur-Objekt für die Oberfläche des Quaders
	 */
	public GLQuader(double pMX, double pMY, double pMZ, double pLX, double pLY, double pLZ, GLTextur pTextur) {
		super(pTextur);
		skaliere(pLX, pLY, pLZ);
		verschiebe(pMX, pMY, pMZ);
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		doRenderGL(gl);
	}

	@Override
	void doRenderGL(GL2 gl) {
		gl.glBegin(GL2.GL_QUAD_STRIP);
		gl.glTexCoord2d(0, 1);
		gl.glNormal3d(-0.5, 0.5, 0.5);
		gl.glVertex3d(-0.5, 0.5, 0.5);

		gl.glTexCoord2d(0, 0);
		gl.glNormal3d(-0.5, -0.5, 0.5);
		gl.glVertex3d(-0.5, -0.5, 0.5);

		gl.glTexCoord2d(1, 1);
		gl.glNormal3d(0.5, 0.5, 0.5);
		gl.glVertex3d(0.5, 0.5, 0.5);

		gl.glTexCoord2d(1, 0);
		gl.glNormal3d(0.5, -0.5, 0.5);
		gl.glVertex3d(0.5, -0.5, 0.5);

		gl.glTexCoord2d(0, 1);
		gl.glNormal3d(0.5, 0.5, -0.5);
		gl.glVertex3d(0.5, 0.5, -0.5);

		gl.glTexCoord2d(0, 0);
		gl.glNormal3d(0.5, -0.5, -0.5);
		gl.glVertex3d(0.5, -0.5, -0.5);

		gl.glTexCoord2d(1, 1);
		gl.glNormal3d(-0.5, 0.5, -0.5);
		gl.glVertex3d(-0.5, 0.5, -0.5);

		gl.glTexCoord2d(1, 0);
		gl.glNormal3d(-0.5, -0.5, -0.5);
		gl.glVertex3d(-0.5, -0.5, -0.5);

		gl.glTexCoord2d(0, 1);
		gl.glNormal3d(-0.5, 0.5, 0.5);
		gl.glVertex3d(-0.5, 0.5, 0.5);

		gl.glTexCoord2d(0, 0);
		gl.glNormal3d(-0.5, -0.5, 0.5);
		gl.glVertex3d(-0.5, -0.5, 0.5);
		gl.glEnd();

		// Deckel
		gl.glBegin(GL2.GL_QUADS);

		gl.glTexCoord2d(0, 0);
		gl.glNormal3d(-0.5, 0.5, 0.5);
		gl.glVertex3d(-0.5, 0.5, 0.5);

		gl.glTexCoord2d(0, 1);
		gl.glNormal3d(0.5, 0.5, 0.5);
		gl.glVertex3d(0.5, 0.5, 0.5);

		gl.glTexCoord2d(1, 1);
		gl.glNormal3d(0.5, 0.5, -0.5);
		gl.glVertex3d(0.5, 0.5, -0.5);

		gl.glTexCoord2d(1, 0);
		gl.glNormal3d(-0.5, 0.5, -0.5);
		gl.glVertex3d(-0.5, 0.5, -0.5);
		gl.glEnd();

		// Boden
		gl.glBegin(GL2.GL_QUADS);

		gl.glTexCoord2d(0, 0);
		gl.glNormal3d(-0.5, -0.5, 0.5);
		gl.glVertex3d(-0.5, -0.5, 0.5);

		gl.glTexCoord2d(0, 1);
		gl.glNormal3d(-0.5, -0.5, -0.5);
		gl.glVertex3d(-0.5, -0.5, -0.5);

		gl.glTexCoord2d(1, 1);
		gl.glNormal3d(0.5, -0.5, -0.5);
		gl.glVertex3d(0.5, -0.5, -0.5);

		gl.glTexCoord2d(1, 0);
		gl.glNormal3d(0.5, -0.5, 0.5);
		gl.glVertex3d(0.5, -0.5, 0.5);
		gl.glEnd();
	}

}
