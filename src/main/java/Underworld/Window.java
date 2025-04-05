package Underworld;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private String title;
    private long glfwWindow;
    public float r,g,b,a;
    private boolean fadeToBlack = false;
    private static Window window = null;

    private static Scene currentScene;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Underworld";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown scene ' " + newScene + "'";
        }
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run(){
       System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // terminate GLFW and free the error calback
        glfwTerminate();
        glfwSetErrorCallback(null);
    }

    public void init(){
        // Error message
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to intialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE,GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // Setting the MouseListener callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener :: mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        // Setting the KeyListener callback
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable V-Sync
        glfwSwapInterval(1);

        // Make the window visiable
        glfwShowWindow(glfwWindow);

        /*Line is critical for LWJGL's interoperation with GLFW's
        OpenGL context, or any context that is managed externally.
        LWJGL detects the context that is current in the current thread,
        creates the GLCapabilities instance and makes the OpenGL
        bindings available for use.*/
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop(){
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;


        while(!glfwWindowShouldClose(glfwWindow)){
            // Poll Events
            glfwPollEvents();

            glClearColor(r,g,b,a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            // setting the delta time
            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }
}
