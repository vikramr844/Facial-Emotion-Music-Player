package com.emotion.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.*;

public class MP3MusicService {
    private MediaPlayer currentPlayer;
    private Map<String, List<String>> emotionMP3Urls;
    private String currentEmotion;
    
    public MP3MusicService() {
        initializeMP3Urls();
    }
    
    private void initializeMP3Urls() {
        emotionMP3Urls = new HashMap<>();
        
        // Royalty-free MP3 URLs from SoundHelix
        emotionMP3Urls.put("happy", Arrays.asList(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
        ));
        
        emotionMP3Urls.put("sad", Arrays.asList(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3"
        ));
        
        emotionMP3Urls.put("angry", Arrays.asList(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3"
        ));
        
        emotionMP3Urls.put("neutral", Arrays.asList(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3"
        ));
        
        emotionMP3Urls.put("surprised", Arrays.asList(
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3"
        ));
        
        System.out.println("✅ MP3 URLs initialized for all emotions");
    }
    
    public void playMusicForEmotion(String emotion) {
        if (emotion.equalsIgnoreCase(currentEmotion) && currentPlayer != null) {
            return; // Same emotion, no need to change music
        }
        
        stopCurrentMusic();
        currentEmotion = emotion.toLowerCase();
        
        List<String> mp3Urls = emotionMP3Urls.get(currentEmotion);
        if (mp3Urls != null && !mp3Urls.isEmpty()) {
            Random random = new Random();
            String mp3Url = mp3Urls.get(random.nextInt(mp3Urls.size()));
            playMP3(mp3Url, emotion);
        } else {
            System.err.println("❌ No MP3 URLs found for emotion: " + emotion);
        }
    }
    
    private void playMP3(String mp3Url, String emotion) {
        try {
            System.out.println("🔊 Loading MP3 for " + emotion + ": " + mp3Url);
            
            Media media = new Media(mp3Url);
            currentPlayer = new MediaPlayer(media);
            
            currentPlayer.setOnReady(() -> {
                System.out.println("✅ MP3 Loaded: " + emotion + " music");
                currentPlayer.play();
            });
            
            currentPlayer.setOnPlaying(() -> {
                System.out.println("🎵 Now Playing: " + emotion + " music");
            });
            
            currentPlayer.setOnEndOfMedia(() -> {
                System.out.println("🔁 Song ended, playing next...");
                playMusicForEmotion(emotion); // Play another song for same emotion
            });
            
            currentPlayer.setOnError(() -> {
                System.err.println("❌ MP3 Error: " + currentPlayer.getError());
                // Try another URL if current fails
                playMusicForEmotion(emotion);
            });
            
        } catch (Exception e) {
            System.err.println("❌ Error playing MP3: " + e.getMessage());
        }
    }
    
    public void stopCurrentMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }
    
    public void pauseMusic() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }
    
    public void resumeMusic() {
        if (currentPlayer != null) {
            currentPlayer.play();
        }
    }
    
    public boolean isPlaying() {
        return currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    public String getCurrentSongInfo() {
        if (currentEmotion != null) {
            return "Playing: " + currentEmotion + " mood music (MP3 Stream)";
        }
        return "No music playing";
    }
    
    public String getCurrentEmotion() {
        return currentEmotion;
    }
}