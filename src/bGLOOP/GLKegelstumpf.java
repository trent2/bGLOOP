package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.VectorUtil;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Ein Kegelstumpf ist ein Kegel, dessen Spitze abgeschitten wurde. Die
 * anfängliche Lage ist analog zu der des {@link GLKegel Kegels}. <br>
 * <img alt="Abbildung Kegelstumpf" src="./doc-files/Kegelstumpf-1.png">
 * 
 * @see GLKegel
 * @author R. Spillner
 */
public class GLKegelstumpf extends GLPrismoid {
	/**
	 * Erzeugt einen Kegelstumpf längs zur z-Achse.
	 * <div style="float:right"> <img alt=
	 * "Abbildung Kegelstumpf" src="./doc-files/Kegelstumpf-1.png"> </div> <div>
	 * <p>
	 * Der Kegelstumpf liegt in der Höhe parallel zur z-Achse. Die Bodenfläche
	 * hat den Radius <code>pRadius1</code> und zeigt in positive z-Richtung,
	 * die Deckelfläche hat den Radius <code>pRadius2</code> und zeigt in
	 * negative z-Richtung.
	 * </p>
	 * <p>
	 * Der Mittelpunkt <code>M(pMX, pMY, pMZ)</code>des Kegelstumpfs ist der
	 * Punkt auf halber Höhe <code>pHoehe</code>.
	 * </p>
	 * <p>
	 * <em>Abbildung:</em> Lage eines Kegelstumpfs mit Mittelpunkt
	 * <code>M(0,0,0)</code> und Höhe 2. Der Mittelpunkt des Bodenkreises liegt
	 * folglich in <code>(0,0,1)</code>, die Spitze in <code>(0,0,-1)</code>.
	 * </p>
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts
	 * @param pMY y-Koordinate des Mittelpunkts
	 * @param pMZ z-Koordinate des Mittelpunkts
	 * @param pRadius1 Radius der Deckelfläche
	 * @param pRadius2 Radius der Bodenfläche
	 * @param pHoehe Höhe des Kegelstumpfs
	 * </div>
	 * <div style="clear:right"></div>
	 */
	public GLKegelstumpf(double pMX, double pMY, double pMZ, double pRadius1, double pRadius2, double pHoehe) {
		this(pMX, pMY, pMZ, pRadius1, pRadius2, pHoehe, null);
	}

	/**
	 * Erzeugt einen Kegelstumpf mit Textur.
	 * 
	 * @param pMX x-Koordinate des Mittelpunkts
	 * @param pMY y-Koordinate des Mittelpunkts
	 * @param pMZ z-Koordinate des Mittelpunkts
	 * @param pRadius1 Radius der Bodenfläche
	 * @param pRadius2 Radius der Deckelfläche
	 * @param pHoehe Höhe des Kegelstumpfs
	 * @param pTextur Textur-Objekt des Kegelstumpfs
	 */
	public GLKegelstumpf(double pMX, double pMY, double pMZ, double pRadius1, double pRadius2, double pHoehe,
			GLTextur pTextur) {
		super(pMX, pMY, pMZ, pRadius1, pRadius2, 12, pHoehe, pTextur);
		aMantelglaettung = true;
		aKonzentrischeKreise = 3;
		aEcken = 0;
	}

	private void setQualityBasedOnCameraDistance() {
		float[] trl = transformationMatrix.getMatrix();
		float dist = VectorUtil.normVec3(new float[] {
				(float)(trl[12] - associatedCam.aPos[0]),
				(float)(trl[13] - associatedCam.aPos[1]),
				(float)(trl[14] - associatedCam.aPos[2])
			});

		aEcken = (int) (-1.25 * dist / max(aRad1, aRad2) + 50);
		aEcken = max(min(aEcken, 50), 10);

		needsRedraw = true;
	}

	@Override
	void render(GL2 gl, GLU glu) {
		if (aEcken == 0)
			setQualityBasedOnCameraDistance();
		super.render(gl, glu);
	}

	@Override
	public synchronized void setzeQualitaet(int pQ) {
		aEcken = pQ;
		needsRedraw = true;
		scheduleRender();
	}
}