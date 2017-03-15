/**
 * This Source Code Form is subject to the terms of 
 * the Mozilla Public License, v. 2.0. If a copy of 
 * the MPL was not distributed with this file, You 
 * can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fr.efl.chaine.xslt.utils;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import net.sf.saxon.s9api.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merges parameters
 * @author Christophe Marchand
 */
public class ParametersMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParametersMerger.class);
    
    private static final QName INPUT_BASENAME = new QName("input-basename");
    private static final QName INPUT_NAME = new QName("input-name");
    private static final QName INPUT_EXTENSION = new QName("input-extension");
    private static final QName INPUT_ABSOLUTE = new QName("input-absolute");


    /**
     * Merges the two lists into a new list, and let the two original lists unchanged.
     * @param highPriority First list of parameters
     * @param lowPriority Second list of parameters
     * @return A new list that contains all elements from <tt>l1</tt> and <tt>l2</tt>
     */
    public static HashMap<QName,ParameterValue> merge(final HashMap<QName,ParameterValue> highPriority, final HashMap<QName,ParameterValue> lowPriority) {
//        LOGGER.debug("merging : "+highPriority+" with "+lowPriority);
        HashMap<QName,ParameterValue> ret = new HashMap<>();
        ret.putAll(highPriority);
        for(ParameterValue pv: lowPriority.values()) {
            if(!ret.containsKey(pv.getKey()))
                ret.put(pv.getKey(), pv);
        }
        // on fait les éventuelles substitutions, pour ne les faire qu'une fois.
        for(ParameterValue pv:ret.values()) {
            pv.setValue(processParametersReplacement(pv.getValue(), ret));
        }
        return ret;
    }
    
    /**
     * Replaces the parameters in string
     * @param initialValue The String to change parameters in
     * @param parameters The parameters values
     * @return The initialValue with all parameters replaced
     */
    public static Object processParametersReplacement(Object initialValue, final HashMap<QName,ParameterValue> parameters) {
        if(initialValue instanceof String) {
            String ret = initialValue.toString();
            if(ret.contains("$[")) {
                for(ParameterValue pv: parameters.values()) {
                    if(pv.getValue() instanceof String) {
                        try {
                            // issue #14
                            ret = ret.replaceAll("\\$\\["+pv.getKey()+"\\]", Matcher.quoteReplacement(pv.getValue().toString()));
                        } catch(java.lang.IllegalArgumentException ex) {
                            LOGGER.error("while replacing "+pv.getKey()+" -> "+pv.getValue(),ex);
                            throw ex;
                        }
                    }
                    if(!ret.contains("$[")) break;
                }
            }
            return ret;
        } else {
            return initialValue;
        }
    }
    /**
     * Replaces the parameters in string
     * @param initialValue The String to change parameters in
     * @param parameters The parameters values
     * @return The initialValue with all parameters replaced
     */
    public static String processParametersReplacement(String initialValue, final HashMap<QName,ParameterValue> parameters) {
        String ret = initialValue;
        if(ret.contains("$[")) {
            for(ParameterValue pv: parameters.values()) {
                if(pv.getValue() instanceof String) {
                    try {
                        // issue #14
                        ret = ret.replaceAll("\\$\\["+pv.getKey()+"\\]", Matcher.quoteReplacement(pv.getValue().toString()));
                    } catch(java.lang.IllegalArgumentException ex) {
                        LOGGER.error("while replacing "+pv.getKey()+" -> "+pv.getValue(),ex);
                        throw ex;
                    }
                }
                if(!ret.contains("$[")) break;
            }
        }
        return ret;
    }
    /**
     * Replaces the parameters in string
     * @param parameters The parameters values
     * @param inputFile The input file actually processed. input-basename, input-name, input-absolute and input-extension are added, so can be used.
     * 
     * @return The parameters whith input-name, input-basename, input-absolute and input-extension added
     */
    public static HashMap<QName,ParameterValue> addInputInParameters(final HashMap<QName,ParameterValue> parameters, final File inputFile) {
        HashMap<QName,ParameterValue> fileParams = new HashMap<>();
        String name = inputFile.getName();
        String basename = name.substring(0, name.lastIndexOf("."));
        String extension = name.substring(basename.length()+1);
        fileParams.put(INPUT_BASENAME, new ParameterValue(INPUT_BASENAME, basename));
        fileParams.put(INPUT_NAME, new ParameterValue(INPUT_NAME, name));
        fileParams.put(INPUT_EXTENSION, new ParameterValue(INPUT_EXTENSION, extension));
        fileParams.put(INPUT_ABSOLUTE, new ParameterValue(INPUT_ABSOLUTE, inputFile.getAbsolutePath()));
        return merge(parameters, fileParams);
    }
}
