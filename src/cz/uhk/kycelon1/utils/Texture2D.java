package cz.uhk.kycelon1.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * @author Ondřej Kycelt
 */

public class Texture2D {

	private int[] name;

	public Texture2D(GL4 gl, String path) {
		this(gl, readTextureDataFromFile(gl.getGLProfile(), path));
	}

	public Texture2D(GL4 gl, int width, int height, int internalFormat, int pixelFormat, int pixelType, Buffer buffer) {
		name = new int[1];
		gl.glGenTextures(1, name, 0);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, name[0]);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, internalFormat,
				width, height, 0,
				pixelFormat, pixelType, buffer);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
	}

	public Texture2D(GL4 gl, TextureData textureData) {
		this(gl, textureData.getWidth(),	textureData.getHeight(),
				textureData.getInternalFormat(), textureData.getPixelFormat(),
				textureData.getPixelType(), textureData.getBuffer());
	}

	private static TextureData readTextureDataFromFile(GLProfile glProfile, String fileName) {
		System.out.print("Reading name file " + fileName);
		try {
			InputStream is = Texture2D.class.getResourceAsStream(fileName);
			TextureData data = TextureIO.newTextureData(glProfile, is, true,
					getExtension(fileName));
			is.close();
			System.out.println(" ... OK");
			return data;
		} catch (IOException e) {
			System.err.println(" failed");
			throw new RuntimeException(e);
		}
	}

	private static String getExtension(String s) {
		String ext = "";
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public int getName() {
		return name[0];
	}

}
