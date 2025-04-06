package Underworld;

import kotlin.random.Random;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class LevelEditorScene extends Scene {

    private int vertexID, fragmentID, shaderProgram;

    //sets the color for each vertex
    private float[] vertexArray = {
            //position          //color
            100f, 0f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,      1,0,//bottom right  0
            0f, 100f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,      0,1,//top left      1
            100f, 100f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f,      1,1,//top right     2
            0f, 0f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f,      0,0//bottom left   3
    };
    //IMPORTANT: Must be in counter-clockwise order
    //Sets the location for each triangle
    private int[] elementArray = {
            2,1,0, //top right triangle
            0,1,3, //bottom left triangle
    };

    private int vaoID, vboID, eboID;
    private  Shader defaultShader;
    private Texture testTexture;


    public LevelEditorScene(){

    }

    @Override
    public void init() {

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/images/testImage.jpg");

        //Generating the VAO, VBO, and EBO buffer objects, and send them to the GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50f;

        defaultShader.use();

        //Upload Texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        //bind the VAO
        glBindVertexArray(vaoID);

        //enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();
    }

}
