package oep.views.workflow.table;

import org.apache.oozie.client.OEPOozieClient;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides the rows / columns for the {@link WorkflowTableView}
 * 
 * @author Chris White
 */
public class WorkflowContentProvider implements IStructuredContentProvider {
	/** Client for making Oozie query calls */
	protected OEPOozieClient oozieClient;

	/** Start Offset when paginating */
	protected int pageOffset;

	/** Page length when paginating */
	protected int pageLength;

	public OEPOozieClient getOozieClient() {
		return oozieClient;
	}

	public void setOozieClient(OEPOozieClient oozieClient) {
		this.oozieClient = oozieClient;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	public void setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
	}

	public int getPageLength() {
		return pageLength;
	}

	public void setPageLength(int pageLength) {
		this.pageLength = pageLength;
	}

	/**
	 * @return number of workflow item on server, or -1 if an error occurs
	 */
	public int getTotalItemCount() {
		try {
			return oozieClient.getWorkflowTotal();
		} catch (OozieClientException oce) {
			// TODO: Log error
			return -1;
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		WorkflowJob[] jobs = new WorkflowJob[0];

		if (oozieClient != null) {
			try {
				jobs = oozieClient.getJobsInfo("", pageOffset, pageLength)
						.toArray(jobs);
			} catch (OozieClientException e) {
				// TODO: log error
				return new WorkflowJob[0];
			}
		}

		return jobs;
	}
}
