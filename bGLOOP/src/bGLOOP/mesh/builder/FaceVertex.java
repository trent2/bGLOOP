package bGLOOP.mesh.builder;
//code copied and modified from https://github.com/seanrowens/oObjLoader
//code is licensed under UNLICENSE which is basically public domain

public class FaceVertex {

    int index = -1;
    public VertexGeometric v = null;
    public VertexTexture t = null;
    public VertexNormal n = null;

    @Override
	public String toString() {
        return v + "|" + n + "|" + t;
    }
}