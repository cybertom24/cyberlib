package cyberLib;

public class Loop {
	
	private long delay = 1000;
	private Job job;
	private	boolean going = false; 
	
	public Loop(Job job) {
		this.job = job;
	}
	
	public Loop(Job job, int delay) {
		this.delay = delay;
		this.job = job;
	}
	
	public Loop(Job job, long delay) {
		this.delay = delay;
		this.job = job;
	}
	
	public void setJob(Job newjob) {
		job = newjob;
	}
	
	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	
	public boolean isGoing() {
		return going;
	}
	
	public void start() {
		if(!going) {
			going = true;
			job.start();
			startThread();
		}
	}
	
	public void stop() {
		going = false;
	}
	
	private void startThread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(going) {
					job.execute();						
					try {
						Thread.sleep(delay);
					} catch (Exception e) {
						e.printStackTrace();
						going = false;
						job.interrupted();
					}
				}
				job.stop();
			}
		}).start();
	}
	
	public interface Job {
		public void start();
		public void execute();
		public void stop();
		public void interrupted();
	}
}
