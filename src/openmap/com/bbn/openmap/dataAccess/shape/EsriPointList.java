// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies, a Verizon Company
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/dataAccess/shape/EsriPointList.java,v $
// $RCSfile: EsriPointList.java,v $
// $Revision: 1.1.1.1 $
// $Date: 2003/02/14 21:35:48 $
// $Author: dietrick $
// 
// **********************************************************************


package com.bbn.openmap.dataAccess.shape;

import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.util.Debug;

/**
 * An EsriGraphicList ensures that only EsriPoints are added to its list.
 * @author Doug Van Auken 
 * @author Don Dietrick
 */
public class EsriPointList extends EsriGraphicList {

    /**
     * Over-ride the add( ) method to trap for inconsistent shape
     * geometry.
     * @param shape the non-null OMGraphic to add 
     */
    public void add(OMGraphic shape) {
	try {
	    if (shape instanceof OMPoint) {
		shape = EsriPoint.convert((OMPoint) shape);
		// test for null in next if statement.
	    }

	    if (shape instanceof EsriPointList) {
		OMGraphicList list = (OMGraphicList)shape;
		EsriGraphic graphic = (EsriGraphic)list.getOMGraphicAt(0);
		
		if (graphic instanceof EsriPoint ||
		    graphic instanceof EsriPointList) {
		    graphics.add(shape);
		    addExtents(((EsriGraphicList)shape).getExtents());
		} else if (graphic instanceof OMGraphic) {
		    // Try recursively...
		    add((OMGraphic)graphic);
		} else {
		    Debug.message("esri", "EsriPointList.add()- graphic list isn't EsriPointList, can't add.");
		}
	    } else if (shape instanceof EsriPoint) {
		graphics.add(shape);
		addExtents(((EsriPoint)shape).getExtents());
	    } else {
		Debug.message("esri", "EsriPointList.add()- graphic isn't an EsriPoint, can't add.");
		return;
	    }
	} catch (ClassCastException cce) {
	}
    }

    /**
     * Get the list type in ESRI type number form - 0.
     */
    public int getType() {
	return SHAPE_TYPE_POINT;
    }

    /**
     * Construct an EsriPointList.
     */
    public EsriPointList() {
	super();
    }
    
    /**
     * Construct an EsriPointList with an initial capacity. 
     *
     * @param initialCapacity the initial capacity of the list 
     */
    public EsriPointList(int initialCapacity) {
	super(initialCapacity);
    }

    /**
     * Construct an EsriPointList with an initial capacity and
     * a standard increment value.
     *
     * @param initialCapacity the initial capacity of the list 
     * @param capacityIncrement the capacityIncrement for resizing 
     * @deprecated capacityIncrement doesn't do anything.
     */
    public EsriPointList(int initialCapacity, int capacityIncrement) {
	super(initialCapacity);
    }
}
