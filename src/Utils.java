package ca.dioo.java.MonitorLib;

public class Utils {
	public static String repeat(String s, int nb) {
		StringBuffer sb = new StringBuffer(s);

		if (nb == 0) return "";

		for (int i = 1; i < nb; i++) {
			sb.append(s);
		}

		return sb.toString();
	}


	public static String join(CharSequence delimiter, CharSequence... elements) {
		if (elements.length < 1) {
			return "";
		}

		StringBuffer sb = new StringBuffer(elements[0]);
		for (int i = 1; i < elements.length; i++) {
			sb.append(delimiter);
			sb.append(elements[i]);
		}

		return sb.toString();
	}


	public static String join(CharSequence delim0, CharSequence delim1, CharSequence[]... elements) {
		if (elements.length < 1) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			StringBuffer sbp = new StringBuffer();

			if (elements[i].length > 0) {
				sbp.append(elements[i][0]);
			}

			for (int j = 1; j < elements[i].length; j++) {
				sbp.append(delim1);
				sbp.append(elements[i][j]);
			}
			sb.append(delim0);
			sb.append(sbp);
		}

		return sb.toString();
	}


	public static int bool2int(boolean b) {
		return b ? 1 : 0;
	}
}
