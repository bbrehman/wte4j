package org.wte4j.examples.showcase.client.main;

import com.google.gwt.user.client.ui.IsWidget;

public interface MainDisplay extends MenuDisplay {

	public void setMainContent(IsWidget conent);

	public void setRightContent(IsWidget content);
}
