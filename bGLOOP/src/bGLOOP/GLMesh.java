package bGLOOP;

import static java.lang.Math.max;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import bGLOOP.mesh.Parse;
import bGLOOP.mesh.builder.Build;
import bGLOOP.mesh.builder.Face;
import bGLOOP.mesh.builder.FaceVertex;
import bGLOOP.mesh.builder.Material;

public class GLMesh extends GLTransformableObject {
	private boolean parseOk = false;
	private Build meshBuild;
	private double aMeshScale, aMaxCoordScale;
	private double[] meshDiameter = new double[3];
	private HashMap<String, GLTextur> texMap;

	public GLMesh(String pDateiname, double pMeshMaxScale, GLTextur pTextur) {
		super(pTextur);
		texMap = new HashMap<String, GLTextur>(5);
		try {
			new Parse(meshBuild = new Build(), pDateiname);
			parseOk = true;
			needsRedraw = true;
			aMeshScale = pMeshMaxScale < 0 ? associatedCam.getWconf().meshMaxScale : pMeshMaxScale;
			for(int i = 0; i<3; ++i)
				meshDiameter[i] = meshBuild.vertexCoordinateRanges[2*i+1] - meshBuild.vertexCoordinateRanges[2*i];
			aMaxCoordScale = max(meshDiameter[0], max(meshDiameter[1], meshDiameter[2]));

			skaliere(aMeshScale / aMaxCoordScale);

			if (pTextur == null)
				for (Map.Entry<String, Material> e : meshBuild.materialLib.entrySet()) {
					String s;
					if ((s = e.getValue().mapKdFilename) != null)
						texMap.put(e.getKey(), new GLTextur(new File(new File(pDateiname).getParent(), s)));
				}
				
		} catch (IOException fnfe) {
			fnfe.printStackTrace();
		}
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		aVisible = true;
	}

	public GLMesh(String pDateiname, double pMeshScale) {
		this(pDateiname, pMeshScale, null);
	}

	public GLMesh(String pDateiname) {
		this(pDateiname, -1, null);
	}

	public double gibDurchmesserX() {
		return aMeshScale*meshDiameter[0]/aMaxCoordScale;
	}

	public double gibDurchmesserY() {
		return aMeshScale*meshDiameter[1]/aMaxCoordScale;
	}

	public double gibDurchmesserZ() {
		return aMeshScale*meshDiameter[2]/aMaxCoordScale;
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		throw new AssertionError("Diese Methode dürfte nie aufgerufen worden sein.");
	}

	@Override
	void doRenderGL_VBO(GL2 gl) {
		doRenderGL(gl);
	}

	@Override
	void generateDisplayList(GL2 gl) {
		GLTextur currTex = null, newTex;
		String currMatName = null, newMatName;
		if (!parseOk)
			return;
		gl.glNewList(bufferName, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_TRIANGLES);

		boolean userTextureAvailable = (aTex != null) && aTex.isReady();
		boolean textureCoordinatesPresent = meshBuild.verticesT.size() > 0;

		for (Face fa : meshBuild.faces) {
			if(!userTextureAvailable) {
				newMatName = fa.material.name;
				if(!newMatName.equals(currMatName)) {
					newTex = texMap.get(currMatName = newMatName);
					if(newTex != currTex) {
						gl.glEnd();
						(currTex = newTex).loadAndEnable(gl);
						gl.glBegin(GL2.GL_TRIANGLES);
					}
				}
			}

			for (FaceVertex fv : fa.vertices) {
				if(fv.n != null)
					gl.glNormal3f(fv.n.x, fv.n.y, fv.n.z);

				// use texture coordinates
				if(textureCoordinatesPresent && (currTex != null || userTextureAvailable))
					gl.glTexCoord2d(fv.t.u, fv.t.v);
				else if(userTextureAvailable)
					gl.glTexCoord2d((fv.v.x + meshBuild.vertexCoordinateRanges[0]) / aMaxCoordScale,
							(fv.v.z + meshBuild.vertexCoordinateRanges[4]) / (aMaxCoordScale));

				// set the vertex
				gl.glVertex3f(fv.v.x, fv.v.y, fv.v.z);
			}
		}
		gl.glEnd();
		gl.glEndList();
		if(currTex != null && currTex.isReady())
			currTex.disable(gl);
	}
}
