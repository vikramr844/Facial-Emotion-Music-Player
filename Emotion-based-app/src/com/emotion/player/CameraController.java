package com.emotion.player;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CameraController {
    
    @FXML private ImageView cameraView;
    @FXML private Button startCameraBtn;
    @FXML private Button stopCameraBtn;
    @FXML private Label statusLabel;
    @FXML private Label emotionLabel;
    @FXML private Label songLabel;
    @FXML private Label statsLabel;
    @FXML private Button playBtn;
    @FXML private Button pauseBtn;
    @FXML private Button stopBtn;
    
    private VideoCapture capture;
    private boolean cameraActive;
    private ScheduledExecutorService timer;
    private MP3MusicService musicService;
    
    private int frameCount = 0;
    private long startTime = System.currentTimeMillis();
    private boolean musicEnabled = true;
    
    @FXML
    public void initialize() {
        this.cameraActive = false;
        this.capture = new VideoCapture();
        
        // Initialize music service
        musicService = new MP3MusicService();
        
        updateCameraButtons();
        System.out.println("✅ Camera Controller Initialized with MP3 Music Service");
    }
    
    @FXML
    protected void handleStartCamera() {
        if (!cameraActive) {
            capture.open(0);
            
            if (capture.isOpened()) {
                cameraActive = true;
                capture.set(3, 640);
                capture.set(4, 480);
                
                Runnable frameGrabber = new Runnable() {
                    private Mat frame = new Mat();
                    
                    @Override
                    public void run() {
                        if (cameraActive && capture.read(frame)) {
                            frameCount++;
                            updateStatistics();
                            processFrame(frame);
                        }
                    }
                };
                
                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
                
                updateCameraStatus("Camera: Running");
                updateCameraButtons();
                
                // Auto-start music when camera starts
                if (musicEnabled) {
                    playMusicForEmotion("neutral");
                }
            } else {
                updateCameraStatus("Error: Cannot open camera");
            }
        }
    }
    
    @FXML
    protected void handleStopCamera() {
        if (cameraActive) {
            cameraActive = false;
            stopCamera();
            updateCameraStatus("Camera: Stopped");
            updateCameraButtons();
        }
    }
    
    @FXML
    protected void handlePlayMusic() {
        musicEnabled = true;
        String currentEmotion = emotionLabel.getText().toLowerCase();
        playMusicForEmotion(currentEmotion);
        updateMusicButtons();
    }
    
    @FXML
    protected void handlePauseMusic() {
        musicService.pauseMusic();
        songLabel.setText("Music Paused");
        updateMusicButtons();
    }
    
    @FXML
    protected void handleStopMusic() {
        musicService.stopCurrentMusic();
        musicEnabled = false;
        songLabel.setText("Music Stopped");
        updateMusicButtons();
    }
    
    private void processFrame(Mat frame) {
        Image imageToShow = mat2Image(frame);
        updateImageView(cameraView, imageToShow);
        simulateEmotionDetection();
    }
    
    private void simulateEmotionDetection() {
        String[] emotions = {"neutral", "happy", "sad", "surprised", "angry"};
        String simulatedEmotion = emotions[frameCount / 100 % emotions.length];
        
        Platform.runLater(() -> {
            String previousEmotion = emotionLabel.getText();
            emotionLabel.setText(simulatedEmotion);
            updateEmotionUI(simulatedEmotion);
            
            if (!simulatedEmotion.equalsIgnoreCase(previousEmotion) && musicEnabled) {
                playMusicForEmotion(simulatedEmotion);
            }
        });
    }
    
    private void playMusicForEmotion(String emotion) {
        musicService.playMusicForEmotion(emotion);
        Platform.runLater(() -> {
            songLabel.setText(musicService.getCurrentSongInfo());
        });
    }
    
    private void updateEmotionUI(String emotion) {
        String baseStyle = "-fx-font-size: 32px; -fx-font-weight: bold; -fx-padding: 10; " +
                          "-fx-background-color: white; -fx-border-width: 2; -fx-border-radius: 5;";
        
        switch(emotion.toLowerCase()) {
            case "happy": 
                emotionLabel.setStyle(baseStyle + "-fx-text-fill: #27ae60; -fx-border-color: #27ae60;");
                break;
            case "sad": 
                emotionLabel.setStyle(baseStyle + "-fx-text-fill: #3498db; -fx-border-color: #3498db;");
                break;
            case "angry": 
                emotionLabel.setStyle(baseStyle + "-fx-text-fill: #e74c3c; -fx-border-color: #e74c3c;");
                break;
            case "surprised": 
                emotionLabel.setStyle(baseStyle + "-fx-text-fill: #f39c12; -fx-border-color: #f39c12;");
                break;
            default: 
                emotionLabel.setStyle(baseStyle + "-fx-text-fill: #2980b9; -fx-border-color: #2980b9;");
        }
    }
    
    private void updateStatistics() {
        if (frameCount % 30 == 0) {
            long currentTime = System.currentTimeMillis();
            double elapsedSeconds = (currentTime - startTime) / 1000.0;
            double fps = frameCount / elapsedSeconds;
            
            Platform.runLater(() -> {
                String musicStatus = musicService.isPlaying() ? "Playing" : "Stopped";
                statsLabel.setText(String.format("FPS: %.1f\nFrames: %d\nRuntime: %.1fs\nMusic: %s", 
                    fps, frameCount, elapsedSeconds, musicStatus));
            });
        }
    }
    
    private Image mat2Image(Mat frame) {
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".png", frame, buffer);
            byte[] byteArray = buffer.toArray();
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage bufImage = ImageIO.read(in);
            return javafx.embed.swing.SwingFXUtils.toFXImage(bufImage, null);
        } catch (Exception e) {
            System.err.println("Cannot convert Mat to Image: " + e);
            return null;
        }
    }
    
    private void updateImageView(ImageView view, Image image) {
        Platform.runLater(() -> {
            if (image != null) {
                view.setImage(image);
            }
        });
    }
    
    private void updateCameraStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }
    
    private void updateCameraButtons() {
        Platform.runLater(() -> {
            startCameraBtn.setDisable(cameraActive);
            stopCameraBtn.setDisable(!cameraActive);
        });
    }
    
    private void updateMusicButtons() {
        Platform.runLater(() -> {
            boolean isPlaying = musicService.isPlaying();
            playBtn.setDisable(isPlaying && musicEnabled);
            pauseBtn.setDisable(!isPlaying);
            stopBtn.setDisable(!isPlaying && !musicEnabled);
        });
    }
    
    private void stopCamera() {
        if (timer != null && !timer.isShutdown()) {
            timer.shutdown();
            try {
                timer.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.err.println("Error stopping camera: " + e);
            }
        }
        
        if (capture.isOpened()) {
            capture.release();
        }
        
        Platform.runLater(() -> cameraView.setImage(null));
    }
    
    public void setClosed() {
        stopCamera();
        musicService.stopCurrentMusic();
    }
}