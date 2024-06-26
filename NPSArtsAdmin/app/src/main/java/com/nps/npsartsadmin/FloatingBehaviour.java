package com.nps.npsartsadmin;
import android.content.Context;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
    public class FloatingBehaviour extends FloatingActionButton.Behavior {

        public FloatingBehaviour(Context context, AttributeSet attrs) {
            super();
        }

        @Override
        public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                           final FloatingActionButton child,
                                           final View directTargetChild, final View target,
                                           final int nestedScrollAxes) {
            // Ensure we react to vertical scrolling
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                    || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                                   View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                                   int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                    dyUnconsumed);

            if (dyConsumed > 0
                    && child.getVisibility() == View.VISIBLE) {
                child.hide();
            } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
                    if (coordinatorLayout.getChildAt(i) instanceof Snackbar.SnackbarLayout) {
                        child.show();
                        return;
                    }
                }

                child.setTranslationY(0.0f);
                child.show();
            }
        }
    }

