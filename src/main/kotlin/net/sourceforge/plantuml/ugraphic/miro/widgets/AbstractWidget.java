package net.sourceforge.plantuml.ugraphic.miro.widgets;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractWidget implements Widget {
	// uid may be used before saving the widget in Miro to guarantee uniqueness - needed???
	protected final UUID uid = UUID.randomUUID();
	protected long id; // 0 before saving in Miro
	protected final double x;
	protected final double y;

	protected AbstractWidget(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public UUID getUid() {
		return uid;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

//	public void setX(double x) {
//		this.x = x;
//	}

	public double getY() {
		return y;
	}
//
//	public void setY(double y) {
//		this.y = y;
//	}

//	public AbstractWidget setXY(double x, double y) {
//		setX(x);
//		setY(y);
//		return this;
//	}
//
//	public void addToContainer() {
//		ThreadLocalWidgetsContainer.add(this);
//	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AbstractWidget that = (AbstractWidget) o;
		return id == that.id && uid.equals(that.uid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uid, id);
	}
}
