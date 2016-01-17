package bGLOOP;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Quaternion;

/**
 * Ein Quadermodell.<br>
 * <img alt="Abbildung Quader" src="./doc-files/Quader-1.png">
 * 
 * @author R. Spillner
 */
public class GLQuader extends TransformableSurfaceObject {
	private FloatBuffer fb;
	private int[] firstOffsets = { 0, 4, 8, 12, 16, 20}, countOffsets = { 4, 4, 4, 4, 4, 4 };

	/**
	 * Erzeugt einen Quader mit Mittelpunkt <code>M(pMX, pMY, pMZ)</code> und
	 * Seitenlängen <code>pLX</code>, <code>pLY</code> und <code>pLZ</code>.
	 * <div style="float:right">
	 * <img alt="Abbildung Quader" src="./doc-files/Quader-1.png">
	 * </div><div>
	 * <p>
	 * <em>Abbildung:</em> Lage eines Quaders mit Mittelpunkt
	 * <code>M(0,0,0)</code> und Seitenlängen <code>(1.4,0.6,1)</code>.
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
		aVisible = true;
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

        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_STRIP, firstOffsets, 0, countOffsets, 0, 6);

        gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY);

        // unbind buffer
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
	}

	@Override
	void generateDisplayList_GLU(GL2 gl, GLU glu) {
		generateDisplayList_GL(gl);
	}

	@Override		
	void generateDisplayList_GL(GL2 gl) {
		float[] v = new float[] { 0.5f, 0.5f, -0.5f },
				n = new float[] { 1, 0, 0};
		Quaternion rot1 = new Quaternion().rotateByAngleNormalAxis((float)Math.PI/2, n[0], n[1], n[2]),
				rot2 = new Quaternion().rotateByAngleY((float)Math.PI/2);

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_QUADS);
		for(int k=0; k<2; ++k) {
			for(int face = 0; face < 4-(2*k); ++face) {
				for(int vertex = 0; vertex < 4; ++vertex) {
					gl.glNormal3fv(n, 0);
					gl.glTexCoord2f(((vertex+3)%4) >> 1, 1-(vertex >> 1));
					gl.glVertex3fv(v, 0);
					rot1.rotateVector(v, 0, v, 0);
				}
				rot2.rotateVector(n, 0, n, 0);
				rot2.rotateVector(v, 0, v, 0);
				rot1.setIdentity().rotateByAngleNormalAxis((float)Math.PI/2, n[0], n[1], n[2]);
			}
			n[0] = 0; n[1] = 1; n[2] = 0;
			rot1.setIdentity().rotateByAngleY((float)Math.PI/2);
			rot2.setIdentity().rotateByAngleZ((float)Math.PI);
		}
		gl.glEnd();
		gl.glEndList();
	}

	@Override
	void generateVBO(GL2 gl) {
		int[] t = new int[1];
		if (fb != null && bufferName != -1) {
			t[0] = bufferName;
			gl.glDeleteBuffers(1, t, 0);
			fb.clear();
		}
		gl.glGenBuffers(1, t, 0);
		bufferName = t[0];
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, 192 * Buffers.SIZEOF_FLOAT, null, GL2.GL_STATIC_DRAW);
		fb = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).order(ByteOrder.nativeOrder()).asFloatBuffer();

		float[] v = new float[] { 0.5f, 0.5f, -0.5f }, n = new float[] { 1, 0, 0 };

		Quaternion rot1 = new Quaternion().rotateByAngleNormalAxis((float) Math.PI / 2, n[0], n[1], n[2]),
				rot2 = new Quaternion().rotateByAngleY((float) Math.PI / 2);

		for (int k = 0; k < 2; ++k) {
			for (int face = 0; face < 4-(2*k); ++face) {
				for (int vertex = 0; vertex < 4; ++vertex) {
					fb.put(n);
					fb.put((vertex+1) & 1);
					fb.put(1-(vertex >> 1));
					fb.put(v);
					rot1.rotateVector(v, 0, v, 0);
					rot1.rotateByAngleNormalAxis((float) Math.PI / 2, n[0], n[1], n[2]);
				}
				rot1.set(rot2).rotateByAngleNormalAxis((float) Math.PI, n[0], n[1], n[2]);
				rot2.rotateVector(n, 0, n, 0);
				rot1.rotateVector(v, 0, v, 0);
				rot1.setIdentity().rotateByAngleNormalAxis((float) Math.PI / 2, n[0], n[1], n[2]);
			}
			n[0] = 0;
			n[1] = 1;
			n[2] = 0;
			rot1.setIdentity().rotateByAngleY((float) Math.PI / 2);
			rot2.setIdentity().rotateByAngleZ((float) Math.PI);
		}

		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}
