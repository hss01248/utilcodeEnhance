/*
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.hss01248.glidebase.drawable;

import android.graphics.drawable.Drawable;

/**
 * A drawable parent that has a single child.
 */
public interface DrawableParent {

    /**
     * Sets the new child drawable.
     *
     * @param newDrawable a new child drawable to set
     * @return the old child drawable
     */
    Drawable setDrawable(Drawable newDrawable);

    /**
     * Gets the child drawable.
     *
     * @return the current child drawable
     */
    Drawable getDrawable();

}
