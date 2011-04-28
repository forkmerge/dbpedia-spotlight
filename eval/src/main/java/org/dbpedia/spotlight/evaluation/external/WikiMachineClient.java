/*
 * *
 *  * Copyright 2011 Pablo Mendes, Max Jakob
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dbpedia.spotlight.evaluation.external;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.Element;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;



/**
 *
 * @author pablomendes (first incomplete version, final touches to Andres' code.)
 * @author Andres Garcia-Silva (main implementation)
 */
public class WikiMachineClient extends AnnotationClient {

    String wikiPrefix = "http://en.wikipedia.org/wiki/";

    @Override
    public List<DBpediaResource> extract(Text text) throws AnnotationException {
        String response = process(text.text());
        //System.out.println(response);
        return parse(response);
    }

    protected String process(String text) {
        String url = "http://thewikimachine.fbk.eu/gui/basic";
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Content-type","application/x-www-form-urlencoded");

        NameValuePair[] params = {
                new NameValuePair("context",text),
                new NameValuePair("type","link"),
                new NameValuePair("si","10"),
        };
        method.setRequestBody(params);
        LOG.debug("Sending request to WikiMachine: "+params);

        String response = request(method);
        return response;
    }

    public List<DBpediaResource> parse(String html) throws AnnotationException {

        Source parser;
        String wikiUrl;
        String surfaceForm;
        List<DBpediaResource> entities = new ArrayList<DBpediaResource>();
        if (html==null || html.equals("")) return entities;

        try {
            InputStream is = new ByteArrayInputStream(html.getBytes("UTF-8"));
            parser = new Source(is);
            parser.fullSequentialParse();
            parser.getElementById("div");
        } catch (IOException e) {
            throw new AnnotationException("Error reading output from WikiMachine ",e);
        }
        List<Element>KeywordElements=parser.getAllElementsByClass("keywords");

        if (KeywordElements!=null && !KeywordElements.isEmpty()){
            Element keywordElement= KeywordElements.get(0);
            for (Element linkElement : keywordElement.getAllElements()) {
                wikiUrl="";
                surfaceForm="";
                wikiUrl=linkElement.getAttributeValue("href");
                if (wikiUrl!=null)
                    if (wikiUrl.startsWith(wikiPrefix)) {
                        surfaceForm = linkElement.getContent().getTextExtractor().toString();
                        entities.add(new DBpediaResource(wikiUrl.replaceAll(wikiPrefix,"")));
                        //System.out.println(surfaceForm+" "+wikiUrl);
                    }
            }
        }
        LOG.trace(entities);
        return entities;
    }

    public static void main(String[] args) throws Exception {

        WikiMachineClient client = new WikiMachineClient();

        //File manualEvalOutput = new File("/home/pablo/eval/manual/systems/WikiMachine.list");
        //File manualEvalInput = new File("/home/pablo/eval/manual/AnnotationText.txt");
        //client.evaluateManual(manualEvalInput, manualEvalOutput);

        //File cucerzanEvalInput = new File("/home/pablo/eval/cucerzan/cucerzan.txt");
        //File cucerzanEvalOutput = new File("/home/pablo/eval/cucerzan/systems/WikiMachine.list");
//        client.evaluateManual(cucerzanEvalInput, cucerzanEvalOutput);

        File wikifyEvalInput = new File("/home/pablo/eval/wikify/gold/WikifyAllInOne.txt");
        File wikifyEvalOutput = new File("/home/pablo/eval/wikify/systems/WikiMachine.list");
        client.evaluateManual(wikifyEvalInput, wikifyEvalOutput);

    }



}
