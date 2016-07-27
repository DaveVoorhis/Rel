package org.reldb.dbrowser.ui.feedback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.reldb.dbrowser.ui.version.Version;

/** Send a message back home. */
public class Feedback {
	
	private Button btnSend;
	private Label lblProgress;
	private ProgressBar progressBar;
	
	private SendWorker sendWorker = null;
	private Object sendWorkerMutex = new Integer(0);

	public Feedback(Button btnSend, Label lblProgress, ProgressBar progressBar) {
		this.btnSend = btnSend;
		this.lblProgress = lblProgress;
		this.progressBar = progressBar;
	}
	
	protected void initialiseProgress(String msg, int steps) {
		btnSend.setEnabled(false);
		lblProgress.setEnabled(true);
		progressBar.setEnabled(true);
		progressBar.setMaximum(steps);
		updateProgress(msg, 0);
	}
	
	protected void updateProgress(String msg, int step) {
		lblProgress.setText(msg);
		progressBar.setSelection(step);
	}
	
	protected void resetProgress() {
		updateProgress("Progress...", 0);
		lblProgress.setEnabled(false);
		progressBar.setEnabled(false);
		btnSend.setEnabled(true);
	}

    private static class SendProgress {
    	String msg;
    	int progress;
    	public SendProgress(String msg, int progress) {
    		this.msg = msg;
    		this.progress = progress;
    	}
    }
    
    public static class SendStatus {
    	private Exception exception;
    	private String response;
    	public SendStatus(Exception e) {
    		this.exception = e;
    		this.response = null;
    	}
    	public SendStatus(String response) {
    		this.exception = null;
    		this.response = response;
    	}
    	public boolean isOk() {
    		return this.exception == null;
    	}
    	public Exception getException() {
    		return exception;
    	}
    	public String getResponse() {
    		return response;
    	}
    }
    
    private class SendWorker extends Thread {

    	private String report;
    	
    	public SendWorker(String report) {
    		this.report = report;
    	}
    	
    	public void publish(SendProgress progressMessage) {
    		lblProgress.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
			    	updateProgress(progressMessage.msg, progressMessage.progress);  		
				}
    		});
    	}
    	
    	private SendStatus status = null;
    	
    	public void run() {
    		try {
    			status = doInBackground();
    		} catch (Exception e) {
    			status = new SendStatus(e);
    		}
    		lblProgress.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					done(status);
				}
    		});
    	}

		protected SendStatus doInBackground() throws Exception {			
			publish(new SendProgress("Generating message...", 10));

	        HttpClient client = new DefaultHttpClient();
	        try {
	            HttpPost httppost = new HttpPost(Version.getReportLogURL());

	            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	            formparams.add(new BasicNameValuePair("RelErrorReport", report));
	            
	            HttpEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
	            httppost.setEntity(entity);
	            
	            publish(new SendProgress("Sending message...", 50));
	            HttpResponse response = client.execute(httppost);
	            entity = response.getEntity();
	            
	            publish(new SendProgress("Getting response...", 75));	            
	            BufferedReader is = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String input;
	            String result = "";
	            while ((input = is.readLine()) != null) {
	            	if (input.startsWith("Success") || input.startsWith("ERROR"))
	            		result = input;
	            }
	            is.close();
	            
	            publish(new SendProgress("Done", 100));
	            try {
	            	Thread.sleep(1000);
	            } catch (InterruptedException e) {}
	            
	            return new SendStatus(result);
	        } catch (Exception e) {
	        	return new SendStatus(e);
	        }
		}
		
		protected void done(SendStatus sendStatus) {
			synchronized (sendWorkerMutex) {
				sendWorker = null;
			}
			resetProgress();
			completed(sendStatus);
		}
    }

    public void completed(SendStatus sendStatus) {}

	public void quit() {}
    
	public void doSend(String message) {
		initialiseProgress("Sending...", 100);
		synchronized (sendWorkerMutex) {
			sendWorker = new SendWorker(message);
		}
		sendWorker.start();
	}
	
	public void doCancel() {
		synchronized (sendWorkerMutex) {
			if (sendWorker != null)
				sendWorker.interrupt();
			else
				quit();
		}
	}

}
