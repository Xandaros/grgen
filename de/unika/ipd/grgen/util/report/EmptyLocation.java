/**
 * @author shack
 * @version $Id$
 */
package de.unika.ipd.grgen.util.report;

/**
 * An empty location.
 */
public class EmptyLocation implements Location {

	private static final EmptyLocation EMPTY;
	
	static {
		EMPTY = new EmptyLocation();
	}

  /**
   * Return the empty string always.
   * @see de.unika.ipd.grgen.util.report.Location#getLocation()
   */
  public String getLocation() {
    return "<nowhere>";
  }

  /**
   * This location is never valid.
   * @see de.unika.ipd.grgen.util.report.Location#hasLocation()
   */
  public boolean hasLocation() {
    return false;
  }
  
  /**
   * Get a new empty location
   * @return an empty location
   */
  public static EmptyLocation getEmptyLocation() {
  	return EMPTY;
  }

}
