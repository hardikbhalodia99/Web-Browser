
package de.mrapp.android.tabswitcher.gesture;

import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.mrapp.android.tabswitcher.TabSwitcher;

/**
 * An abstract base class for all event handlers, which allow to handle drag gestures.
 *
 */
public abstract class AbstractDragGestureEventHandler extends AbstractTouchEventHandler {

    /**
     * The bounds of the onscreen area, the handler takes into consideration for handling
     * touch events.
     */
    private final RectF touchableArea;

    /**
     * Creates a new handler, which can be managed by a {@link TouchEventDispatcher} in order to
     * dispatch touch events to it.
     *
     * @param tabSwitcher
     *         The tab switcher, the event handler belongs to, as an instance of the class {@link
     *         TabSwitcher}. The tab switcher may not be null
     * @param dragThreshold
     *         The threshold of the drag helper, which is used to recognize drag gestures, in pixels
     *         as an {@link Integer} value The threshold must be at least 0
     * @param touchableArea
     *         The bounds of the onscreen area, the handler should take into consideration for
     *         handling touch events, as an instance of the class {@link RectF} or null, if the are
     *         should not be restricted
     */
    public AbstractDragGestureEventHandler(@NonNull final TabSwitcher tabSwitcher,
                                           final int dragThreshold,
                                           @Nullable final RectF touchableArea) {
        super(MAX_PRIORITY, tabSwitcher, dragThreshold);
        this.touchableArea = touchableArea;
    }

    @Nullable
    @Override
    public final RectF getTouchableArea() {
        return touchableArea;
    }

}