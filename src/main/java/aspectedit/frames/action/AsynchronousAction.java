
package aspectedit.frames.action;

import aspectedit.frames.AspectEdit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public abstract class AsynchronousAction<T extends SwingWorker> extends AbstractAction {

    private Class<T> workerClass;
    private PropertyChangeListener workerListener;

    private T workerInstance;


    public AsynchronousAction(Class<T> workerClass, String name, Icon icon) {

        super(name, icon);

        this.workerClass = workerClass;
        
        workerListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                workerPropertyChanged(evt);
            }
        };
    }


    private void workerPropertyChanged(PropertyChangeEvent evt) {
        if("state".equals(evt.getPropertyName()) && workerInstance.isDone()) {
            doAfterAction(workerInstance);

            workerInstance.removePropertyChangeListener(workerListener);
        }
    }


    @Override
    public final void actionPerformed(ActionEvent e) {
        try {
            workerInstance = workerClass.newInstance();

            workerInstance.addPropertyChangeListener(workerListener);

            doAction(workerInstance);

        } catch (InstantiationException ex) {
            throw new RuntimeException("Worker class does not have a default constructor!");

        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Cannot access woker class' default constructor.");
        }
    }


    /**
     * A utility message that implementations may use to display an error message.
     * 
     * @param msg The error message
     */
    protected void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(
                null,
                msg,
                AspectEdit.APP_NAME,
                JOptionPane.ERROR_MESSAGE);
    }


    /**
     * Called as a result of the actionPerformed method. Implementations are
     * responsible for calling the worker's execute method if the action is to
     * take place.
     *
     * @param worker The SwingWorker.
     */
    protected abstract void doAction(T worker);

    /**
     * Called after the worker has finished executing.
     *
     * @param worker The SwingWorker.
     */
    protected abstract void doAfterAction(T worker);

}
