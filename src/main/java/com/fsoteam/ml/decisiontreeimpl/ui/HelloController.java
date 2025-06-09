package com.fsoteam.ml.decisiontreeimpl.ui;

import com.fsoteam.ml.decisiontreeimpl.utils.DatasetInitializer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloController {

    @FXML
    private AnchorPane containerPane;

    @FXML
    private ImageView logoImage;

    private AnchorPane sceneContainer;
    private static final Map<String, Node> scenes = new HashMap<>();
    private SharedData sharedData;

    @FXML
    public void initialize() throws IOException {
        sharedData = SharedData.getInstance();
        sharedData.setDatasetInitializer(new DatasetInitializer());

        // Créer le conteneur pour afficher les différentes scènes
        sceneContainer = new AnchorPane();
        AnchorPane.setLeftAnchor(sceneContainer, 223.0);
        AnchorPane.setRightAnchor(sceneContainer, 0.0);
        AnchorPane.setTopAnchor(sceneContainer, 0.0);
        AnchorPane.setBottomAnchor(sceneContainer, 0.0);
        containerPane.getChildren().add(sceneContainer);

        // Rendre le logo circulaire
        if (logoImage != null) {
            double radius = Math.min(logoImage.getFitWidth(), logoImage.getFitHeight()) / 2;
            Circle clip = new Circle(radius, radius, radius);
            logoImage.setClip(clip);
        }

        loadAllScenes(); // charge welcome et upload

        // OPTION 1 : Pré-charger directement la scène runTest (ou déplace cet appel après l'upload si tu préfères)
        try {
            dataHasLoaded();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la scène RunTest");
            e.printStackTrace();
        }
    }

    public static void dataHasLoaded() throws IOException {
        if (scenes.containsKey("runTest")) return;

        System.out.println("📦 Chargement de la scène runTest...");
        Node runTestScene = FXMLLoader.load(HelloController.class.getResource(
                "/com/fsoteam/ml/decisiontreeimpl/subScenes/runTest.fxml"));

        runTestScene.setVisible(false);
        scenes.put("runTest", runTestScene);

        // Ajout dynamique au conteneur
        // ATTENTION : sceneContainer n'est pas static → il faut l'ajouter ailleurs
        // Solution : l'ajouter ici uniquement si on fournit une référence
    }

    public static void modelHasTrained() throws IOException {
        if (scenes.containsKey("evaluate") || scenes.containsKey("viewData")) return;

        System.out.println("📦 Chargement des scènes evaluate & viewData...");
        Node evaluateScene = FXMLLoader.load(HelloController.class.getResource(
                "/com/fsoteam/ml/decisiontreeimpl/subScenes/evaluate.fxml"));
        Node viewDataScene = FXMLLoader.load(HelloController.class.getResource(
                "/com/fsoteam/ml/decisiontreeimpl/subScenes/viewData.fxml"));

        evaluateScene.setVisible(false);
        viewDataScene.setVisible(false);

        scenes.put("evaluate", evaluateScene);
        scenes.put("viewData", viewDataScene);
    }

    public void loadAllScenes() throws IOException {
        Node uploadData = FXMLLoader.load(getClass().getResource(
                "/com/fsoteam/ml/decisiontreeimpl/subScenes/uploadData.fxml"));
        Node welcomeInterface = FXMLLoader.load(getClass().getResource(
                "/com/fsoteam/ml/decisiontreeimpl/subScenes/welcome-interface.fxml"));

        uploadData.setVisible(false);
        welcomeInterface.setVisible(false);

        scenes.put("uploadData", uploadData);
        scenes.put("welcomeInterface", welcomeInterface);

        for (Node scene : scenes.values()) {
            sceneContainer.getChildren().add(scene);
        }

        showScene("welcomeInterface");
    }

    public void showScene(String sceneKey) {
        for (Node scene : scenes.values()) {
            scene.setVisible(false);
        }

        Node selectedScene = scenes.get(sceneKey);
        if (selectedScene != null) {
            System.out.println("🔁 Affichage de la scène : " + sceneKey);
            selectedScene.setVisible(true);

            // Vérifie s'il n'est pas encore dans le container
            if (!sceneContainer.getChildren().contains(selectedScene)) {
                sceneContainer.getChildren().add(selectedScene);
            }
        } else {
            System.err.println("❌ Scène non trouvée : " + sceneKey);
        }
    }

    @FXML
    public void loadUploadDataScene() {
        showScene("uploadData");
    }

    @FXML
    public void loadRunTestScene() {
        System.out.println("✅ Bouton Run Test cliqué");
        showScene("runTest");
    }

    @FXML
    public void loadEvaluateScene() {
        showScene("evaluate");
    }

    @FXML
    public void loadViewDataScene() {
        showScene("viewData");
    }
}
