package cz.uhk.kycelon1;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;

/**
 * @author Ondřej Kycelt
 */

public class App {

    private static final String APP_NAME = "pgrf3_u1";

    private App(int width, int height) {
        init(width, height);
    }

    private void init(int width, int height) {
        GLProfile profile = GLProfile.getMaximum(true);
        GLCapabilities capabilities = new GLCapabilities(profile);

        Renderer renderer = new Renderer();

        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(renderer);
        canvas.addMouseListener(renderer);
        canvas.addMouseMotionListener(renderer);
        canvas.addKeyListener(renderer);
        canvas.setSize(width, height);
        canvas.setFocusable(true);

        Frame frame = new Frame();
        frame.setTitle(APP_NAME);
        frame.add(canvas);
        frame.setSize(width, height);

        final FPSAnimator animator = new FPSAnimator(canvas, 60, true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                new Thread(() -> {
                    if (animator.isStarted()) animator.stop();
                    System.exit(0);
                }).start();
            }
        });

        frame.pack();
        frame.setVisible(true);

        animator.start();
    }

    public static void main(String[] args) {
        new App(1280, 720);
    }

}
