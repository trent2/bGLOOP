package bGLOOP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import bGLOOP.GLObjekt.Darstellungsmodus;
import bGLOOP.GLObjekt.Rendermodus;

/**
 * Configuration object specific to a certain bGLOOP window (i.e.
 * {@link GLKamera} object). All {@link GLObjekt} objects share the
 * GLWindowConfig object of the camera which was active camera during the
 * object's generation.
 * 
 * @author Robert Spillner
 */
class GLWindowConfig {
	/**
	 * Configuration object for initialization of the camera when no tampering
	 * with the camera's GLWindowConfig object has been done before
	 */
	final static GLWindowConfig defaultWindowConfig = new GLWindowConfig();

	private final static String DEFAULT_PROPERTIES_FILE_NAME = ".bgloop";
	private Properties bgloopSetting;
	/**
	 * global draw Mode
	 */

	// package default global constants
	int globalDefaultWidth;
	int globalDefaultHeight;
	int xDivision, yDivision;
	double axesLength;
	double mouseWheelScale;
	double meshMaxScale;
	float axesWidth;
	float wireframeLineWidth;
	Darstellungsmodus globalDrawMode = Darstellungsmodus.FUELLEN;
	int globalShadeModel = 0x1D01; // GL_SMOOTH
	Rendermodus globalObjectRenderMode = Rendermodus.RENDER_GLU;
	boolean globalLighting = true, aDisplayAxes = false, aWireframe = false,
			doubleBuffering = true;

	long clickTimeRange, moveTimeRange;

	GLWindowConfig() {
		InputStream defaultSettings;
		File userFileName;
		bgloopSetting = new Properties();

		try {
			// load bGLOOPs default config file
			defaultSettings = getClass().getResourceAsStream("/" + DEFAULT_PROPERTIES_FILE_NAME);
			if (defaultSettings != null)
				bgloopSetting.load(defaultSettings);

			// load user config file if it exists
			userFileName = new File(DEFAULT_PROPERTIES_FILE_NAME);
			if (userFileName.canRead())
				bgloopSetting.load(new FileInputStream(userFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initializeGlobalVariables();
	}

	private void initializeGlobalVariables() {
		String t;

		xDivision = Integer.parseInt(bgloopSetting.getProperty("DEFAULT_QUALITY_X"));
		yDivision = Integer.parseInt(bgloopSetting.getProperty("DEFAULT_QUALITY_X"));
		axesLength = Double.parseDouble(bgloopSetting.getProperty("DEFAULT_AXES_LENGTH"));
		clickTimeRange = Long.parseLong(bgloopSetting.getProperty("MOUSE_CLICK_TIME_RANGE"));
		moveTimeRange = Long.parseLong(bgloopSetting.getProperty("MOUSE_MOVE_TIME_RANGE"));
		mouseWheelScale = Double.parseDouble(bgloopSetting.getProperty("DEFAULT_MOUSE_WHEEL_SCALE"));
		meshMaxScale = Double.parseDouble(bgloopSetting.getProperty("DEFAULT_MESH_MAX_SCALE"));

		axesWidth = Float.parseFloat(bgloopSetting.getProperty("DEFAULT_AXES_WIDTH"));
		wireframeLineWidth = Float.parseFloat(bgloopSetting.getProperty("DEFAULT_LINE_WIDTH"));

		globalDefaultWidth = Integer.parseInt(bgloopSetting.getProperty("DEFAULT_WINDOW_WIDTH"));

		globalDefaultHeight = Integer.parseInt(bgloopSetting.getProperty("DEFAULT_WINDOW_HEIGHT"));

		globalShadeModel = Integer.parseInt(bgloopSetting.getProperty("DEFAULT_SHADE_MODEL"), 16);

		t = bgloopSetting.getProperty("DEFAULT_OBJECT_RENDER_MODE");

		if ("GLU".equals(t))
			globalObjectRenderMode = Rendermodus.RENDER_GLU;
		else if ("GL".equals(t))
			globalObjectRenderMode = Rendermodus.RENDER_GL;
		else if ("VBO".equals(t))
			globalObjectRenderMode = Rendermodus.RENDER_VBOGL;

		t = bgloopSetting.getProperty("DEFAULT_RENDER_DRAW_MODE");
		if ("POINT".equals(t))
			globalDrawMode = Darstellungsmodus.PUNKT;
		else if ("LINE".equals(t))
			globalDrawMode = Darstellungsmodus.LINIE;
		else if ("FILL".equals(t))
			globalDrawMode = Darstellungsmodus.FUELLEN;

		t = bgloopSetting.getProperty("LIGHTING");
		if ("on".equals(t))
			globalLighting = true;
		else if ("off".equals(t))
			globalLighting = false;
		t = bgloopSetting.getProperty("DOUBLE_BUFFERING");
		if ("on".equals(t))
			doubleBuffering = true;
		else
			doubleBuffering = false;
	}

	boolean isAWT() {
		return "AWT".equals(bgloopSetting.getProperty("DEFAULT_WINDOW_MODE"));
	}

	boolean isNEWT() {
		return "NEWT".equals(bgloopSetting.getProperty("DEFAULT_WINDOW_MODE"));
	}

}
