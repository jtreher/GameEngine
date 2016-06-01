package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
		/*
		 * What is a Vertex Array Object (VAO)
		 * 
		 * Contains attribute lists 16 max
		 * 
		 * Such as
		 * 
		 * Vertex positions
		 * vertex colours
		 * normal vectors
		 * texture coordinates
		 * 
		 * Stored as vertex buffer object in each attribute
		 * 
		 * Each VAO has a unique id, we access via it's unique id
		 * 
		 * A VBO is just an array of data
		 * 
		 * Each triangle has three vertices
		 * Each vertex has a 3D coordinate
		 * 
		 * For instance, for vertex positions, you have a giant buffer
		 * 
		 * x1, y1, z1, x2, y2, z2, x3, y3, z3 (now you have one triangle...)
		 * 
		 * x4, y4, z4 (now you have a Quadrilateral made of two triangles)
		 * 
		 * x5, y5, z5 (now you have a polygon made of 3 triangles, etc)
		 * 
		 * 
		 * Put all the points together into the VBO in a VAO
		 * 
		 * A rectangle is just two triangles
		 * 
		 * 
		 * 
		 */
		private static final int WIDTH = 1280;
		private static final int HEIGHT = 720;
		private static final int FPS_CAP = 120;
		
		public static void createDisplay(){
			
				ContextAttribs attribs = new ContextAttribs(3,2)
						.withForwardCompatible(true)
						.withProfileCore(true);
				
				try {
					Display.setDisplayMode( new DisplayMode( WIDTH, HEIGHT ) );
					Display.create( new PixelFormat(), attribs);
					Display.setTitle("Our First Display");
				} catch (LWJGLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				GL11.glViewport(0,  0,  WIDTH, HEIGHT);
		}
		
		public static void updateDisplay(){
			Display.sync(FPS_CAP);
			Display.update();
		}
		
		public static void closeDisplay(){
			Display.destroy();
		}
}
