package cz.uhk.kycelon1.utils;

import com.jogamp.opengl.GL4;
import cz.uhk.kycelon1.utils.OGLBuffers;

/**
 * @author Ondřej Kycelt
 */

public class GridFactory {

    public static OGLBuffers create(GL4 gl, int xspan, int yspan) {

        // create vertex data

        float[] vertexData = new float[xspan * yspan * 2];
        for (int x = 0; x < xspan; x++) {
            for (int y = 0; y < yspan; y++) {
                vertexData[(x * yspan + y) * 2] = x / (float) (xspan - 1);
                vertexData[(x * yspan + y) * 2 + 1] = y / (float) (yspan - 1);
            }
        }

        // create index data

        int[] indexData = new int[(xspan - 1) * (yspan - 1) * 2 * 3];
        for (int x = 0; x < xspan - 1; x++) {
            for (int y = 0; y < yspan - 1; y++) {
                indexData[(x * (yspan - 1) + y) * 2 * 3] = x * (yspan) + y;
                indexData[(x * (yspan - 1) + y) * 2 * 3 + 1] = x * (yspan) + y + 1;
                indexData[(x * (yspan - 1) + y) * 2 * 3 + 2] = (x + 1) * (yspan) + y + 1;
                indexData[(x * (yspan - 1) + y) * 2 * 3 + 3] = x * (yspan) + y;
                indexData[(x * (yspan - 1) + y) * 2 * 3 + 4] = (x + 1) * (yspan) + y + 1;
                indexData[(x * (yspan - 1) + y) * 2 * 3 + 5] = (x + 1) * (yspan) + y;
            }
        }

        // create attributes

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("gridCoords", 2)
        };

        return new OGLBuffers(gl, vertexData, attributes, indexData);
    }

    public static OGLBuffers createStrip(GL4 gl, int xspan, int yspan) {

        // create vertex data

        float[] vertexData = new float[xspan * yspan * 2];
        for (int x = 0; x < xspan; x++) {
            for (int y = 0; y < yspan; y++) {
                vertexData[(x * yspan + y) * 2] = x / (float) (xspan - 1);
                vertexData[(x * yspan + y) * 2 + 1] = y / (float) (yspan - 1);
            }
        }

        // create index data

        int[] indexData = new int[(xspan - 1) * yspan * 2 + (xspan - 2) * 2];
        for (int x = 0; x < xspan - 1; x++) {
            if (x % 2 == 0) {
                //even row
                for (int y = 0; y < yspan; y++) {
                    int index = 2 * x * yspan + 2 * y + 2 * x;
                    indexData[index] = x * yspan + y;
                    indexData[index + 1] = (x + 1) * yspan + y;
                }
                //if not last row, add last item twice more
                if (x != xspan - 2) {
                    int index = 2 * x * yspan + 2 * yspan + 2 * x;
                    indexData[index] = indexData[index - 1];
                    indexData[index + 1] = indexData[index - 1];
                }
            } else {
                //odd row
                for (int y = 0; y < yspan; y++) {
                    int index = 2 * x * yspan + 2 * y + 2 * x;
                    indexData[index] = (x + 1) * yspan + (yspan - 1 - y);
                    indexData[index + 1] = x * yspan + (yspan - 1 - y);
                }
                // if not last row, add second to last item twice more
                if (x != xspan - 2) {
                    int index = 2 * x * yspan + 2 * yspan + 2 * x;
                    indexData[index] = indexData[index - 2];
                    indexData[index + 1] = indexData[index - 2];
                }
            }
        }

        // create attributes

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("gridCoords", 2)
        };

        return new OGLBuffers(gl, vertexData, attributes, indexData);
    }

}
