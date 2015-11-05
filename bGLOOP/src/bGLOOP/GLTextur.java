package bGLOOP;

import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Ein Texturobjekt, welches an ein {@link GLObjekt} geknüpft werden kann.
 * 
 * @author R. Spillner
 */
public class GLTextur {
	Texture aTexture;
	File aTexFileName;
	private boolean aReady;

	/**
	 * Erstellt ein Textur-Objekt. Der Dateiname wird im <em>classpath</em>
	 * gesucht, das ist meistens der Projektordner, von dem aus Java gestartet
	 * wurde.
	 * 
	 * @param pTexturDateiname
	 *            Dateiname der Texturdatei.
	 */
	public GLTextur(String pTexturDateiname) {
		aTexFileName = new File(pTexturDateiname);
		aReady = false;
	}

	/**
	 * Load texture if not loaded before.
	 */
	void load(GL2 gl) {
		if (aTexture == null)
			try {
				aTexture = TextureIO.newTexture(aTexFileName, false);
				aTexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
				aTexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
				aReady = true;
			} catch (GLException | IOException e) {
				aReady = false;
				System.err.println("Die Datei " + aTexFileName.getName() + " konnte nicht "
						+ "korrekt geladen werden. Prüfen Sie die weiteren " + "Fehlermeldungen.");
				e.printStackTrace();
			}
	}

	boolean isReady() {
		return aReady;
	}
}
