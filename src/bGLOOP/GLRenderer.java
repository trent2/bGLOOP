package bGLOOP;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

import bGLOOP.GLObjekt.Rendermodus;
import bGLOOP.GLTextur.GLTextureImpl;
import bGLOOP.mesh.Parse;
import bGLOOP.windowimpl.Window;

class GLRenderer implements GLEventListener {
    private Logger log = Logger.getLogger(Parse.class.getName());

	private int window_rendering_needed = 2;
	private long animatorLastFPSTime = 0;
	private Animator animator;
	private GLKamera aCam;
	private GLWindowConfig wconf;
	private CopyOnWriteArrayList<GLDisplayItem> noTextureItemList;
	private ConcurrentHashMap<GLTextureImpl, CopyOnWriteArrayList<GLDisplayItem>>
		textureItemMap;

	private Window win;
	private boolean currentLighting;

	private boolean makeScreenshot = false;
	private String screenshotFilename = null;

	// TODO this does not belong here!!!!!!! This is just a hack
	// to be removed when I know better --- and when I have to subclass
	// ConcurrentHashMap for that
	private GLHimmel sky;

	GLRenderer(GLWindowConfig wc, int width, int height, GLKamera cam, boolean pFullscreen, boolean pNoDecoration) {
		log.setLevel(Level.INFO);
		wconf = wc;
		aCam = cam;
		win = Window.createWindowFactory(wconf.isAWT());

		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);

		caps.setHardwareAccelerated(true);
		caps.setDoubleBuffered(wconf.doubleBuffering);
		caps.setDepthBits(24);

		win.createWindow(caps, width, height);
		win.setFullscreen(pFullscreen);
		win.setDecoration(pNoDecoration);

		win.getAutoDrawable().addGLEventListener(this);
		animator = win.getAnimator();
		animator.start();
		animator.setUpdateFPSFrames(wconf.doubleBuffering?60:2000, null);
		win.startDisplay();
		currentLighting = !wconf.globalLighting;
		textureItemMap = new ConcurrentHashMap<GLTextureImpl, CopyOnWriteArrayList<GLDisplayItem>>(10);
		noTextureItemList = new CopyOnWriteArrayList<GLDisplayItem>();
	}

	CopyOnWriteArrayList<GLDisplayItem> getNoTextureItemList() {
			return noTextureItemList;
	}

	// remove oldImpl from map
	void addObjectToTextureMap(GLTextureImpl tImp, GLDisplayItem di, GLTextureImpl oldImpl) {
		if (!(di instanceof GLHimmel)) {
			if (oldImpl != null) {
				CopyOnWriteArrayList<GLDisplayItem> dil = textureItemMap.get(oldImpl);
				if (dil != null)
					dil.remove(di);
			}
			addObjectToTextureMap(tImp, di);
		}
		else
			sky = (GLHimmel)di;
	}

	private void addObjectToTextureMap(GLTextureImpl tImp, GLDisplayItem di) {
		if(textureItemMap.containsKey(tImp))
			textureItemMap.get(tImp).add(di);
		else {
			CopyOnWriteArrayList<GLDisplayItem> dil;
			dil = new CopyOnWriteArrayList<GLDisplayItem>();
			dil.add(di);
			textureItemMap.put(tImp, dil);
		}
	}

	Window getWindow() {
		return win;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Get the OpenGL graphics context
		GL2 gl = drawable.getGL().getGL2();
		// Get GL Utilities after the GL context created.

		gl.glShadeModel(wconf.globalShadeModel);
		// Set background color in RGBA. Alpha: 0 (transparent) 1 (opaque)
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Setup the depth buffer and enable the depth testing
		gl.glClearDepth(10000.0f); // clear z-buffer to the farthest
		gl.glEnable(GL2.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // the type of depth test to do
		if(gl.isFunctionAvailable("glBindBuffer"))
			gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0);
		else
			if(wconf.globalObjectRenderMode == Rendermodus.RENDER_VBOGL)
				wconf.globalObjectRenderMode = Rendermodus.RENDER_GLU; 

		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
		// Do the best perspective correction
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		doLighting(gl);

		/*
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_DONT_CARE);
		*/
		gl.glLineWidth(wconf.wireframeLineWidth);

		window_rendering_needed = wconf.doubleBuffering?3:1;
		// Volker zusätzlich
		/*
		 * gl.glEnable(GL2.GL_ALPHA_TEST); gl.glAlphaFunc(GL2.GL_GREATER, 0.1F);
		 * 
		 * this.gl.glEnable(GL2.GL_RESCALE_NORMAL);
		 */
	}

	private void doLighting(GL2 gl) {
		// only call these when lighting has changed
		if (currentLighting != wconf.globalLighting) {
			currentLighting = wconf.globalLighting;
			if (currentLighting) {
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glEnable(GL2.GL_NORMALIZE);
			} else {
				gl.glDisable(GL2.GL_LIGHTING);
				gl.glDisable(GL2.GL_NORMALIZE);
			}
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if (window_rendering_needed > 0 && noTextureItemList != null) {
			renderScene(drawable.getGL().getGL2());

			if (window_rendering_needed > 0)
				window_rendering_needed--;
			drawable.getGL().glDisable(12288);
		}
		updateFPSView();

		if(makeScreenshot) {
			takeScreenshot(drawable.getGL());
			makeScreenshot = false;
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		float aspect = (float) width / height;
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// fovy, aspect, zNear, zFar
		GLU.createGLU().gluPerspective(60, aspect, 0.1, 10000.0);

		window_rendering_needed = wconf.doubleBuffering?3:1;
	}

	void scheduleRender() {
		window_rendering_needed = wconf.doubleBuffering?3:1;
	}

	void scheduleScreenshot(String filename) {
		screenshotFilename = filename;
		makeScreenshot = true;
	}

	private void renderScene(GL2 gl) {
		GLU glu = GLU.createGLU();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // reset
		doLighting(gl);

		synchronized (aCam) {
			aCam.renderPreObjects(gl, glu);
		}

		GLTextureImpl tImp = null;

		// render sky before all other objects
		if(sky != null && sky.aTex != null && sky.aTex.aTexturImpl != null) {
			tImp = sky.aTex.aTexturImpl;
			tImp.load(gl);
			if (tImp.isReady()) {
				tImp.getTexture().enable(gl);
				tImp.getTexture().bind(gl);
			}
			if (sky.aVisible)
				synchronized (sky) {
					sky.render(gl, glu);
				}
		}

		// render objects without texture
		for (ConcurrentHashMap.Entry<GLTextureImpl, CopyOnWriteArrayList<GLDisplayItem>> entry : textureItemMap
				.entrySet()) {
			// load texture
			tImp = entry.getKey();
			tImp.load(gl);
			if (tImp.isReady()) {
				tImp.getTexture().enable(gl);
				tImp.getTexture().bind(gl);
			}
			// render objects
			for (GLDisplayItem di : entry.getValue())
				di.render(gl, glu);
		}
		if(tImp != null && tImp.isReady())
			tImp.getTexture().disable(gl);

		gl.glDisable(GL2.GL_TEXTURE_2D);
		// render objects with texture
		for (GLDisplayItem di : noTextureItemList)
			if (di.aVisible)
					di.render(gl, glu);

		synchronized (aCam) {
			aCam.renderPostObjects(gl, glu);
		}
	}

	private void updateFPSView() {
		long currentAnimFPSUpdateTime = animator.getLastFPSUpdateTime();
		if (currentAnimFPSUpdateTime != animatorLastFPSTime) {
			win.updateFPS(animator.getLastFPS());
			animatorLastFPSTime = currentAnimFPSUpdateTime;
		}
	}

	private void takeScreenshot(GL gl) {
		GLProfile glp = GLProfile.getDefault();

		// take screenshot
		AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(glp, false);
		BufferedImage bi = screenshot.readPixelsToBufferedImage(gl, true);

		File f;
		if(screenshotFilename == null)
			f = new File(
				    wconf.screenshotPrefix + "-"
				  + getNextScreenshotNumber(wconf.screenshotFormat, wconf.screenshotPrefix)
				  + "." + wconf.screenshotFormat
				);
			else
				f = new File(screenshotFilename);

		try {
			ImageIO.write(bi, wconf.screenshotFormat, f);
			log.info("Screenshot taken and saved to " + f.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getNextScreenshotNumber(final String fileFormat, final String filePrefix) {
		/* Here, one could use a lambda expression
		String[] files = new File(".").list((dir, filename) -> { return true; });
		return null;
		*/
		String[] files;
		Arrays.sort(files = new File(".").list(new FilenameFilter() {
			private Pattern p = Pattern.compile("-\\d\\d\\d\\d\\." + fileFormat + "$"); 

			@Override
			public boolean accept(File dir, String filename) {
				boolean res;
				res = filename.startsWith(filePrefix);
				res &= p.matcher(filename).find();
				return res;
			}
		}));
		
		String res = "0001";
		
		if(files.length != 0) {			
			res = files[files.length-1];
			res = res.substring(res.length()-8, res.length()-4);
			res = String.format("%04d", Integer.parseInt(res) + 1);
		}

		return res;
	}
}