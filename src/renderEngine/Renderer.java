package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer {

	public void prepare(){
		GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
		GL11.glClearColor(1,  0,  0,  1);
		
	}
	
	public void render( RawModel model){
		// have to bind to start working with something
		GL30.glBindVertexArray(model.getVaoID());
		// tell it which slow we are using in the VAO (of the 16)
		GL20.glEnableVertexAttribArray(0);
		// this way is slightly different than draw arrays since we are no longer just drawing
		// as a raw dump of every single vertex, we are using a bufferindex
		GL11.glDrawElements(GL11.GL_TRIANGLES,  model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		// we are using triangles x,y,z with first starting at 0 and using our 
		// handle calculation to return vertices / 3 for the count
		//GL11.glDrawArrays(GL11.GL_TRIANGLES,  0,  model.getVertexCount());
		// unload it
		GL20.glDisableVertexAttribArray(0);
		// unbind it
		GL30.glBindVertexArray(0);
	}
}
