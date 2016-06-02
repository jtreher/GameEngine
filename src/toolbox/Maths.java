package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

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

	public static Matrix4f createViewMatrix( Camera camera ){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		//rotate x y and z
		Matrix4f.rotate( (float)Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), matrix, matrix );
		Matrix4f.rotate( (float)Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), matrix, matrix );
		Matrix4f.rotate( (float)Math.toRadians(camera.getRoll()), new Vector3f(0,0,1), matrix, matrix );

		Vector3f cameraPos = camera.getPosition();
		
		// we want to move the opposite way that it appears on the screen to create the panning effect
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z );
		Matrix4f.translate(negativeCameraPos, matrix, matrix);
		
		return matrix;
		
	}
}
