package com.zoi;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Downloader extends JPanel implements Runnable {

  private URL downloadURL;
  private InputStream inputS;
  private OutputStream outputS;
  private byte[] byteBuffer;

  private int fileSize;
  private int bytesRead;

  private JLabel urlLabel;
  private JLabel sizeLabel;
  private JLabel completeLabel;
  private JProgressBar progressBar;

  private Thread thread;

  public final static int BUFFER_SIZE = 1000;

  private boolean stopped;
  private boolean sleepScheduled;
  private boolean suspended;

  public final static int SLEEP_TIME = 5 * 1000;

  public Downloader(URL url, OutputStream os) throws IOException {
    downloadURL = url;
    outputS = os;
    bytesRead = 0;

    URLConnection connection = downloadURL.openConnection();
    fileSize = connection.getContentLength();

    if (fileSize == -1) {
      throw new FileNotFoundException(url.toString());
    }

    inputS = new BufferedInputStream(connection.getInputStream());

    byteBuffer = new byte[BUFFER_SIZE];
    thread = new Thread(this);
    buildLayout();

    stopped = false;
    sleepScheduled = false;
    suspended = false;
  }

  public void startDownload() {
    thread.start();
  }

  public synchronized void resumeDownload() {
    this.notify();
  }

  public void stopDownload() {
    thread.interrupt();
  }

  public synchronized void setStopped(boolean predicate) {
    stopped = predicate;
  }

  public synchronized boolean isStopped() {
    return stopped;
  }

  public synchronized void setSleepScheduled(boolean predicate) {
    sleepScheduled = predicate;
  }

  public synchronized boolean isSleepScheduled() {
    return sleepScheduled;
  }

  public synchronized void setSuspended(boolean predicate) {
    suspended = predicate;
  }

  public synchronized boolean isSuspended() {
    return suspended;
  }

  private void buildLayout() {
    JLabel label;
    setLayout(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();

    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(5, 10, 5, 10);

    constraints.gridx = 0;

    label = new JLabel("URL: ", JLabel.LEFT);
    add(label, constraints);

    label = new JLabel("Complete: ", JLabel.LEFT);
    add(label, constraints);

    label = new JLabel("Downloaded: ", JLabel.LEFT);
    add(label, constraints);

    constraints.gridx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.weightx = 1;

    urlLabel = new JLabel(downloadURL.toString());
    add(urlLabel, constraints);

    progressBar = new JProgressBar(0, fileSize);
    progressBar.setStringPainted(true);
    add(progressBar, constraints);

    constraints.gridwidth = 1;
    completeLabel = new JLabel(Integer.toString(bytesRead));
    add(completeLabel, constraints);

    constraints.gridx = 2;
    constraints.weightx = 0;
    constraints.anchor = GridBagConstraints.EAST;
    label = new JLabel("Size: ", JLabel.LEFT);
    add(label, constraints);

    constraints.gridx = 3;
    constraints.weightx = 1;
    sizeLabel = new JLabel(Integer.toString(fileSize));
    add(sizeLabel, constraints);
  }

  public void run() {
    performDownload();
  }

  public void performDownload() {
    int byteCount;
    Runnable progressDownload = new Runnable() {
      public void run() {
        progressBar.setValue(bytesRead);
        completeLabel.setText(Integer.toString(bytesRead));
      }
    };

    while((bytesRead < fileSize) && (!isStopped())) {
      try {
        if(isSleepScheduled()) {
          try {
            Thread.sleep(SLEEP_TIME);
            setSleepScheduled(false);
          } catch(InterruptedException ie) {
            setStopped(true);
            break;
          }
        }

        byteCount = inputS.read(byteBuffer);
        if (byteCount == -1) {
          setStopped(true);
          break;
        } else {
          outputS.write(byteBuffer, 0, byteCount);
          bytesRead += byteCount;
          SwingUtilities.invokeLater(progressDownload);
        }
      } catch (IOException ioe) {
        setStopped(true);
        JOptionPane.showMessageDialog(
          this,
          ioe.getMessage(),
          "I/O Error",
          JOptionPane.ERROR_MESSAGE
        );
        break;
      }
      synchronized(this) {
        if (isSuspended()) {
          try {
            this.wait();
            setSuspended(false);
          } catch(InterruptedException ie) {
            setStopped(true);
            break;
          }
        }
      }
      if(Thread.interrupted()) {
        setStopped(true);
        break;
      }
    }
    try {
      outputS.close();
      inputS.close();
    } catch(IOException ioe) {}
  }
}
