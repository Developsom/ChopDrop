import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

@ScriptManifest(name = "Woodcutter", author = "Your Name", version = 1.0, info = "", logo = "")
public class WoodcutterScript extends Script implements ActionListener {
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

    private void chopTree() {
        RS2Object tree = getObjects().closest(object -> object != null && object.getName().equals(selectedTree));
        if (tree != null && tree.isVisible()) {
            if (tree.interact("Chop down")) {
                MethodProvider.sleepUntil(() -> myPlayer().isAnimating(), random(3000, 5000));
            }
        } else {
            // Walk to the nearest tree
            getWalking().webWalk(tree.getPosition());
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
            JFrame frame = new JFrame("Woodcutter");
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
            log("Woodcutter script started!");
            EventQueue.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                frame.dispose();
            });
        }
    }

    @Override
    public void onExit() {
        log("Thank you for using WoodcutterScript!");
    }
}

