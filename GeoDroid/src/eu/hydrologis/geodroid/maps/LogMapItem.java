package eu.hydrologis.geodroid.maps;

import java.io.Serializable;

/**
 * Item representing a gps log.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LogMapItem extends MapItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String startTime = " - "; //$NON-NLS-1$
    private String endTime = " - "; //$NON-NLS-1$

    public LogMapItem( long id, String text, String color, float width, boolean isVisible, String startTime, String endTime ) {
        super(id, text, color, width, isVisible);
        if (startTime != null)
            this.startTime = startTime;
        if (endTime != null)
            this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime( String startTime ) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime( String endTime ) {
        this.endTime = endTime;
    }

}
