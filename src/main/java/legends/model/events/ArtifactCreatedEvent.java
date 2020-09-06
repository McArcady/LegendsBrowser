package legends.model.events;

import legends.model.World;
import legends.model.events.basic.ArtifactRelatedEvent;
import legends.model.events.basic.Event;
import legends.model.events.basic.HfEvent;
import legends.model.events.basic.SiteRelatedEvent;
import legends.xml.annotation.Xml;
import legends.xml.annotation.XmlSubtype;

@XmlSubtype("artifact created")
public class ArtifactCreatedEvent extends HfEvent implements SiteRelatedEvent, ArtifactRelatedEvent {
	@Xml("artifact_id")
	private int artifactId = -1;
	@Xml("unit_id")
	private int unitId = -1;
	@Xml("site_id,site")
	private int siteId = -1;

	@Xml(value = "reason", track = true)
	private String reason;
	@Xml("sanctify_hf")
	private int sanctifyHfId = -1;

	@Xml("name_only")
	private boolean nameOnly;

	private int calcDefeatedHfId = -1;

	public int getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(int artifactId) {
		this.artifactId = artifactId;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	@Override
	public boolean isRelatedToArtifact(int artifactId) {
		return this.artifactId == artifactId;
	}

	@Override
	public boolean isRelatedToSite(int siteId) {
		return this.siteId == siteId;
	}

	@Override
	public void process() {
		if (nameOnly) {
			Event prev = World.getHistoricalEvent(getId() - 1);
			if (prev instanceof HfWoundedEvent)
				calcDefeatedHfId = ((HfWoundedEvent) prev).getWoundeeHfId();
			if (prev instanceof HfDiedEvent)
				calcDefeatedHfId = ((HfDiedEvent) prev).getHfId();
		}
	}

	@Override
	public String getShortDescription() {
		String artifact = World.getArtifact(artifactId).getLink();
		String hf = World.getHistoricalFigure(hfId).getLink();
		String site = "";
		if (siteId != -1)
			site = " in " + World.getSite(siteId).getLink();
		if (!nameOnly)
			return hf + " created " + artifact + site;
		String defeated = World.getHistoricalFigure(calcDefeatedHfId).getLink();
		if (siteId != -1) {
			if ("sanctify_hf".equals(reason))
				return String.format("%s received its name in %s from %s in order to sanctify %s as the item was a favorite possession", artifact,
						World.getSite(siteId).getLink(), hf, World.getHistoricalFigure(sanctifyHfId).getLink());
			return String.format("%s received its name in %s from %s after defeating %s", artifact,
					World.getSite(siteId).getLink(), hf, defeated);
		}
		return String.format("%s received its name from %s after defeating %s", artifact, hf, defeated);
	}

}
