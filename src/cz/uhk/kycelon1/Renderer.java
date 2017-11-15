package cz.uhk.kycelon1;

import com.jogamp.opengl.*;
import cz.uhk.kycelon1.utils.GridFactory;
import cz.uhk.kycelon1.utils.OGLBuffers;
import cz.uhk.kycelon1.utils.Program;
import cz.uhk.kycelon1.utils.Texture2D;
import cz.uhk.pgrf.transforms.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondřej Kycelt
 */

public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    private static final int OBJECT_0 = 0;
    private static final int OBJECT_1 = 1;
    private static final int OBJECT_2 = 2;
    private static final int OBJECT_3 = 3;
    private static final int OBJECT_4 = 4;
    private static final int OBJECT_5 = 5;
    private static final int OBJECT_6 = 6;

    private static final double ROTATION_ANGLE = Math.PI / 256;

    private int width, height, ox, oy;
    private int currentObject;
    private OGLBuffers grid;
    private Program surfaceCartesian;
    private Mat4 model, projection;
    private Camera camera;
    private Point3D lightPosition;
    private List<Col> colors;
    private Texture2D texActive, texActiveNormal, texActiveHeight, texActiveSpecular, texActiveAmbOccl;
    private Texture2D texCobblestone, texCobblestoneNormal, texCobblestoneHeight, texCobblestoneSpecular, texCobblestoneAmbOccl;
    private Texture2D texWood, texWoodNormal, texWoodHeight, texWoodSpecular, texWoodAmbOccl;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = new DebugGL4(drawable.getGL().getGL4());
        drawable.setGL(gl);

        System.out.println(gl.glGetString(GL4.GL_VENDOR));
        System.out.println(gl.glGetString(GL4.GL_RENDERER));
        System.out.println(gl.glGetString(GL4.GL_VERSION));
        System.out.println();

        initShaders(gl);
        initBuffers(gl);
        initMatrices();
        initColors();

        currentObject = 3;

        texCobblestone = new Texture2D(gl, "/textures/cobblestone.jpeg");
        texCobblestoneNormal = new Texture2D(gl, "/textures/cobblestonen.jpeg");
        texCobblestoneHeight = new Texture2D(gl, "/textures/cobblestoneh.jpeg");
        texCobblestoneSpecular = new Texture2D(gl, "/textures/cobblestones.jpeg");
        texCobblestoneAmbOccl = new Texture2D(gl, "/textures/cobblestonea.jpeg");

        texWood = new Texture2D(gl, "/textures/wood.jpeg");
        texWoodNormal = new Texture2D(gl, "/textures/woodn.jpeg");
        texWoodHeight = new Texture2D(gl, "/textures/woodh.jpg");
        texWoodSpecular = new Texture2D(gl, "/textures/woods.jpeg");
        texWoodAmbOccl = new Texture2D(gl, "/textures/wooda.jpeg");

        texActive = texCobblestone;
        texActiveNormal = texCobblestoneNormal;
        texActiveHeight = texCobblestoneHeight;
        texActiveSpecular = texCobblestoneSpecular;
        texActiveAmbOccl = texCobblestoneAmbOccl;
    }

    private void initShaders(GL4 gl) {
        surfaceCartesian = new Program(gl, "/grid");
        surfaceCartesian.addUniforms(new String[] {
                "objectModelMatrix", "modelMatrix", "viewMatrix", "projectionMatrix", "lightPosition", "object",
                "baseColor", "diffuseTexture", "normalTexture", "heightTexture", "roughnessTexture", "ambOccTexture"
        });
    }

    private void initBuffers(GL4 gl) {
        grid = GridFactory.createStrip(gl, 40, 40);
    }

    private void initMatrices() {
        model = new Mat4Identity();
        camera = new Camera().withPosition(new Vec3D(8, 0, 4))
                .withAzimuth(Math.PI)
                .withZenith(Math.PI * - 0.15);
        lightPosition = new Point3D(5, 5,5);
    }

    private void initColors() {
        colors = new ArrayList<>();
        colors.add(new Col(0x563e20));
        colors.add(new Col(0xec96a4));
        colors.add(new Col(0xe6df44));
        colors.add(new Col(0x1995ad));
        colors.add(new Col(0x5b7065));
        colors.add(new Col(0x258039));
        colors.add(new Col(0xf52549));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

        float[] modelMatrix = model.floatArray();
        float[] viewMatrix = camera.getViewMatrix().floatArray();
        float[] projectionMatrix = projection.floatArray();

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClearDepthf(1.0f);
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glEnable(GL4.GL_CULL_FACE);
        gl.glCullFace(GL4.GL_BACK);

        surfaceCartesian.use();

        lightPosition = lightPosition.mul(new Mat4RotZ(ROTATION_ANGLE));
        Point3D transfLsPos = lightPosition;
        gl.glUniform3f(surfaceCartesian.getUniform("lightPosition"),
                (float) transfLsPos.getX(), (float) transfLsPos.getY(), (float) transfLsPos.getZ());
        gl.glUniformMatrix4fv(surfaceCartesian.getUniform("modelMatrix"),
                1, false, modelMatrix, 0);
        gl.glUniformMatrix4fv(surfaceCartesian.getUniform("viewMatrix"),
                1, false, viewMatrix, 0);
        gl.glUniformMatrix4fv(surfaceCartesian.getUniform("projectionMatrix"),
                1, false, projectionMatrix, 0);
        gl.glUniform1i(surfaceCartesian.getUniform("object"), currentObject);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texActive.getName());
        gl.glUniform1i(surfaceCartesian.getUniform("diffuseTexture"), 0);

        gl.glActiveTexture(GL4.GL_TEXTURE1);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texActiveNormal.getName());
        gl.glUniform1i(surfaceCartesian.getUniform("normalTexture"), 1);

        gl.glActiveTexture(GL4.GL_TEXTURE2);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texActiveHeight.getName());
        gl.glUniform1i(surfaceCartesian.getUniform("heightTexture"), 2);

        gl.glActiveTexture(GL4.GL_TEXTURE3);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texActiveSpecular.getName());
        gl.glUniform1i(surfaceCartesian.getUniform("roughnessTexture"), 3);

        gl.glActiveTexture(GL4.GL_TEXTURE4);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texActiveAmbOccl.getName());
        gl.glUniform1i(surfaceCartesian.getUniform("ambOccTexture"), 4);

        Col color = colors.get(currentObject);
        gl.glUniform3f(surfaceCartesian.getUniform("baseColor"),
                (float) color.getR(), (float) color.getG(), (float) color.getB());

        grid.draw(GL4.GL_TRIANGLE_STRIP, surfaceCartesian.getName());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        projection = new Mat4PerspRH(Math.PI / 6, height / (double) width, 0.01, 1000.0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        surfaceCartesian.delete();
    }

    // MARK: - MouseMotionListener

    @Override
    public void mouseDragged(MouseEvent e) {
        camera = camera.addAzimuth(0.5 * Math.PI * (e.getX() - ox) / width)
                .addZenith(0.5 * Math.PI * (e.getY() - oy) / height);
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("mousePressed");
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyChar());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                camera = camera.forward(0.25);
                break;
            case KeyEvent.VK_S:
                camera = camera.backward(0.25);
                break;
            case KeyEvent.VK_D:
                camera = camera.right(0.25);
                break;
            case KeyEvent.VK_A:
                camera = camera.left(0.25);
                break;
            case KeyEvent.VK_J:
                model = model.mul(new Mat4RotZ(-ROTATION_ANGLE));
                break;
            case KeyEvent.VK_L:
                model = model.mul(new Mat4RotZ(ROTATION_ANGLE));
                break;
            case KeyEvent.VK_I:
                model = model.mul(new Mat4RotY(-ROTATION_ANGLE));
                break;
            case KeyEvent.VK_K:
                model = model.mul(new Mat4RotY(ROTATION_ANGLE));
                break;
            case KeyEvent.VK_1:
                currentObject = OBJECT_1;
                break;
            case KeyEvent.VK_2:
                currentObject = OBJECT_2;
                break;
            case KeyEvent.VK_3:
                currentObject = OBJECT_3;
                break;
            case KeyEvent.VK_4:
                currentObject = OBJECT_4;
                break;
            case KeyEvent.VK_5:
                currentObject = OBJECT_5;
                break;
            case KeyEvent.VK_6:
                currentObject = OBJECT_6;
                break;
            case KeyEvent.VK_0:
                currentObject = OBJECT_0;
                break;
            case KeyEvent.VK_N:
                texActive = texCobblestone;
                texActiveNormal = texCobblestoneNormal;
                texActiveHeight = texCobblestoneHeight;
                texActiveSpecular = texCobblestoneSpecular;
                texActiveAmbOccl = texCobblestoneAmbOccl;
                break;
            case KeyEvent.VK_M:
                texActive = texWood;
                texActiveNormal = texWoodNormal;
                texActiveHeight = texWoodHeight;
                texActiveSpecular = texWoodSpecular;
                texActiveAmbOccl = texWoodAmbOccl;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
