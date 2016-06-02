package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import models.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();

		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer( shader );
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

		
		RawModel model = OBJLoader.loadObjModel("shoe", loader);
		
		ModelTexture texture = new ModelTexture(loader.loadTexture("pinkshoe-uv"));
		
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		Entity entity = new Entity(texturedModel, new Vector3f( 0,-2, -10 ), 0, 0, 0, 1 );
		
		Light light = new Light( new Vector3f(0,10,-10 ), new Vector3f(1,1,1));
		Camera camera = new Camera();
		
		while ( ! Display.isCloseRequested() ) {
			// game logic
			//entity.increasePosition(0, 0.0f, -0.002f);
			//entity.increaseRotation(0, 0, 0);
			entity.increaseRotation(0.1f, 0.1f, 0);
			camera.move();
			renderer.prepare();
			shader.start();
			shader.loadLight(light);
			shader.loadViewMatrix(camera);
			renderer.render(entity,shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}
		
		shader.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();
	}
}
