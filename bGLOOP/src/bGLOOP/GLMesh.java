package bGLOOP;

import static java.lang.Math.max;

import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import bGLOOP.mesh.Parse;
import bGLOOP.mesh.builder.Build;
import bGLOOP.mesh.builder.BuilderPOJOs.Face;
import bGLOOP.mesh.builder.BuilderPOJOs.FaceVertex;
import bGLOOP.mesh.builder.BuilderPOJOs.Material;

/** Klasse, die ein Mesh im OBJ-Format interpretiert und im
 * bGLOOP-Kontext verfügbar macht.
 * 
 * @author R. Spillner
 */
public class GLMesh extends GLTransformableObject {
	private boolean parseOk = false;
	private Build meshBuild;
	private double aMeshScale, aMaxCoordScale;
	private double[] meshDiameter = new double[3];
	private File meshFile;
	// private HashMap<String, GLTextur> texMap;

	/** Erstellt ein <code>GLMesh</code>-Objekt.
	 * @param pDateiname Dateiname der OBJ-Datei. Eventuell weitere benötigte Textur-Dateien
	 * werden in einer in der OBJ-Datei referenzierten MTL-Datei beschrieben. Hier
	 * sind alle Angaben relativ zum Ort der OBJ-Datei zu verstehen 
	 * @param pMeshMaxScale Maximale Ausdehnung des Models in eine der drei
	 * Hauptkoordinatenrichtungen
	 */
	public GLMesh(String pDateiname, double pMeshMaxScale) {
		super();
		// texMap = new HashMap<String, GLTextur>(5);
		try {
			new Parse(meshBuild = new Build(), meshFile = new File(pDateiname));
			parseOk = true;
			needsRedraw = true;
			aMeshScale = pMeshMaxScale < 0 ? associatedCam.getWconf().meshMaxScale : pMeshMaxScale;
			for (int i = 0; i < 3; ++i)
				meshDiameter[i] = meshBuild.vertexCoordinateRanges[2 * i + 1] - meshBuild.vertexCoordinateRanges[2 * i];
			aMaxCoordScale = max(meshDiameter[0], max(meshDiameter[1], meshDiameter[2]));

			skaliere(aMeshScale / aMaxCoordScale);
		} catch (IOException fnfe) {
			fnfe.printStackTrace();
		}
		conf.objectRenderMode = Rendermodus.RENDER_GL;
		setzeDarstellungsModus(conf.displayMode);
		aVisible = true;
	}

	public GLMesh(String pDateiname) {
		this(pDateiname, -1);
	}

	public double gibDurchmesserX() {
		return aMeshScale * meshDiameter[0] / aMaxCoordScale;
	}

	public double gibDurchmesserY() {
		return aMeshScale * meshDiameter[1] / aMaxCoordScale;
	}

	public double gibDurchmesserZ() {
		return aMeshScale * meshDiameter[2] / aMaxCoordScale;
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
	void generateDisplayList_GL(GL2 gl) {
		int currentVerticeSize = -1;
		boolean isEnded = true;
		GLTextur currTex = null, newTex;
		String currMatName = null, newMatName;
		if (!parseOk)
			return;
		gl.glNewList(bufferName, GL2.GL_COMPILE);

		boolean userTextureAvailable = (aTex != null) && aTex.isReady();

		for (Face fa : meshBuild.faces) {
			if (!isEnded && currentVerticeSize != (currentVerticeSize = fa.vertices.size())) {
				isEnded = true;
				gl.glEnd();
			}
			if (!userTextureAvailable && fa.material != null) {
				newMatName = fa.material.name;
				Material m = fa.material;
				if (!newMatName.equals(currMatName)) {
					loadMaterial(gl, m.ka.getFloatv(), m.kd.getFloatv(), m.ks.getFloatv(), aEmission,
							(float) m.nsExponent);
					if (fa.material.mapKdFilename != null) {
						newTex = new GLTextur(new File(meshFile.getParent(), fa.material.mapKdFilename));
						if (!newTex.equals(currTex)) {
							if (!isEnded) {
								isEnded = true;
								gl.glEnd();
							}
							(currTex = newTex).loadAndEnable(gl);
						}
					} else {
						gl.glDisable(GL2.GL_TEXTURE_2D);
					}
				}
			}
			if (isEnded) {
				if (fa.vertices.size() == 3)
					gl.glBegin(GL2.GL_TRIANGLES);
				else
					gl.glBegin(GL2.GL_QUADS);
				isEnded = false;
			}
			for (FaceVertex fv : fa.vertices) {
				if (fv.n != null)
					gl.glNormal3f(fv.n.x, fv.n.y, fv.n.z);

				// use texture coordinates
				if (fv.t != null && (currTex != null || userTextureAvailable))
					gl.glTexCoord2d(fv.t.u, fv.t.v);
				else if (userTextureAvailable)
					gl.glTexCoord2d((fv.v.x + meshBuild.vertexCoordinateRanges[0]) / aMaxCoordScale,
							(fv.v.z + meshBuild.vertexCoordinateRanges[4]) / (aMaxCoordScale));

				// set the vertex
				gl.glVertex3f(fv.v.x, fv.v.y, fv.v.z);
			}
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glEndList();
	}

	@Override
	void generateDisplayList_GLU(GL2 gl, GLU glu) {
		doRenderGL(gl);
	}
}
