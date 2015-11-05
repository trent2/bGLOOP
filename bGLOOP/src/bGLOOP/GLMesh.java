package bGLOOP;

import java.io.File;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class GLMesh extends GLObjekt {
	private File aFile;

	public GLMesh(String pDateiname) {
		aFile = new File(pDateiname);
	}

	@Override
	void doRenderGLU(GL2 gl, GLU glu) {
		// TODO Auto-generated method stub

	}

	@Override
	void doRenderGL(GL2 gl) {
		// TODO Auto-generated method stub

	}

}
