package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class GLPrismoid extends GLTransformableObject implements IGLSubdivisable {
	int aEckenzahl;
	double aRad1;
	double aRad2;
	double aTiefe;
	boolean aMantelglaettung;
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
	void doRenderGLU(GL2 gl, GLU glu) {
		throw new AssertionError("Diese Methode dürfte nie aufgerufen worden sein.");
	}

	@Override
	public void wechselRendermodus() {
		throw new RuntimeException("Die Methode ist für diesen Typ nicht implementiert");
	}

	@Override
	void doRenderGL(GL2 gl) {
		double lWinkel = 360.0 / this.aEckenzahl;

		double lNorm = 0;
		double lMantelschritt = this.aTiefe / this.aMantelqualitaet;

		for (int j = 0; j < this.aMantelqualitaet; j++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);

			double x = Math.sin(Math.toRadians(lWinkel / 2));
			double y = -Math.cos(Math.toRadians(lWinkel / 2));

			for (int i = 0; i <= this.aEckenzahl; i++) {
				double x2 = Math.sin(Math.toRadians(lWinkel / 2 + ((i + 1) * lWinkel)));
				double y2 = -Math.cos(Math.toRadians(lWinkel / 2 + ((i + 1) * lWinkel)));

				double rad1 = aRad1 + j * (aRad2 - aRad1) / aMantelqualitaet;
				double rad2 = aRad1 + (j + 1) * (aRad2 - aRad1) / aMantelqualitaet;

				if (!aMantelglaettung) {
					lNorm = Math.sqrt(Math.pow(x + x2, 2) + Math.pow(y + y2, 2));
					gl.glNormal3d((x + x2) / lNorm, (y + y2) / lNorm, 0);
				} else {
					gl.glNormal3d(x, y, 0);
				}
				gl.glTexCoord2d(i * lWinkel / 360, j / aMantelqualitaet);
				gl.glVertex3d(x * rad1, y * rad1, aTiefe / 2 - j * lMantelschritt);
				gl.glTexCoord2d(i * lWinkel / 360, (j + 1) / aMantelqualitaet);
				gl.glVertex3d(x * rad2, y * rad2, this.aTiefe / 2 - (j + 1) * lMantelschritt);

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

	public void setzeMantelglaettung(boolean pG) {
		aMantelglaettung = pG;
		scheduleRender();
	}
}
