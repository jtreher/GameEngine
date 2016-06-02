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
	public static RawModel loadObjModal( String fileName, Loader loader){
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
					textureArray = new float[vertices.size()*2];
					normalsArray = new float[vertices.size()*3];
					break;
				}
			}
			
			// now we need to know all about our faces
			while( line != null){
				if( !line.startsWith("f ") ){
					line = reader.readLine();
					continue;
				}
				// example // f 30401/92734/182234 73591/92481/182234 92583/90984/182234
				String [] currentLine = line.split(" ");
				// example // 30401/92734/182234
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				// process the current triangle
				processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		verticesArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		for(Vector3f vertex:vertices){
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		for(int i=0; i<indices.size();i++){
			indicesArray[i] = indices.get(i);
		}
		
		return loader.loadToVAO(verticesArray, textureArray, indicesArray);
		
		
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
		// obj files start at 1 rather than 0
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
		textureArray[currentVertexPointer*2] = currentTex.x;
		textureArray[currentVertexPointer*2+1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
		normalsArray[currentVertexPointer*3] = currentNorm.x;
		normalsArray[currentVertexPointer*3+1] = currentNorm.y;
		normalsArray[currentVertexPointer*3+2] = currentNorm.z;
		
	}
}
