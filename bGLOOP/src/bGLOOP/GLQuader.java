package bGLOOP;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Ein Quadermodell.<br>
 * <img alt="Abbildung Quader" src="./doc-files/Quader-1.png">
 * 
 * @author R. Spillner
 */
public class GLQuader extends GLTransformableObject {
	private FloatBuffer fb;
	private int[] firstOffsets = { 0, 10, 14 }, countOffsets = { 10, 4, 4 };

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
	 * @param pMX x-Koordinate des Mittelpunkts des Quaders
	 * @param pMY y-Koordinate des Mittelpunkts des Quaders
	 * @param pMZ z-Koordinate des Mittelpunkts des Quaders
	 * @param pLX Seitenlänge in x-Richtung des Quaders
	 * @param pLY Seitenlänge in y-Richtung des Quaders
	 * @param pLZ Seitenlänge in z-Richtung des Quaders
	 * @param pTextur Textur-Objekt für die Oberfläche des Quaders
	 */
	public GLQuader(double pMX, double pMY, double pMZ, double pLX, double pLY, double pLZ, GLTextur pTextur) {
		super(pTextur);
		if(pLX<0 || pLY<0 || pLZ<0)
			throw new IllegalArgumentException("Die Seitenlängen dürfen nicht negativ sein!");
		skaliere(pLX, pLY, pLZ);
		verschiebe(pMX, pMY, pMZ);
	}

	@Override
	void doRenderGL_VBO(GL2 gl) {
		if(needsRedraw)
			generateVBO(gl);

		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, bufferName);
        gl.glEnableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glEnableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glEnableClientState( GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glNormalPointer(GL.GL_FLOAT, 8 * Buffers.SIZEOF_FLOAT, 0 );
        gl.glTexCoordPointer( 2, GL.GL_FLOAT, 8 * Buffers.SIZEOF_FLOAT, 3 * Buffers.SIZEOF_FLOAT );
        gl.glVertexPointer(3, GL.GL_FLOAT, 8 * Buffers.SIZEOF_FLOAT, 5 * Buffers.SIZEOF_FLOAT);

        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_STRIP, firstOffsets, 0, countOffsets, 0, 3);

        gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 10); 
        gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 10); 
        gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 10); 

        gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY);

        // unbind buffer
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		doRenderGL(gl);
	}

	@Override		
	void generateDisplayList(GL2 gl) {
		gl.glNewList(bufferName, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_QUAD_STRIP);
		gl.glNormal3d(-0.5, 0.5, 0.5);
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(-0.5, 0.5, 0.5);

		gl.glNormal3d(-0.5, -0.5, 0.5);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-0.5, -0.5, 0.5);

		gl.glNormal3d(0.5, 0.5, 0.5);
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(0.5, 0.5, 0.5);

		gl.glNormal3d(0.5, -0.5, 0.5);
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(0.5, -0.5, 0.5);

		gl.glNormal3d(0.5, 0.5, -0.5);
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(0.5, 0.5, -0.5);

		gl.glNormal3d(0.5, -0.5, -0.5);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(0.5, -0.5, -0.5);

		gl.glNormal3d(-0.5, 0.5, -0.5);
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(-0.5, 0.5, -0.5);

		gl.glNormal3d(-0.5, -0.5, -0.5);
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(-0.5, -0.5, -0.5);

		gl.glNormal3d(-0.5, 0.5, 0.5);
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(-0.5, 0.5, 0.5);

		gl.glNormal3d(-0.5, -0.5, 0.5);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-0.5, -0.5, 0.5);
		gl.glEnd();

		// Deckel
		gl.glBegin(GL2.GL_QUADS);

		gl.glNormal3d(-0.5, 0.5, 0.5);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-0.5, 0.5, 0.5);

		gl.glNormal3d(0.5, 0.5, 0.5);
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(0.5, 0.5, 0.5);

		gl.glNormal3d(0.5, 0.5, -0.5);
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(0.5, 0.5, -0.5);

		gl.glNormal3d(-0.5, 0.5, -0.5);
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(-0.5, 0.5, -0.5);

		// Boden
		gl.glNormal3d(-0.5, -0.5, 0.5);
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-0.5, -0.5, 0.5);

		gl.glNormal3d(-0.5, -0.5, -0.5);
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(-0.5, -0.5, -0.5);

		gl.glNormal3d(0.5, -0.5, -0.5);
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(0.5, -0.5, -0.5);

		gl.glNormal3d(0.5, -0.5, 0.5);
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(0.5, -0.5, 0.5);
		gl.glEnd();

		gl.glEndList();
	}

	private void generateVBO(GL2 gl) {
		int[] t = new int[1];
		if(bufferName != -1) {
			t[0] = bufferName;
			gl.glDeleteBuffers(1, t, 0);
			fb.clear();
		}
		gl.glGenBuffers(1, t, 0);
		bufferName = t[0];
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, 144 * Buffers.SIZEOF_FLOAT, null,
				GL2.GL_STATIC_DRAW);
		fb = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		// ready for drawing (to buffer)
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(1);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0.5f);

		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(0);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);

		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(1);
		fb.put(1);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0.5f);

		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(1);
		fb.put(0);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);

		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(0);
		fb.put(1);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);

		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0);
		fb.put(0);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);

		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(1);
		fb.put(1);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);

		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(1);
		fb.put(0);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);

		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(1);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0.5f);

		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(0);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);

		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(0);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0.5f);

		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(1);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0.5f);

		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(1);
		fb.put(0);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);

		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(1);
		fb.put(1);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(-0.5f);

		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(0);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);

		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(0);
		fb.put(1);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);

		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);
		fb.put(1);
		fb.put(0);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(0.5f);

		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);
		fb.put(1);
		fb.put(1);
		fb.put(0.5f);
		fb.put(-0.5f);
		fb.put(-0.5f);

		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}
