import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class MusicPlayerGUI {
    private JFrame frame;
    private DefaultListModel<String> songListModel;
    private JList<String> songList;
    private JTextField titleField, pathField;
    private JButton addButton, playButton, stopButton, pauseButton, deleteButton ;
    private ArrayList<Song> songs;
    private Clip clip;

    public MusicPlayerGUI() {
        songs = new ArrayList<>();

        // Initialize components
        frame = new JFrame("Music Player");
        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        titleField = new JTextField(50);
        pathField = new JTextField(50);
        addButton = new JButton("Add Song");
        playButton = new JButton("Play Song");
        pauseButton = new JButton("Pause song");
        stopButton = new JButton("Stop Song");
        deleteButton = new JButton("Delete Song");

        // Add KeyListeners for Enter key navigation
        titleField.addActionListener(e -> pathField.requestFocus());
        pathField.addActionListener(e -> addSong());

        // Set up layout
        frame.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Song Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("File Path:"));
        inputPanel.add(pathField);
        inputPanel.add(addButton);

        JPanel controlPanel = new JPanel();
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(pauseButton);
        controlPanel.add(deleteButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(songList), BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Add listeners
        addButton.addActionListener(e -> addSong());
        playButton.addActionListener(e -> playSong());
        pauseButton.addActionListener(e -> pauseSong());
        stopButton.addActionListener(e -> stopSong());
        deleteButton.addActionListener(e -> deleteSong());

        // Finalize frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 350);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLayout(null);
    }

    private void addSong() {
        String title = titleField.getText().trim();
        String path = pathField.getText().trim();

        if (title.isEmpty() || path.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Both fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "File does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        songs.add(new Song(title, path));
        songListModel.addElement(title);
        titleField.setText("");
        pathField.setText("");
        JOptionPane.showMessageDialog(frame, "Song added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void playSong() {
        int selectedIndex = songList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a song to play!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Song song = songs.get(selectedIndex);

        try {
            if (clip != null && pausePosition > 0) {
                // Resume from the paused position
                clip.setMicrosecondPosition(pausePosition);
                clip.start();
                JOptionPane.showMessageDialog(frame, "Resuming: " + song.getTitle(), "Resuming", JOptionPane.INFORMATION_MESSAGE);
            } else {
                stopSong(); // Stop any currently playing song before starting a new one
                File audioFile = new File(song.getPath());
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                pausePosition = 0; // Reset pause position when starting new playback
                JOptionPane.showMessageDialog(frame, "Playing: " + song.getTitle(), "Playing", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error playing the audio file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopSong() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
            clip = null;
            pausePosition = 0; // Reset pause position when stopping playback
        }
    }
    private long pausePosition = 0; // To store the paused position

    private void pauseSong() {
        if (clip != null && clip.isRunning()) {
            pausePosition = clip.getMicrosecondPosition(); // Save current position
            clip.stop(); // Stop playback
            JOptionPane.showMessageDialog(frame, "Playback paused.", "Paused", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No song is currently playing to pause.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteSong() {
        int selectedIndex = songList.getSelectedIndex();
        if (selectedIndex != -1) {
            songs.remove(selectedIndex); // Remove from the ArrayList
            songListModel.remove(selectedIndex); // Remove from the JList model
            JOptionPane.showMessageDialog(frame, "Song deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(frame, "Please select a song to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}