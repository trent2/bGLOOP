package bGLOOP.mesh.builder;
//code copied and modified from https://github.com/seanrowens/oObjLoader
//code is licensed under UNLICENSE which is basically public domain

public class VertexNormal {
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public void add(float x, float y, float z) {
	this.x += x;
	this.y += y;
	this.z += z;
    }

    public VertexNormal(float x, float y, float z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    @Override
	public String toString() {
    	return x+","+y+","+z;
    }
}