package eu.geopaparazzi.library.forms.constraints;

import eu.geodroid.library.util.Utilities;

/**
 * A numeric range constraint.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RangeConstraint implements IConstraint {

    private boolean isValid = false;

    private double lowValue;
    private final boolean includeLow;
    private final double highValue;
    private final boolean includeHigh;

    public RangeConstraint( Number low, boolean includeLow, Number high, boolean includeHigh ) {
        this.includeLow = includeLow;
        this.includeHigh = includeHigh;
        highValue = high.doubleValue();
        lowValue = low.doubleValue();
    }

    public void applyConstraint( Object value ) {
        if (value instanceof String) {
            String str = (String) value;
            if (str.length() == 0) {
                // empty can be still ok, we just check for ranges if we have a value
                isValid = true;
                return;
            }
        }

        Double adapted = Utilities.adapt(value, Double.class);
        if (adapted != null) {
            double doubleValue = adapted.doubleValue();
            if (//
            ((includeLow && doubleValue >= lowValue) || (!includeLow && doubleValue > lowValue)) && //
                    ((includeHigh && doubleValue <= highValue) || (!includeHigh && doubleValue < highValue)) //
            ) {
                isValid = true;
            } else {
                isValid = false;
            }
        } else {
            isValid = false;
        }
    }

    public boolean isValid() {
        return isValid;
    }

    @SuppressWarnings("nls")
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (includeLow) {
            sb.append("[");
        } else {
            sb.append("(");
        }
        sb.append(lowValue);
        sb.append(",");
        sb.append(highValue);
        if (includeHigh) {
            sb.append("]");
        } else {
            sb.append(")");
        }
        return sb.toString();
    }

}
