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
 * ThreadUtils.java
 * -----------------------------------------------------------------------------
 * This is a specialized java class designed to handle multithreading related
 * tasks.
 * <p>
 * This class is a part of the CoreFramework, and is essential for the
 * normal functions of this software.
 * <p>
 * This class should not be changed under any circumstances.
 * -----------------------------------------------------------------------------
 */

package main.java.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

    public static ExecutorService mouse_worker;     // thread for handling mouse movement calculations
    public static ExecutorService frost_worker;     // thread for handling blurring effect calculations
    public static ExecutorService position_worker;  // thread for handling the position of graphic objects

    static ExecutorService message_update_worker; // dedicated thread for handling logging events

    /**
     * Method that initiates message_update_worker;
     * must be initiated as soon as the program launches
     * in order to bring the logging system online.
     */
    public static void priorityInit() {

        /* allocate dedicated thread */
        message_update_worker = Executors.newSingleThreadExecutor();

    }

    /**
     * Method that initiates the remaining threads.
     */
    public static void init() {

        LogUtils.printCoreMessage("Initializing worker threads...");

        /* allocate shared cached thread pools */
        mouse_worker = Executors.newCachedThreadPool();
        LogUtils.printCoreMessage("Mouse worker initiated!");

        frost_worker = Executors.newCachedThreadPool();
        LogUtils.printCoreMessage("Frost worker initiated!");

        position_worker = Executors.newCachedThreadPool();
        LogUtils.printCoreMessage("Position worker initiated!");

        LogUtils.printCoreMessage("All worker threads had been successfully initiated!");

    }

}
