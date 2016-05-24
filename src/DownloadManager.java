import java.awt.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;

import com.zoi.Downloader;

public class DownloadManager extends JPanel {

  private Downloader downloader;

  private JButton startButton;
  private JButton sleepButton;
  private JButton suspendButton;
  private JButton resumeButton;
  private JButton stopButton;
  private JButton closeButton;

  public static void main(String[] args) throws Exception {
    URL url = new URL(args[0]);
    FileOutputStream fos = new FileOutputStream(args[1]);

    JFrame frame = new JFrame();

    DownloadManager dm = new DownloadManager(url, fos);

    frame.getContentPane().add(dm);
    frame.setSize(800, 600);
    frame.setVisible(true);
  }

  public DownloadManager(URL url, OutputStream os) throws IOException {
    downloader = new Downloader(url, os);
    buildLayout();

    Border border = new BevelBorder(BevelBorder.RAISED);
    String name = url.toString();
    int index = name.indexOf('/');
    border = new TitledBorder(border, name.substring(index + 1));
    setBorder(border);
  }

  private void buildLayout() {
    setLayout(new BorderLayout());
    downloader.setBorder(new BevelBorder(BevelBorder.RAISED));
    add(downloader, BorderLayout.CENTER);

    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel getButtonPanel() {
    JPanel outerPanel;
    JPanel innerPanel;

    innerPanel = new JPanel();
    innerPanel.setLayout(new GridLayout(1, 5, 10, 0));

    startButton = new JButton("Start");
    startButton.addActionListener(evt -> {
      downloader.startDownload();
      startButton.setEnabled(false);
      sleepButton.setEnabled(true);
      suspendButton.setEnabled(true);
      resumeButton.setEnabled(false);
      stopButton.setEnabled(true);
    });

    sleepButton = new JButton("Sleep");
    sleepButton.addActionListener(evt -> {
      downloader.setSleepScheduled(true);
    });

    suspendButton = new JButton("Suspend");
    suspendButton.addActionListener(evt -> {
      downloader.setSuspended(true);
      suspendButton.setEnabled(false);
      sleepButton.setEnabled(false);
      resumeButton.setEnabled(true);
    });

    resumeButton = new JButton("Resume");
    resumeButton.addActionListener(evt -> {
      downloader.resumeDownload();
      suspendButton.setEnabled(true);
      sleepButton.setEnabled(true);
      resumeButton.setEnabled(false);
    });

    stopButton = new JButton("Stop");
    stopButton.addActionListener(evt -> {
      downloader.stopDownload();
      startButton.setEnabled(true);
      sleepButton.setEnabled(false);
      suspendButton.setEnabled(false);
      resumeButton.setEnabled(false);
      stopButton.setEnabled(false);
    });

    closeButton = new JButton("Close");
    closeButton.addActionListener(evt -> System.exit(0));

    startButton.setEnabled(true);
    sleepButton.setEnabled(false);
    suspendButton.setEnabled(false);
    resumeButton.setEnabled(false);
    stopButton.setEnabled(false);

    innerPanel.add(startButton);
    innerPanel.add(sleepButton);
    innerPanel.add(suspendButton);
    innerPanel.add(resumeButton);
    innerPanel.add(stopButton);
    innerPanel.add(closeButton);

    outerPanel = new JPanel();
    outerPanel.add(innerPanel);
    return outerPanel;
  }
}
