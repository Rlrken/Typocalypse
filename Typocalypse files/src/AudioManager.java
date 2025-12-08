import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;

public class AudioManager {

    private static Clip heartbeatClip1;
    private static Clip heartbeatClip2;
    private static Clip heartbeatClip3;
    private static Clip riserClip;
    private static Clip errorClip;
    private static Clip bgm2Clip;
    private static Clip bgm1Clip;
    private static Clip buttonClip;
    private static Clip powerOffClip;
    private static Clip congratulationsClip;
    private static Clip distractionClip;
    private static Clip cryClip;
    private static Clip screamClip;
    private static Clip correctClip;


    // Utility method to create a Clip and optionally loop it
    private static Clip playSoundAsync(String filePath, boolean loop) {
        try {
            AudioInputStream audioIn = null;

            // Try loading from classpath first
            InputStream resourceStream = AudioManager.class.getResourceAsStream("/" + filePath);
            if (resourceStream != null) {
                audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(resourceStream));
            } else {
                // Fallback to file system path
                File soundFile = new File(filePath);
                if (!soundFile.exists()) {
                    System.err.println("Error: File not found at " + filePath);
                    return null;
                }
                audioIn = AudioSystem.getAudioInputStream(soundFile);
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            // Start playback
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop indefinitely
            } else {
                clip.start(); // Play once
            }

            return clip; // Return the clip so it can be stopped later
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
            return null;
        }
    }

    // Play Heartbeat1 (normal heartbeat that loops)
    public static void playHeartbeat1() {
        // Start looping heartbeat sound and store its clip
        if (heartbeatClip1 == null || !heartbeatClip1.isRunning()) {
            heartbeatClip1 = playSoundAsync("rsc/JumpscareAssets/Heartbeat1.wav", true); // Looping
            System.out.println("Playing heartbeat1");
        }
    }

    // Play Heartbeat2 (normal heartbeat that loops)
    public static void playHeartbeat2() {
        // Start one-shot heartbeat sound and store its clip (no looping)
        if (heartbeatClip2 == null || !heartbeatClip2.isRunning()) {
            heartbeatClip2 = playSoundAsync("rsc/JumpscareAssets/Heartbeat2.wav", true); // Looping
            System.out.println("Playing heartbeat2");

        }
    }

    // Play Heartbeat2 (normal heartbeat that loops)
    public static void playHeartbeat3() {
        // Start one-shot heartbeat sound and store its clip (no looping)
        if (heartbeatClip3 == null || !heartbeatClip3.isRunning()) {
            heartbeatClip3 = playSoundAsync("rsc/JumpscareAssets/Heartbeat3.wav", true); // Looping
            System.out.println("Playing heartbeat3");

        }

    }

    // Stop Heartbeat1
    public static void stopHeartbeat1() {
        if (heartbeatClip1 != null && heartbeatClip1.isRunning()) {
            heartbeatClip1.stop();
            heartbeatClip1.close();
            heartbeatClip1 = null;
            System.out.println("Stoping heartbeat1");

        }
    }

    // Stop Heartbeat2
    public static void stopHeartbeat2() {
        if (heartbeatClip2 != null && heartbeatClip2.isRunning()) {
            heartbeatClip2.stop();
            heartbeatClip2.close();
            heartbeatClip2 = null;
            System.out.println("Stoping heartbeat2");
        }
    }

    // Stop Heartbeat3
    public static void stopHeartbeat3() {
        if (heartbeatClip3 != null && heartbeatClip3.isRunning()) {
            heartbeatClip3.stop();
            heartbeatClip3.close();
            heartbeatClip3 = null;
            System.out.println("Stoping heartbeat3");
        }
    }

    // Play the Riser sound (one-shot)
    public static void playRiser() {
        if (riserClip == null || !riserClip.isRunning()) {
            riserClip = playSoundAsync("rsc/JumpscareAssets/Riser1.wav", false); // No looping
        }
    }

    // Stop the Riser sound
    public static void stopRiser() {
        if (riserClip != null && riserClip.isRunning()) {
            riserClip.stop();
            riserClip.close();
            riserClip = null;
        }
    }

    public static void playError() {
        // Start looping error sound and store its clip
        if (errorClip == null || !errorClip.isRunning()) {
            errorClip = playSoundAsync("rsc/JumpscareAssets/error.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopError() {
        if (errorClip != null && errorClip.isRunning()) {
            errorClip.stop();
            errorClip.close();
            errorClip = null;
        }
    }

    public static void playDistraction() {
        // Start looping error sound and store its clip
        if (distractionClip == null || !distractionClip.isRunning()) {
            distractionClip = playSoundAsync("rsc/JumpscareAssets/distraction.wav", true); // Looping
        }
    }

    public static void playCorrect() {
        // Start looping error sound and store its clip
        if (correctClip == null || !correctClip.isRunning()) {
            correctClip = playSoundAsync("rsc/JumpscareAssets/Correct.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopCorrect() {
        if (correctClip != null && correctClip.isRunning()) {
            correctClip.stop();
            correctClip.close();
            correctClip = null;
        }
    }

    // Stop the error sound
    public static void stopDistraction() {
        if (distractionClip != null && distractionClip.isRunning()) {
            distractionClip.stop();
            distractionClip.close();
            distractionClip = null;
        }
    }

    public static void playButton() {
        // Start looping error sound and store its clip
        if (buttonClip == null || !buttonClip.isRunning()) {
            buttonClip = playSoundAsync("rsc/JumpscareAssets/click.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopButton() {
        if (buttonClip != null && buttonClip.isRunning()) {
            buttonClip.stop();
            buttonClip.close();
            buttonClip = null;
        }
    }

    public static void playPowerOff() {
        // Start looping error sound and store its clip
        if (powerOffClip == null || !powerOffClip.isRunning()) {
            powerOffClip = playSoundAsync("rsc/JumpscareAssets/PowerOff2.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopPowerOff() {
        if (powerOffClip != null && powerOffClip.isRunning()) {
            powerOffClip.stop();
            powerOffClip.close();
            powerOffClip = null;
        }
    }

    public static void playCry() {
        // Start looping error sound and store its clip
        if (cryClip == null || !cryClip.isRunning()) {
            cryClip = playSoundAsync("rsc/JumpscareAssets/cry.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopCry() {
        if (cryClip != null && cryClip.isRunning()) {
            cryClip.stop();
            cryClip.close();
            cryClip = null;
        }
    }

    public static void playDistraction2() {
        // Start looping error sound and store its clip
        if (screamClip == null || !screamClip.isRunning()) {
            screamClip = playSoundAsync("rsc/JumpscareAssets/MiniJumpscare.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopDistraction2() {
        if (screamClip != null && screamClip.isRunning()) {
            screamClip.stop();
            screamClip.close();
            screamClip = null;
        }
    }

    public static void playCongratulations() {
        // Start looping error sound and store its clip
        if (congratulationsClip == null || !congratulationsClip.isRunning()) {
            congratulationsClip = playSoundAsync("rsc/JumpscareAssets/Congratulations.wav", false); // Looping
        }
    }

    // Stop the error sound
    public static void stopCongratulations() {
        if (congratulationsClip != null && congratulationsClip.isRunning()) {
            congratulationsClip.stop();
            congratulationsClip.close();
            congratulationsClip = null;
        }
    }


    // Play BGM1
    public static void playBGM1() {
        // Start looping heartbeat sound and store its clip
        if (bgm1Clip == null || !bgm1Clip.isRunning()) {
            bgm1Clip = playSoundAsync("rsc/JumpscareAssets/BGM.wav", true); // Looping
        }
    }

    // Stop BGM1
    public static void stopBGM1() {
        if (bgm1Clip != null && bgm1Clip.isRunning()) {
            bgm1Clip.stop();
            bgm1Clip.close();
            bgm1Clip = null;
        }
    }

    // Play BGM2
    public static void playBGM2() {
        // Start looping heartbeat sound and store its clip
        if (bgm2Clip == null || !bgm2Clip.isRunning()) {
            bgm2Clip = playSoundAsync("rsc/JumpscareAssets/BGM2.wav", true); // Looping
        }
    }

    // Stop BGM2
    public static void stopBGM2() {
        if (bgm2Clip != null && bgm2Clip.isRunning()) {
            bgm2Clip.stop();
            bgm2Clip.close();
            bgm2Clip = null;
        }
    }



    // Stop all sounds
    public static void stopAllSounds() {
        stopHeartbeat1();
        stopHeartbeat2();
        stopHeartbeat3();
        stopRiser();
        stopError();
        stopBGM2();
        System.out.println("Stoping all sounds");
    }
}