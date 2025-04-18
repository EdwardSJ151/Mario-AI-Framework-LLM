import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import engine.core.MarioGame;
import engine.core.MarioResult;
import engine.helper.GameStatus; // Correct import for game status

public class PlayLevel {
    // Keep the original signature that includes the MarioGame object
    public static void printResults(MarioResult result, MarioGame game) { 
        System.out.println("****************************************************************");
        System.out.println("Game Status: " + result.getGameStatus().toString() +
                " Percentage Completion: " + result.getCompletionPercentage());
        System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() +
                " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        System.out.println("Mario State: " + result.getMarioMode() +
                " (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
        System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() +
                " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() +
                " Falls: " + result.getKillsByFall() + ")");
        System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() +
                " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
        
        // Print Mario's path using the game object
        if (game != null) { // Add null check just in case
             System.out.println("Mario Path:");
             for (int[] position : game.getMarioPath()) {
                 System.out.println("[" + position[0] + ", " + position[1] + "]");
             }
        }
        System.out.println("****************************************************************");
    }

    public static String getLevel(String filepath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            System.err.println("Error reading level file: " + filepath);
            // Return empty string on error, checked in main
        }
        return content;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java PlayLevel <level_file>");
            return;
        }

        String levelFile = args[0];
        String levelContent = getLevel(levelFile); // Load level content once

        if (levelContent == null || levelContent.isEmpty()) { // Check if level loading failed
             System.err.println("Level content could not be loaded or is empty. Exiting.");
             return;
        }

        MarioResult result = null;
        MarioGame game = null; // Declare game object outside the loop
        boolean restartOnLoss = false; // Set to true to enable restart, false to run only once

        do {
            game = new MarioGame(); // Create a fresh game instance for each attempt
            // System.out.println("Starting game...");
            try {
                // Use playGame to visualize, or runGame for headless with an agent
                // Pass the level *content* to playGame
                result = game.playGame(levelContent, 200, 0); 
                // Pass both result and game to printResults
                printResults(result, game); 

            } catch (Exception e) {
                System.err.println("Error running the game with level file: " + levelFile);
                e.printStackTrace();
                result = null; // Ensure we don't loop infinitely on error
                break; // Exit loop on error
            }

            // Check if we should restart: Use the correct GameStatus enum
            if (restartOnLoss && result != null && 
                (result.getGameStatus() == GameStatus.LOSE || result.getGameStatus() == GameStatus.TIME_OUT)) { 
                System.out.println("Mario died or timed out! Restarting level...");
            } else {
                break; // Exit loop if won, or if restart is disabled, or if result is null
            }

        } while (true); // Loop indefinitely until 'break' is hit

        System.out.println("Game finished.");
        if (result != null) {
             System.out.println("Final Status: " + result.getGameStatus());
        }
    }
}
