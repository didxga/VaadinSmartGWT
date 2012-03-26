package org.vaadin.smartgwt.client.ui.grid;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.rpc.client.ClientSideHandler;
import org.vaadin.rpc.client.ClientSideProxy;
import org.vaadin.smartgwt.client.core.PaintableProperty;
import org.vaadin.smartgwt.client.core.PaintablePropertyUpdater;
import org.vaadin.smartgwt.client.core.VJSObject;
import org.vaadin.smartgwt.client.ui.layout.VMasterContainer;
import org.vaadin.smartgwt.client.ui.utils.PainterHelper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VListGrid extends ListGrid implements Paintable, ClientSideHandler
{
	private final PaintablePropertyUpdater propertyUpdater = new PaintablePropertyUpdater();
	private final ClientSideProxy rpc = new ClientSideProxy("VListGrid", this);

	public VListGrid()
	{
		propertyUpdater.addProperty(new PaintableProperty("field")
			{
				@Override
				public void postUpdate(Paintable[] paintables)
				{
					final List<ListGridField> fields = new ArrayList<ListGridField>(paintables.length);

					for (Paintable paintable : paintables)
					{
						fields.add(((VListGridField) paintable).getJSObject());
					}

					setFields(fields.toArray(new ListGridField[0]));
				}
			});

		addSelectionChangedHandler(new SelectionChangedHandler()
			{
				@Override
				public void onSelectionChanged(SelectionEvent event)
				{
					JavaScriptObject selections = JSOHelper.convertToJavaScriptArray(getSelectedRecords());
					if (selections != null)
					{
						rpc.call("selectionChanged", JSON.encode(selections));
						rpc.forceSync();
					}
				}
			});
	}

	@Override
	public Element getElement()
	{
		return VMasterContainer.getDummy();
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client)
	{
		propertyUpdater.updateFromUIDL(uidl, client);
		rpc.update(this, uidl, client);

		if (uidl.hasAttribute("dataSource"))
		{
			final Paintable paintable = uidl.getPaintableAttribute("dataSource", client);
			setDataSource(((VJSObject<DataSource>) paintable).getJSObject());
		}

		PainterHelper.updateSmartGWTComponent(client, this, uidl);
		rpc.clientInitComplete();
	}

	@Override
	public boolean initWidget(Object[] params)
	{
		rpc.clientInitComplete();
		return true;
	}

	@Override
	public void handleCallFromServer(String method, Object[] params)
	{}

}