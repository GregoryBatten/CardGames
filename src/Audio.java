import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/* This class encapsulates all audio related functions inlcuding mixing sound effects, music,
 * and ambient noises. Future plans are to mix multiple sound effect sources into one sfxClip
 * that can mute/unmute everything in this catagory without disturbing the master gain.
 */

public class Audio {
    // Customizable class constants
    private static final String DRAW_SOUND = "audio/card_draw.wav";
    private static final String MENU_MUSIC = "audio/Blue Moon - Frank Sinatra.wav";
    private static final String MENU_AMBIENCE = "audio/menu_ambience.wav";
    private static final String KNOCK_SOUND = "audio/knock.wav";
    private static final String CASINO_JAZZ = "audio/casino_jazz.wav";
    private static final String CASINO_AMBIENCE = "audio/casino_ambience.wav";

    // Local variables for audio clips
    private static Clip musicClip;
    private static Clip ambienceClip;
	private static boolean muteMusic = false;

    // Defines the method for sending the output signal of one song
    public static void playMusic(String audioFile, boolean repeat) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(audioFile).getAbsoluteFile())) {
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInputStream);
            if(repeat) musicClip.loop(Clip.LOOP_CONTINUOUSLY);

            // Defines the default volume (gain) of all music
            FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            if(muteMusic) gainControl.setValue(-80.0f);
            else gainControl.setValue(-10.0f);
            musicClip.start();
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    // Defines the method to stop the current music clip
    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    // Defines the method to toggle mute for the music clip
    public static void toggleMusic() {
        if(musicClip != null) {
            FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            if (muteMusic)
                gainControl.setValue(-10.0f);
            else
                gainControl.setValue(-80.0f); // default music volume is hard-coded here
            muteMusic = !muteMusic;
        }
    }

    // Returns true if the music is muted
    public static boolean isMusicMuted() {
        return muteMusic;
    }

    // Defines the method for sending the output signal of one ambient track
    public static void playAmbience(String audioFile) {
        new Thread(() -> {
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(audioFile).getAbsoluteFile())) {
                ambienceClip = AudioSystem.getClip();
                ambienceClip.open(audioInputStream);
                ambienceClip.loop(Clip.LOOP_CONTINUOUSLY);

                // Defines the default volume (gain) of all ambience
                FloatControl gainControl = (FloatControl) ambienceClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-7.0f);
                ambienceClip.start();

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            }
        }).start();
    }

    // Defines the method to stop the current ambience clip
    public static void stopAmbience() {
         if (ambienceClip != null && ambienceClip.isRunning()) {
            ambienceClip.stop();
            ambienceClip.close();
        }
    }

    // Defines the method for threading sound effects and sending the output signal
    public static void playSFX(String audioFile) {
        new Thread(() -> {
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(audioFile).getAbsoluteFile())) {
                Clip sfxClip = AudioSystem.getClip();
                sfxClip.open(audioInputStream);
                sfxClip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Defines the method to start the main menu song
    public static void menuMusic(boolean repeat) {
        playMusic(MENU_MUSIC, repeat);
    }

    // Defines the method to start the main menu ambience
    public static void menuAmbience() {
        playAmbience(MENU_AMBIENCE);
    }

    // Defines the method to start the casino song
    public static void casinoJazz(boolean repeat) {
        playMusic(CASINO_JAZZ, repeat);
    }

    // Defines the method to start the casino ambience
    public static void casinoAmbience() {
        playAmbience(CASINO_AMBIENCE);
    }

    // Defines the method to start the draw card sound effect
    public static void drawSound() {
        playSFX(DRAW_SOUND);
    }

    // Defines the method to start the table knock sound effect
    public static void knockSound() {
        playSFX(KNOCK_SOUND);
    }
}