/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.util;

import aspectedit.Config;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author mark
 */
public class FileChooserBuilder {

    private static final String DIR_KEY = "DirectoryKey";
    
    public static FileChooserBuilder buildDialog() {
        return new FileChooserBuilder();
    }


    private JFileChooser chooser;

    private FileChooserBuilder() {
        chooser = new JFileChooser();

        chooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                    File dir = chooser.getCurrentDirectory();

                    String propKey = null;

                    Object key = chooser.getClientProperty(DIR_KEY);
                    if(key != null) {
                        propKey = key.toString();
                    }

                    if(propKey != null) {
                        Config.getInstance().setProperty(propKey, dir.getAbsolutePath());
                    }
                }

                chooser.removeActionListener(this);
            }

        });
    }

    public FileChooserBuilder filters(FileFilter ... filters) {

        for(FileFilter filter : filters) {
            chooser.addChoosableFileFilter(filter);
        }

        return this;
    }

    public FileChooserBuilder defaultFilter(FileFilter filter) {
        chooser.setFileFilter(filter);

        return this;
    }

    public FileChooserBuilder title(String title) {
        chooser.setDialogTitle(title);

        return this;
    }

    public FileChooserBuilder disableAcceptAllFilter() {
        chooser.setAcceptAllFileFilterUsed(false);

        return this;
    }

    public FileChooserBuilder withCurrentFolderKey(String folderKey) {
        chooser.putClientProperty(DIR_KEY, folderKey);
        
        String folder = Config.getInstance().getProperty(folderKey);
        if(folder == null) {
            //folder = mode == MODE_OPEN ? lastOpenPath : lastSavePath;
            folder = System.getProperty("user.home");
        }

        File dir = new File(folder);
        if(dir.exists()) {
            chooser.setCurrentDirectory(dir);
        }

        return this;
    }

    public JFileChooser build() {
        return chooser;
    }
}
