/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.util;

import aspectedit.frames.action.AsynchronousAction;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;

/**
 *
 * @author mark
 */
public class AsyncActionBuilder<T extends AsynchronousAction> {

    public static <T extends AsynchronousAction> AsyncActionBuilder<T> buildAction(Class<T> clazz) {
        return new AsyncActionBuilder<T>(clazz);
    }



    private Class<T> actionClass;
    private T action;

    private AsyncActionBuilder(Class<T> clazz) {
        this.actionClass = clazz;
        try {
            action = clazz.newInstance();

        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public AsyncActionBuilder<T> name(String text) {
        action.putValue(AsynchronousAction.NAME, text);
        return this;
    }

    public AsyncActionBuilder<T> icon(Icon icon) {
        action.putValue(AsynchronousAction.SMALL_ICON, icon);
        return this;
    }

    public AsyncActionBuilder<T> withPropertyListeners(PropertyChangeListener ... listeners) {
        for(PropertyChangeListener l : listeners) {
            action.addPropertyChangeListener(l);
        }

        return this;
    }

    public T build() {
        return action;
    }
}
