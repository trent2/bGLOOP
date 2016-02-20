package bGLOOP;

import static java.lang.Math.toRadians;

import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;

import bGLOOP.linalg.Matrix4;

abstract class TransformableObject extends GLObjekt implements IGLTransformierbar, IGLDisplayable {
	Matrix4 transformationMatrix;

	TransformableObject() {
		super();
		// this must be initialized BEFORE adding to any display lists
		transformationMatrix = new Matrix4();
		setzeDarstellungsModus(conf.displayMode = wconf.globalDrawMode);
	}

	@Override
	public synchronized void verschiebe(double pX, double pY, double pZ) {
		final float[] m = transformationMatrix.getMatrix();
		m[12] = (float)(m[12] + pX*m[15]);
		m[13] = (float)(m[13] + pY*m[15]);
		m[14] = (float)(m[14] + pZ*m[15]);
		m[15] = 1;
		scheduleRender();
	}

	@Override
	public synchronized void setzePosition(double pX, double pY, double pZ) {
		final float[] m = transformationMatrix.getMatrix();
		m[12] = (float) pX;
		m[13] = (float) pY;
		m[14] = (float) pZ;
		m[15] = 1;
		scheduleRender();
	}

	@Override
	public synchronized void resetSkalierungUndRotation() {
		float[] tm = transformationMatrix.getMatrix();
		float[] tr = { tm[12], tm[13], tm[14] };
		transformationMatrix = new Matrix4();
		transformationMatrix.translateFromLeft(tr[0], tr[1], tr[2]);
		scheduleRender();
	}

	@Override
	public synchronized void drehe(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ) {
		Quaternion rot = new Quaternion().rotateByEuler((float) toRadians(pWinkelX), (float) toRadians(pWinkelY),
				(float) toRadians(pWinkelZ));
		float[] r_mat = new float[16];

		transformationMatrix.translateFromLeft((float) -pX, (float) -pY, (float) -pZ);
		rot.toMatrix(r_mat, 0);
		transformationMatrix.multMatrixFromLeft(r_mat);
		// shift to it's position from before
		transformationMatrix.translateFromLeft((float) pX, (float) pY, (float) pZ);

		scheduleRender();
	}

	@Override
	@Deprecated
	public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ, double pX, double pY, double pZ) {
		drehe(pWinkelX, pWinkelY, pWinkelZ, pX, pY, pZ);
	}

	@Override
	public void drehe(double pWinkelX, double pWinkelY, double pWinkelZ) {
		float[] tr = transformationMatrix.getMatrix();
		this.drehe(pWinkelX, pWinkelY, pWinkelZ, tr[12], tr[13], tr[14]);
	}

	@Override
	@Deprecated
	public void dreheDich(double pWinkelX, double pWinkelY, double pWinkelZ) {
		drehe(pWinkelX, pWinkelY, pWinkelZ);
	}

	@Override
	public void drehe(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX, double pRichtY,
			double pRichtZ) {
		float[] axis = new float[] { (float)pRichtX, (float)pRichtY, (float)pRichtZ };
		VectorUtil.normalizeVec3(axis);
		Quaternion rot = new Quaternion().rotateByAngleNormalAxis((float) toRadians(pWinkel), axis[0], axis[1], axis[2]);
		float[] r_mat = new float[16];

		transformationMatrix.translateFromLeft((float) -pOrtX, (float) -pOrtY, (float) -pOrtZ);
		rot.toMatrix(r_mat, 0);
		transformationMatrix.multMatrixFromLeft(r_mat);
		// shift to it's position from before
		transformationMatrix.translateFromLeft((float) pOrtX, (float) pOrtY, (float) pOrtZ);

		scheduleRender();
	}

	@Override
	@Deprecated	public void rotiere(double pWinkel, double pOrtX, double pOrtY, double pOrtZ, double pRichtX, double pRichtY,
			double pRichtZ) {
		drehe(pWinkel, pOrtX, pOrtY, pOrtZ, pRichtX, pRichtY, pRichtZ);
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
	public double gibX() {
		return transformationMatrix.getMatrix()[12] / transformationMatrix.getMatrix()[15];
	}

	@Override
	public double gibY() {
		return transformationMatrix.getMatrix()[13] / transformationMatrix.getMatrix()[15];
	}

	@Override
	public double gibZ() {
		return transformationMatrix.getMatrix()[14] / transformationMatrix.getMatrix()[15];
	}
}
