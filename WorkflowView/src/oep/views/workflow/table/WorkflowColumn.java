package oep.views.workflow.table;

/**
 * Workflow Column definitions
 * 
 * @author Chris White
 */
public enum WorkflowColumn {
	ID("Id", 50),
	APP_NAME("App Name", 250),
	USER("User", 100),
	GROUP("Group", 100),
	STATUS("Status", 100),
	CREATED("Created", 150),
	STARTED("Started", 150),
	ENDED("Ended", 150),
	MODIFIED("Modified", 150),
	RUN("Run", 50),
	PARENT_ID("Parent Id", 250),
	APP_PATH("App Path", 250);

	/** Initial column width */
	private final int width;

	/** Column label */
	private final String label;

	/**
	 * @param label Column label
	 * @param width Initial width in pixels
	 */
	private WorkflowColumn(String label, int width) {
		this.label = label;
		this.width = width;
	}

	@Override
	public String toString() {
		return label;
	}
	
	/**
	 * @return Initial column width (in pixels)
	 */
	public int getWidth() {
		return width;
	}
}
