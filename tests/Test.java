package ca.dioo.java.MonitorLib;

class Test {
	public static void main(String args[]) {
		XmlStringWriter xsw = new XmlStringWriter("client_message", new int[]{1, 0});
		xsw.writeTag("action", new String[][] {{"type", "get_message_list"}});

		System.out.println(xsw.getXmlString());

		XmlStringReader xsr = new XmlStringReader(xsw.getXmlString());


		xsw = new XmlStringWriter("control_message", new int[]{1, 0});
		xsw.writeTag("item", new String[][] {{"id", "1"}});
		xsw.writeTag("movie", null, "/path/to/file.mp4");
		xsw.writeEndTag();
		xsw.writeEndTag();

		System.out.println(xsw.getXmlString());

		xsr = new XmlStringReader(xsw.getXmlString());
	}
}
