package bGLOOP;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
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
	private FloatBuffer fb;
	private int[] firstOffsets, countOffsets;

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
		this(pMX, pMY, pMZ, pRadius, null);
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
		if(pRadius<0)
			throw new IllegalArgumentException("Der Radius darf nicht negativ sein!");
		verschiebe(pMX, pMY, pMZ);
		aRad = pRadius;
		aVisible = true;
	}

	@Override
	void generateDisplayList_GLU(GL2 gl, GLU glu) {
		// gl.glColor3f(1, 1, 1);
		gl.glNewList(bufferName, GL2.GL_COMPILE);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		glu.gluQuadricTexture(quadric, true);
		glu.gluSphere(quadric, aRad, conf.xDivision, conf.yDivision);
		// glu.gluDeleteQuadric(quadric);
		gl.glEndList();
	}

	@Override
	void generateDisplayList_GL(GL2 gl) {
		double lX, lZ;
		boolean texturePresent = (aTex != null) && aTex.isReady();
		float qx = (float) (PI / conf.xDivision);
		float qy = (float) (2 * PI / conf.yDivision);

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		for (int i = 0; i < conf.xDivision; ++i) { // conf.xDivision; i++) {
			double ring1Y = cos(i * qx);
			double ring2Y = cos((i + 1) * qx);

			double ring1X = sin(i * qx);
			double ring2X = sin((i + 1) * qx);

			// need to go around one whole turn
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int j = 0; j <= conf.yDivision; j++) {
				lX = cos(j * qy);
				lZ = sin(j * qy);

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
		gl.glEndList();
	}

	@Override
	void drawVBO(GL2 gl) {
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, bufferName);
        gl.glEnableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glEnableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glEnableClientState( GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glNormalPointer(GL.GL_FLOAT, 8 * Buffers.SIZEOF_FLOAT, 0 );
        gl.glTexCoordPointer( 2, GL.GL_FLOAT, 8 * Buffers.SIZEOF_FLOAT, 3 * Buffers.SIZEOF_FLOAT );
        gl.glVertexPointer(3, GL.GL_FLOAT, 8 * Buffers.SIZEOF_FLOAT, 5 * Buffers.SIZEOF_FLOAT);
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_STRIP, firstOffsets, 0, countOffsets, 0, conf.xDivision);

        gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY);

        // unbind buffer
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
	}

	@Override
	void generateVBO(GL2 gl) {
		int[] t = new int[1];
		if(fb != null && bufferName != -1) {
			t[0] = bufferName;
			gl.glDeleteBuffers(1, t, 0);
			fb.clear();
		}
		gl.glGenBuffers(1, t, 0);
		bufferName = t[0];
		firstOffsets = new int[conf.xDivision];
		countOffsets = new int[conf.xDivision];
		for(int i=0; i<conf.xDivision; ++i) {
			firstOffsets[i] = 2 * (conf.yDivision + 1) * i;
			countOffsets[i] = 2 * (conf.yDivision + 1);
		}
		int vertexBufferSize = 16 * conf.xDivision * (conf.yDivision + 1) * Buffers.SIZEOF_FLOAT;

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBufferSize, null,
				GL2.GL_STATIC_DRAW);
		fb = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		// ready for drawing (to buffer)
		float lX, lZ;
		float qx = (float)(PI / conf.xDivision);
		float qy = (float)(2*PI / conf.yDivision);
		for (int i = 0; i < conf.xDivision; ++i) { // conf.xDivision; i++) {
			float ring1Y = (float)cos(i * qx);
			float ring2Y = (float)cos((i + 1) * qx);

			float ring1X = (float)sin(i * qx);
			float ring2X = (float)sin((i + 1) * qx);

			// need to go around one whole turn
			for (int j = 0; j <= conf.yDivision; j++) {
				lX = (float) cos(j * qy);
				lZ = (float) sin(j * qy);
				// vertex #1
				// normals
				fb.put(lX * ring1X);
				fb.put(lZ * ring1X);
				fb.put(ring1Y);
				// texture coordinates
				fb.put(1f * (conf.yDivision - j) / conf.yDivision);
				fb.put(1f * (conf.yDivision - i) / conf.yDivision);
				// vertex
				fb.put(lX * (float) aRad * ring1X);
				fb.put(lZ * (float) aRad * ring1X);
				fb.put(ring1Y * (float) aRad);

				// vertex #2
				// normals
				fb.put(lX * ring2X);
				fb.put(lZ * ring2X);
				fb.put(ring2Y);
				// texture coordinates
				fb.put(1f * (conf.yDivision - j) / conf.yDivision);
				fb.put(1f * (conf.yDivision - i - 1) / conf.yDivision);
				// vertex
				fb.put(lX * (float) aRad * ring2X);
				fb.put(lZ * (float) aRad * ring2X);
				fb.put(ring2Y * (float) aRad);
			}
		}
		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}