/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blazartech.util.multithreadlog4j;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This appender will manage multiple multiple instances of a FileAppender,
 * each with the thread name in the file name, and choose log a message to the 
 * one appropriate to the calling thread.
 *
 * The appender supports the following properties:
 * <ul>
 *   <li><pre>subAppender</pre> the underlying appender implementation, which must
 * be derived from FileAppender.
 *   <li><pre>fileBase</pre> the base for the log file names. The thread name will be
 * suffixed.
 *   <li><pre>datePattern</pre> if the implementation is a DailyRollingFileAppender, this
 * provides the data pattern to be used in the log file names and rollover.
 *   <li><pre>maxBackupIndex</pre> if the implementation is a RollingFileAppender, this
 * provides the maximum number of backup files to store.
 *   <li><pre>maxFileSize</pre> if the implementation is a RollingFileAppender, this
 * provides the maximum file size for each log
 * </ul>
 *
 * @author Dr. Scott E. Aaron
 * @version $Id$
 */

/* $Log$
 *******************************************************************************/

public class MultiThreadFileAppender extends AppenderSkeleton {
    
    private String fileBase;
    private String appenderImplementationClass;
    private String datePattern;
    
    private static final ThreadLocal<FileAppender> threadAppender = new ThreadLocal<FileAppender>();
    
    /**
     * Creates a new instance of MultiThreadFileAppender
     */
    public MultiThreadFileAppender() {
    }
    
    /**
     * Configurators call this method to determine if the appender requires a layout. If this method returns true, meaning that layout is required, then the configurator will configure an layout using the configuration information at its disposal. If this method returns false, meaning that a layout is not required, then layout configuration will be skipped even if there is available layout configuration information at the disposal of the configurator..
     * 
     * In the rather exceptional case, where the appender implementation admits a layout but can also work without it, then the appender should return true.
     * @return is a layout needed?
     */
    @Override
    public boolean requiresLayout() {
        return true;
    }
    
    /**
     * Get the appender implementation.
     * @return implementation
     */
    protected Appender getAppenderImplementation() {
        FileAppender a = threadAppender.get();
        if (a == null) {
            try {
                a = (FileAppender) Class.forName(getAppenderImplementationClass()).newInstance();
                a.setLayout(getLayout());
                a.setFile(getFileBase() + "-" + Thread.currentThread().getName() + ".log");
                if (a instanceof DailyRollingFileAppender) {
                    ((DailyRollingFileAppender) a).setDatePattern(getDatePattern());
                }
                if (a instanceof RollingFileAppender) {
                    RollingFileAppender rfa = (RollingFileAppender) a;
                    rfa.setMaxFileSize(getMaxFileSize());
                    rfa.setMaxBackupIndex(getMaxBackupIndex());
                }
                a.activateOptions();  // open the file
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("error instantiating appender: " + ex.getMessage(), ex);
            }
            threadAppender.set(a);
        }
        return a;
    }
    
    /**
     * Release any resources allocated within the appender such as file handles, network connections, etc.
     */
    @Override
    public void close() {
        Appender a = getAppenderImplementation();
        a.close();
    }
    
    /**
     * Subclasses of AppenderSkeleton should implement this method to perform actual logging.
     * @param loggingEvent the logging event
     */
    @Override
    protected void append(LoggingEvent loggingEvent) {
        Appender a = getAppenderImplementation();
        a.doAppend(loggingEvent);
    }
    
    /**
     * get the value of the fileBase property
     * @return value of fileBase
     */
    public String getFileBase() {
        return fileBase;
    }
    
    /**
     * 
     * @param fileBase 
     */
    public void setFileBase(String fileBase) {
        this.fileBase = fileBase;
    }
    
    /**
     * 
     * @return 
     */
    public String getAppenderImplementationClass() {
        return appenderImplementationClass;
    }
    
    /**
     * 
     * @param appenderImplementationClass
     */
    public void setAppenderImplementationClass(String appenderImplementationClass) {
        this.appenderImplementationClass = appenderImplementationClass;
    }
    
    /**
     * 
     * @return 
     */
    public String getDatePattern() {
        return datePattern;
    }
    
    /**
     * 
     * @param datePattern 
     */
    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    /**
     * Holds value of property maxFileSize.
     */
    private String maxFileSize;

    /**
     * Getter for property maxFileSize.
     * @return Value of property maxFileSize.
     */
    public String getMaxFileSize() {
        return this.maxFileSize;
    }

    /**
     * Setter for property maxFileSize.
     * @param maxFileSize New value of property maxFileSize.
     */
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * Holds value of property maxBackupIndex.
     */
    private int maxBackupIndex;

    /**
     * Getter for property maxBackupIndex.
     * @return Value of property maxBackupIndex.
     */
    public int getMaxBackupIndex() {
        return this.maxBackupIndex;
    }

    /**
     * Setter for property maxBackupIndex.
     * @param maxBackupIndex New value of property maxBackupIndex.
     */
    public void setMaxBackupIndex(int maxBackupIndex) {
        this.maxBackupIndex = maxBackupIndex;
    }
}
