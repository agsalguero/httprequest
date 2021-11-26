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
public interface ILogger {
    public enum Level {DEBUG, INFO, WARNING, ERROR};
    
    public void log(String message);
    public void log(String message, Level level);
    
    public void setLevel(Level level);
    public Level getLevel();
}
