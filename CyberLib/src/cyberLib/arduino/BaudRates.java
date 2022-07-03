package cyberLib.arduino;

public enum BaudRates {
	$300(300), 
	$600(600), 
	$1200(1200), 
	$2400(2400), 
	$4800(4800), 
	$9600(9600),
	$14400(14400), 
	$19200(19200), 
	$28800(28800), 
	$38400(38400), 
	$57600(57600), 
	$74880(74880),
	$115200(115200);
	
	public final int rate;
	private BaudRates(int baud) {
		rate = baud;
	}
};