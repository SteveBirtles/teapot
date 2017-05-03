import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.Random;

public class HelloWorld {

    // The window handle
    private long window;

    private int width = 1024;
    private int height = 768;

    private Random rand;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Cake!", glfwGetPrimaryMonitor(), NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        rand = new Random();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            long millis = System.currentTimeMillis() / 10;
            float r = (float) ((Math.sin(millis / 100.0) + 1) / 2.0);
            float g = (float) ((Math.sin(millis / 100.0 + 2) + 1) / 2.0);
            float b = (float) ((Math.sin(millis / 100.0 + 4) + 1) / 2.0);

            // Set the clear color
            glClearColor(r, g, b, 0f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glViewport(0,0,width,height);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0,width,0,height,-1,1);
            glMatrixMode(GL_MODELVIEW);

            for (float x = 0; x < width; x += 64) {
                for (float y = 0; y < width; y += 64) {

                    int z = (int) (x * width + y);

                    r = (float) ((Math.sin((millis + z) / 100.0) + 1) / 2.0); 
                    g = (float) ((Math.sin(z / 100.0) + 1) / 2.0);
                    b = (float) ((Math.sin(millis / 100.0) + 1) / 2.0);

                    float i = (float) (Math.sin((millis + z) / 50.0) * 20);
                    float j = (float) (Math.cos((millis + z) / 50.0) * 20);

                    float w = (float) (30 + 30 * (Math.cos((millis + z) / 100.0) + 1) / 2.0);
                    float h = (float) (30 + 30 * (Math.sin((millis + z) / 100.0) + 1) / 2.0);

                    glPushMatrix();  
                    glTranslatef(x - w/2, y - h/2, 0f);  
                    glBegin(GL_QUADS);   
                    glColor3f(r, g, b); 
                    glVertex2f(0f + i, 0f + j);       
                    glVertex2f(0f + i, h + j);
                    glVertex2f(w + i, h + j);
                    glVertex2f(w + i, 0f + j);       
                    glEnd();
                    glPopMatrix();

                }
            }

            glfwSwapBuffers(window); // swap the color buffers

            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();        
    }

}