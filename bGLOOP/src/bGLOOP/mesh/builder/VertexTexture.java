package bGLOOP.mesh.builder;
//code copied and modified from https://github.com/seanrowens/oObjLoader
//code is licensed under UNLICENSE which is basically public domain

public class VertexTexture {

	public float u = 0;
	public float v = 0;

	VertexTexture(float u, float v) {
		this.u = u;
		this.v = v;
	}

	@Override
	public String toString() {
		return u + "," + v;
	}
}