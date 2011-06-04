/**
 * Copyright 2011 Pablo Mendes, Max Jakob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dbpedia.spotlight.web.rest.output;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("annotation")
public class Annotation {

    @XStreamAsAttribute
    private String text;

    @XStreamImplicit
    private List<Spot> spots;

    public Annotation(String text) {
        this.setText(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }


    private static OutputSerializer serializer = new OutputSerializer();
    public String toXML() {
        return serializer.toXML(this);
    }
    @Override
    public String toString() {
        return toXML();
    }
}
