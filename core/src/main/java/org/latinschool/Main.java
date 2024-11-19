package org.latinschool;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends ApplicationAdapter {
    private World world;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private List<Body> circles;
    private List<Body> squares;
    private List<Body> frozenSquares;
    private List<Body> frozenCircles;
    private List<Body> sSquares;
    private List<Body> rSquares;
    private Body groundBody;
    private Body leftWallBody;
    private Body rightWallBody;
    private int toolMode = 1;
    private boolean launchActive = false;
    private float previousX;
    private float previousY;


    @Override
    public void create() {
        camera = new OrthographicCamera(50, 50 * (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));
        camera.position.set(0, 0, 0);
        camera.update();
        world = new World(new Vector2(0, -9.8f), true);
        shapeRenderer = new ShapeRenderer();
        circles = new ArrayList<>();
        squares = new ArrayList<>();
        frozenSquares = new ArrayList<>();
        frozenCircles = new ArrayList<>();
        sSquares = new ArrayList<>();
        rSquares = new ArrayList<>();
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(0, -15);
        groundBody = world.createBody(groundDef);
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(20, 1);
        groundBody.createFixture(groundShape, 0);
        groundShape.dispose();
        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(-25, 0);
        leftWallBody = world.createBody(leftWallDef);
        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(1, 25);
        leftWallBody.createFixture(wallShape, 0);
        wallShape.dispose();
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(25, 0);
        rightWallBody = world.createBody(rightWallDef);
        wallShape = new PolygonShape();
        wallShape.setAsBox(1, 25);
        rightWallBody.createFixture(wallShape, 0);
        wallShape.dispose();
        Gdx.input.setInputProcessor(new InputHandler());
    }

    private class InputHandler extends com.badlogic.gdx.InputAdapter {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 screenCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = camera.unproject(screenCoords);
            if (toolMode == 1) {
                createCircle(worldCoords.x, worldCoords.y);
            } else if (toolMode == 2) {
                createSquare(worldCoords.x, worldCoords.y);
            } else if (toolMode == 3) {
                createFrozenSquare(worldCoords.x, worldCoords.y);
            } else if (toolMode == 4) {
                createFrozenCircle(worldCoords.x, worldCoords.y);
            } else if (toolMode == 5) {
                getCoords();
            } else if (toolMode == 6) {
                createSlipperySquare(worldCoords.x, worldCoords.y);
            } else if (toolMode == 7) {
                createRoughSquare(worldCoords.x, worldCoords.y);
            } else if (toolMode == 8) {
                if (launchActive) {
                    float newX = worldCoords.x - previousX;
                    float newY = worldCoords.y - previousY;
                    Vector2 force = new Vector2((newX * -500),(newY * -500));
                    launchableBall(worldCoords.x, worldCoords.y, force);
                    launchActive = false;
                } else {
                    launchActive = true;
                    previousX = worldCoords.x;
                    previousY = worldCoords.y;
                }
            }
            return true;
        }

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == com.badlogic.gdx.Input.Keys.NUM_1) {
                toolMode = 1;
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_2) {
                toolMode = 2;
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_3) {
                toolMode = 3;
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_4) {
                toolMode = 4;
            } else if (keycode == com.badlogic.gdx.Input.Keys.SPACE) {
                for (Body circle: circles) {
                    world.destroyBody(circle);
                }
                for (Body square: squares) {
                    world.destroyBody(square);
                }
                for (Body frozenCircle: frozenCircles) {
                    world.destroyBody(frozenCircle);
                }
                for (Body frozenSquare: frozenSquares) {
                    world.destroyBody(frozenSquare);
                }
                circles.clear();
                squares.clear();
                frozenSquares.clear();
                frozenCircles.clear();
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_5) {
                toolMode = 5;
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_6) {
                toolMode = 6;
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_7) {
                toolMode = 7;
            } else if (keycode == com.badlogic.gdx.Input.Keys.NUM_8) {
                toolMode = 8;
            }
            return true;
        }
    }

    private void getCoords() {
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();

        Vector3 screenCoords = new Vector3(screenX, screenY, 0);
        Vector3 worldCoords = camera.unproject(screenCoords);
        System.out.println("Mouse Coordinates in World: (" + worldCoords.x + ", " + worldCoords.y + ")");
    }

    private void stationaryCircle(float x, float y) {

    }


    private void createCircle(float x, float y) {
        BodyDef circleDef = new BodyDef();
        circleDef.type = BodyDef.BodyType.DynamicBody;
        circleDef.position.set(x, y);
        Body circleBody = world.createBody(circleDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;
        circleBody.createFixture(fixtureDef);
        circleShape.dispose();
        circles.add(circleBody);
    }

    private void createSquare(float x, float y) {
        BodyDef boxDef = new BodyDef();
        boxDef.type = BodyDef.BodyType.DynamicBody;
        boxDef.position.set(x, y);
        boxDef.allowSleep = true;
        boxDef.fixedRotation = false;
        Body boxBody = world.createBody(boxDef);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(1, 1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;
        boxBody.createFixture(fixtureDef);
        boxShape.dispose();
        squares.add(boxBody);
    }

    private void createFrozenSquare(float x, float y) {
        BodyDef frozenSDef = new BodyDef();
        frozenSDef.type = BodyDef.BodyType.StaticBody;
        frozenSDef.position.set(x, y);

        Body frozenBody = world.createBody(frozenSDef);

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(1, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;
        frozenBody.createFixture(fixtureDef);
        boxShape.dispose();
        frozenSquares.add(frozenBody);
    }
    private void createFrozenCircle(float x, float y) {
        BodyDef frozenCDef = new BodyDef();
        frozenCDef.type = BodyDef.BodyType.StaticBody;
        frozenCDef.position.set(x, y);

        Body frozenBody = world.createBody(frozenCDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;


        frozenBody.createFixture(fixtureDef);
        circleShape.dispose();

        frozenCircles.add(frozenBody);
    }
    private void createSlipperySquare(float x, float y) {
        BodyDef SboxDef = new BodyDef();
        SboxDef.type = BodyDef.BodyType.DynamicBody;
        SboxDef.position.set(x, y);
        SboxDef.allowSleep = true;
        SboxDef.fixedRotation = false;
        Body SboxBody = world.createBody(SboxDef);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(1, 1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0001f;
        fixtureDef.restitution = 0.2f;
        SboxBody.createFixture(fixtureDef);
        boxShape.dispose();
        sSquares.add(SboxBody);
    }
    private void createRoughSquare(float x, float y) {
        BodyDef RboxDef = new BodyDef();
        RboxDef.type = BodyDef.BodyType.DynamicBody;
        RboxDef.position.set(x, y);
        RboxDef.allowSleep = true;
        RboxDef.fixedRotation = false;
        Body RboxBody = world.createBody(RboxDef);
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(1, 1);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boxShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 1000.0f;
        fixtureDef.restitution = 0.2f;
        RboxBody.createFixture(fixtureDef);
        boxShape.dispose();
        rSquares.add(RboxBody);
    }

    private void launchableBall(float x, float y, Vector2 force) {
        // Create the ball
        BodyDef ballDef = new BodyDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        ballDef.position.set(x, y);
        Body ballBody = world.createBody(ballDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;

        ballBody.createFixture(fixtureDef);
        circleShape.dispose();
        circles.add(ballBody);
        ballBody.applyForceToCenter(force, true);
    }
    public void drawAimLine(float x, float y, float x2, float y2) {
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.line(x, y, x2, y2);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(1 / 60f, 6, 2);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(groundBody.getPosition().x - 20, groundBody.getPosition().y - 1, 40, 2);
        shapeRenderer.rect(leftWallBody.getPosition().x - 1, leftWallBody.getPosition().y - 25, 2, 50);
        shapeRenderer.rect(rightWallBody.getPosition().x - 1, rightWallBody.getPosition().y - 25, 2, 50);
        shapeRenderer.setColor(new Color(0.6f, 1f, 0.6f, 1f));
        for (Body circle : circles) {
            shapeRenderer.circle(circle.getPosition().x, circle.getPosition().y, 1, 20);
        }
        for (Body square : squares) {
            float x = square.getPosition().x;
            float y = square.getPosition().y;
            float angle = square.getAngle();
            shapeRenderer.rect(x - 1, y - 1, 1, 1, 2, 2, 1, 1, (float) Math.toDegrees(angle));
        }
        shapeRenderer.setColor(new Color(0.4f, 0.8f, 1f, 1f));
        for (Body frozenSquare : frozenSquares) {
            float x = frozenSquare.getPosition().x;
            float y = frozenSquare.getPosition().y;
            float angle = frozenSquare.getAngle();
            shapeRenderer.rect(x - 1, y - 1, 1, 1, 2, 2, 1, 1, (float) Math.toDegrees(angle));
        }
        for (Body circle : frozenCircles) {
            shapeRenderer.circle(circle.getPosition().x, circle.getPosition().y, 1, 20);
        }
        shapeRenderer.setColor(new Color(0.6f, 1f, 0.6f, 1f));
        for (Body sSquare : sSquares) {
            float x = sSquare.getPosition().x;
            float y = sSquare.getPosition().y;
            float angle = sSquare.getAngle();
            shapeRenderer.rect(x - 1, y - 1, 1, 1, 2, 2, 1, 1, (float) Math.toDegrees(angle));
        }
        for (Body rSquare : rSquares) {
            float x = rSquare.getPosition().x;
            float y = rSquare.getPosition().y;
            float angle = rSquare.getAngle();
            shapeRenderer.rect(x - 1, y - 1, 1, 1, 2, 2, 1, 1, (float) Math.toDegrees(angle));
        }
        if (launchActive) {
            int screenX = Gdx.input.getX();
            int screenY = Gdx.input.getY();
            Vector3 screenCoords = new Vector3(screenX, screenY, 0);
            Vector3 worldCoords = camera.unproject(screenCoords);
            drawAimLine(previousX, previousY, worldCoords.x, worldCoords.y);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        world.dispose();
        shapeRenderer.dispose();
    }
}
