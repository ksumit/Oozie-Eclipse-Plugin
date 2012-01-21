package org.apache.oozie.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

import org.apache.oozie.client.rest.RestConstants;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Extension to {@link XOozieClient} to allow acquisition of total number of job
 * (for each type: wf, coord, bundle)
 * 
 * @author Chris White
 */
public class OEPOozieClient extends XOozieClient {
	/**
	 * @return the total number of workflow jobs
	 * @throws OozieClientException
	 */
	public int getWorkflowTotal() throws OozieClientException {
		return new JobCount("wf").call();
	}

	/**
	 * @return the total number of coordinator jobs
	 * @throws OozieClientException
	 */
	public int getCoordTotal() throws OozieClientException {
		return new JobCount("coord").call();
	}

	/**
	 * @return the total number of bundle jobs
	 * @throws OozieClientException
	 */
	public int getBundleTotal() throws OozieClientException {
		return new JobCount("bundle").call();
	}

	/**
	 * Job Count callable - similar to the
	 * {@link OozieClient#getJobsInfo(String)} call, but extracts the "total"
	 * JSON field from the result
	 */
	private class JobCount extends ClientCallable<Integer> {
		JobCount(String jobType) {
			super("GET", RestConstants.JOBS, "", prepareParams(
					RestConstants.JOBS_FILTER_PARAM, "",
					RestConstants.JOBTYPE_PARAM, jobType,
					RestConstants.OFFSET_PARAM, Integer.toString(0),
					RestConstants.LEN_PARAM, Integer.toString(1)));
		}

		@Override
		protected Integer call(HttpURLConnection conn) throws IOException,
				OozieClientException {
			conn.setRequestProperty("content-type",
					RestConstants.XML_CONTENT_TYPE);
			if ((conn.getResponseCode() == HttpURLConnection.HTTP_OK)) {
				Reader reader = new InputStreamReader(conn.getInputStream());
				JSONObject json = (JSONObject) JSONValue.parse(reader);
				return (Integer) json.get("total");
			} else {
				handleError(conn);
			}
			return null;
		}
	}
}
