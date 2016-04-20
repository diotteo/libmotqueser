import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import ca.dioo.java.libmotqueser.*;

public class Test {
	public static void main(String args[]) {
		packageVersion();
		test2();
	}

	public static void packageVersion() {
		System.out.println("libmotqueser version = " + Version.VERSION);
	}


	public static void test2() {
		Message.Version v1 = new Message.Version(1, 0);
		Message.Version v2 = new Message.Version(1, 0);
		Message.Version v3 = new Message.Version(1, 1);

		System.out.println("v1=v2?" + v1.equals(v2) + " v1=v3?" + v1.equals(v3) + " v2=v3?" + v2.equals(v3));
	}


	public static void test1() {
		XmlStringWriter xsw = new XmlStringWriter("client_message", new Message.Version(1, 0));
		xsw.writeTag("action", Arrays.asList(new Attribute<String, String>("type", "get_message_list")));

		System.out.println(xsw.getXmlString());

		XmlParser xp = XmlStringReader.getFromString(xsw.getXmlString());
		Message msg;
		try {
			msg = MessageFactory.parse(xp);
		} catch (Exception e) {
			throw new Error(e.getMessage());
		}
		assert msg instanceof ClientMessage;
		System.out.println(((ClientMessage)msg).getXmlString());


		System.out.println();


		xsw = new XmlStringWriter("control_message", new Message.Version(1, 0));
		xsw.writeTag("item", Arrays.asList(new Attribute<String, String>("id", "1")));
		xsw.writeTag("media", null, "/path/to/file.mp4");
		xsw.writeEndTag();
		xsw.writeEndTag();

		System.out.println(xsw.getXmlString());

		xp = XmlStringReader.getFromString(xsw.getXmlString());


		ControlMessage cm = new ControlMessage(new Message.Version(1, 0));
		ControlMessage.Item it = new ControlMessage.Item("1");
		cm.add(it);
		System.out.println("ControlMessage " + cm);


		ClientMessage clm = new ClientMessage(new Message.Version(1, 0));
		ClientMessage.Request req = new ClientMessage.ItemListRequest(3);
		clm.add(req);
		req = new ClientMessage.ItemRequest(4);
		clm.add(req);
		System.out.println("ClientMessage " + clm);
		System.out.println("\n" + clm.getXmlString());


		ServerMessage sm = new ServerMessage(new Message.Version(1, 0));
		sm.setResponse(new ServerMessage.ItemResponse(2));
		System.out.println("\nServerMessage " + sm);
		System.out.println("\n" + sm.getXmlString());
	}
}
