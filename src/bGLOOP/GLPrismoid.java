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
 * <em>regelmäßigen</em> n-Ecks mit unterschiedlichem Radius, deren Ecken respektive
 * miteinander verbunden sind. Die n-Ecks liegen so zueinander im Raum, dass die
 * Verbindungslinie durch die beiden Mittelpunkte senkrecht zu beiden n-Ecks steht
 * (also sind die beiden n-Ecks insbesondere parallel zueinander).</p>
 * <p>In bGLOOP werden Prismoiden vor allem als Basisklasse für
 * {@link GLKegelstumpf Kegelstümpfe} und {@link GLKegel Kegel} verwendet, aber es
 * können auch GLPrismoid-Objekte instanziiert werden.</p>
 * @author R. Spillner
 */
public class GLPrismoid extends GLTransformableObject implements IGLSubdivisable {
	int aEcken, numberOfRadsNotEqualToZero = 0;
	double aRad1;
	double aRad2;
	double aHoehe;
	boolean aMantelglaettung;
	private FloatBuffer fb;
	private int[] firstOffsets, countOffsets;
	int aKonzentrischeKreise;
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
		aEcken = pEckenzahl;
		aRad1 = pRadius1;
		aRad2 = pRadius2;
		aHoehe = pHoehe;

		aMantelglaettung = false;
		aKonzentrischeKreise = 1;
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

        // drawing from buffer
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_STRIP, firstOffsets, 0, countOffsets, 0, aKonzentrischeKreise);
        gl.glMultiDrawArrays(GL2.GL_TRIANGLE_FAN, firstOffsets, aKonzentrischeKreise, countOffsets, aKonzentrischeKreise, numberOfRadsNotEqualToZero);
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
		gl.glEnable(GL2.GL_CULL_FACE);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		glu.gluQuadricTexture(quadric, true);
		gl.glTranslated(0, 0, aHoehe/2);
		glu.gluDisk(quadric, 0, aRad1, aEcken, 1);
		gl.glPushMatrix();
		gl.glRotated(180, 1, 0, 0);
		gl.glTranslated(0, 0, aHoehe);
		glu.gluDisk(quadric, 0, aRad2, aEcken, 1);
		gl.glPopMatrix();
		gl.glTranslated(0, 0, -aHoehe);
		glu.gluCylinder(quadric, aRad2, aRad1, aHoehe, aEcken, aKonzentrischeKreise);
		// glu.gluDeleteQuadric(quadric);
		gl.glEndList();
	}

	@Override
	void generateDisplayList_GL(GL2 gl) {
		double mittelpunktswinkel = 2 * PI / aEcken;

		double lNorm = 0;
		double lMAbschnitt = aHoehe / aKonzentrischeKreise;
		boolean texturePresent = (aTex != null) && aTex.isReady();

		gl.glNewList(bufferName, GL2.GL_COMPILE);
		for (int j = 0; j < aKonzentrischeKreise; j++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);

			double x = sin(mittelpunktswinkel / 2);
			double y = -cos(mittelpunktswinkel / 2);

			for (int i = 0; i <= aEcken; i++) {
				double x2 = sin(mittelpunktswinkel / 2 + ((i + 1) * mittelpunktswinkel));
				double y2 = -cos(mittelpunktswinkel / 2 + ((i + 1) * mittelpunktswinkel));

				double rad1 = aRad1 + j * (aRad2 - aRad1) / aKonzentrischeKreise;
				double rad2 = aRad1 + (j + 1) * (aRad2 - aRad1) / aKonzentrischeKreise;

				if (!aMantelglaettung) {
					lNorm = (x + x2) * (x+x2) + (y + y2) * (y + y2);
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
				} else {
					gl.glNormal3d(x, y, 0);
				}
				if(texturePresent)
					gl.glTexCoord2d(i * mittelpunktswinkel / (2*PI), (double)j / aKonzentrischeKreise);
				gl.glVertex3d(x * rad1, y * rad1, aHoehe / 2 - j * lMAbschnitt);

				if(texturePresent)
					gl.glTexCoord2d(i * mittelpunktswinkel / (2*PI), (j + 1.0) / aKonzentrischeKreise);
				gl.glVertex3d(x * rad2, y * rad2, aHoehe / 2 - (j + 1) * lMAbschnitt);

				if (!aMantelglaettung) {
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
					if(texturePresent)
						gl.glTexCoord2d((i * mittelpunktswinkel + mittelpunktswinkel) / (2*PI), (double)j / aKonzentrischeKreise);
					gl.glVertex3d(x2 * rad1, y2 * rad1, aHoehe / 2 - j * lMAbschnitt);
					if(texturePresent)
						gl.glTexCoord2d((i * mittelpunktswinkel + mittelpunktswinkel) / (2*PI), (j + 1.0) / aKonzentrischeKreise);
					gl.glVertex3d(x2 * rad2, y2 * rad2, aHoehe / 2 - (j + 1) * lMAbschnitt);
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
			gl.glVertex3d(0, 0, aHoehe / 2);
			for (int i = 0; i <= aEcken; i++) {
				double x = sin(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				double y = -cos(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				if(texturePresent)
					gl.glTexCoord2d(0.5 + x / 2, 0.5 - y / 2);
				gl.glVertex3d(x * aRad1, y * aRad1, aHoehe / 2);
			}
			gl.glEnd();
		}
		if (aRad2 != 0) {
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glNormal3d(0, 0, -1);
			if(texturePresent)
				gl.glTexCoord2d(0.5, 0.5);
			gl.glVertex3d(0.0, 0.0, -aHoehe / 2);
			for (int i = aEcken; i >= 0; i--) {
				double x = sin(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				double y = -cos(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				if(texturePresent)
					gl.glTexCoord2d(0.5 + x / 2, 0.5 + y / 2);
				gl.glVertex3d(x * aRad2, y * aRad2, -aHoehe / 2);
			}
			gl.glEnd();
		}
		gl.glEndList();
	}

	/**
	 * Bei geglättetem Mantel erscheint dieser in der {@link Darstellungsmodus}
	 * FUELLEN als zusammenhängende Fläche. Ist die Mantelglättung deaktiviert,
	 * so sind die Rechtecke sichtbar, die durch die polygonale Näherung der Boden-
	 * und Deckelfläche an einen Kreis entstehen.
	 * @param pG aktiviert Mantelglättung
	 */
	public void setzeMantelglaettung(boolean pG) {
		aMantelglaettung = pG;
		needsRedraw = true;
		scheduleRender();
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

		// build offset arrays to address different stages during
		// drawing process of the prismoid
		numberOfRadsNotEqualToZero = (aRad1!=0?1:0) + (aRad2!=0?1:0);
		firstOffsets = new int[aKonzentrischeKreise+numberOfRadsNotEqualToZero];
		countOffsets = new int[aKonzentrischeKreise+numberOfRadsNotEqualToZero];

		
		for (int i = 0; i < aKonzentrischeKreise; ++i) {
			firstOffsets[i] = (aMantelglaettung ? 2 : 4) * (aEcken + 1) * i;
			countOffsets[i] = (aMantelglaettung ? 2 : 4) * (aEcken + 1);
		}
		int t2 = 0;
		if(aRad1!=0) {
			firstOffsets[aKonzentrischeKreise] = firstOffsets[aKonzentrischeKreise-1] + countOffsets[aKonzentrischeKreise-1];
			countOffsets[aKonzentrischeKreise] = aEcken + 2;
			t2++;
		}
		if (aRad2 != 0) {
			firstOffsets[aKonzentrischeKreise + t2] = firstOffsets[aKonzentrischeKreise + t2 - 1]
					+ countOffsets[aKonzentrischeKreise + t2 - 1];
			countOffsets[aKonzentrischeKreise + t2] = aEcken + 2;
		}
		
		int vertexBufferSize =  16 * ((aMantelglaettung ? 1 : 2) * (aEcken + 1) * aKonzentrischeKreise + aEcken + 2) * Buffers.SIZEOF_FLOAT;
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBufferSize, null,
				GL2.GL_STATIC_DRAW);
		fb = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).order(ByteOrder.nativeOrder())
				.asFloatBuffer();

		// ready for drawing (to buffer)
		double mittelpunktswinkel = 2 * PI / aEcken;
		double lNorm = 0;
		double lMAbschnitt = aHoehe / aKonzentrischeKreise;

		for (int j = 0; j < aKonzentrischeKreise; j++) {
			double x = sin(mittelpunktswinkel / 2);
			double y = -cos(mittelpunktswinkel / 2);

			for (int i = 0; i <= aEcken; i++) {
				double x2 = sin(mittelpunktswinkel / 2 + ((i + 1) * mittelpunktswinkel));
				double y2 = -cos(mittelpunktswinkel / 2 + ((i + 1) * mittelpunktswinkel));

				double rad1 = aRad1 + j * (aRad2 - aRad1) / aKonzentrischeKreise;
				double rad2 = aRad1 + (j + 1) * (aRad2 - aRad1) / aKonzentrischeKreise;

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


				fb.put((float)(i * mittelpunktswinkel / (2*PI)));
				fb.put((float)j / aKonzentrischeKreise);
				fb.put((float)(x * rad1));
				fb.put((float)(y * rad1));
				fb.put((float)(aHoehe / 2 - j * lMAbschnitt));  // 8

				if (!aMantelglaettung) {
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
				} else {
					fb.put((float)x);
					fb.put((float)y);
					fb.put(0);
				}
				fb.put((float)(i * mittelpunktswinkel / (2*PI)));
				fb.put((j + 1f) / aKonzentrischeKreise);
				fb.put((float)(x * rad2));
				fb.put((float)(y * rad2));
				fb.put((float)(aHoehe / 2 - (j + 1) * lMAbschnitt));  // 16

				if (!aMantelglaettung) {
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
					fb.put((float)((i * mittelpunktswinkel + mittelpunktswinkel) / (2*PI)));
					fb.put((float)j / aKonzentrischeKreise);
					fb.put((float)(x2 * rad1));
					fb.put((float)(y2 * rad1));
					fb.put((float)(aHoehe / 2 - j * lMAbschnitt));  // 24
					fb.put((float)((x + x2) / lNorm));
					fb.put((float)((y + y2) / lNorm));
					fb.put(0);
					fb.put((float)((i * mittelpunktswinkel + mittelpunktswinkel) / (2*PI)));
					fb.put((j + 1f) / aKonzentrischeKreise);
					fb.put((float)(x2 * rad2));
					fb.put((float)(y2 * rad2));
					fb.put((float)(aHoehe / 2 - (j + 1) * lMAbschnitt));  // 32
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
			fb.put((float)(aHoehe / 2)); // 8

			for (int i = 0; i <= aEcken; i++) {
				double x = sin(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				double y = -cos(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				fb.put(0);
				fb.put(0);
				fb.put(1);
				fb.put((float)(0.5 + x / 2));
				fb.put((float)(0.5 - y / 2));
				fb.put((float)(x * aRad1));
				fb.put((float)(y * aRad1));
				fb.put((float)(aHoehe / 2)); // 8
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
			fb.put((float)(-aHoehe / 2)); // 8
			for (int i = aEcken; i >= 0; i--) {
				double x = sin(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				double y = -cos(mittelpunktswinkel / 2 + i * mittelpunktswinkel);
				fb.put(0);
				fb.put(0);
				fb.put(-1);
				fb.put((float)(0.5 + x / 2));
				fb.put((float)(0.5 + y / 2));
				fb.put((float)(x * aRad2));
				fb.put((float)(y * aRad2));
				fb.put((float)(-aHoehe / 2));  // 8
			}
		}
		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		needsRedraw = false;
	}
}
