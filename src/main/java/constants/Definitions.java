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
 * Definitions.java
 * -----------------------------------------------------------------------------
 * This file contains the enumerated type that defines whether a constant is
 * used for CoreLogic, CoreGraphics, and CoreFramework, or whether it is used
 * by the GUI elements.
 * <p>
 * This enum is a part of the CoreFramework, and is essential for the
 * normal functions of this software.
 * <p>
 * This class should not be changed under any circumstances.
 * -----------------------------------------------------------------------------
 */

package main.java.constants;

public enum Definitions {

    /* enumerated types containing their respective constant file paths */
    CORE_CONSTANTS("/constants/CoreDefinitions.constants"),
    UI_CONSTANTS("/constants/UIDefinitions.constants"),

    /* zero day patch */
    ZERO_DAY_PATCH("/constants/zero_day_patch/AddedDefinitions_1.constants"),
    ZERO_DAY_PATCH_SUPPLEMENTARY("/constants/zero_day_patch/AddedDefinitions_2.constants");

    private String path;    // path to definition file

    /**
     * Enum constructor
     * @param path  path to the definition file
     */
    Definitions(String path) {

        this.path = path;

    }

    /**
     * Method that returns the file path.
     * @return  string, file path
     */
    String getPath() {

        return path;

    }

}
