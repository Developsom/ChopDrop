import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;



@ScriptManifest(name = "ChopDrop", author = "BackToRS", version = 1.0, info = "", logo = "")
public class ChopDrop extends Script implements ActionListener {
    private final List<String> treeOptions = new ArrayList<>();
    private final List<String> logOptions = new ArrayList<>();
    private String selectedTree;
    private String selectedLog;

    @Override
    public void onStart() {
        setupGUI();
    }

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

    private void chopTree() throws InterruptedException {
        RS2Object tree = getObjects().closest(object -> object != null && object.getName().equals(selectedTree));
        if (tree != null && tree.isVisible()) {
            if (tree.interact("Chop down")) {
                // Wait for the player to start chopping
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < random(3000, 5000)) {
                    if (myPlayer().isAnimating()) {
                        break;
                    }
                    sleep(random(200, 500));
                }

                // Wait until the player stops chopping or the inventory is full
                startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < random(8000, 12000)) {
                    if (!myPlayer().isAnimating() || getInventory().isFull()) {
                        break;
                    }
                    sleep(random(200, 500));
                }
            }
        } else {
            Position targetPosition = tree != null ? tree.getPosition() : getWalking().getWebPathFinder().getNextTileOnWeb(getLocalPlayer().getPosition());
            if (targetPosition != null) {
                if (getMap().canReach(targetPosition)) {
                    getWalking().webWalk(targetPosition);
                } else {
                    log("Cannot reach the target position!");
                }
            } else {
                log("No reachable trees found!");
            }
        }
    }


    private void dropLogs() {
        Inventory inventory = getInventory();
        inventory.dropAll(item -> item != null && item.getName().equals(selectedLog));
    }

    private void setupGUI() {
        treeOptions.add("Tree");
        treeOptions.add("Oak");
        treeOptions.add("Willow");
        treeOptions.add("Teak");

        logOptions.add("Logs");
        logOptions.add("Oak logs");
        logOptions.add("Willow logs");
        logOptions.add("Teak logs");

        selectedTree = treeOptions.get(0);
        selectedLog = logOptions.get(0);

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("ChopDrop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.setLayout(new FlowLayout());

            JComboBox<String> treeSelector = new JComboBox<>(treeOptions.toArray(new String[0]));
            treeSelector.addActionListener(e -> selectedTree = (String) treeSelector.getSelectedItem());

            JComboBox<String> logSelector = new JComboBox<>(logOptions.toArray(new String[0]));
            logSelector.addActionListener(e -> selectedLog = (String) logSelector.getSelectedItem());

            JButton startButton = new JButton("Start");
            startButton.addActionListener(this);

            frame.add(treeSelector);
            frame.add(logSelector);
            frame.add(startButton);
            frame.setVisible(true);
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start")) {
            log("ChopDrop script started!");
            EventQueue.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                frame.dispose();
            });
        }
    }

    @Override
    public void onExit() {
        log("Thank you for using ChopDrop!");
    }
}

