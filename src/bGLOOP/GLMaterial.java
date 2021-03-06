package bGLOOP;

/**
 * Materialkonstanten für verschiedene Oberflächeneinfärbungen. <br>
 * Um ein {@link GLObjekt} <code>glo</code> mit dem Material <code>GOLD2</code>
 * zu versehen, nutzt man die Methode {@link GLObjekt#setzeMaterial(GLMaterial) setzeMaterial}
 * und schreibt
 * 
 * <pre>
 * glo.setzeMaterial(GLMaterial.GOLD2)
 * </pre>
 * bzw. nach Import der Materialkonstanten aus der Klasse <code>GLMaterial</code> 
 * <pre>
 * <b>import static</b> bGLOOP.GLMaterial.*;
 * ...
 * glo.setzeMaterial(GOLD2)
 * </pre>
 * @author R. Spillner
 */
public enum GLMaterial {
	
	/** GOLD. 
	 * <img alt="GOLD" src="./doc-files/material-GOLD.jpg">
	 */
	GOLD(new float[][] { { 0.25f, 0.22f, 0.06f, 1.0f }, { 0.35f, 0.31f, 0.09f, 1.0f }, { 0.8f, 0.72f, 0.21f, 1.0f },
			{ 83.6f } }),

	/** GOLD1.
	 * <img alt="GOLD1" src="./doc-files/material-GOLD1.jpg">
	 */
	GOLD1(new float[][] { { 0.24725f, 0.1995f, 0.0745f, 1.0f }, { 0.75164f, 0.60648f, 0.22648f, 1.0f },
							{ 0.628281f, 0.555802f, 0.366065f, 1.0f }, { 51.2f } }),

	/** GOLD2.
	 * <img alt="GOLD2" src="./doc-files/material-GOLD2.jpg">
	 */
	GOLD2(new float[][] { { 0.24725f, 0.2245f, 0.0645f, 1.0f }, { 0.34615f, 0.3143f, 0.0903f, 1.0f },
			{ 0.797357f, 0.723991f, 0.208006f, 1.0f }, { 83.2f } }),

	/** Jade.
	 * <img alt="JADE" src="./doc-files/material-JADE.jpg">
	 */
	JADE(new float[][] { { 0.14f, 0.22f, 0.16f, 1.0f }, { 0.54f, 0.89f, 0.63f, 1.0f }, { 0.32f, 0.32f, 0.32f, 1.0f },
			{ 12.8f } }),

	/** Rubinfarben.
	 * <img alt="RUBIN" src="./doc-files/material-RUBIN.jpg">
	 */
	RUBIN(new float[][] { { 0.17f, 0.01f, 0.01f, 0.5f }, { 0.61f, 0.04f, 0.04f, 0.5f }, { 0.73f, 0.63f, 0.63f, 0.5f },
			{ 76.8f } }),

	/** rotes Glas.
	 * <img alt="ROTGLAS" src="./doc-files/material-ROTGLAS.jpg">
	 */
	ROTGLAS(new float[][] { { 0.1f, 0.0f, 0.0f, 0.5f }, { 1.0f, 0.0f, 0.0f, 0.5f }, { 1.0f, 0.0f, 0.0f, 0.5f },
			{ 50.0f } }),

	/** grünes Glas.
	 * <img alt="GRUENGLAS" src="./doc-files/material-GRUENGLAS.jpg">
	 */
	GRUENGLAS(new float[][] { { 0.0f, 0.1f, 0.0f, 0.5f }, { 0.0f, 1.0f, 0.0f, 0.5f }, { 0.0f, 1.0f, 0.0f, 0.5f },
			{ 50.0f } }),

	/** blaues Glas.
	 * <img alt="BLAUGLAS" src="./doc-files/material-BLAUGLAS.jpg">
	 */
	BLAUGLAS(new float[][] { { 0.0f, 0.0f, 0.1f, 0.5f }, { 0.0f, 0.0f, 1.0f, 0.5f }, { 0.0f, 0.0f, 1.0f, 0.5f },
			{ 50.0f } }),

	/** Glas.
	 * <img alt="GLAS" src="./doc-files/material-GLAS.jpg">
	 */
	GLAS(new float[][] { { 0.05f, 0.05f, 0.05f, 0.0f }, { 0.4f, 0.4f, 0.4f, 0.2f }, { 1.0f, 1.0f, 1.0f, 0.5f },
			{ 128.0f } }),

	/** Messing.
	 * <img alt="MESSING" src="./doc-files/material-MESSING.jpg">
	 */
	MESSING(new float[][] { { 0.329412f, 0.223529f, 0.027451f, 1.0f }, { 0.780392f, 0.568627f, 0.113725f, 1.0f },
			{ 0.992157f, 0.941176f, 0.807843f, 1.0f }, { 27.8974f } }),

	/** poliertes Kupfer.
	 * <img alt="KUPFER_POLIERT" src="./doc-files/material-KUPFER_POLIERT.jpg">
	 */
	KUPFER_POLIERT(new float[][] { { 0.2295f, 0.08825f, 0.0275f, 1.0f }, { 0.5508f, 0.2118f, 0.066f, 1.0f },
			{ 0.580594f, 0.223257f, 0.0695701f, 1.0f }, { 51.2f } }),

	/** Kupfer.
	 * <img alt="KUPFER" src="./doc-files/material-KUPFER.jpg">
	 */
	KUPFER(new float[][] { { 0.19125f, 0.0735f, 0.0225f, 1.0f }, { 0.7038f, 0.27048f, 0.0828f, 1.0f },
			{ 0.256777f, 0.137622f, 0.086014f, 1.0f }, { 12.8f } }),

	/**Bronze.
	 *  <img alt="BRONZE" src="./doc-files/material-BRONZE.jpg">
	 */
	BRONZE(new float[][] { { 0.2125f, 0.1275f, 0.054f, 1.0f }, { 0.714f, 0.4284f, 0.18144f, 1.0f },
			{ 0.393548f, 0.271906f, 0.166721f, 1.0f }, { 25.6f } }),

	/** polierte Bronze. 
	 * <img alt="BRONZE_POLIERT" src="./doc-files/material-BRONZE_POLIERT.jpg">
	 */
	BRONZE_POLIERT(new float[][] { { 0.25f, 0.148f, 0.06475f, 1.0f }, { 0.4f, 0.2368f, 0.1036f, 1.0f },
			{ 0.774597f, 0.458561f, 0.200621f, 1.0f }, { 76.8f } }),

	/** Chrom.
	 * <img alt="CHROM" src="./doc-files/material-CHROM.jpg">
	 */
	CHROM(new float[][] { { 0.25f, 0.25f, 0.25f, 1.0f }, { 0.4f, 0.4f, 0.4f, 1.0f },
			{ 0.774597f, 0.774597f, 0.774597f, 1.0f }, { 76.8f } }),

	/** Zinn.
	 * <img alt="ZINN" src="./doc-files/material-ZINN.jpg">
	 */
	ZINN(new float[][] { { 0.105882f, 0.058824f, 0.013725f, 1.0f }, { 0.427451f, 0.470588f, 0.541176f, 1.0f },
			{ 0.333333f, 0.333333f, 0.521569f, 1.0f }, { 9.84615f } }),

	/** Silber.
	 * <img alt="SILBER" src="./doc-files/material-SILBER.jpg">
	 */
	SILBER(new float[][] { { 0.19225f, 0.19225f, 0.19225f, 1.0f }, { 0.50754f, 0.50754f, 0.50754f, 1.0f },
			{ 0.508273f, 0.508273f, 0.508273f, 1.0f }, { 51.2f } }),

	/** poliertes Silber.
	 * <img alt="SILBER_POLIERT" src="./doc-files/material-SILBER_POLIERT.jpg">
	 */
	SILBER_POLIERT(new float[][] { { 0.23125f, 0.23125f, 0.23125f, 1.0f }, { 0.2775f, 0.2775f, 0.2775f, 1.0f },
			{ 0.773911f, 0.773911f, 0.773911f, 1.0f }, { 89.6f } }),

	/** Smaragd.
	 * <img alt="SMARAGD" src="./doc-files/material-SMARAGD.jpg">
	 */
	SMARAGD(new float[][] { { 0.0215f, 0.1745f, 0.0215f, 0.55f }, { 0.07568f, 0.61424f, 0.07568f, 0.55f },
			{ 0.633f, 0.727811f, 0.633f, 0.55f }, { 76.8f } }),

	/** Obsidian.
	 * <img alt="OBSIDIAN" src="./doc-files/material-OBSIDIAN.jpg">
	 */
	OBSIDIAN(new float[][] { { 0.05375f, 0.05f, 0.06625f, 0.82f }, { 0.18275f, 0.17f, 0.22525f, 0.82f },
			{ 0.332741f, 0.328634f, 0.346435f, 0.82f }, { 38.4f } }),

	/** Perlmutt.
	 * <img alt="PERLMUTT" src="./doc-files/material-PERLMUTT.jpg">
	 */
	PERLMUTT(new float[][] { { 0.25f, 0.20725f, 0.20725f, 0.922f }, { 1.0f, 0.829f, 0.829f, 0.922f },
			{ 0.296648f, 0.296648f, 0.296648f, 0.922f }, { 11.264f } }),

	/** Türkis.
	 * <img alt="TUERKIS" src="./doc-files/material-TUERKIS.jpg">
	 */
	TUERKIS(new float[][] { { 0.1f, 0.18725f, 0.1745f, 0.8f }, { 0.396f, 0.74151f, 0.69102f, 0.8f },
			{ 0.297254f, 0.30829f, 0.306678f, 0.8f }, { 12.8f } }),

	/** Plastik.
	 * <img alt="PLASTIK" src="./doc-files/material-PLASTIK.jpg">
	 */
	PLASTIK(new float[][] { { 0.0f, 0.0f, 0.0f, 1.0f }, { 0.01f, 0.01f, 0.01f, 1.0f }, { 0.5f, 0.5f, 0.5f, 1.0f },
			{ 32.0f } }),

	/** Gummi.
	 * <img alt="GUMMI" src="./doc-files/material-GUMMI.jpg">
	 */
	GUMMI(new float[][] { { 0.02f, 0.02f, 0.02f, 1.0f }, { 0.01f, 0.01f, 0.01f, 1.0f }, { 0.4f, 0.4f, 0.4f, 1.0f },
			{ 10.0f } });

	private float[][] aMat;

	private GLMaterial(float[][] material) {
		aMat = material.clone();
	}

	float[] getAmbient() {
		return aMat[0];
	}

	float[] getDiffuse() {
		return aMat[1];
	}

	float[] getSpecular() {
		return aMat[2];
	}

	float getShininess() {
		return aMat[3][0];
	}
}
