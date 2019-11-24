package android.support.v7.internal.view.menu;

import android.view.Menu;

/**
 * Hack to allow inflating ActionMode menus on Android 4.0.x with AppCompat v21
 */
public class MenuUnwrapper {

    public static Menu unwrap(Menu menu) {
        if (menu instanceof MenuWrapperICS) {
            return ((MenuWrapperICS) menu).getWrappedObject();
        }
        return menu;
    }
}