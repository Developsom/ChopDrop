import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(name = "ChopDrop", author = "BackToRS", version = 1.0, info = "Chop and drop", logo = "")
public class ChopDrop extends Script {
    private final int TREE_ID = 12345; // Replace with the actual tree object ID

    private final Position[] treePositions = {
            new Position(x1, y1, z1), // Add the positions of the trees you want to chop
            new Position(x2, y2, z2),
            // Add more positions as needed
    };

    private boolean dropLogs = false;

    @Override
    public int onLoop() throws InterruptedException {
        if (getInventory().isFull()) {
            dropLogs();
        } else if (myPlayer().isAnimating()) {
            sleep(random(500, 1000));
        } else {
            chopTree();
        }
        return random(200, 300); // Adjust the delay between actions if needed
    }

    private void chopTree() {
        RS2Object tree = getObjects().closest(TREE_ID);
        if (tree != null && tree.isVisible()) {
            if (tree.interact("Chop down")) {
                MethodProvider.sleepUntil(() -> myPlayer().isAnimating(), random(3000, 5000));
            }
        } else {
            // Walk to the nearest tree
            getWalking().webWalk(treePositions);
        }
    }

    private void dropLogs() {
        getInventory().dropAll(log -> log != null && log.getName().toLowerCase().contains("log"));
    }

    @Override
    public void onExit() {
        log("Thank you for using ChopDrop!");
    }
}
