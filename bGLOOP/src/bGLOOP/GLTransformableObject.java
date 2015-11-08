package bGLOOP;

import bGLOOP.linalg.Matrix4;

abstract class GLTransformableObject extends GLObjekt implements IGLTransformierbar,
	IGLDisplayable, IGLSubdivisable {
	boolean needsRedraw = true;

	GLTransformableObject() {
		this(null);
	}

	GLTransformableObject(GLTextur pTex) {
		super(pTex);
		conf.objectRenderMode = wconf.globalObjectRenderMode;
		setzeDarstellungsModus(conf.displayMode = wconf.globalDrawMode);
	}

	@Override
	public synchronized void verschiebe(double pX, double pY, double pZ) {
		transformationMatrix.translateFromLeft((float) pX, (float) pY, (float) pZ);
		scheduleRender();
	}

	@Override
	public synchronized void drehe(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ) {
		transformationMatrix.translateFromLeft((float) -pX, (float) -pY, (float) -pZ);

		// c.f. http://www.j3d.org/matrix_faq/matrfaq_latest.html, Q36
		// don't open link in eclipse, it's a 404!
		// rotation is done in the following order:
		// first x-axis, then y-axis, then z-axis
		double A = Math.cos(Math.toRadians(pWinkelX));
		double B = Math.sin(Math.toRadians(pWinkelX));
		double C = Math.cos(Math.toRadians(pWinkelY));
		double D = Math.sin(Math.toRadians(pWinkelY));
		double E = Math.cos(Math.toRadians(pWinkelZ));
		double F = Math.sin(Math.toRadians(pWinkelZ));
		double AD = A * D;
		double BD = B * D;
		float[] r_mat = { (float) (C * E), (float) (BD * E + A * F), (float) (-AD * E + B * F), 0, (float) (-C * F),
				(float) (-BD * F + A * E), (float) (AD * F + B * E), 0, (float) D, (float) (-B * C), (float) (A * C), 0,
				(float) 0, (float) 0, 0, 1 };
		transformationMatrix.multMatrixFromLeft(r_mat);

		// shift to it's position from before
		transformationMatrix.translateFromLeft((float) pX, (float) pY, (float) pZ);
		scheduleRender();
	}

	@Override
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ) {
		float[] tr = transformationMatrix.getMatrix();
		this.drehe(pWinkelX, pWinkelY, pWinkelZ, tr[12], tr[13], tr[14]);
	}

	@Override
	public synchronized void skaliere(double pFaktorX, double pFaktorY, double pFaktorZ) {
		float[] tm = transformationMatrix.getMatrix();
		float[] tr = { tm[12], tm[13], tm[14] };
		transformationMatrix.translateFromLeft(-tr[0], -tr[1], -tr[2]);
		Matrix4 scaleMatrix = new Matrix4();
		/*
		 * transformationMatrix.multiplyFromLeft(Matrix.getScalationMatrix(
		 * pFaktorX, pFaktorY, pFaktorZ));
		 */
		scaleMatrix.scale((float) pFaktorX, (float) pFaktorY, (float) pFaktorZ);
		scaleMatrix.multMatrix(transformationMatrix);
		scaleMatrix.translateFromLeft(tr[0], tr[1], tr[2]);
		transformationMatrix = scaleMatrix;
		scheduleRender();
	}

	@Override
	public void skaliere(double pFaktor) {
		skaliere(pFaktor, pFaktor, pFaktor);
	}

	@Override
	public synchronized void setzeDarstellungsModus(Darstellungsmodus dm) {
		if (conf.displayMode != dm) {
			conf.displayMode = dm;
			scheduleRender();
		}
	}

	@Override
	public synchronized void setzeQualitaet(int pBreitengrade, int pLaengengrade) {
		if(pBreitengrade != conf.xDivision || pLaengengrade != conf.yDivision) {
			conf.xDivision = pBreitengrade;
			conf.yDivision = pLaengengrade;
			needsRedraw = true;
			scheduleRender();
		}
	}

	@Override
	public synchronized void setzeQualitaet(int pUnterteilungen) {
		setzeQualitaet(pUnterteilungen, pUnterteilungen);
	}

	@Override
	public double gibX() {
		return transformationMatrix.getMatrix()[12]/transformationMatrix.getMatrix()[15];
	}

	@Override
	public double gibY() {
		return transformationMatrix.getMatrix()[13]/transformationMatrix.getMatrix()[15];
	}

	@Override
	public double gibZ() {
		return transformationMatrix.getMatrix()[14]/transformationMatrix.getMatrix()[15];
	}
}
