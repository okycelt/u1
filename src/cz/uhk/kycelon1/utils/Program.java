package cz.uhk.kycelon1.utils;

import com.jogamp.opengl.GL4;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ondřej Kycelt
 */

public class Program {

    private GL4 gl;
    private int name;
    private Map<String, Integer> uniforms;

    public Program(GL4 gl, String shaderFileName) {
        this.gl = gl;
        this.name = ShaderUtils.loadProgram(gl, shaderFileName);
        this.uniforms = new HashMap<>();
    }

    public void addUniform(String name) {
        this.uniforms.put(name, gl.glGetUniformLocation(this.name, name));
    }

    public void addUniforms(String[] names) {
        for (int i = 0; i < names.length; i++) {
            this.uniforms.put(names[i], gl.glGetUniformLocation(this.name, names[i]));
        }
    }

    public int getUniform(String name) {
        return this.uniforms.get(name);
    }

    public int getName() {
        return name;
    }

    public void use() {
        this.gl.glUseProgram(name);
    }

    public void delete() {
        this.gl.glDeleteProgram(name);
    }

}
