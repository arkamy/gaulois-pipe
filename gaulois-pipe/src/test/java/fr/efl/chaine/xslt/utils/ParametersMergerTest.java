/**
 * This Source Code Form is subject to the terms of 
 * the Mozilla Public License, v. 2.0. If a copy of 
 * the MPL was not distributed with this file, You 
 * can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fr.efl.chaine.xslt.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.type.ValidationException;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import top.marchand.xml.gaulois.config.typing.DatatypeFactory;

/**
 * Tests ParametersMerger
 * @author Christophe Marchand christophe@marchand.top
 */
public class ParametersMergerTest {
    private static DatatypeFactory factory;
    
    @BeforeClass
    public static void beforeClass() throws ValidationException {
        factory = DatatypeFactory.getInstance(Configuration.newConfiguration());
    }
    
    @Test
    public void initialContainsBackslash() {
        String initial = "C:\\Users\\ext-cmarchand\\$[source]";
        HashMap<QName, ParameterValue> parameters = new HashMap<>();
        QName qn = new QName("source");
        parameters.put(qn, new ParameterValue(qn,"src/main/xsl", factory.XS_STRING));
        String ret = ParametersMerger.processParametersReplacement(initial, parameters);
        assertEquals("C:\\Users\\ext-cmarchand\\src/main/xsl", ret);
    }
    
    @Test
    public void noReplacementForXdmValue() {
        XdmValue initial = new XdmAtomicValue(BigDecimal.ZERO);
        HashMap<QName, ParameterValue> parameters = new HashMap<>();
        QName qn = new QName("source");
        parameters.put(qn, new ParameterValue(qn,"src/main/xsl", factory.XS_STRING));
        Object ret = ParametersMerger.processParametersReplacement(initial, parameters);
        assertTrue(ret instanceof XdmValue);
        assertEquals(((XdmValue)ret).toString(), "0");
    }
}
