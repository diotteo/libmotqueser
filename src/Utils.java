package ca.dioo.java.MonitorLib;

class Utils {
	public static String repeat(String s, int nb) {
		StringBuffer sb = new StringBuffer(s);

		if (nb == 0) return "";

		for (int i = 1; i < nb; i++) {
			sb.append(s);
		}

		return sb.toString();
	}
}
