package bGLOOP;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

/** <p>Ein Prismoid ist eine Art polygonaler Kegelstumpf. Es besteht aus zwei
 * <em>regelmäßigen</em> n-Ecks mit unteschiedlichem Radius, deren Ecken respektive
 * miteinander verbunden sind. Die n-Ecks liegen so zueinander im Raum, dass die
 * Verbindungslinie durch die beiden Mittelpunkte senkrecht zu beiden n-Ecks steht
 * (also sind die beiden n-Ecks insbesondere parallel zueinander).</p>
 * <p>In bGLOOP werden Prismoiden vor allem als Basisklasse für
 * {@link GLKegelstumpf Kegelstümpfe} und {@link GLKegel Kegel} verwendet, aber es
 * können auch GLPrismoid-Objekte instanziiert werden.</p>
 * @author R. Spillner
 */
public class GLPrismoid extends GLTransformableObject implements IGLSubdivisable {
	int aEckenzahl, numberOfRadsNotEqualToZero = 0;
	double aRad1;
	double aRad2;
	double aTiefe;
	boolean aMantelglaettung;
	private FloatBuffer fb;
	private int[] firstOffsets, countOffsets;
	int aMantelqualitaet;
	GLUquadric qbot, qtop;

	/** Erzeugt ein Prismoidobjekt mit Mittelpunkt <code>M(pMX, pMY, pMZ)</code>,
	 * Radien <code>pRadius1</code> und <code>pRadius2</code> und Höhe
	 * <code>pHoehe</code>. Die 
	 * @param pMX x-Koordinate des Mittelpunkts
	 * @param pMY y-Koordinate des Mittelpunkts
	 * @param pMZ z-Koordinate des Mittelpunkts
	 * @param pRadius1 Radius der Bodenfläche
	 * @param pRadius2 Radius der Deckelfläche
	 * @param pEckenzahl Anzahl n der regelmäßigen n-Ecks der Boden- und Deckelfläche
	 * @param pHoehe Abstand zwischen Boden- und Deckelfläche
	 */
	public GLPrismoid(double pMX, double pMY, double pMZ, double pRadius1, double pRadius2, int pEckenzahl,
			double pHoehe) {
		this(pMX, pMY, pMZ, pRadius1, pRadius2, pEckenzahl, pHoehe, null);
	}
	/** Erzeugt ein Prismoidobjekt mit Textur.
	 * @param pMX x-Koordinate des Mittelpunkts
	 * @param pMY y-Koordinate des Mittelpunkts
	 * @param pMZ z-Koordinate des Mittelpunkts
	 * @param pRadius1 Radius der Bodenfläche
	 * @param pRadius2 Radius der Deckelfläche
	 * @param pEckenzahl Anzahl n der regelmäßigen n-Ecks der Boden- und Deckelfläche
	 * @param pHoehe Abstand zwischen Boden- und Deckelfläche
	 * @param pTextur Textur-Objekt des Prismoids
	 */
	public GLPrismoid(double pMX, double pMY, double pMZ, double pRadius1, double pRadius2, int pEckenzahl, double pHoehe,
			GLTextur pTextur) {
		super(pTextur);

		verschiebe(pMX, pMY, pMZ);

		if(pEckenzahl<0 || pRadius1<0 || pRadius2<0 || pHoehe < 0)
			throw new IllegalArgumentException("Eckenzahl, Radien und Höhe dürfen nicht negativ sein!");
		aEckenzahl = pEckenzahl;
		aRad1 = pRadius1;
		aRad2 = pRadius2;
		aTiefe = pHoehe;

		aMantelglaettung = false;
		aMantelqualitaet = 1;
		aVisible = true;
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
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_FAN, firstOffsets, aMantelqualitaet, countOffsets, aMantelqualitaet, numberOfRadsNotEqualToZero);
        // ----------------

        gl.glDisableClientState( GL2.GL_VERTEX_ARRAY );
        gl.glDisableClientState( GL2.GL_NORMAL_ARRAY );
        gl.glDisableClientState( GL2.GL_TEXTURE_COORD_ARRAY);

        // unbind buffer
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
	}

	@Override
	void generateDisplayList_GLU(GL2 gl, GLU glu) {
		// gl.glColor3f(1, 1, 1);
		gl.glNewList(bufferName, GL2.GL_COMPILE);
		gl.glDisable(GL2.GL_CULL_FACE);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		gl.glTranslated(0, 0, aTiefe / 2);
		glu.gluDisk(quadric, 0, aRad1, aEckenzahl, aMantelqualitaet);
		gl.glTranslated(0, 0, -aTiefe / 2);
		glu.gluDisk(quadric, 0, aRad2, aEckenzahl, aMantelqualitaet);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glTranslated(0, 0, -aTiefe / 2);
		glu.gluCylinder(quadric, aRad2, aRad1, aTiefe, aEckenzahl, aMantelqualitaet);
		// glu.gluDeleteQuadric(quadric);
		gl.glEndList();
	}

	@Override
	void generateDisplayList_GL(GL2 gl) {
		double lWinkel = 2 * PI / aEckenzahl;

		double lNorm = 0;
		double lMantelschritt = aTiefe / aMantelqualitaet;
		boolean texturePresent = (aTex != null) && aTex.isReady();

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		for (int j = 0; j < aMantelqualitaet; j++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);

			double x = sin(lWinkel / 2);
			double y = -cos(lWinkel / 2);

			for (int i = 0; i <= aEckenzahl; i++) {
				double x2 = sin(lWinkel / 2 + ((i + 1) * lWinkel));
				double y2 = -cos(lWinkel / 2 + ((i + 1) * lWinkel));

				double rad1 = aRad1 + j * (aRad2 - aRad1) / aMantelqualitaet;
				double rad2 = aRad1 + (j + 1) * (aRad2 - aRad1) / aMantelqualitaet;

				if (!aMantelglaettung) {
					lNorm = (x + x2) * (x+x2) + (y + y2) * (y + y2);
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
				} else {
					gl.glNormal3d(x, y, 0);
				}
				if(texturePresent)
					gl.glTexCoord2d(i * lWinkel / (2*PI), j / aMantelqualitaet);
				gl.glVertex3d(x * rad1, y * rad1, aTiefe / 2 - j * lMantelschritt);
				if(texturePresent)
					gl.glTexCoord2d(i * lWinkel / (2*PI), (j + 1) / aMantelqualitaet);
				gl.glVertex3d(x * rad2, y * rad2, aTiefe / 2 - (j + 1) * lMantelschritt);

				if (!aMantelglaettung) {
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
					if(texturePresent)
						gl.glTexCoord2d((i * lWinkel + lWinkel) / (2*PI), j / aMantelqualitaet);
					gl.glVertex3d(x2 * rad1, y2 * rad1, aTiefe / 2 - j * lMantelschritt);
					if(texturePresent)
						gl.glTexCoord2d((i * lWinkel + lWinkel) / (2*PI), (j + 1) / aMantelqualitaet);
					gl.glVertex3d(x2 * rad2, y2 * rad2, aTiefe / 2 - (j + 1) * lMantelschritt);
				}

				x = x2;
				y = y2;
			}
			gl.glEnd();
		}

		if (aRad1 != 0) {
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, 0, 1);
			if(texturePresent)
				gl.glTexCoord2d(0.5, 0.5);
			gl.glVertex3d(0, 0, aTiefe / 2);
			for (int i = 0; i <= aEckenzahl; i++) {
				double x = sin(lWinkel / 2 + i * lWinkel);
				double y = -cos(lWinkel / 2 + i * lWinkel);
				if(texturePresent)
					gl.glTexCoord2d(0.5 + x / 2, 0.5 - y / 2);
				gl.glVertex3d(x * aRad1, y * aRad1, aTiefe / 2);
			}
			gl.glEnd();
		}
		if (aRad2 != 0) {
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, 0, -1);
			if(texturePresent)
				gl.glTexCoord2d(0.5, 0.5);
			gl.glVertex3d(0.0, 0.0, -aTiefe / 2);
			for (int i = aEckenzahl; i >= 0; i--) {
				double x = sin(lWinkel / 2 + i * lWinkel);
				double y = -cos(lWinkel / 2 + i * lWinkel);
				if(texturePresent)
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

		// build offset arrays to address different stages during
		// drawing process of the prismoid
		numberOfRadsNotEqualToZero = (aRad1!=0?1:0) + (aRad2!=0?1:0);
		firstOffsets = new int[aMantelqualitaet+numberOfRadsNotEqualToZero];
		countOffsets = new int[aMantelqualitaet+numberOfRadsNotEqualToZero];

		
		for (int i = 0; i < aMantelqualitaet; ++i) {
			firstOffsets[i] = (aMantelglaettung ? 2 : 4) * (aEckenzahl + 1) * i;
			countOffsets[i] = (aMantelglaettung ? 2 : 4) * (aEckenzahl + 1);
		}
		int t2 = 0;
		if(aRad1!=0) {
			firstOffsets[aMantelqualitaet] = firstOffsets[aMantelqualitaet-1] + countOffsets[aMantelqualitaet-1];
			countOffsets[aMantelqualitaet] = aEckenzahl + 2;
			t2++;
		}
		if (aRad2 != 0) {
			firstOffsets[aMantelqualitaet + t2] = firstOffsets[aMantelqualitaet + t2 - 1]
					+ countOffsets[aMantelqualitaet + t2 - 1];
			countOffsets[aMantelqualitaet + t2] = aEckenzahl + 2;
		}
		
		int vertexBufferSize =  16 * ((aMantelglaettung ? 1 : 2) * (aEckenzahl + 1) * aMantelqualitaet + aEckenzahl + 2) * Buffers.SIZEOF_FLOAT;
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBufferSize, null,
				GL2.GL_STATIC_DRAW);
		fb = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		// ready for drawing (to buffer)
		double lWinkel = 2 * PI / aEckenzahl;
		double lNorm = 0;
		double lMantelschritt = aTiefe / aMantelqualitaet;

		for (int j = 0; j < aMantelqualitaet; j++) {
			double x = sin(lWinkel / 2);
			double y = -cos(lWinkel / 2);

			for (int i = 0; i <= aEckenzahl; i++) {
				double x2 = sin(lWinkel / 2 + ((i + 1) * lWinkel));
				double y2 = -cos(lWinkel / 2 + ((i + 1) * lWinkel));

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


				fb.put((float)(i * lWinkel / (2*PI)));
				fb.put((float)(j / aMantelqualitaet));
				fb.put((float)(x * rad1));
				fb.put((float)(y * rad1));
				fb.put((float)(aTiefe / 2 - j * lMantelschritt));  // 8

				if (!aMantelglaettung) {
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
				} else {
					fb.put((float)x);
					fb.put((float)y);
					fb.put(0);
				}
				fb.put((float)(i * lWinkel / (2*PI)));
				fb.put((float)((j + 1) / aMantelqualitaet));
				fb.put((float)(x * rad2));
				fb.put((float)(y * rad2));
				fb.put((float)(aTiefe / 2 - (j + 1) * lMantelschritt));  // 16

				if (!aMantelglaettung) {
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
					fb.put((float)((i * lWinkel + lWinkel) / (2*PI)));
					fb.put((float)(j / aMantelqualitaet));
					fb.put((float)(x2 * rad1));
					fb.put((float)(y2 * rad1));
					fb.put((float)(aTiefe / 2 - j * lMantelschritt));  // 24
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
					fb.put((float)((i * lWinkel + lWinkel) / (2*PI)));
					fb.put((float)((j + 1) / aMantelqualitaet));
					fb.put((float)(x2 * rad2));
					fb.put((float)(y2 * rad2));
					fb.put((float)(aTiefe / 2 - (j + 1) * lMantelschritt));  // 32
				}
				x = x2;
				y = y2;
			}
		}

		if (aRad1 != 0) {
			fb.put(0);
			fb.put(0);
			fb.put(1);
			fb.put(0.5f);
			fb.put(0.5f);
			
			fb.put(0);
			fb.put(0);
			fb.put((float)(aTiefe / 2)); // 8

			for (int i = 0; i <= aEckenzahl; i++) {
				double x = sin(lWinkel / 2 + i * lWinkel);
				double y = -cos(lWinkel / 2 + i * lWinkel);
				fb.put(0);
				fb.put(0);
				fb.put(1);
				fb.put((float)(0.5 + x / 2));
				fb.put((float)(0.5 - y / 2));
				fb.put((float)(x * aRad1));
				fb.put((float)(y * aRad1));
				fb.put((float)(aTiefe / 2)); // 8
			}
		}
		if (aRad2 != 0) {
			fb.put(0);
			fb.put(0);
			fb.put(-1);
			fb.put(0.5f);
			fb.put(0.5f);
			fb.put(0);
			fb.put(0);
			fb.put((float)(-aTiefe / 2)); // 8
			for (int i = aEckenzahl; i >= 0; i--) {
				double x = sin(lWinkel / 2 + i * lWinkel);
				double y = -cos(lWinkel / 2 + i * lWinkel);
				fb.put(0);
				fb.put(0);
				fb.put(-1);
				fb.put((float)(0.5 + x / 2));
				fb.put((float)(0.5 + y / 2));
				fb.put((float)(x * aRad2));
				fb.put((float)(y * aRad2));
				fb.put((float)(-aTiefe / 2));  // 8
			}
		}
		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}
