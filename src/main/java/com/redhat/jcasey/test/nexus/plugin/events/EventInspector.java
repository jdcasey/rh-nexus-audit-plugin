package com.redhat.jcasey.test.nexus.plugin.events;

import org.sonatype.plexus.appevents.Event;

public class EventInspector implements
		org.sonatype.nexus.proxy.events.EventInspector {

	public boolean accepts(Event<?> evt) {
		return false;
	}

	public void inspect(Event<?> evt) {
		evt.getEventContext();
	}

}
