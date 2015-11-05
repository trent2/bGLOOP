package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Ein Kugelmodell.<br>
 * <img alt="Abbildung Kugel" src="./doc-files/Kugel-1.png">
 * 
 * @author R. Spillner
 */
public class GLKugel extends GLTransformableObject {
	private double aRad;

	/**
	 * Erzeugt eine Kugel mit Mittelpunkt <code>M(pMX, pMY, pMZ)</code> und
	 * Radius <code>pRadius</code>. <div style="float:right"> <img alt=
	 * "Abbildung Kugel" src="./doc-files/Kugel-1.png"> </div><div>
	 * <p>
	 * <em>Abbildung:</em> Lage eine Kugel mit Mittelpunkt <code>M(0,0,0)</code>
	 * und Radius <code>1</code>.
	 * </p>
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts der Kugel
	 * @param pMY y-Koordinate des Mittelpunkts der Kugel
	 * @param pMZ z-Koordinate des Mittelpunkts der Kugel
	 * @param pRadius
	 *            Radius der Kugel </div><div style="clear:right"></div>
	 */
	public GLKugel(double pMX, double pMY, double pMZ, double pRadius) {
		super();
		verschiebe(pMX, pMY, pMZ);
		aRad = pRadius;
	}

	/**
	 * Erzeugt eine Kugel mit Textur.
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts der Kugel
	 * @param pMY y-Koordinate des Mittelpunkts der Kugel
	 * @param pMZ z-Koordinate des Mittelpunkts der Kugel
	 * @param pRadius
	 *            Radius der Kugel
	 * @param pTextur
	 *            Textur-Objekt für die Oberfläche der Kugel
	 * @see #GLKugel(double, double, double, double)
	 */
	public GLKugel(double pMX, double pMY, double pMZ, double pRadius, GLTextur pTextur) {
		super(pTextur);
		verschiebe(pMX, pMY, pMZ);
		aRad = pRadius;
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		// gl.glColor3f(1, 1, 1);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		glu.gluSphere(quadric, aRad, conf.xDivision, conf.yDivision);
		// glu.gluDeleteQuadric(quadric);
	}

	@Override
	void doRenderGL(GL2 gl) {
		double lX, lZ;
		double qx = 180.0 / conf.xDivision;
		double qy = 360.0 / conf.yDivision;
		boolean texturePresent = (aTex != null) && aTex.isReady();

		for (int i = 0; i < conf.xDivision; ++i) { // conf.xDivision; i++) {
			double ring1Y = Math.cos(Math.toRadians(i * qx));
			double ring2Y = Math.cos(Math.toRadians((i + 1) * qx));

			double ring1X = Math.sin(Math.toRadians(i * qx));
			double ring2X = Math.sin(Math.toRadians((i + 1) * qx));

			gl.glBegin(GL2.GL_QUAD_STRIP);
			// need to go around one hole turn
			for (int j = 0; j <= conf.yDivision; j++) {
				lX = Math.cos(Math.toRadians(j * qy));
				lZ = Math.sin(Math.toRadians(j * qy));

				// first vertex of the quad is the third of the previous
				gl.glNormal3d(lX * ring1X, lZ * ring1X, ring1Y);
				if (texturePresent)
					gl.glTexCoord2d(1.0 * (conf.yDivision - j) / conf.yDivision,
							1.0 * (conf.yDivision - i) / conf.yDivision);
				gl.glVertex3d(lX * aRad * ring1X, lZ * aRad * ring1X, ring1Y * aRad);

				// second vertex of the quad is the fourth of the previous
				gl.glNormal3d(lX * ring2X, lZ * ring2X, ring2Y);
				if (texturePresent)
					gl.glTexCoord2d(1.0 * (conf.yDivision - j) / conf.yDivision,
							1.0 * (conf.yDivision - i - 1) / conf.yDivision);
				gl.glVertex3d(lX * aRad * ring2X, lZ * aRad * ring2X, ring2Y * aRad);
			}
			gl.glEnd();
		}
	}
}