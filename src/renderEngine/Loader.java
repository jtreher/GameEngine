package renderEngine;

import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import models.RawModel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;


public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public RawModel loadToVAO( float[] positions, float[] textureCoords, int[] indices){
		// this is our unique id for the current VAO, which will remain around until destroyed
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2, textureCoords);
		unbindVAO();
		return new RawModel(vaoID, indices.length );
	}
	
	public int loadTexture(String fileName){
		Texture texture = null;
		
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
		
	}
	
	public void cleanUp(){
		for( int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		
		for( int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		
		for( int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}
	
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	// setup an attribute list and populate it with our array
	// of vertices. To work with anything in GL, you need to find it first
	// binding with an id of 0 causes everything to be unbound
	// notice we first create an empty VBO, then bind it to start workign with it
	// then convert our float array to a floatbuffer then setup the buffer data
	// with our FloatBuffer, 
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		// usage (3rd param) static argument means we will not be editing the data
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// 1st arg is 0, simply for convenience since we have 16 available
		// and are just going to put vertices in the first slow of the VAO
		// 3 is the length of each vertex since we are using (x,y,z)
		// Float is the data type of each vertex
		// normalized? I know of this term from Blender, but I'll need to look up what normalized vertices means.
		// 		Vertex normals and face normals have to do with calculating an average
		//			The average can be calculated for every polygon sharing a vertex or it can just use the normal
		//			calculated for the polygon
		
		//			Important: If light is able to reflect off of a vertex using each face normal
		//			Then you will be able to see the hard polygons. Rather, smooth shading using 
		//			just an average of the polygons, you will not see the hard edges since there is just
		//			one particular angle based on a single vector coming off the vertex.
		//		of the vertex which is use to essentially draw a vector perpendicular to the face
		//		will be used for the calculations in how the surface acts with light.
		
		// first zero is the distance between vertices, which in our case is nothing
		// I'm not sure why you'd want stuff in between your vertices elements at this point
		// Finally, we are starting at zero. I'm not sure why you would have padding
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0	);
	}
	
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer( int[] indices ){
		int vboID = GL15.glGenBuffers();
		vbos.add( vboID );
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// why isn't this being unbound???
		/*
		 * According to the OpenGL wiki, unlike a GL_ARRAY_BUFFER, when you call
		 * glBindBuffer on a GL_ELEMENT_ARRAY_BUFFER, you are actually binding
		 * that element array to the VAO, whereas when you call glBindBuffer on
		 * a GL_ARRAY_BUFFER, you are only telling OpenGL to use that buffer
		 * when calling glVertexAttribPointer(). The way I like to think about
		 * it is that the position of a vertex is an actual attribute of the
		 * vertex (these attributes are actually specified inside the shaders),
		 * whereas the Indices are telling the VAO how to draw those vertices.
		 * The indices are not themselves an aspect of the vertices, and
		 * therefore are not added to the attribute list.
		 * 
		 * 
		 * also
		 * 
		 * There is one special slot in a VAO specifically for an index buffer.
		 * When you bind a VBO as a GL_ELEMENT_ARRAY_BUFFER it tells OpenGL that
		 * it's an index buffer and automatically binds it to the special index
		 * buffer slot in the VAO. The call to glDrawElements() renders the
		 * currently bound VAO using the indices in the index buffer associated
		 * with that VAO. If no index buffer is bound to the currently bound VAO
		 * when you call glDrawElements() then OpenGL throws an error.
		 */
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data ){
		IntBuffer buffer = BufferUtils.createIntBuffer( data.length );
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		// means we finished writing to it
		buffer.flip();
		return buffer;
	}
}
