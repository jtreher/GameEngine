package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Maths {
	// you can take a transformation and create a matrix here
	// since the maths on the transformation matrix are much easier
	// than attempting to use translation, rotation, and scale individually
	public static Matrix4f createTransformationMatrix( Vector3f translation, float rx, float ry, float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		//position
		Matrix4f.translate(translation,  matrix,  matrix);
		// rotation
		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		// scale
		// interestly enough, you can scale just on one axis.
		Matrix4f.scale( new Vector3f(scale,scale,scale),  matrix, matrix);
		
		return matrix;
		
	}
}
