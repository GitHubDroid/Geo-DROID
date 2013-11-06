package eu.hydrologis.geodroid.maps.overlays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.OverlayWay;

import android.content.Context;

/**
 * ArrayGeopaparazziOverlay is a thread-safe implementation of the {@link GeopaparazziOverlay} class using an {@link ArrayList} as
 * internal data structure. Default paints for all {@link OverlayWay OverlayWays} without individual paints can be
 * defined via the constructor.
 */
public class ArrayGeopaparazziOverlay extends GeopaparazziOverlay {
    private static final int INITIAL_CAPACITY = 8;
    private static final String THREAD_NAME = "ArrayGeopaparazziOverlay"; //$NON-NLS-1$

    private final List<OverlayWay> overlayWays;
    private final List<OverlayItem> overlayItems;

    /**
     */
    public ArrayGeopaparazziOverlay( Context context ) {
        super(context);
        this.overlayWays = new ArrayList<OverlayWay>(INITIAL_CAPACITY);
        this.overlayItems = new ArrayList<OverlayItem>(INITIAL_CAPACITY);
    }

    /**
     * Adds the given way to the overlay.
     * 
     * @param overlayWay
     *            the way that should be added to the overlay.
     */
    public void addWay( OverlayWay overlayWay ) {
        synchronized (this.overlayWays) {
            this.overlayWays.add(overlayWay);
        }
        populate();
    }

    /**
     * Adds all ways of the given collection to the overlay.
     * 
     * @param c
     *            collection whose ways should be added to the overlay.
     */
    public void addWays( Collection<OverlayWay> c ) {
        synchronized (this.overlayWays) {
            this.overlayWays.addAll(c);
        }
        populate();
    }

    /**
     * Removes all ways from the overlay.
     */
    public void clearWays() {
        synchronized (this.overlayWays) {
            this.overlayWays.clear();
        }
        populate();
    }

    @Override
    public String getThreadName() {
        return THREAD_NAME;
    }

    /**
     * Removes the given way from the overlay.
     * 
     * @param overlayWay
     *            the way that should be removed from the overlay.
     */
    public void removeWay( OverlayWay overlayWay ) {
        synchronized (this.overlayWays) {
            this.overlayWays.remove(overlayWay);
        }
        populate();
    }

    @Override
    public int waySize() {
        synchronized (this.overlayWays) {
            return this.overlayWays.size();
        }
    }

    @Override
    protected OverlayWay createWay( int index ) {
        synchronized (this.overlayWays) {
            if (index >= this.overlayWays.size()) {
                return null;
            }
            return this.overlayWays.get(index);
        }
    }

    /**
     * Adds the given item to the overlay.
     * 
     * @param overlayItem
     *            the item that should be added to the overlay.
     */
    public void addItem( OverlayItem overlayItem ) {
        synchronized (this.overlayItems) {
            this.overlayItems.add(overlayItem);
        }
        populate();
    }

    /**
     * Adds all items of the given collection to the overlay.
     * 
     * @param c
     *            collection whose items should be added to the overlay.
     */
    public void addItems( Collection<OverlayItem> c ) {
        synchronized (this.overlayItems) {
            this.overlayItems.addAll(c);
        }
        populate();
    }

    /**
     * Removes all items from the overlay.
     */
    public void clearItems() {
        synchronized (this.overlayItems) {
            this.overlayItems.clear();
        }
        populate();
    }

    /**
     * Removes the given item from the overlay.
     * 
     * @param overlayItem
     *            the item that should be removed from the overlay.
     */
    public void removeItem( OverlayItem overlayItem ) {
        synchronized (this.overlayItems) {
            this.overlayItems.remove(overlayItem);
        }
        populate();
    }

    @Override
    public int itemSize() {
        synchronized (this.overlayItems) {
            return this.overlayItems.size();
        }
    }

    @Override
    protected OverlayItem createItem( int index ) {
        synchronized (this.overlayItems) {
            if (index >= this.overlayItems.size()) {
                return null;
            }
            return this.overlayItems.get(index);
        }
    }

}
