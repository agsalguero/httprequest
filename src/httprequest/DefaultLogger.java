/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package httprequest;

/**
 *
 * @author Albaerto
 */
public class DefaultLogger implements ILogger {
    Level currLevel = Level.ERROR;
    
    protected boolean isOutputEnabledFor(Level level) {
        /*return (currLevel == Level.ERROR && level == Level.ERROR) ||
           (currLevel == Level.WARNING && (level == Level.ERROR || level == Level.WARNING)) ||
           (currLevel == Level.INFO);*/
        switch(currLevel) {
            case DEBUG:         // all messages
                return true;
            case INFO:          // all messages but debugging
                if(level == Level.DEBUG){ return false; }
                return true;
            case WARNING:       // all warnings or errors
                if(level == Level.INFO || level == Level.DEBUG){ return false; }
                return true;
            case ERROR:
                if(level == Level.INFO || level == Level.DEBUG || level == Level.WARNING){ return false; }
                return true;
        }
        return false;
    }

    @Override
    public void log(String message) {
        System.out.println(message);
    }

    @Override
    public void log(String message, Level level) {
        if(isOutputEnabledFor(level)) {
            System.out.println(message);
        }
    }

    @Override
    public void setLevel(Level level) {
        currLevel = level;
    }

    @Override
    public Level getLevel() {
        return currLevel;
    }
    
}
