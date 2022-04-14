package net.sourceforge.plantuml.ugraphic.miro.widgets;

import java.util.UUID;

public interface Widget {

	UUID getUid();

	long getId();

	void setId(long id);
}
