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
 * LogUtils.java
 * -----------------------------------------------------------------------------
 * This is a specialized java class designed to output debug information onto
 * the console, as well as writing log to file via FileIO.
 * <p>
 * This class is a part of the CoreFramework, and is essential for the
 * normal functions of this software.
 * -----------------------------------------------------------------------------
 */

package main.java.common;

import main.java.constants.Constants;
import main.java.constants.Definitions;

import javax.swing.Timer;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogUtils {

    /* the messages will be buffered for one second before being updated to preserve system resources */
    private static String log_directory;            // log storage directory
    private static Queue<String> message_buffer;    // queue of buffered messages
    private static Timer message_update_daemon;     // background daemon that updates messages

    /* controller booleans */
    private static boolean show_debug_messages;
    private static boolean show_general_messages;
    private static boolean show_error_messages;
    private static boolean show_repaint_messages;

    /* message update interval (ms) */
    private static int update_intervals;

    /**
     * Initializer method
     */
    public static void init() {

        /* reserve thread for logging functions */
        ThreadUtils.priorityInit();

        /* initialize logging directory */
        log_directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/AirControlV3/logs/";
        /* initialize logging buffer */
        message_buffer = new ConcurrentLinkedQueue<>();

        printCoreMessage("Message queue initialized at " + getDate());

        /* initialize controller variables */
        update_intervals = Constants.getInt("updateIntervals", Definitions.CORE_CONSTANTS);
        show_debug_messages = Constants.getBoolean("showDebugMessages", Definitions.CORE_CONSTANTS);
        show_general_messages = Constants.getBoolean("showGeneralMessages", Definitions.CORE_CONSTANTS);
        show_error_messages = Constants.getBoolean("showErrorMessages", Definitions.CORE_CONSTANTS);
        show_repaint_messages = Constants.getBoolean("showRepaintMessages", Definitions.CORE_CONSTANTS);

        /* initialize logging daemon */
        message_update_daemon = new Timer(update_intervals, new UpdateMessageEvent());
        message_update_daemon.start();

        /* end of initialization */
        printCoreMessage("Logging system successfully initialized as of " + getDate() + "!");

    }

    /**
     * Method that submits incoming messages to
     * queue.
     * @param message
     */
    private static void submitMessageToQueue(String message) {

        /* offers message to queue */
        message_buffer.offer(message);

    }

    /**
     * Method that prints all incoming messages from
     * the CoreFramework methods, directly from the
     * AWT EDT.
     * @param message
     */
    public static void printCoreMessage(String message) {
        submitMessageToQueue(getDate() + " -> |" + getCallerClass() + "| " + " CoreFramework: " + message);
    }

    /**
     * Prints a generic message to console and log,
     * including time and the originating class name.
     * @param message
     */
    public static void printGeneralMessage(String message) {
        ThreadUtils.message_update_worker.submit(() -> {
            if (show_general_messages)
                submitMessageToQueue(getDate() + " -> |" + getCallerClass() + "| " + message);
        });
    }

    /**
     * Prints a message to console and log with the DEBUG tag,
     * including time and the originating class name.
     * @param message
     */
    public static void printDebugMessage(String message) {
        ThreadUtils.message_update_worker.submit(() -> {
            if (show_debug_messages)
                submitMessageToQueue(getDate() + " -> |" + getCallerClass() + "| " + " DEBUG: " + message);
        });
    }

    /**
     * Prints a message to console with the ERROR tag,
     * including time and the originating class name.
     * @param message
     */
    public static void printErrorMessage(String message) {
        ThreadUtils.message_update_worker.submit(() -> {
            if (show_error_messages)
                submitMessageToQueue(getDate() + " -> |" + getCallerClass() + "| " + " ERROR: " + message);
        });
    }

    /**
     * Prints a message for every repaint event.
     */
    public static void printRepaintMessage() {
        ThreadUtils.message_update_worker.submit(() -> {
            if (show_repaint_messages)
                submitMessageToQueue(getDate() + " -> |" + getCallerClass() + "|" + " REPAINT EVENT: Invoked successfully.");
        });
    }

    /**
     * Prints a message for repaint related events.
     */
    public static void printRepaintMessage(String message) {
        ThreadUtils.message_update_worker.submit(() -> {
            if (show_repaint_messages)
                submitMessageToQueue(getDate() + " -> |" + getCallerClass() + "|" + " CoreGraphics: " + message);
        });
    }


    /**
     * Prints a generic message to console and log, including time.
     * DEPRECATED CODE | USE printGeneralMessage()
     * @param message
     */
    @Deprecated
    public static void print(String message) {
        ThreadUtils.message_update_worker.submit(() -> submitMessageToQueue(getDate() + " -> " + message +
                "\nLogUtils.print() had been deprecated. Use Console.printGeneralMessage() instead."));
    }

    /**
     * Class that returns the class name from which the
     * printMessage() method is called.
     * Reduces dependency on the deprecated sun.internal.Reflect
     * package.
     * @return name of caller class
     */
    private static String getCallerClass() {

        /* create a stack trace from the current thread */
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

        /* run through the trace to find class names that do not equal LogUtils (this class) */
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogUtils.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {

                /* if class is called from a concurrent thread, handle messages from the LogUtils class */
                if (ste.getClassName().contains("concurrent")) {
                    return LogUtils.class.getName();
                }

                return ste.getClassName();
            }
        }
        return null;
    }

    /**
     * Gets the date from system for console output.
     * @return string, current time
     */
    private static String getDate() {

        /* return a formatted Date object */
        DateFormat date_format = new SimpleDateFormat("HH:mm:ss:SSS");
        Date date_object = new Date();

        return date_format.format(date_object);
    }

    /**
     * Custom ActionListener class; empties the message queue,
     * prints the queue contents to console and to log file.
     */
    private static class UpdateMessageEvent implements ActionListener {

        /**
         * Overriden actionPerformed() method.
         * @param e (ActionEvent object)
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            /* submit updateMessages() method to message updater thread,
               ignoring any errors generated. */
            ThreadUtils.message_update_worker.submit(() -> {
                try {
                    updateMessages();
                } catch (Exception f) {
                    printErrorMessage(f.getMessage());
                }
            });

        }

        /**
         * Method that empties out the message queue.
         * @throws Exception    throw all exceptions to parent method for handling
         */
        private void updateMessages()
                throws Exception {

            try {
                /* create path object and check directory integrity */
                Path dir_path = java.nio.file.Paths.get(log_directory);
                Files.createDirectories(dir_path);
            } catch (Exception e) {
                printErrorMessage(e.getMessage());
                printErrorMessage("Cannot write to target logging directory. This log session will not be saved.");
            }

            /* initialize PrintWriter for FileIO */
            PrintWriter writer = null;

            try {
                writer = new PrintWriter(new FileOutputStream(log_directory + "log_" + getDate() + ".AirControlLog", true));
            } catch (Exception e) {
                printErrorMessage(e.getMessage());
                printErrorMessage("Cannot write to target logging directory. This log session will not be saved.");
            }

            /* String buffer to hold queued messages */
            String buffered_message;
            buffered_message = message_buffer.poll();

            /* write message to console, via err stream if message is error message */
            while (buffered_message != null) {
                if (buffered_message.toLowerCase().contains("error") && !buffered_message.contains("showErrorMessages")) {
                    System.err.println(buffered_message);
                } else {
                    System.out.println(buffered_message);
                }

                /* write message to log file */
                if (writer != null)
                writer.println(buffered_message);

                /* poll next message from queue */
                buffered_message = message_buffer.poll();
            }

            /* flush and close writer */
            writer.flush();
            writer.close();

        }

        /**
         * Method that returns the date format for log files.
         * @return  string, current time
         */
        private static String getDate() {
            DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH");
            Date date_object = new Date();

            return date_format.format(date_object);
        }

    }

}
