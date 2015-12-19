package bGLOOP.mesh.builder;
//code copied and modified from https://github.com/seanrowens/oObjLoader
//code is licensed under UNLICENSE which is basically public domain

import java.util.ArrayList;

public class BuilderPOJOs {
	public static class Face {

	    public ArrayList<FaceVertex> vertices = new ArrayList<FaceVertex>();
	    public Material material = null;
	    public Material map = null;

	    public Face() {
	    }

	    public void add(FaceVertex vertex) {
	        vertices.add(vertex);
	    }
	    public VertexNormal faceNormal = new VertexNormal(0, 0, 0);

	    // @TODO: This code assumes the face is a triangle.  
	    public void calculateTriangleNormal() {
	        float[] edge1 = new float[3];
	        float[] edge2 = new float[3];
	        float[] normal = new float[3];
	        VertexGeometric v1 = vertices.get(0).v;
	        VertexGeometric v2 = vertices.get(1).v;
	        VertexGeometric v3 = vertices.get(2).v;
	        float[] p1 = {v1.x, v1.y, v1.z};
	        float[] p2 = {v2.x, v2.y, v2.z};
	        float[] p3 = {v3.x, v3.y, v3.z};

	        edge1[0] = p2[0] - p1[0];
	        edge1[1] = p2[1] - p1[1];
	        edge1[2] = p2[2] - p1[2];

	        edge2[0] = p3[0] - p2[0];
	        edge2[1] = p3[1] - p2[1];
	        edge2[2] = p3[2] - p2[2];

	        normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
	        normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
	        normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

	        faceNormal.x = normal[0];
	        faceNormal.y = normal[1];
	        faceNormal.z = normal[2];
	    }
	    
	    @Override
		public String toString() { 
	        String result = "\tvertices: "+vertices.size()+" :\n";
	        for(FaceVertex f : vertices) {
	            result += " \t\t( "+f.toString()+" )\n";
	        }
	        return result;
	    }
	}

	public static class FaceVertex {

	    int index = -1;
	    public VertexGeometric v = null;
	    public VertexTexture t = null;
	    public VertexNormal n = null;

	    @Override
		public String toString() {
	        return v + "|" + n + "|" + t;
	    }
	}

	public static class Material {

	    public String name;
	    public ReflectivityTransmiss ka = new ReflectivityTransmiss();
	    public ReflectivityTransmiss kd = new ReflectivityTransmiss();
	    public ReflectivityTransmiss ks = new ReflectivityTransmiss();
	    public ReflectivityTransmiss tf = new ReflectivityTransmiss();
	    public int illumModel = 0;
	    public boolean dHalo = false;
	    public double dFactor = 0.0;
	    public double nsExponent = 0.0;
	    public double sharpnessValue = 0.0;
	    public double niOpticalDensity = 0.0;
	    public String mapKaFilename = null;
	    public String mapKdFilename = null;
	    public String mapKsFilename = null;
	    public String mapNsFilename = null;
	    public String mapDFilename = null;
	    public String decalFilename = null;
	    public String dispFilename = null;
	    public String bumpFilename = null;
	    public int reflType = BuilderInterface.MTL_REFL_TYPE_UNKNOWN;
	    public String reflFilename = null;

	    public Material(String name) {
	        this.name = name;
	    }
	}

	public static class ReflectivityTransmiss {

	    public boolean isRGB = false;
	    public boolean isXYZ = false;
	    public double rx;
	    public double gy;
	    public double bz;

	    public ReflectivityTransmiss() {
	    }

	    public float[] getFloatv() {
	    	return new float[] { (float)rx, (float)gy, (float)bz };
	    }
	}

	public static class VertexGeometric {

	    public float x = 0;
	    public float y = 0;
	    public float z = 0;

	    public VertexGeometric(float x, float y, float z) {
	        this.x = x;
	        this.y = y;
	        this.z = z;
	    }

	    @Override
		public String toString() {
	    	return x + "," + y + "," + z;
	    }
	}

	public static class VertexNormal {
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

	public static class VertexTexture {

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
}
