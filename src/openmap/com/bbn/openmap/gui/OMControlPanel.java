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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/gui/OMControlPanel.java,v $
// $RCSfile: OMControlPanel.java,v $
// $Revision: 1.1 $
// $Date: 2003/04/04 14:34:26 $
// $Author: dietrick $
// 
// **********************************************************************


package com.bbn.openmap.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.PropertyConsumer;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.PropUtils;

/**
 * The OMControlPanel is an all-in-one panel that holds an overview
 * map, pan and zoom buttons, projection stack buttons, scale text
 * field and a LayersPanel.  All of the sub-components share the same
 * property prefix as the OMControlPanel, all have access to
 * components in the MapHandler.  The sub-components are not given to
 * the MapHandler themselves, however.
 */
public class OMControlPanel extends OMComponentPanel implements MapPanelChild {

    LinkedList children = new LinkedList();

    public OMControlPanel() {

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	JPanel navBox = new JPanel();
	navBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	navBox.setLayout(new BorderLayout());

	OverviewMapHandler overviewMap = new OverviewMapHandler();
	overviewMap.setUseAsTool(false);
	overviewMap.setPreferredSize(new Dimension(100, 100));
	overviewMap.setBorder(BorderFactory.createRaisedBevelBorder());
	children.add(overviewMap);

	NavigatePanel navPanel = new NavigatePanel();
	ZoomPanel zoomPanel = new ZoomPanel();
	ProjectionStackTool projStack = new ProjectionStackTool();
	ScaleTextPanel scalePanel = new ScaleTextPanel();
	scalePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

	JPanel navBoxRN = new JPanel();
	children.add(navPanel);
	navBoxRN.add(navPanel);
	navBoxRN.add(Box.createHorizontalGlue());
	children.add(zoomPanel);
	navBoxRN.add(zoomPanel);

	JPanel navBoxRS = new JPanel();
	navBoxRS.setLayout(new BorderLayout());
	children.add(projStack);
	children.add(scalePanel);
	navBoxRS.add(projStack, BorderLayout.NORTH);
	navBoxRS.add(scalePanel, BorderLayout.SOUTH);

	JPanel navBoxR = new JPanel();
	navBoxR.setLayout(new BorderLayout());
	navBoxR.add(navBoxRN, BorderLayout.NORTH);
	navBoxR.add(navBoxRS, BorderLayout.SOUTH);

	navBox.add(overviewMap, BorderLayout.CENTER);
	navBox.add(navBoxR, BorderLayout.EAST);

	add(navBox);

	LayersPanel layersPanel = new LayersPanel();
	layersPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	children.add(layersPanel);
	add(layersPanel);
	validate();
    }

    public void setProperties(String prefix, Properties props) {
	super.setProperties(prefix, props);

	String pl = props.getProperty(PropUtils.getScopedPropertyPrefix(prefix) + PreferredLocationProperty);
	if (pl != null) {
	    setPreferredLocation(pl);
	}

	Iterator it = children.iterator();
	while (it.hasNext()) {
	    Object obj = it.next();
	    if (obj instanceof PropertyConsumer) {
		((PropertyConsumer)obj).setProperties(prefix, props);
	    }
	}
    }

    public Properties getProperties(Properties props) {
	props = super.getProperties(props);

	props.put(PropUtils.getScopedPropertyPrefix(this) + 
		  PreferredLocationProperty, getPreferredLocation());

	Iterator it = children.iterator();
	while (it.hasNext()) {
	    Object obj = it.next();
	    if (obj instanceof PropertyConsumer) {
		((PropertyConsumer)obj).getProperties(props);
	    }
	}
	return props;
    }


    public Properties getPropertyInfo(Properties props) {
	props = super.getPropertyInfo(props);

	props.put(PreferredLocationProperty, "The preferred BorderLayout direction to place this component.");

	Iterator it = children.iterator();
	while (it.hasNext()) {
	    Object obj = it.next();
	    if (obj instanceof PropertyConsumer) {
		((PropertyConsumer)obj).getPropertyInfo(props);
	    }
	}
	return props;
    }

    public void findAndInit(Object someObj) {
	Iterator it = children.iterator();
	while (it.hasNext()) {
	    Object obj = it.next();
	    if (obj instanceof LightMapHandlerChild) {
		((LightMapHandlerChild)obj).findAndInit(someObj);
	    }
	}
    }

    public void findAndUndo(Object someObj) {
	Iterator it = children.iterator();
	while (it.hasNext()) {
	    Object obj = it.next();
	    if (obj instanceof LightMapHandlerChild) {
		((LightMapHandlerChild)obj).findAndUndo(someObj);
	    }
	}
    }


    /**
     * BorderLayout.WEST by default for this class.
     */
    protected String preferredLocation = java.awt.BorderLayout.WEST;

    /**
     * MapPanelChild method.
     */
    public void setPreferredLocation(String value) {
	preferredLocation = value;
    }

    /** 
     * MapPanelChild method. 
     */
    public String getPreferredLocation() {
	return preferredLocation;
    }
}