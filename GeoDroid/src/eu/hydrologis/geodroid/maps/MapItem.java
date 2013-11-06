package eu.hydrologis.geodroid.maps;

import java.io.Serializable;

/**
 * Item representing a map entry.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public class MapItem implements Serializable {

    private static final long serialVersionUID = 1L;

    protected boolean isDirty = false;
    protected String name;
    protected long id;
    protected float width;
    protected String color;
    protected boolean isVisible;
    protected int type;

    public MapItem( long id, String text, String color, float width, boolean isVisible ) {
        this.id = id;
        name = text;
        this.color = color;
        this.width = width;
        this.isVisible = isVisible;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty( boolean isDirty ) {
        this.isDirty = isDirty;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth( float width ) {
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor( String color ) {
        this.color = color;
    }

    public void setVisible( boolean isVisible ) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setType( int type ) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (isDirty ? 1231 : 1237);
        result = prime * result + (isVisible ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + type;
        result = prime * result + Float.floatToIntBits(width);
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MapItem other = (MapItem) obj;
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color))
            return false;
        if (id != other.id)
            return false;
        if (isDirty != other.isDirty)
            return false;
        if (isVisible != other.isVisible)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type != other.type)
            return false;
        if (Float.floatToIntBits(width) != Float.floatToIntBits(other.width))
            return false;
        return true;
    }
}
