package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();

		Loader loader = new Loader();
		Renderer renderer = new Renderer();
		StaticShader shader = new StaticShader();
		// first we just manually add some vertices, but you'd normally not want to do this...
		// First, we are dealing with triangles, we pick any vertex to start with but we must 
		// work our way clock wise
		//
		// 	{
		//		v1 : { x : -0.5f, y : 0.5f, z : 0f }
		//		, v2 : { x : -0.5f, y : -0.5f, z: 0f }
		//		, v3 : { x : 0.5f, y : 0.5f, z : 0 }
		//		, v4 : { x : -0.5f, y : -0.5f, z : 0 }
		//		, v5 : { x : 0.5f, y : -0.5f, z : 0 }
		//		, v6 : { x : 0.5f, y : 0.5f, z : 0 }
		//	}
		// 
		// The above is very efficient as there are two identical vertices ( v2 and v4 and v3 and v6 )

		// To make this more efficient, we can send along another buffer called an index buffer which
		// will tell openGL how to connect those dots so we can eliminate v4 and v6
		// This ends up saving a ton whenever you start adding normals and texture coordinates to each vertex
		// but not any savings in the most basic 2d example with 2 triangles
		float[] vertices = { 
				-0.5f, 0.5f, 0f,
				-0.5f, -0.5f, 0f,
				0.5f, -0.5f, 0f,
				0.5f, 0.5f, 0f
		};

		int[] indices = { 0, 1, 3, 1, 2, 3};
		
		RawModel model = loader.loadToVAO( vertices, indices );

		while (!Display.isCloseRequested()) {
			// game logic
			renderer.prepare();
			shader.start();
			renderer.render(model);
			shader.stop();
			DisplayManager.updateDisplay();
		}
		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
