/**
 * This Source Code Form is subject to the terms of 
 * the Mozilla Public License, v. 2.0. If a copy of 
 * the MPL was not distributed with this file, You 
 * can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fr.efl.chaine.xslt;

import fr.efl.chaine.xslt.config.Config;
import fr.efl.chaine.xslt.config.ConfigUtil;
import fr.efl.chaine.xslt.testUtilities.TestMessageListener;
import fr.efl.chaine.xslt.utils.ParameterValue;
import java.util.ArrayList;
import java.util.Collection;
import net.sf.saxon.Configuration;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author cmarchand
 */
public class MessageListenerTest {
    private static Collection<ParameterValue> emptyInputParams;
    private static SaxonConfigurationFactory configFactory;

    @BeforeClass
    public static void initialize() {
        emptyInputParams = new ArrayList<>();
        configFactory = new SaxonConfigurationFactory() {
            Configuration config = Configuration.newConfiguration();
            @Override
            public Configuration getConfiguration() {
                return config;
            }
        };
    }
    
    @Test
    public void testMessageListener() throws Exception {
        GauloisPipe piper = new GauloisPipe(configFactory);
        ConfigUtil cu = new ConfigUtil(configFactory.getConfiguration(), piper.getUriResolver(), "./src/test/resources/messageListener.xml");
        Config config = cu.buildConfig(emptyInputParams);
        config.__messageListenerClassName = "fr.efl.chaine.xslt.testUtilities.TestMessageListener";
        piper.setConfig(config);
        piper.setInstanceName("MSG_LISTENER");
        piper.launch();
        assertNotNull("Message Listener is not loaded", piper.getMessageListener());
        assertTrue("Message listener is not a TestMessageListener", piper.getMessageListener() instanceof TestMessageListener);
        TestMessageListener tml = (TestMessageListener)piper.getMessageListener();
        assertEquals("Messages are not correctly collected","this is a test message",tml.toString());
    }
    
}
