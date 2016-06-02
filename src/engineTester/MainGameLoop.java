package engineTester;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
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
		/*
		float[] vertices = { 
				-0.5f, 0.5f, 0f,
				-0.5f, -0.5f, 0f,
				0.5f, -0.5f, 0f,
				0.5f, 0.5f, 0f
		};

		int[] indices = { 0, 1, 3, 1, 2, 3};
		
		float[] textureCoords = {
				0,0,
				0,1,
				1,1,
				1,0
		};
		*/
		
		float[] vertices = {			
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,0.5f,-0.5f,		
				
				-0.5f,0.5f,0.5f,	
				-0.5f,-0.5f,0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				0.5f,0.5f,-0.5f,	
				0.5f,-0.5f,-0.5f,	
				0.5f,-0.5f,0.5f,	
				0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,-0.5f,	
				-0.5f,-0.5f,-0.5f,	
				-0.5f,-0.5f,0.5f,	
				-0.5f,0.5f,0.5f,
				
				-0.5f,0.5f,0.5f,
				-0.5f,0.5f,-0.5f,
				0.5f,0.5f,-0.5f,
				0.5f,0.5f,0.5f,
				
				-0.5f,-0.5f,0.5f,
				-0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,-0.5f,
				0.5f,-0.5f,0.5f
				
		};
		
		float[] textureCoords = {
				
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,			
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0,
				0,0,
				0,1,
				1,1,
				1,0

				
		};
		
		int[] indices = {
				0,1,3,	
				3,1,2,	
				4,5,7,
				7,5,6,
				8,9,11,
				11,9,10,
				12,13,15,
				15,13,14,	
				16,17,19,
				19,17,18,
				20,21,23,
				23,21,22

		};
		
		RawModel model = loader.loadToVAO( vertices, textureCoords, indices );
		
		ModelTexture texture = new ModelTexture(loader.loadTexture("optical"));
		
		TexturedModel texturedModel = new TexturedModel(model, texture);
		
		Entity entity = new Entity(texturedModel, new Vector3f( 0, 0, -5 ), 0, 0, 0, 1 );
		
		Camera camera = new Camera();
		
		while ( ! Display.isCloseRequested() ) {
			// game logic
			//entity.increasePosition(0, 0.0f, -0.002f);
			//entity.increaseRotation(0, 0, 0);
			entity.increaseRotation(1, 1, 0);
			camera.move();
			renderer.prepare();
			shader.start();
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
