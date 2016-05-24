package com.zoi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ButtonPress extends JFrame {
  public ButtonPress() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Execute");
    menuBar.add(menu);

    JMenuItem item = new JMenuItem("DB Query");
    menu.add(item);

    item.addActionListener(event -> {
      new Thread(() -> performDatabaseQuery()).start();
    });

    setJMenuBar(menuBar);
  }

  private Object performDatabaseQuery() {
    try {
      Thread.sleep(5000);
    } catch(Exception e) {};

    return null;
  }
}
