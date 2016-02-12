package ca.dioo.java.MonitorLib;

import java.io.StringReader;
import javax.xml.stream.XMLStreamReader;

class Test {
	public static void main(String args[]) {
		XmlStringWriter xsw = new XmlStringWriter("client_message", new int[]{1, 0});
		xsw.writeTag("action", new String[][] {{"type", "get_message_list"}});

		System.out.println(xsw.getXmlString());

		XMLStreamReader xsr = XmlStringReader.getFromString(xsw.getXmlString());
		Message msg = MessageFactory.parse(xsr);
		assert msg instanceof ClientMessage;
		System.out.println(((ClientMessage)msg).getXmlString());


		System.out.println();


		xsw = new XmlStringWriter("control_message", new int[]{1, 0});
		xsw.writeTag("item", new String[][] {{"id", "1"}});
		xsw.writeTag("movie", null, "/path/to/file.mp4");
		xsw.writeEndTag();
		xsw.writeEndTag();

		System.out.println(xsw.getXmlString());

		xsr = XmlStringReader.getFromString(xsw.getXmlString());


		ControlMessage cm = new ControlMessage(new int[]{1, 0});
		ControlMessage.Item it = new ControlMessage.Item(1);
		it.add(new ControlMessage.Media("/path/to/file.mp4"));
		cm.add(it);
		System.out.println("ControlMessage " + cm);


		ClientMessage clm = new ClientMessage(new int[]{1, 0});
		ClientMessage.Action a = new ClientMessage.Action(ClientMessage.Action.ActionType.GET_MSG_LIST);
		clm.add(a);
		a = new ClientMessage.Action(ClientMessage.Action.ActionType.GET_MSG);
		clm.add(a);
		System.out.println("ClientMessage " + clm);
		System.out.println("\n" + clm.getXmlString());
	}
}
