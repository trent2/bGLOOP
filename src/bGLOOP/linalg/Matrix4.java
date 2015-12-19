package bGLOOP.linalg;

import com.jogamp.opengl.math.FloatUtil;

public class Matrix4 extends com.jogamp.opengl.math.Matrix4 {
	/**
	 * Multiply matrix: [this] = [m] x [this]
	 * 
	 * @param m
	 *            4x4 matrix in column-major order
	 */
	public final void multMatrixFromLeft(final float[] m) {
		FloatUtil.multMatrix(m, getMatrix(), getMatrix());
	}

	/**
	 * Schedule translation before all other operations described in this
	 * matrix.
	 */
	public final void translateFromLeft(final float x, final float y, final float z) {
		float[] m = getMatrix();
		m[12] += x;
		m[13] += y;
		m[14] += z;
	}
}
