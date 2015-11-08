package bGLOOP;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class GLPrismoid extends GLTransformableObject implements IGLSubdivisable {
	int aEckenzahl;
	double aRad1;
	double aRad2;
	double aTiefe;
	boolean aMantelglaettung;
	private FloatBuffer fb;
	private int[] firstOffsets, countOffsets;
;
	int aMantelqualitaet;
	// TODO: remove stupid Manteglättung, Mantelqualität

	public GLPrismoid(double pX, double pY, double pZ, double pRadius1, double pRadius2, int pEckenzahl,
			double pHoehe) {
		this(pX, pY, pZ, pRadius1, pRadius2, pEckenzahl, pHoehe, null);
	}

	public GLPrismoid(double pX, double pY, double pZ, double pRadius1, double pRadius2, int pEckenzahl, double pHoehe,
			GLTextur pTextur) {
		super(pTextur);
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		setzeDarstellungsModus(conf.displayMode);

		verschiebe(pX, pY, pZ);

		aEckenzahl = pEckenzahl;
		aRad1 = pRadius1;
		aRad2 = pRadius2;
		aTiefe = pHoehe;

		aMantelglaettung = false;
		aMantelqualitaet = 1;
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

        // drawing from buffer
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_STRIP, firstOffsets, 0, countOffsets, 0, aMantelqualitaet);
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_FAN, firstOffsets, aMantelqualitaet, countOffsets, aMantelqualitaet, 2);
        // ----------------

        gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY);

        // unbind buffer
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		throw new AssertionError("Diese Methode dürfte nie aufgerufen worden sein.");
	}

	@Override
	void generateDisplayList(GL2 gl) {
		double lWinkel = 360.0 / aEckenzahl;

		double lNorm = 0;
		double lMantelschritt = aTiefe / aMantelqualitaet;

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		for (int j = 0; j < aMantelqualitaet; j++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);

			double x = Math.sin(Math.toRadians(lWinkel / 2));
			double y = -Math.cos(Math.toRadians(lWinkel / 2));

			for (int i = 0; i <= aEckenzahl; i++) {
				double x2 = Math.sin(Math.toRadians(lWinkel / 2 + ((i + 1) * lWinkel)));
				double y2 = -Math.cos(Math.toRadians(lWinkel / 2 + ((i + 1) * lWinkel)));

				double rad1 = aRad1 + j * (aRad2 - aRad1) / aMantelqualitaet;
				double rad2 = aRad1 + (j + 1) * (aRad2 - aRad1) / aMantelqualitaet;

				if (!aMantelglaettung) {
					lNorm = (x + x2) * (x+x2) + (y + y2) * (y + y2);
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
				} else {
					gl.glNormal3d(x, y, 0);
				}
				gl.glTexCoord2d(i * lWinkel / 360, j / aMantelqualitaet);
				gl.glVertex3d(x * rad1, y * rad1, aTiefe / 2 - j * lMantelschritt);
				gl.glTexCoord2d(i * lWinkel / 360, (j + 1) / aMantelqualitaet);
				gl.glVertex3d(x * rad2, y * rad2, aTiefe / 2 - (j + 1) * lMantelschritt);

				if (!aMantelglaettung) {
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
					gl.glTexCoord2d((i * lWinkel + lWinkel) / 360, j / aMantelqualitaet);
					gl.glVertex3d(x2 * rad1, y2 * rad1, aTiefe / 2 - j * lMantelschritt);
					gl.glTexCoord2d((i * lWinkel + lWinkel) / 360, (j + 1) / aMantelqualitaet);
					gl.glVertex3d(x2 * rad2, y2 * rad2, aTiefe / 2 - (j + 1) * lMantelschritt);
				}

				x = x2;
				y = y2;
			}
			gl.glEnd();
			
		}

		if (aRad1 > 0) {
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, 0, 1);
			gl.glTexCoord2d(0.5, 0.5);
			gl.glVertex3d(0, 0, aTiefe / 2);
			for (int i = 0; i <= aEckenzahl; i++) {
				double x = Math.sin(Math.toRadians(lWinkel / 2 + i * lWinkel));
				double y = -Math.cos(Math.toRadians(lWinkel / 2 + i * lWinkel));
				gl.glTexCoord2d(0.5 + x / 2, 0.5 - y / 2);
				gl.glVertex3d(x * aRad1, y * aRad1, aTiefe / 2);
			}
			gl.glEnd();
		}
		if (aRad2 > 0) {
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, 0, -1);
			gl.glTexCoord2d(0.5, 0.5);
			gl.glVertex3d(0.0, 0.0, -aTiefe / 2);
			for (int i = aEckenzahl; i >= 0; i--) {
				double x = Math.sin(Math.toRadians(lWinkel / 2 + i * lWinkel));
				double y = -Math.cos(Math.toRadians(lWinkel / 2 + i * lWinkel));
				gl.glTexCoord2d(0.5 + x / 2, 0.5 + y / 2);
				gl.glVertex3d(x * aRad2, y * aRad2, -aTiefe / 2);
			}
			gl.glEnd();
		}
		gl.glEndList();
	}

	public void setzeMantelglaettung(boolean pG) {
		aMantelglaettung = pG;
		needsRedraw = true;
		scheduleRender();
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

		firstOffsets = new int[aMantelqualitaet+2];
		countOffsets = new int[aMantelqualitaet+2];
		for(int i=0; i<aMantelqualitaet; ++i) {
			firstOffsets[i] = 32 * (aEckenzahl + 1) * i;
			countOffsets[i] = 32 * (aEckenzahl + 1);
		}
		firstOffsets[aMantelqualitaet] = aMantelqualitaet * 32 * (aEckenzahl + 1);
		countOffsets[aMantelqualitaet] = 8 * (aEckenzahl + 2);
		firstOffsets[aMantelqualitaet + 1] = firstOffsets[aMantelqualitaet] + countOffsets[aMantelqualitaet]; 
		countOffsets[aMantelqualitaet + 1] = 8 * (aEckenzahl + 2);
		
		int vertexBufferSize = 16 * conf.xDivision * (conf.yDivision + 1) * Buffers.SIZEOF_FLOAT;
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBufferSize, null,
				GL2.GL_STATIC_DRAW);
		fb = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		// ready for drawing (to buffer)
		double lWinkel = 360.0 / aEckenzahl;
		double lNorm = 0;
		double lMantelschritt = aTiefe / aMantelqualitaet;
		for (int j = 0; j < aMantelqualitaet; j++) {
			double x = Math.sin(Math.toRadians(lWinkel / 2));
			double y = -Math.cos(Math.toRadians(lWinkel / 2));
			for (int i = 0; i <= aEckenzahl; i++) {
				double x2 = Math.sin(Math.toRadians(lWinkel / 2 + ((i + 1) * lWinkel)));
				double y2 = -Math.cos(Math.toRadians(lWinkel / 2 + ((i + 1) * lWinkel)));
				double rad1 = aRad1 + j * (aRad2 - aRad1) / aMantelqualitaet;
				double rad2 = aRad1 + (j + 1) * (aRad2 - aRad1) / aMantelqualitaet;
				if (!aMantelglaettung) {
					lNorm = (x + x2) * (x+x2) + (y + y2) * (y + y2);
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
				} else {
					fb.put((float)x);
					fb.put((float)y);
					fb.put(0);
				}
				fb.put((float)(i * lWinkel / 360));
				fb.put((float)(j / aMantelqualitaet));
				fb.put((float)(x * rad1));
				fb.put((float)(y * rad1));
				fb.put((float)(aTiefe / 2 - j * lMantelschritt));

				if (!aMantelglaettung) {
					lNorm = (x + x2) * (x+x2) + (y + y2) * (y + y2);
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
				} else {
					fb.put((float)x);
					fb.put((float)y);
					fb.put(0);
				}

				fb.put((float)(i * lWinkel / 360));
				fb.put((float)((j + 1) / aMantelqualitaet));
				fb.put((float)(x * rad2));
				fb.put((float)(y * rad2));
				fb.put((float)(aTiefe / 2 - (j + 1) * lMantelschritt));

				if (!aMantelglaettung) {
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
					fb.put((float)((i * lWinkel + lWinkel) / 360));
					fb.put((float)(j / aMantelqualitaet));
					fb.put((float)(x2 * rad1));
					fb.put((float)(y2 * rad1));
					fb.put((float)(aTiefe / 2 - j * lMantelschritt));
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
					fb.put((float)((i * lWinkel + lWinkel) / 360));
					fb.put((float)((j + 1) / aMantelqualitaet));
					fb.put((float)(x2 * rad2));
					fb.put((float)(y2 * rad2));
					fb.put((float)(aTiefe / 2 - (j + 1) * lMantelschritt));
				}

				x = x2;
				y = y2;
			}
		}

		fb.put(0);
		fb.put(0);
		fb.put(1);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(0);
		fb.put((float)(aTiefe / 2));
		for (int i = 0; i <= aEckenzahl; i++) {
			double x = Math.sin(Math.toRadians(lWinkel / 2 + i * lWinkel));
			double y = -Math.cos(Math.toRadians(lWinkel / 2 + i * lWinkel));
			fb.put(0);
			fb.put(0);
			fb.put(1);
			fb.put((float)(0.5 + x / 2));
			fb.put((float)(0.5 - y / 2));
			fb.put((float)(x * aRad1));
			fb.put((float)(y * aRad1));
			fb.put((float)(aTiefe / 2));
		}

		fb.put(0);
		fb.put(0);
		fb.put(-1);
		fb.put(0.5f);
		fb.put(0.5f);
		fb.put(0);
		fb.put(0);
		fb.put((float)(-aTiefe / 2));
		for (int i = aEckenzahl; i >= 0; i--) {
			double x = Math.sin(Math.toRadians(lWinkel / 2 + i * lWinkel));
			double y = -Math.cos(Math.toRadians(lWinkel / 2 + i * lWinkel));
			fb.put(0);
			fb.put(0);
			fb.put(1);
			fb.put((float)(0.5 + x / 2));
			fb.put((float)(0.5 + y / 2));
			fb.put((float)(x * aRad2));
			fb.put((float)(y * aRad2));
			fb.put((float)(-aTiefe / 2));
		}
		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}
