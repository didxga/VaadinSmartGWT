package org.vaadin.smartgwt.server.data;

import java.util.Map.Entry;

import argo.jdom.JsonNode;
import argo.jdom.JsonStringNode;

/**
 * Updates a Record with JSON information. 
 */
public class RecordJSONUpdater {
	/**
	 * updates the Record with information from the JsonNode. 
	 * 
	 * @param record to be updated.
	 * @param node containing update information.
	 */
	public void update(Record record, JsonNode node) {
		for (Entry<JsonStringNode, JsonNode> entry : node.getFields().entrySet()) {
			final String name = entry.getKey().getText();
			if (entry.getValue().isBooleanValue()) {
				record.setAttribute(name, entry.getValue().getBooleanValue());
			} else if (entry.getValue().isNumberValue()) {
				record.setAttribute(name, Integer.parseInt(entry.getValue().getNumberValue()));
			} else if (entry.getValue().isStringValue()) {
				record.setAttribute(name, entry.getValue().getStringValue());
			} else {
				throw new RuntimeException("unhandled node type " + entry.getValue());
			}
		}
	}
}