package oep.views.workflow.table;

import java.text.SimpleDateFormat;

import org.apache.oozie.client.WorkflowJob;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Label and image provider for the Workflow Table
 * 
 * @author Chris White
 */
public class WorkflowLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	/** Date formatter - order allows 'textual' sorting */
	protected SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss ZZZ");

	/** Status paused image */
	protected Image imgPause;

	/** Status completed image */
	protected Image imgComplete;

	/** Status failed image */
	protected Image imgFailed;

	/** Status running image */
	protected Image imgRunning;

	/** Status prep image */
	protected Image imgPrep;

	/** Status killed image */
	protected Image imgKilled;

	public WorkflowLabelProvider() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		imgPause = new Image(display, getClass().getResourceAsStream(
				"/pause.gif"));
		imgFailed = new Image(display, getClass().getResourceAsStream(
				"/error.gif"));
		imgComplete = new Image(display, getClass().getResourceAsStream(
				"/complete_status.gif"));
		imgRunning = new Image(display, getClass().getResourceAsStream(
				"/start_task.gif"));
		imgPrep = new Image(display, getClass().getResourceAsStream(
				"/config_obj.gif"));
		imgKilled = new Image(display, getClass().getResourceAsStream(
				"/nav_stop.gif"));
	}

	@Override
	public void dispose() {
		super.dispose();

		imgPause.dispose();
		imgComplete.dispose();
		imgFailed.dispose();
		imgRunning.dispose();
		imgPrep.dispose();
		imgKilled.dispose();
	}

	public String getColumnText(Object obj, int index) {
		WorkflowJob jobInfo = (WorkflowJob) obj;

		WorkflowColumn colDef = WorkflowColumn.values()[index];

		switch (colDef) {
		case ID:
			return jobInfo.getId();
		case APP_NAME:
			return jobInfo.getAppName();
		case USER:
			return jobInfo.getUser();
		case GROUP:
			return jobInfo.getGroup();
		case STATUS:
			return jobInfo.getStatus().toString();
		case CREATED:
			return sdf.format(jobInfo.getCreatedTime());
		case STARTED:
			return sdf.format(jobInfo.getStartTime());
		case ENDED:
			return sdf.format(jobInfo.getEndTime());
		case MODIFIED:
			return sdf.format(jobInfo.getLastModifiedTime());
		case RUN:
			return Integer.toString(jobInfo.getRun());
		case APP_PATH:
			return jobInfo.getAppPath();

		default:
			return "<Error>";
		}
	}

	public Image getColumnImage(Object obj, int index) {
		if (index == WorkflowColumn.STATUS.ordinal()) {
			WorkflowJob jobInfo = (WorkflowJob) obj;

			WorkflowJob.Status status = jobInfo.getStatus();
			String imageName = null;
			switch (status) {
			case SUSPENDED:
				return imgPause;
			case FAILED:
				return imgFailed;
			case KILLED:
				return imgKilled;
			case RUNNING:
				return imgRunning;
			case PREP:
				return imgPrep;
			case SUCCEEDED:
				return imgComplete;
			}
		}

		return null;
	}
};
