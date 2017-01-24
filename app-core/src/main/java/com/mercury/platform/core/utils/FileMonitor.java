package com.mercury.platform.core.utils;


import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * Exslims
 * 08.12.2016
 */
public class FileMonitor {
    private static final long POLLING_INTERVAL = 100;
    private MessageFileHandler fileHandler;
    public FileMonitor(String gamePath) {
        File folder = new File(gamePath+"logs");
        fileHandler = new MessageFileHandler(gamePath + "logs/Client.txt");

        FileAlterationObserver observer = new FileAlterationObserver(folder);
        FileAlterationMonitor monitor = new FileAlterationMonitor(POLLING_INTERVAL);
        FileAlterationListener listener = new FileAlterationListenerAdaptor(){
            @Override
            public void onFileChange(File file) {
                if (file.getAbsolutePath().contains("Client.txt")) {
                    fileHandler.parse();
                }
            }
        };

        observer.addListener(listener);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
