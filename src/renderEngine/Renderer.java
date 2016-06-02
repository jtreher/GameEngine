package renderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import toolbox.Maths;

public class Renderer {

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	
	public Renderer(StaticShader shader) {
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix( projectionMatrix );
		shader.stop();
	}

	public void prepare(){
		// tell the engine to ensure a triangle should be visible based on which triangle is in front of which.
		// and render in the correct order.
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear( GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT );
		GL11.glClearColor(1,  0,  0,  1);
		
	}
	
	public void render( Entity entity, StaticShader shader ){
		TexturedModel texturedModel = entity.getModel();
		RawModel model = texturedModel.getRawModel();
		
		// have to bind to start working with something
		GL30.glBindVertexArray(model.getVaoID());
		// tell it which slow we are using in the VAO (of the 16)
		GL20.glEnableVertexAttribArray(0);
		// we are using one for our texture coords
		GL20.glEnableVertexAttribArray(1);

		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotationX(), entity.getRotationY(), entity.getRotationZ(), entity.getScale() );
		
		shader.loadTransformationMatrix(transformationMatrix);
		
		// texture banks need to be loaded with a texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  texturedModel.getTexture().getID() );
		// this way is slightly different than draw arrays since we are no longer just drawing
		// as a raw dump of every single vertex, we are using a bufferindex
		GL11.glDrawElements(GL11.GL_TRIANGLES,  model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		// we are using triangles x,y,z with first starting at 0 and using our 
		// handle calculation to return vertices / 3 for the count
		//GL11.glDrawArrays(GL11.GL_TRIANGLES,  0,  model.getVertexCount());
		// unload it
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		// unbind it
		GL30.glBindVertexArray(0);
	}
	
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (( 1f / Math.tan(Math.toRadians(FOV / 2f ))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = - ( (FAR_PLANE + NEAR_PLANE) / frustrum_length );
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -( (2*FAR_PLANE*NEAR_PLANE) / frustrum_length );
		projectionMatrix.m33 = 0;
		
	}
}
