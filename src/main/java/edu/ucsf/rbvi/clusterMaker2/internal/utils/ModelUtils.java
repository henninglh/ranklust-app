package edu.ucsf.rbvi.clusterMaker2.internal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;

import org.cytoscape.work.util.ListSingleSelection;

public class ModelUtils {
	public static final String NONEATTRIBUTE = "--None--";

	public static List<CyEdge> getConnectingEdges(CyNetwork network, List<CyNode> nodes) {
		List<CyEdge> edgeList = new ArrayList<CyEdge>();
		for (int rowIndex = 0; rowIndex < nodes.size(); rowIndex++) {
			for (int colIndex = rowIndex; colIndex < nodes.size(); colIndex++) {
				List<CyEdge> connectingEdges = network.getConnectingEdgeList(nodes.get(rowIndex), nodes.get(colIndex), CyEdge.Type.ANY);
				if (connectingEdges != null) edgeList.addAll(connectingEdges);
			}
		}
		return edgeList;
	}

	public static String getNodeName(CyNetwork network, CyNode node) {
		return network.getRow(node).get(CyNetwork.NAME, String.class);
	}

	public static String getNetworkName(CyNetwork network) {
		return network.getRow(network).get(CyNetwork.NAME, String.class);
	}

	public static boolean hasAttribute(CyNetwork network, CyIdentifiable value, String column) {
		if (!CyTableUtil.getColumnNames(network.getRow(value).getTable()).contains(column))
			return false;

		if (network.getRow(value).getRaw(column) == null)
			return false;

		return true;
	}

	public static void createAndSet(CyNetwork net, CyIdentifiable obj, String column, 
	                                Object value, Class type, Class elementType) {
		CyTable tab = net.getRow(obj).getTable();
		if (tab.getColumn(column) == null) {
			if (type.equals(List.class))
				tab.createListColumn(column, elementType, false);
			else
				tab.createColumn(column, type, false);
		}
		net.getRow(obj).set(column, value);
	}

	public static ListSingleSelection<String> updateEdgeAttributeList(CyNetwork network, 
	                                                                  ListSingleSelection<String> attribute) {
		List<String> attributeArray = getAllAttributes(network, network.getDefaultEdgeTable());
		if (attributeArray.size() > 0){
			ListSingleSelection<String> newAttribute = new ListSingleSelection<String>(attributeArray);	
			if (attribute != null) {
				newAttribute.setSelectedValue(attribute.getSelectedValue());
			}
			if (attribute != null && attributeArray.contains(attribute.getSelectedValue())) {
				newAttribute.setSelectedValue(attribute.getSelectedValue());
			} else
				newAttribute.setSelectedValue(attributeArray.get(0));

			return newAttribute;
		}
		return new ListSingleSelection<String>("--None--");
	}
	

	private static List<String> getAllAttributes(CyNetwork network, CyTable table) {
		String[] attributeArray = new String[1];
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		attributeList.add(NONEATTRIBUTE);
		getAttributesList(attributeList, table);
		String[] attrArray = attributeList.toArray(attributeArray);
		if (attrArray.length > 1) 
			Arrays.sort(attrArray);
		return Arrays.asList(attrArray);
	}

	private static void getAttributesList(List<String>attributeList, CyTable attributes) {
		Collection<CyColumn> names = attributes.getColumns();
		java.util.Iterator<CyColumn> itr = names.iterator();
		for (CyColumn column: attributes.getColumns()) {
			if (column.getType() == Double.class ||
					column.getType() == Integer.class) {
					attributeList.add(column.getName());
			}
		}
	}
	
}