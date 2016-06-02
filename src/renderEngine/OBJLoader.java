package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

public class OBJLoader {
	public static RawModel loadObjModel( String fileName, Loader loader){
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(new File("res/" + fileName + ".obj" ));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Couldn't load file");
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader( fileReader );
		String line;
		// these lists are just used in this method temporarily storing the data
		// we parse out of the OBJ. We'll eventually need to load it into float arrays 
		// 
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		
		try{
			// read every line in the file and see if we have vertices, texture coord, normal, or faces and populate our 
			// vectors
			while(true){
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				
				// example //v 0.34 0.1 0.3
				// splits to ["v","0.34","0.1","0.3"]
				if( line.startsWith("v ")){
					Vector3f vertex = new Vector3f( Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat( currentLine[3]));
					vertices.add(vertex);
				}
				// looks like vt 0.8423 0.7981
				else if(line.startsWith("vt ")){
					Vector2f texture = new Vector2f( Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]) );
					textures.add(texture);
				}
				else if(line.startsWith("vn ")){
					Vector3f normal = new Vector3f( Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat( currentLine[3]));
					normals.add(normal);
				}
				// example // f 30401/92734/182234 73591/92481/182234 92583/90984/182234
				else if(line.startsWith("f " )){
					// uv is just 2 dimensional, since, it's ugh, just a 2d image
					textureArray = new float[vertices.size()*2];
					normalsArray = new float[vertices.size()*3];
					break;
				}
			}
			
			// now we need to know all about our faces which we can use to rearrange our data in the 
			// lists
			while( line != null){
				if( !line.startsWith("f ") ){
					line = reader.readLine();
					continue;
				}
				// example // f 30401/92734/182234 73591/92481/182234 92583/90984/182234
				// this has all the information for a triangle. The first 30401/92734/182234
				// means that the vertex is vertex 30401, the texture is 92734, and the normal is 182234
				// then the same applies for the other two vertices that make up this triangle
				String [] currentLine = line.split(" ");
				// currentLine = ["f","30401/92734/182234","73591/92481/182234","92583/90984/182234"]
				
				// vertex1 = ["30401","92734","182234"] // vertex, texture, normal
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				// process the current triangle essentially we are rearranging the OBJ positions so that
				// the vectors, textures, and normals all line up so that our individual data points are not randomly ordered
				processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		// during processVertex we populated our normals and textures
		// now we have to wrap up the vertices and indices
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		// we just go through and spit these out as they are ordered
		// it almost seems like you could just do this in the while loop and then instead of doing
		// vertices.size(), you could do verticesArray.length/3
		int vertexPointer = 0;
		for(Vector3f vertex:vertices){
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		// now we just add our indices that will list every vertex needed to make up all the triangles
		// this is another one where I'm not quite sure why we just don't do this in processVertex instead of dealing with the list
		// unless it is purely for the simplicity of managing the size
		for(int i=0; i<indices.size();i++){
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVAO(verticesArray, textureArray, indicesArray, normalsArray);
		
		
	}
	
	// an object file has no notion of ordering vertices in any way that would be useful, so we get the faces
	// exported that will allow us to line up which vertex belongs to which triangles so we do not have to duplicate vertices
	// and can use our indexBuffer. Remember the index buffer is an array of ints that points to position in our vertex array
	// and will reuse positions for ever vertex that appears on more than one face
	//
	
	// we have to translate this: // f 30401/92734/182234 73591/92481/182234 92583/90984/182234
	// to our standard long list of indexes
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
			List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
		// obj files start at 1 rather than 0, so f 30401 is the 30401 vertex starting from 1, but we've done stored
		// our data in Lists that start at 0.
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		// this is the only procedure populating indices list
		indices.add(currentVertexPointer);

		// the texture and normals arrays were initialized to the appropriate size in the first while loop
		// now it is time to set the data.
		
		// get the Vector out of our textures list at the  92734-1 position of this vertex : f 30401/92734/182234 
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
		// we use *2 here because we always need to store two data points
		// for instance, if we had just 3 vertexes total, the 1st vertex would occupy textureArray[0] & [1]
		// the 2nd vertex would occupy textureArray[2] & [3] and so forth
		// ultimately we end up with a completely populated array with two values for every vertex
		textureArray[currentVertexPointer*2] = currentTex.x;
		textureArray[currentVertexPointer*2+1] = 1 - currentTex.y; // 1- because openGL starts from the top left, while 3d modeling starts from bottom left
		
		// do the same as above, but we have three data points to put in our array per vertex
		// just like we manually would write these out manually. It's written in groups of three, but it is still one 
		// long buffer that is simply read in and chunked off into vectors
		// float[] normals = [
		//	0.1,0.75,1
		//	, 0.5,0.23,1.5
		//	
		//
		// ]
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
		normalsArray[currentVertexPointer*3] = currentNorm.x;
		normalsArray[currentVertexPointer*3+1] = currentNorm.y;
		normalsArray[currentVertexPointer*3+2] = currentNorm.z;
		
	}
}
