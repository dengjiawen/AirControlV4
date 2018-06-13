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
 * Constants.java
 * -----------------------------------------------------------------------------
 * This is a specialized java class designed to handle requests from classes
 * for constant values from XML files.
 * <p>
 * This class is a part of the CoreFramework, and is essential for the
 * normal functions of this software.
 * <p>
 * This class should not be changed under any circumstances.
 * -----------------------------------------------------------------------------
 * This is a legacy class ported from the TeslaUI project. Compatibility is
 * not guaranteed.
 * -----------------------------------------------------------------------------
 */

package main.java.constants;

import main.java.common.LogUtils;

import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {

    /**
     * A concurrent hashmap that keeps tabs on loaded constants
     * in order to avoid repeated parsing.
     */
    private static ConcurrentHashMap<String, Integer> integer_parameters = new ConcurrentHashMap<>();

    /**
     * Method that parses XML entries into integers.
     *
     * @param resource_name XML tag
     * @param type          variable type (Core/UI)
     * @return parsed integer
     */
    public static int getInt(String resource_name, Definitions type) {

        /* try finding the variable in the hashmap first */
        try {

            LogUtils.printCoreMessage("Requesting constant " + resource_name + "!");

            return integer_parameters.get(resource_name);
        } catch (NullPointerException e) {

            LogUtils.printErrorMessage(e.getMessage() + ", will try to handle.");

            LogUtils.printCoreMessage("Constant " + resource_name + " not found in " +
                    "existing constants, will try to load from configuration files.");

            try {
                integer_parameters.put(resource_name,
                        ParseUtils.parseInt(type.getPath(), resource_name));
            } catch (Exception f) {
                LogUtils.printErrorMessage(f.getMessage());
                missingResource();
            }

            LogUtils.printCoreMessage("Constant " + resource_name + " found in the " +
                    "configuration files!");

            LogUtils.printCoreMessage("Error handled successfully.");

            return getInt(resource_name, type);
        }
    }

    private static void missingResource() {

        JOptionPane.showMessageDialog(null, "Cannot find resource files!", "Missing Files", JOptionPane.ERROR_MESSAGE);
        System.exit(0);

    }

    /**
     * Method that parses XML entries into booleans.
     * (0 = false, 1 = true)
     *
     * @param resource_name XML tag
     * @param type          variable type (Core/UI)
     * @return  parsed boolean
     */
    public static boolean getBoolean(String resource_name, Definitions type) {

        LogUtils.printCoreMessage("Constant " + resource_name + " requested. Will try" +
                " to convert constant into boolean value.");

        return getInt(resource_name, type) == 1;

    }

}
