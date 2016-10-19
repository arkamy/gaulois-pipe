/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.efl.chaine.xslt.testUtilities;

import javax.xml.transform.SourceLocator;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.MessageListener;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;

/**
 * A simple message listener that collects the messages
 * @author cmarchand
 */
public class TestMessageListener implements MessageListener {
    
    private final StringBuilder buffer;
    private final String lineSeparator;
    
    public TestMessageListener() {
        super();
        this.buffer = new StringBuilder();
        this.lineSeparator = System.getProperty("line.separator");
    }

    @Override
    public void message(XdmNode content, boolean terminate, SourceLocator locator) {
        XdmNode xdmMessage = (XdmNode) content.axisIterator(Axis.CHILD).next();
        String textMessage = xdmMessage.getStringValue();
        XdmNodeKind kind = xdmMessage.getNodeKind();
        if(buffer.length()>0) {
            buffer.append(lineSeparator);
        }
        buffer.append(textMessage);
    }
    
    @Override
    public String toString() {
        return buffer.toString();
    }
    
}
