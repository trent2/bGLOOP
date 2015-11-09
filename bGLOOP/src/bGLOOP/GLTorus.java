package bGLOOP;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * Ein Torusmodell.<br>
 * <!-- <img alt="Abbildung Torus" src="./doc-files/Torus-1.png">  -->
 * 
 * @author R. Spillner
 */
public class GLTorus extends GLTransformableObject {
	private double aRadA, aRadQ;
	private FloatBuffer fb;
	private int[] firstOffsets, countOffsets;

	/** Erzeugt einen Torus mit Mittelpunkt <code>M(pMX, pMY, pMZ)</code>,
	 * äußerem Radius <code>pRadiusA</code> und Querschnittsrradius <code>pRadiusQ</code>.
	 * Dabei liegt der Torus in der xy-Ebene, das Loch in der Mitte ist parallel
	 * zur z-Achse.
	 * <!--<div style="float:right"><img alt="Abbildung Torus" src="./doc-files/Torus-1.png">
	 * </div>-->
	 * <div><p>
	 * <em>Abbildung:</em> Lage eines Torus mit Mittelpunkt <code>M(0,0,0)</code>
	 * und Radius <code>2</code>.
	 * </p>
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts des Torus
	 * @param pMY y-Koordinate des Mittelpunkts des Torus
	 * @param pMZ z-Koordinate des Mittelpunkts des Torus
	 * @param pRadiusA äußerer Radius des Torus
	 * @param pRadiusQ Radius des Schnittkreises des Torus
	 * </div><div style="clear:right"></div>
	 */
	public GLTorus(double pMX, double pMY, double pMZ, double pRadiusA, double pRadiusQ) {
		this(pMX, pMY, pMZ, pRadiusA, pRadiusQ, null);
	}

	/**
	/** Erzeugt einen Torus mit Textur.
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts des Torus
	 * @param pMY y-Koordinate des Mittelpunkts des Torus
	 * @param pMZ z-Koordinate des Mittelpunkts des Torus
	 * @param pRadiusA äußerer Radius des Torus
	 * @param pRadiusQ Radius des Schnittkreises des Torus
	 * </div><div style="clear:right"></div>
	 * @param pTextur Textur-Objekt für die Oberfläche der Kugel
	 * @see #GLKugel(double, double, double, double)
	 */
	public GLTorus(double pMX, double pMY, double pMZ, double pRadiusA, double pRadiusQ, GLTextur pTextur) {
		super(pTextur);
		if(conf.objectRenderMode == Rendermodus.RENDER_GLU)
			conf.objectRenderMode = Rendermodus.RENDER_GL;
		setzeDarstellungsModus(conf.displayMode);
		verschiebe(pMX, pMY, pMZ);
		if(pRadiusA<0 || pRadiusQ<0)
			throw new IllegalArgumentException("Die Radien dürfen nicht negativ sein.");
		aRadA = pRadiusA;
		aRadQ = pRadiusQ;
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		throw new AssertionError("Diese Methode dürfte nie aufgerufen worden sein.");
	}
	@Override
	void generateDisplayList(GL2 gl) {
		double lxy, lz;
		boolean texturePresent = (aTex != null) && aTex.isReady();

		float qStrip = (float) (2 * Math.PI / conf.yDivision);
		float qRound = (float) (2 * Math.PI / conf.xDivision);

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		for (int i = 0; i < conf.xDivision; ++i) { // conf.xDivision; i++) {
			double ring1X = Math.cos(i*qRound);
			double ring2X = Math.cos((i + 1) * qRound);

			double ring1Y = Math.sin(i * qRound);
			double ring2Y = Math.sin((i + 1) * qRound);

			// need to go around one whole turn
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int j = 0; j <= conf.yDivision; j++) {
				lxy = Math.cos(j * qStrip);
				lz  = Math.sin(j * qStrip);

				gl.glNormal3d(lxy * ring1X, lxy * ring1Y, lz);
				if (texturePresent)
					gl.glTexCoord2d(1f * i / conf.xDivision, 1f * j / conf.yDivision);
				gl.glVertex3d(ring1X * (aRadA + lxy * aRadQ), ring1Y * (aRadA + lxy * aRadQ), lz * aRadQ);

				gl.glNormal3d(lxy * ring2X, lxy * ring2Y, lz);
				if (texturePresent)
					gl.glTexCoord2d(1f * (i+1) / conf.xDivision, 1f * j / conf.yDivision);
				gl.glVertex3d(ring2X * (aRadA + lxy * aRadQ), ring2Y * (aRadA + lxy * aRadQ), lz * aRadQ);
			}
			gl.glEnd();
		}
		gl.glEndList();
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
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_STRIP, firstOffsets, 0, countOffsets, 0, conf.xDivision);

        gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY);

        // unbind buffer
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
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
		float qStrip = (float) (2 * Math.PI / conf.yDivision);
		float qRound = (float) (2 * Math.PI / conf.xDivision);
		double lxy, lz;
		for (int i = 0; i < conf.xDivision; ++i) { // conf.xDivision; i++) {
			double ring1X = Math.cos(i*qRound);
			double ring2X = Math.cos((i + 1) * qRound);

			double ring1Y = Math.sin(i * qRound);
			double ring2Y = Math.sin((i + 1) * qRound);

			// need to go around one whole turn
			for (int j = 0; j <= conf.yDivision; j++) {
				lxy = Math.cos(j * qStrip);
				lz  = Math.sin(j * qStrip);

				fb.put((float)(lxy * ring1X));
				fb.put((float)(lxy * ring1Y));
				fb.put((float)lz);
				fb.put((float)(1f * i / conf.xDivision));
				fb.put((float)(1f * j / conf.yDivision));
				fb.put((float)(ring1X * (aRadA + lxy * aRadQ)));
				fb.put((float)(ring1Y * (aRadA + lxy * aRadQ)));
				fb.put((float)(lz * aRadQ));

				fb.put((float)(lxy * ring2X));
				fb.put((float)(lxy * ring2Y));
				fb.put((float)lz);
				fb.put((float)(1f * (i+1) / conf.xDivision));
				fb.put((float)(1f * j / conf.yDivision));
				fb.put((float)(ring2X * (aRadA + lxy * aRadQ)));
				fb.put((float)(ring2Y * (aRadA + lxy * aRadQ)));
				fb.put((float)(lz * aRadQ));
			}
		}
		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}