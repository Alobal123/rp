package krabec.citysimulator;

import java.awt.Cursor;

import javax.swing.SwingWorker;

public class StepWorker extends SwingWorker<String, String>{

	City city;
	City_window window;
	boolean run_next;
	
	public StepWorker(City city,City_window window,boolean run_next) {
		this.city = city;
		this.window = window;
		this.run_next = run_next;
		window.timer.stop();
	}
	
	@Override
	protected String doInBackground() throws Exception {
		window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		for (int j = 0; j < 1; j++) {
			city.step();
		}
		return null;
	}
	@Override
	protected void done(){
			window.panel.repaint();
			window.repaint();
			window.setCursor(Cursor.getDefaultCursor());
			if(run_next && !window.paused)
				window.timer.start();
	}

}
