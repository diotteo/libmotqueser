package ca.dioo.java.MonitorLib;

class Test {
	public static void main(String args[]) {
		XmlStringWriter xsw = new XmlStringWriter("client_message", new int[]{1, 0});
		xsw.writeTag("action", new String[][] {{"type", "get_message_list"}});

		System.out.println(xsw.getXmlString());

		XmlStringReader xsr = new XmlStringReader(xsw.getXmlString());
	}
}
