package bGLOOP;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.jogamp.common.nio.Buffers;
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
import bGLOOP.windowimpl.Window;

class GLRenderer implements GLEventListener {
	private class TransparencyComparator implements Comparator<DisplayItem> {
		@Override
		public int compare(DisplayItem o1, DisplayItem o2) {
			if(o1.isTransparent() == o2.isTransparent())
				return 0;
			else if(o1.isTransparent() && !o2.isTransparent())
				return 1;
			else
				return -1;
		}
	}

    private Logger log = Logger.getLogger("bGLOOP");

	private int window_rendering_needed = 2;
	private long animatorLastFPSTime = 0;
	private Animator animator;
	private GLKamera aCam;
	private WindowConfig wconf;
	private ConcurrentHashMap<GLTextureImpl, CopyOnWriteArrayList<DisplayItem>>
		renderItemMap;
	private ConcurrentHashMap<Integer, GLObjekt> objectNameMap;

	private TransparencyComparator tc = new TransparencyComparator();

	private Window win;
	private boolean currentLighting;

	private boolean makeScreenshot = false;
	boolean selectionRun = false;
	private String screenshotFilename = null;

	// TODO this does not belong here!!!!!!! This is just a hack
	// to be removed when I know better --- and when I have to subclass
	// ConcurrentHashMap for that
	private GLHimmel sky;

	private int selX = -1, selY = -1;

	GLRenderer(WindowConfig wc, int width, int height, GLKamera cam, boolean pFullscreen, boolean pNoDecoration) {
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
		animator.setUpdateFPSFrames(wconf.doubleBuffering ? 60:2000, null);
		win.startDisplay();
		currentLighting = !wconf.globalLighting;
		renderItemMap = new ConcurrentHashMap<GLTextureImpl, CopyOnWriteArrayList<DisplayItem>>(10);
		objectNameMap = new ConcurrentHashMap<Integer, GLObjekt>(100);
	}

	// remove oldImpl from map
	void updateObjectRenderMap(GLTextur tex, DisplayItem di, GLTextur oldTex) {
		if (di instanceof GLHimmel)  // treat sky separately
			sky = (GLHimmel)di;
		else {
			CopyOnWriteArrayList<DisplayItem> dil;
			dil = renderItemMap.get(oldTex.aTexturImpl);
			if (dil != null)
				dil.remove(di);

			addObjectToRenderMap(tex, di);
		}
	}

	void removeObjectFromRenderMap(GLTextur tex, DisplayItem di) {
		renderItemMap.get(tex.aTexturImpl).remove(di);

		if(di instanceof GLObjekt) {
			GLObjekt g = (GLObjekt)di;
			objectNameMap.remove(g.selectionID);
		}
	}

	void addObjectToRenderMap(GLTextur tex, DisplayItem di) {
		if (di instanceof GLHimmel)
			sky = (GLHimmel) di;
		else {
			CopyOnWriteArrayList<DisplayItem> dil;
			if (renderItemMap.containsKey(tex.aTexturImpl))
				(dil = renderItemMap.get(tex.aTexturImpl)).add(di);
			else {
				(dil = new CopyOnWriteArrayList<DisplayItem>()).add(di);
				renderItemMap.put(tex.aTexturImpl, dil);
			}
			dil.sort(tc);
		}
		if(di instanceof GLObjekt) {
			GLObjekt g = (GLObjekt)di;
			objectNameMap.put(g.selectionID, g);
		}
	}

	Window getWindow() {
		return win;
	}

	@Override
	public void init(final GLAutoDrawable drawable) {
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

		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_DONT_CARE);

		gl.glLineWidth(wconf.wireframeLineWidth);

		window_rendering_needed = wconf.doubleBuffering?3:1;
		gl.glEnable(GL2.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL2.GL_GREATER, 0.1F);

		// gl.glEnable(GL2.GL_RESCALE_NORMAL);
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
	public void dispose(final GLAutoDrawable drawable) {
	}

	@Override
	public void display(final GLAutoDrawable drawable) {
		if(selectionRun)
			doSelectionRun(drawable);

		if (window_rendering_needed > 0 ) {
			log.fine("render scene , run " + window_rendering_needed--);
			renderScene(drawable.getGL().getGL2());
		}

		updateFPSView();

		if(makeScreenshot) {
			takeScreenshot(drawable.getGL());
			makeScreenshot = false;
		}
	}

	private void doSelectionRun(final GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = GLU.createGLU();
		IntBuffer buffer = Buffers.newDirectIntBuffer(512);
		int hits, n, first = -1;
		float z, zmin = Float.POSITIVE_INFINITY;

		int[] viewport = new int[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glSelectBuffer(512, buffer);
		gl.glRenderMode(GL2.GL_SELECT);
		gl.glInitNames();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		glu.gluPickMatrix(selX, viewport[3] - selY, 1.0, 1.0, viewport, 0);
		glu.gluPerspective(60.0, (float)viewport[2] / viewport[3], 1, 100000.0);

		// draw graph
		renderScene(drawable.getGL().getGL2());

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glFlush();

		hits = gl.glRenderMode(GL2.GL_RENDER);
		// log.info("Selection run, got " + hits + " hits!");

		for(int i = 0; i < hits; ++i) {
			n = buffer.get();
			z = (float) (buffer.get() & 0xffffffffL) / 0x7ffffff;
			buffer.get();

			// discard unnecessary stuff
			//buffer.position(buffer.position() + n);
			for(int j=0; j<n-1; ++j)
				buffer.get();

			n = buffer.get();
			if(z < zmin) {
				zmin = z;
				first = n;
			}
			log.info("Buffer: " + n + ", min distance: " + z);
		}
		synchronized (aCam) {
			aCam.selectedObj = objectNameMap.get(first);
			selectionRun = false;
			aCam.notify();
		}
	}

	@Override
	public void reshape(final GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		float aspect = (float) width / height;
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// fovy, aspect, zNear, zFar
		GLU.createGLU().gluPerspective(60, aspect, 1, 100000.0);
		log.fine("Reshaping drawing window");

		window_rendering_needed = wconf.doubleBuffering?3:1;
	}

	void scheduleRender() {
		window_rendering_needed = wconf.doubleBuffering?3:1;
	}

	void scheduleScreenshot(String filename) {
		screenshotFilename = filename;
		makeScreenshot = true;
	}

	void scheduleSelection(int pFensterX, int pFensterY) {
		selX = pFensterX; selY = pFensterY;
		synchronized (aCam) {
			selectionRun = true;
		}
	}

	private void renderScene(GL2 gl) {
		Map<GLTextureImpl, ListIterator<DisplayItem>> transparencyIteratorMap = 
				new ConcurrentHashMap<GLTextur.GLTextureImpl, ListIterator<DisplayItem>>(
								renderItemMap.size());
		ListIterator<DisplayItem> lit;
		DisplayItem di = null;

		GLU glu = GLU.createGLU();
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // reset
		doLighting(gl);

		synchronized (aCam) {
			renderPreObjects(gl, glu);
		}

		GLTextureImpl tImp = null;
		// TODO this is why I needed a GLHimmel reference:
		// render sky before all other objects
		if (sky != null) {
			GLTextur skyTexture = sky.gibTextur();
			if (skyTexture != null && skyTexture.aTexturImpl != null) {
				tImp = skyTexture.aTexturImpl;
				tImp.load(gl);
				if (tImp.isReady()) {
					tImp.getTexture().enable(gl);
					tImp.getTexture().bind(gl);
					log.fine("enabling texture " + tImp.aTexFile);
				}
			}
			if (sky.aVisible) {
				log.fine("render sky");
				sky.render(gl, glu);
			}
		}

		// render intransparent objects
		for (ConcurrentHashMap.Entry<GLTextureImpl, CopyOnWriteArrayList<DisplayItem>> entry : renderItemMap
				.entrySet()) {
			// load texture
			tImp = entry.getKey();
			tImp.load(gl);
			if (tImp.isReady()) {
				tImp.getTexture().enable(gl);
				tImp.getTexture().bind(gl);
				log.fine("enabling texture " + tImp.aTexFile);
			} else
				gl.glDisable(GL2.GL_TEXTURE_2D);

			// render intransparent objects
			if ((lit = entry.getValue().listIterator()).hasNext()) {
				while (lit.hasNext() && !(di = lit.next()).isTransparent())
					if(di.aVisible)
						di.render(gl, glu);
				if (di.isTransparent()) {
					lit.previous();
					transparencyIteratorMap.put(tImp, lit);
				}
			}
		}

		for(Map.Entry<GLTextureImpl,ListIterator<DisplayItem>> entry : transparencyIteratorMap.entrySet()) {
			tImp = entry.getKey();
			tImp.load(gl);
			if (tImp.isReady()) {
				tImp.getTexture().enable(gl);
				tImp.getTexture().bind(gl);
				log.fine("enabling texture " + tImp.aTexFile);
			} else
				gl.glDisable(GL2.GL_TEXTURE_2D);

			// render transparent objects
			lit = entry.getValue();
			while(lit.hasNext())
				if((di = lit.next()).aVisible)
					di.render(gl, glu);
		}
		gl.glDisable(GL2.GL_TEXTURE_2D);

		synchronized (aCam) {
			renderPostObjects(gl, glu);
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

	/* get the next free consecutive number to name the next screenshot
	 */
	private static String getNextScreenshotNumber(final String fileFormat, final String filePrefix) {
		/* Here, with Java 8, one could use a lambda expression. Something like

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

	private void renderPreObjects(GL2 gl, GLU glu) {
		// camera position and look-at point
		glu.gluLookAt(aCam.aPos[0], aCam.aPos[1], aCam.aPos[2], aCam.aLookAt[0], aCam.aLookAt[1], aCam.aLookAt[2],
				aCam.aUp[0], aCam.aUp[1], aCam.aUp[2]);
	}

	private void renderPostObjects(GL2 gl, GLU glu) {
		if (wconf.aDisplayAxes)
			drawAxes(gl);
		if (wconf.aDrawLookAt)
			drawLookAt(gl);
		/*
		if(wconf.aDrawRotAxis)
			drawRotAxis(gl);
		*/
	}

/*
	private void drawRotAxis(GL2 gl) {
		float[][] dirVec = new float[2][3];
		float[] currPos = new float[3];		
		double axesLength = wconf.axesLength;

		VectorUtil.subVec3(currPos, aCam.aPos, aCam.aLookAt);
		VectorUtil.crossVec3(dirVec[0], aCam.aUp, currPos);
		VectorUtil.normalizeVec3(dirVec[0]);
		VectorUtil.scaleVec3(dirVec[0], dirVec[0], 10);
		dirVec[1] = aCam.aUp.clone();
		VectorUtil.normalizeVec3(dirVec[1]);
		VectorUtil.scaleVec3(dirVec[1], dirVec[1], 10);

		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glLineWidth(1);
		for (int k = 0; k < 2; ++k) {
			currPos = aCam.aLookAt.clone();
			gl.glBegin(GL2.GL_LINE_STRIP);
			for (int i = 0; i < axesLength; i += 10) {
				gl.glColor3f((i / 10) % 2, (i / 10) % 2, (i / 10) % 2);
				gl.glVertex3fv(currPos, 0);
				VectorUtil.addVec3(currPos, currPos, dirVec[k]);
				gl.glVertex3fv(currPos, 0);
			}
			gl.glEnd();
		}

		if (wconf.globalLighting)
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glLineWidth(wconf.wireframeLineWidth);
	}
*/

	private void drawAxes(GL2 gl) {
		double axesLength = Math.ceil(wconf.axesLength/20)*20;
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glLineWidth(wconf.axesWidth);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < axesLength; i += 20) {
			gl.glColor3f(1,(i/20)%2,(i/20)%2);
			gl.glVertex3f(i, 0, 0);
			gl.glVertex3f(i + 20, 0, 0);
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f((float) axesLength, 0, 0);
		gl.glVertex3f((float) axesLength - 20, -10, 0);
		gl.glVertex3f((float) axesLength, 0, 0);
		gl.glVertex3f((float) axesLength - 20, 10, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < axesLength; i += 20) {
			gl.glColor3f((i/20)%2, 1, (i/20)%2);
			gl.glVertex3f(0, i, 0);
			gl.glVertex3f(0, i + 20, 0);
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f(0, (float) axesLength, 0);
		gl.glVertex3f(10, (float) axesLength - 20, 0);
		gl.glVertex3f(0, (float) axesLength, 0);
		gl.glVertex3f(-10, (float) axesLength - 20, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < axesLength; i += 20) {
			gl.glColor3f((i/20)%2, (i/20)%2, 1);
			gl.glVertex3f(0, 0, i);
			gl.glVertex3f(0, 0, i + 20);
		}
		gl.glColor3f(1, 1, 1);
		gl.glVertex3f(0, 0, (float) axesLength);
		gl.glVertex3f(0, 10, (float) axesLength - 20);
		gl.glVertex3f(0, 0, (float) axesLength);
		gl.glVertex3f(0, -10, (float) axesLength - 20);
		gl.glEnd();

		if (wconf.globalLighting)
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glLineWidth(wconf.wireframeLineWidth);
	}

	private void drawLookAt(GL2 gl) {
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(1, 1, 1);
		gl.glVertex3d(aCam.aLookAt[0]-5, aCam.aLookAt[1], aCam.aLookAt[2]);
		gl.glVertex3d(aCam.aLookAt[0]+5, aCam.aLookAt[1], aCam.aLookAt[2]);
		gl.glVertex3d(aCam.aLookAt[0], aCam.aLookAt[1]-5, aCam.aLookAt[2]);
		gl.glVertex3d(aCam.aLookAt[0], aCam.aLookAt[1]+5, aCam.aLookAt[2]);
		gl.glVertex3d(aCam.aLookAt[0], aCam.aLookAt[1], aCam.aLookAt[2]-5);
		gl.glVertex3d(aCam.aLookAt[0], aCam.aLookAt[1], aCam.aLookAt[2]+5);
		gl.glEnd();
		
		if (wconf.globalLighting)
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glLineWidth(wconf.wireframeLineWidth);
	}

}