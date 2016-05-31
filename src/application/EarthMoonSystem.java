package application;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EarthMoonSystem extends Application {

	//Planets constraints
  private static final double EARTH_RADIUS  = 400;
  private static final double MOON_RADIUS  = 100;
  private static final double DISTANCE_EARTH_MOON=500;
  private static final double DISTANCE_FROM_OBSERVER_EARTH=900;
  private static final double DISTANCE_FROM_OBSERVER_MOON=100;
		  
  //Rendering constraints
  private static final double VIEWPORT_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
  private static final double VIEWPORT_HEIGTH = Screen.getPrimary().getVisualBounds().getHeight();
  private static final double MAP_WIDTH  = 8192 / 2d;
  private static final double MAP_HEIGHT = 4096 / 2d;
  
  //Animation constraints
  private static final double ROTATE_SECS   = 3;


  private Sphere earth, moon;
  
  private Group buildScene() {

	 //Create 3D objects
    earth = new Sphere(EARTH_RADIUS);
    
    earth.setTranslateX(VIEWPORT_WIDTH / 2); //put the earth at the center of the screen
    earth.setTranslateY(VIEWPORT_HEIGTH / 2);
    earth.setTranslateZ(DISTANCE_FROM_OBSERVER_EARTH);

    moon = new Sphere (MOON_RADIUS);
    moon.setTranslateX(earth.getTranslateX()+DISTANCE_EARTH_MOON);
    moon.setTranslateY(VIEWPORT_HEIGTH / 2d);
    moon.setTranslateZ(DISTANCE_FROM_OBSERVER_MOON);
    
  //Retrieve resources
   String diffuse= EarthMoonSystem.class.getResource("../earth_diffuse.jpg").toString();
   String bump= EarthMoonSystem.class.getResource("../earth_bump.jpg").toString();
   String specular= EarthMoonSystem.class.getResource("../earth_specular.jpg").toString();
   String moonFlat =EarthMoonSystem.class.getResource("../moon_flat.jpg").toString();   
   
   //Create material
    PhongMaterial earthMaterial = new PhongMaterial();
    earthMaterial.setDiffuseMap(new Image(diffuse,MAP_WIDTH,MAP_HEIGHT,true,true));
    earthMaterial.setBumpMap(new Image(bump,MAP_WIDTH,MAP_HEIGHT,true,true));
    earthMaterial.setSpecularMap( new Image(specular,MAP_WIDTH,MAP_HEIGHT,true,true));
    earth.setMaterial(earthMaterial);
    
    PhongMaterial moonMaterial = new PhongMaterial();
    moonMaterial.setDiffuseMap( new Image(moonFlat,MAP_WIDTH,MAP_HEIGHT,true,true));
    moon.setMaterial(moonMaterial);
    
    //Create the light
    PointLight sunLight = new PointLight(Color.WHITE);
    sunLight.setTranslateX(3000);
    sunLight.setTranslateY(VIEWPORT_HEIGTH / 2d);
    sunLight.setTranslateZ(100);
    
    return new Group(earth,moon,sunLight);
  }

  @Override
  public void start(Stage stage) {
    Group group = buildScene();
    
    Scene scene = new Scene(group,VIEWPORT_WIDTH, VIEWPORT_HEIGTH,true,SceneAntialiasing.BALANCED);

    scene.setFill(Color.rgb(10, 10, 40));

    PerspectiveCamera camera = new PerspectiveCamera();
  //leave the camera with default settings pointing on the top left corner of the screen
    camera.setTranslateX(0); 
    camera.setTranslateY(0);
    camera.setTranslateZ(0);
    scene.setCamera(camera);

    stage.setScene(scene);
    scene.setOnScroll((final ScrollEvent e) -> {
        camera.setTranslateZ(camera.getTranslateZ() + e.getDeltaY());
    });
    stage.show();

    stage.setFullScreen(true);

   
    Rotate rotation = new Rotate();
    rotation.setPivotX(earth.getTranslateX()-moon.getTranslateX());
    rotation.setPivotY(earth.getTranslateY()-moon.getTranslateY());
    rotation.setPivotZ(earth.getTranslateZ()-moon.getTranslateZ());
    rotation.setAxis(Rotate.Y_AXIS);
    moon.getTransforms().add(rotation);
    Timeline moonRevolutionTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
            new KeyFrame(Duration.seconds(10), new KeyValue(rotation.angleProperty(), -360)));
    moonRevolutionTimeline.setCycleCount(Timeline.INDEFINITE);

    ParallelTransition parallel= new ParallelTransition();
    parallel.getChildren().addAll(moonRevolutionTimeline,rotateAroundYAxis(earth) );
    parallel.play();
    
  }
  
  private RotateTransition rotateAroundYAxis(Node node) {
    RotateTransition rotate = new RotateTransition(
      Duration.seconds(ROTATE_SECS), 
      node
    );
    rotate.setAxis(Rotate.Y_AXIS);
    rotate.setFromAngle(360);
    rotate.setToAngle(0);
    rotate.setInterpolator(Interpolator.LINEAR);
    rotate.setCycleCount(RotateTransition.INDEFINITE);

    return rotate;
  }
  

  public static void main(String[] args) {
    launch(args);
  }
}
