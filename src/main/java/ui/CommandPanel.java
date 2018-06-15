/**
 * Copyright 2018 (C) Jiawen Deng. All rights reserved.
 * <p>
 * This document is the property of Jiawen Deng.
 * It is considered confidential and proprietary.
 * <p>
 * This document may not be reproduced or transmitted in any form,
 * in whole or in part, without the express written permission of
 * Jiawen Deng.
 * <p>
 * -----------------------------------------------------------------------------
 * CommandPanel.java
 * -----------------------------------------------------------------------------
 * This panel allows the user to input their commands, and send that command
 * to CommandUtils for processing and execution.
 * -----------------------------------------------------------------------------
 */

package main.java.ui;

import main.java.common.LogUtils;
import main.java.logic.CommandUtils;
import main.java.resources.FontResource;
import main.java.speech.SpeechUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class CommandPanel extends FrostedPane {

    private JTextField command_box; // TextField for entering commands
    private JButton submit;         // Button for submitting commands

    CommandPanel() {
        super(25, 25, 500, 100, "COMMAND PANEL");

        LogUtils.printGeneralMessage("Initializing CommandPanel " + this + "!");

        /* initialize instance variables */
        command_box = new JTextField();
        command_box.setBounds(10, 40, 300, 20);
        command_box.getCaret().setBlinkRate(0);
        command_box.setForeground(Color.gray);
        command_box.setFont(FontResource.command_hint);
        command_box.setText("Waiting for Commands...");

        submit = new JButton();
        submit.setBounds(350, 40, 40, 20);
        submit.setText("SEND");
        submit.addActionListener(e -> {
            CommandUtils.TEST_CODE(command_box.getText());
            submit.grabFocus();
        });

        /* reset command box when focus is lost */
        command_box.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                resetCommandBox(command_box, false);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                resetCommandBox(command_box, true);
            }
        });

        add(command_box);
        add(submit);

        LogUtils.printGeneralMessage("Linking CommandPanel " + this + " to CommandUtils.");
        /* link SpeechUtils to CommandPanel */
        SpeechUtils.command_display = command_box;

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * Method that resets the CommandBox to its default state.
     * @param command_box
     * @param doReset   whether to reset box or prepare it for text
     */
    public static void resetCommandBox(JTextField command_box, boolean doReset) {

        if (doReset) {

            /* set box text to grey and command_hint font style */
            if (command_box.getText().replaceAll("\\s", "").equals("")) {
                command_box.setForeground(Color.gray);
                command_box.setFont(FontResource.command_hint);
                command_box.setText("Waiting for Commands...");
            }
        } else {

            /* set box text to black and command_content font style */
            if (command_box.getFont() == FontResource.command_hint) {
                command_box.setForeground(Color.black);
                command_box.setFont(FontResource.command_content);
                command_box.setText("");
            }
        }

    }

}
