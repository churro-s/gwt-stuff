package com.sathkumara.client;

import com.google.gwt.core.client.EntryPoint;
import com.sathkumara.client.bundle.Resources;

public class Main implements EntryPoint {
	
	@Override public void onModuleLoad() {
		Resources.INSTANCE.styles().ensureInjected();
	}
}
