@file:Suppress("DEPRECATION")

package com.google.albertasights

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build


fun Resources.returnDrawable(drawableId: Int, theme: Resources.Theme?): Drawable {
    return if (Build.VERSION.SDK_INT >= 22) {
        this.getDrawable(drawableId, theme)
    } else {
        this.getDrawable(drawableId)
    }
}