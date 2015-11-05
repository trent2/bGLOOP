package bGLOOP.linalg;

public class Matrix implements Cloneable {
	static private double[] id = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

	private double[] mat = new double[16];

	/**
	 * Create a 4x4 identity matrix
	 */
	public Matrix() {
		System.arraycopy(id, 0, mat, 0, 16);
	}

	public void clone(double[] m) { // copy constructor
		if (m.length >= 16)
			System.arraycopy(m, 0, mat, 0, 16);
	}

	public void clone(Matrix m) { // copy constructor
		System.arraycopy(m.mat, 0, mat, 0, 16);
	}

	public Matrix(double[] m) { // copy constructor
		mat = m;
	}

	public void multiplyFromLeft(Matrix m) {
		double[] t = new double[16];

		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j)
				t[i * 4 + j] = mat[i * 4] * m.mat[j] + mat[i * 4 + 1] * m.mat[j + 4] + mat[i * 4 + 2] * m.mat[j + 8]
						+ mat[i * 4 + 3] * m.mat[j + 12];

		System.arraycopy(t, 0, mat, 0, 16);
	}

	public void multiplyFromRight(Matrix m) {
		double[] t = new double[16];

		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j)
				t[i * 4 + j] = m.mat[i * 4] * mat[j] + m.mat[i * 4 + 1] * mat[j + 4] + m.mat[i * 4 + 2] * mat[j + 8]
						+ m.mat[i * 4 + 3] * mat[j + 12];

		System.arraycopy(t, 0, mat, 0, 16);
	}

	// axis=0 --> x-axis, axis=1 --> y-axis, axis=2 --> z-axis
	public static Matrix getRotationMatrix(double angle, int axis) {
		double c = Math.cos(Math.toRadians(angle)), s = Math.sin(Math.toRadians(angle));
		Matrix m = new Matrix();

		m.mat[5 * ((axis + 1) % 3)] = c;
		m.mat[5 * ((axis + 2) % 3)] = c;

		m.mat[(axis + 1) % 3 + 4 * ((axis + 2) % 3)] = -s;
		m.mat[4 * ((axis + 1) % 3) + (axis + 2) % 3] = s;

		return m;
	}

	public static Matrix getScalationMatrix(double scaleX, double scaleY, double scaleZ) {
		Matrix m = new Matrix();

		m.mat[0] = scaleX;
		m.mat[5] = scaleY;
		m.mat[10] = scaleZ;

		return m;
	}

	public void translate(double[] v) {
		if (v == null || v.length < 3)
			throw new IllegalArgumentException("vector length needs to be (at least) 3");

		double w = mat[15];
		mat[12] += v[0] * w;
		mat[13] += v[1] * w;
		mat[14] += v[2] * w;

	}

	public void translate(double x, double y, double z) {
		double w = mat[15];
		mat[12] += x * w;
		mat[13] += y * w;
		mat[14] += z * w;
	}

	public double[] getTranslation() {
		return new double[] { mat[12] / mat[15], mat[13] / mat[15], mat[14] / mat[15] };
	}

	public double[] getMatrix() {
		return mat;
	}

	public static double[] negate(double[] m) {
		for (int i = 0; i < m.length; ++i)
			m[i] *= -1;
		return m;
	}

	public void set(int i, double value) {
		mat[i] = value;
	}
}
