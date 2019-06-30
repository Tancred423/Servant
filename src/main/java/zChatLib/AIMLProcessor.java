package zChatLib;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AIMLProcessor {
    private static int sraiCount = 0;
    static int trace_count = 0;

    static String respond(String input, String that, String topic, zChatLib.Chat chatSession) {
        return respond(input, that, topic, chatSession, 0);
    }

    private static String respond(String input, String that, String topic, zChatLib.Chat chatSession, int srCnt) {
        if (input == null || input.length() == 0) input = MagicStrings.null_input;

        sraiCount = srCnt;
        String response = MagicStrings.default_bot_response;

        try {
            Nodemapper leaf = chatSession.bot.brain.match(input, that, topic);
            if (leaf == null) return response;

            ParseState ps = new ParseState(0, chatSession, input, that, topic, leaf);
            response = evalTemplate(leaf.category.getTemplate(), ps);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return response;
    }

    private static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;

        for(int i = 0; i < chars.length; ++i)
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) found = false;

        return String.valueOf(chars);
    }

    private static String explode(String input) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < input.length(); ++i) result.append(" ").append(input.charAt(i));
        return result.toString().trim();
    }

    private static String evalTagContent(Node node, ParseState ps, Set<String> ignoreAttributes) {
        StringBuilder result = new StringBuilder();

        try {
            NodeList childList = node.getChildNodes();

            for(int i = 0; i < childList.getLength(); ++i) {
                Node child = childList.item(i);
                if (ignoreAttributes == null || !ignoreAttributes.contains(child.getNodeName()))
                    result.append(recursEval(child, ps));
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return result.toString();
    }

    private static String genericXML(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return unevaluatedXML(result, node);
    }

    private static String unevaluatedXML(String result, Node node) {
        String nodeName = node.getNodeName();
        StringBuilder attributes = new StringBuilder();
        if (node.hasAttributes()) {
            NamedNodeMap XMLAttributes = node.getAttributes();
            for(int i = 0; i < XMLAttributes.getLength(); ++i)
                attributes.append(" ").append(XMLAttributes.item(i).getNodeName()).append("=\"").append(XMLAttributes.item(i).getNodeValue()).append("\"");
        }

        return result.equals("") ? "<" + nodeName + attributes + "/>" : "<" + nodeName + attributes + ">" + result + "</" + nodeName + ">";
    }

    private static String srai(Node node, ParseState ps) {
        ++sraiCount;
        if (sraiCount > MagicNumbers.max_recursion) return MagicStrings.too_much_recursion;
        else {
            String response = MagicStrings.default_bot_response;

            try {
                String result = evalTagContent(node, ps, null);
                result = result.trim();
                result = result.replaceAll("(\r\n|\n\r|\r|\n)", " ");
                result = ps.chatSession.bot.preProcessor.normalize(result);
                String topic = ps.chatSession.predicates.get("topic");
                if (MagicBooleans.trace_mode) ++trace_count;

                Nodemapper leaf = ps.chatSession.bot.brain.match(result, ps.that, topic);
                if (leaf == null) return response;

                response = evalTemplate(leaf.category.getTemplate(), new ParseState(ps.depth + 1, ps.chatSession, ps.input, ps.that, topic, leaf));
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            return response.trim();
        }
    }

    private static String getAttributeOrTagValue(Node node, ParseState ps, String attributeName) {
        String result;
        Node m = node.getAttributes().getNamedItem(attributeName);
        if (m == null) {
            NodeList childList = node.getChildNodes();
            result = null;

            for(int i = 0; i < childList.getLength(); ++i) {
                Node child = childList.item(i);
                if (child.getNodeName().equals(attributeName)) {
                    result = evalTagContent(child, ps, null);
                }
            }
        } else result = m.getNodeValue();

        return result;
    }

    private static String sraix(Node node, ParseState ps) {
        HashSet<String> attributeNames = Utilities.stringSet("botid", "host");
        String host = getAttributeOrTagValue(node, ps, "host");
        String botid = getAttributeOrTagValue(node, ps, "botid");
        String hint = getAttributeOrTagValue(node, ps, "hint");
        String defaultResponse = getAttributeOrTagValue(node, ps, "default");
        String result = evalTagContent(node, ps, attributeNames);
        return Sraix.sraix(ps.chatSession, result, defaultResponse, hint, host, botid);
    }

    private static String map(Node node, ParseState ps) {
        String result = MagicStrings.unknown_map_value;
        HashSet<String> attributeNames = Utilities.stringSet("name");
        String mapName = getAttributeOrTagValue(node, ps, "name");
        String contents = evalTagContent(node, ps, attributeNames);
        if (mapName == null) result = "<map>" + contents + "</map>";
        else {
            AIMLMap map = ps.chatSession.bot.mapMap.get(mapName);
            if (map != null) result = map.get(contents.toUpperCase());
            if (result == null) result = MagicStrings.unknown_map_value;
            result = result.trim();
        }

        return result;
    }

    private static String set(Node node, ParseState ps) {
        HashSet<String> attributeNames = Utilities.stringSet("name", "var");
        String predicateName = getAttributeOrTagValue(node, ps, "name");
        String varName = getAttributeOrTagValue(node, ps, "var");
        String value = evalTagContent(node, ps, attributeNames).trim();
        value = value.replaceAll("(\r\n|\n\r|\r|\n)", " ");
        if (predicateName != null) ps.chatSession.predicates.put(predicateName, value);
        if (varName != null) ps.vars.put(varName, value);
        return value;
    }

    private static String get(Node node, ParseState ps) {
        String result = MagicStrings.unknown_predicate_value;
        String predicateName = getAttributeOrTagValue(node, ps, "name");
        String varName = getAttributeOrTagValue(node, ps, "var");
        if (predicateName != null) result = ps.chatSession.predicates.get(predicateName).trim();
        else if (varName != null) result = ps.vars.get(varName).trim();
        return result;
    }

    private static String bot(Node node, ParseState ps) {
        String result = MagicStrings.unknown_property_value;
        String propertyName = getAttributeOrTagValue(node, ps, "name");
        if (propertyName != null) result = ps.chatSession.bot.properties.get(propertyName).trim();
        return result;
    }

    private static String date(Node node, ParseState ps) {
        String jformat = getAttributeOrTagValue(node, ps, "jformat");
        String locale = getAttributeOrTagValue(node, ps, "locale");
        String timezone = getAttributeOrTagValue(node, ps, "timezone");
        return CalendarUtils.date(jformat, locale, timezone);
    }

    private static String interval(Node node, ParseState ps) {
        String style = getAttributeOrTagValue(node, ps, "style");
        String jformat = getAttributeOrTagValue(node, ps, "jformat");
        String from = getAttributeOrTagValue(node, ps, "from");
        String to = getAttributeOrTagValue(node, ps, "to");

        if (style == null) style = "years";
        if (jformat == null) jformat = "MMMMMMMMM dd, yyyy";
        if (from == null) from = "January 1, 1970";
        if (to == null) to = CalendarUtils.date(jformat, null, null);

        String result = "unknown";
        if (style.equals("years")) result = "" + Interval.getYearsBetween(from, to, jformat);
        if (style.equals("months")) result = "" + Interval.getMonthsBetween(from, to, jformat);
        if (style.equals("days")) result = "" + Interval.getDaysBetween(from, to, jformat);
        if (style.equals("hours")) result = "" + Interval.getHoursBetween(from, to, jformat);

        return result;
    }

    private static int getIndexValue(Node node, ParseState ps) {
        int index = 0;
        String value = getAttributeOrTagValue(node, ps, "index");
        if (value != null) {
            try {
                index = Integer.parseInt(value) - 1;
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return index;
    }

    private static String inputStar(Node node, ParseState ps) {
        int index = getIndexValue(node, ps);
        return ps.leaf.starBindings.inputStars.star(index) == null ? "" : ps.leaf.starBindings.inputStars.star(index).trim();
    }

    private static String thatStar(Node node, ParseState ps) {
        int index = getIndexValue(node, ps);
        return ps.leaf.starBindings.thatStars.star(index) == null ? "" : ps.leaf.starBindings.thatStars.star(index).trim();
    }

    private static String topicStar(Node node, ParseState ps) {
        int index = getIndexValue(node, ps);
        return ps.leaf.starBindings.topicStars.star(index) == null ? "" : ps.leaf.starBindings.topicStars.star(index).trim();
    }

    private static String id(ParseState ps) {
        return ps.chatSession.customerId;
    }

    private static String size(ParseState ps) {
        int size = ps.chatSession.bot.brain.getCategories().size();
        return String.valueOf(size);
    }

    private static String vocabulary(ParseState ps) {
        int size = ps.chatSession.bot.brain.getVocabulary().size();
        return String.valueOf(size);
    }

    private static String program() {
        return MagicStrings.programNameVersion;
    }

    private static String that(Node node, ParseState ps) {
        int index = 0;
        int jndex = 0;
        String value = getAttributeOrTagValue(node, ps, "index");
        if (value != null) {
            try {
                String[] spair = value.split(",");
                index = Integer.parseInt(spair[0]) - 1;
                jndex = Integer.parseInt(spair[1]) - 1;
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

        String that = MagicStrings.unknown_history_item;
        History<String> hist = ps.chatSession.thatHistory.get(index);
        if (hist != null) that = hist.get(jndex);

        return that.trim();
    }

    private static String input(Node node, ParseState ps) {
        int index = getIndexValue(node, ps);
        return ps.chatSession.inputHistory.getString(index);
    }

    private static String request(Node node, ParseState ps) {
        int index = getIndexValue(node, ps);
        return ps.chatSession.requestHistory.getString(index).trim();
    }

    private static String response(Node node, ParseState ps) {
        int index = getIndexValue(node, ps);
        return ps.chatSession.responseHistory.getString(index).trim();
    }

    private static String system(Node node, ParseState ps) {
        HashSet<String> attributeNames = Utilities.stringSet("timeout");
        String evaluatedContents = evalTagContent(node, ps, attributeNames);
        return IOUtils.system();
    }

    private static String think(Node node, ParseState ps) {
        evalTagContent(node, ps, null);
        return "";
    }

    private static String explode(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return explode(result);
    }

    private static String normalize(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return ps.chatSession.bot.preProcessor.normalize(result);
    }

    private static String denormalize(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return ps.chatSession.bot.preProcessor.denormalize(result);
    }

    private static String uppercase(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return result.toUpperCase();
    }

    private static String lowercase(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return result.toLowerCase();
    }

    private static String formal(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return capitalizeString(result);
    }

    private static String sentence(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        return result.length() > 1 ? result.substring(0, 1).toUpperCase() + result.substring(1) : "";
    }

    private static String person(Node node, ParseState ps) {
        String result;
        if (node.hasChildNodes()) result = evalTagContent(node, ps, null);
        else result = ps.leaf.starBindings.inputStars.star(0);

        result = " " + result + " ";
        result = ps.chatSession.bot.preProcessor.person(result);
        return result.trim();
    }

    private static String person2(Node node, ParseState ps) {
        String result;
        if (node.hasChildNodes()) result = evalTagContent(node, ps, null);
        else result = ps.leaf.starBindings.inputStars.star(0);

        result = " " + result + " ";
        result = ps.chatSession.bot.preProcessor.person2(result);
        return result.trim();
    }

    private static String gender(Node node, ParseState ps) {
        String result = evalTagContent(node, ps, null);
        result = " " + result + " ";
        result = ps.chatSession.bot.preProcessor.gender(result);
        return result.trim();
    }

    private static String random(Node node, ParseState ps) {
        NodeList childList = node.getChildNodes();
        List<Node> liList = new ArrayList<>();

        for(int i = 0; i < childList.getLength(); ++i)
            if (childList.item(i).getNodeName().equals("li")) liList.add(childList.item(i));

        return evalTagContent(liList.get((int)(Math.random() * (double)liList.size())), ps, null);
    }

    private static String unevaluatedAIML(Node node, ParseState ps) {
        String result = learnEvalTagContent(node, ps);
        return unevaluatedXML(result, node);
    }

    private static String recursLearn(Node node, ParseState ps) {
        String nodeName = node.getNodeName();
        if (nodeName.equals("#text")) return node.getNodeValue();
        else return nodeName.equals("eval") ? evalTagContent(node, ps, null) : unevaluatedAIML(node, ps);
    }

    private static String learnEvalTagContent(Node node, ParseState ps) {
        StringBuilder result = new StringBuilder();
        NodeList childList = node.getChildNodes();

        for(int i = 0; i < childList.getLength(); ++i) {
            Node child = childList.item(i);
            result.append(recursLearn(child, ps));
        }

        return result.toString();
    }

    private static String learn(Node node, ParseState ps) {
        NodeList childList = node.getChildNodes();
        String pattern = "";
        String that = "*";
        String template = "";

        for(int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("category")) {
                NodeList grandChildList = childList.item(i).getChildNodes();

                for(int j = 0; j < grandChildList.getLength(); ++j)
                    switch (grandChildList.item(j).getNodeName()) {
                        case "pattern":
                            pattern = recursLearn(grandChildList.item(j), ps);
                            break;
                        case "that":
                            that = recursLearn(grandChildList.item(j), ps);
                            break;
                        case "template":
                            template = recursLearn(grandChildList.item(j), ps);
                            break;
                    }

                pattern = pattern.substring("<pattern>".length(), pattern.length() - "</pattern>".length());
                if (template.length() >= "<template></template>".length())
                    template = template.substring("<template>".length(), template.length() - "</template>".length());

                if (that.length() >= "<that></that>".length())
                    that = that.substring("<that>".length(), that.length() - "</that>".length());

                pattern = pattern.toUpperCase();
                that = that.toUpperCase();

                Category c;
                if (node.getNodeName().equals("learn"))
                    c = new Category(0, pattern, that, "*", template, MagicStrings.null_aiml_file);
                else {
                    c = new Category(0, pattern, that, "*", template, MagicStrings.learnf_aiml_file);
                    ps.chatSession.bot.learnfGraph.addCategory(c);
                }

                ps.chatSession.bot.brain.addCategory(c);
            }
        }

        return "";
    }

    private static String loopCondition(Node node, ParseState ps) {
        boolean loop = true;
        String result = "";

        byte loopCnt;
        String loopResult;
        for(loopCnt = 0; loop && loopCnt < MagicNumbers.max_loops; result = result + loopResult) {
            loopResult = condition(node, ps);
            if (loopResult.trim().equals(MagicStrings.too_much_recursion)) return MagicStrings.too_much_recursion;

            if (loopResult.contains("<loop/>")) {
                loopResult = loopResult.replace("<loop/>", "");
                loop = true;
            } else loop = false;
        }

        if (loopCnt >= MagicNumbers.max_loops) result = MagicStrings.too_much_looping;
        return result;
    }

    private static String condition(Node node, ParseState ps) {
        NodeList childList = node.getChildNodes();
        ArrayList<Node> liList = new ArrayList<>();
        String predicate;
        String varName;
        String value;
        HashSet<String> attributeNames = Utilities.stringSet("name", "var", "value");
        predicate = getAttributeOrTagValue(node, ps, "name");
        varName = getAttributeOrTagValue(node, ps, "var");

        int i;
        for(i = 0; i < childList.getLength(); ++i)
            if (childList.item(i).getNodeName().equals("li")) liList.add(childList.item(i));

        if (liList.size() == 0 && (value = getAttributeOrTagValue(node, ps, "value")) != null && predicate != null && ps.chatSession.predicates.get(predicate).equals(value))
            return evalTagContent(node, ps, attributeNames);
        else if (liList.size() == 0 && (value = getAttributeOrTagValue(node, ps, "value")) != null && varName != null && ps.vars.get(varName).equals(value))
            return evalTagContent(node, ps, attributeNames);
        else {
            i = 0;

            while(true) {
                if (i < liList.size()) {
                    Node n = liList.get(i);
                    String liPredicate = predicate;
                    String liVarName = varName;
                    if (predicate == null) liPredicate = getAttributeOrTagValue(n, ps, "name");
                    if (varName == null) liVarName = getAttributeOrTagValue(n, ps, "var");

                    value = getAttributeOrTagValue(n, ps, "value");
                    if (value == null) return evalTagContent(n, ps, attributeNames);

                    if (liPredicate != null && (ps.chatSession.predicates.get(liPredicate).equals(value) || ps.chatSession.predicates.containsKey(liPredicate) && value.equals("*")))
                        return evalTagContent(n, ps, attributeNames);

                    if (liVarName == null || !ps.vars.get(liVarName).equals(value) && (!ps.vars.containsKey(liPredicate) || !value.equals("*"))) {
                        ++i;
                        continue;
                    }

                    return evalTagContent(n, ps, attributeNames);
                }

                return "";
            }
        }
    }

    private static String recursEval(Node node, ParseState ps) {
        try {
            String nodeName = node.getNodeName();
            if (nodeName.equals("#text")) return node.getNodeValue();
            else if (nodeName.equals("#comment")) return "";
            else if (nodeName.equals("template")) return evalTagContent(node, ps, null);
            else if (nodeName.equals("random")) return random(node, ps);
            else if (nodeName.equals("condition")) return loopCondition(node, ps);
            else if (nodeName.equals("srai")) return srai(node, ps);
            else if (nodeName.equals("sr")) return respond(ps.leaf.starBindings.inputStars.star(0), ps.that, ps.topic, ps.chatSession, sraiCount);
            else if (nodeName.equals("sraix")) return sraix(node, ps);
            else if (nodeName.equals("set")) return set(node, ps);
            else if (nodeName.equals("get")) return get(node, ps);
            else if (nodeName.equals("map")) return map(node, ps);
            else if (nodeName.equals("bot")) return bot(node, ps);
            else if (nodeName.equals("id")) return id(ps);
            else if (nodeName.equals("size")) return size(ps);
            else if (nodeName.equals("vocabulary")) return vocabulary(ps);
            else if (nodeName.equals("program")) return program();
            else if (nodeName.equals("date")) return date(node, ps);
            else if (nodeName.equals("interval")) return interval(node, ps);
            else if (nodeName.equals("think")) return think(node, ps);
            else if (nodeName.equals("system")) return system(node, ps);
            else if (nodeName.equals("explode")) return explode(node, ps);
            else if (nodeName.equals("normalize")) return normalize(node, ps);
            else if (nodeName.equals("denormalize")) return denormalize(node, ps);
            else if (nodeName.equals("uppercase")) return uppercase(node, ps);
            else if (nodeName.equals("lowercase")) return lowercase(node, ps);
            else if (nodeName.equals("formal")) return formal(node, ps);
            else if (nodeName.equals("sentence")) return sentence(node, ps);
            else if (nodeName.equals("person")) return person(node, ps);
            else if (nodeName.equals("person2")) return person2(node, ps);
            else if (nodeName.equals("gender")) return gender(node, ps);
            else if (nodeName.equals("star")) return inputStar(node, ps);
            else if (nodeName.equals("thatstar")) return thatStar(node, ps);
            else if (nodeName.equals("topicstar")) return topicStar(node, ps);
            else if (nodeName.equals("that")) return that(node, ps);
            else if (nodeName.equals("input")) return input(node, ps);
            else if (nodeName.equals("request")) return request(node, ps);
            else if (nodeName.equals("response")) return response(node, ps);
            else if (!nodeName.equals("learn") && !nodeName.equals("learnf")) return genericXML(node, ps);
            else return learn(node, ps);
        } catch (Exception var3) {
            var3.printStackTrace();
            return "";
        }
    }

    private static String evalTemplate(String template, ParseState ps) {
        String response = MagicStrings.template_failed;

        try {
            template = "<template>" + template + "</template>";
            Node root = DomUtils.parseString(template);
            response = recursEval(root, ps);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return response;
    }

    static List<Category> AIMLToCategories(String directory, String aimlFile) {
        try {
            List<Category> categories = new ArrayList<>();
            Node root = DomUtils.parseFile(directory + "/" + aimlFile);
            String language = MagicStrings.default_language;
            int i;
            if (root.hasAttributes()) {
                NamedNodeMap XMLAttributes = root.getAttributes();

                for(i = 0; i < XMLAttributes.getLength(); ++i) {
                    if (XMLAttributes.item(i).getNodeName().equals("language")) {
                        language = XMLAttributes.item(i).getNodeValue();
                    }
                }
            }

            NodeList nodelist = root.getChildNodes();

            for(i = 0; i < nodelist.getLength(); ++i) {
                Node n = nodelist.item(i);
                if (n.getNodeName().equals("category")) {
                    categoryProcessor(n, categories, "*", aimlFile, language);
                } else if (n.getNodeName().equals("topic")) {
                    String topic = n.getAttributes().getNamedItem("name").getTextContent();
                    NodeList children = n.getChildNodes();

                    for(int j = 0; j < children.getLength(); ++j) {
                        Node m = children.item(j);
                        if (m.getNodeName().equals("category")) {
                            categoryProcessor(m, categories, topic, aimlFile, language);
                        }
                    }
                }
            }

            return categories;
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }

    private static void categoryProcessor(Node n, List<Category> categories, String topic, String aimlFile, String language) {
        NodeList children = n.getChildNodes();
        String pattern = "*";
        String that = "*";
        String template = "";

        String mName;
        for(int j = 0; j < children.getLength(); ++j) {
            Node m = children.item(j);
            mName = m.getNodeName();
            if (!mName.equals("#text")) {
                switch (mName) {
                    case "pattern":
                        pattern = DomUtils.nodeToString(m);
                        break;
                    case "that":
                        that = DomUtils.nodeToString(m);
                        break;
                    case "topic":
                        topic = DomUtils.nodeToString(m);
                        break;
                    case "template":
                        template = DomUtils.nodeToString(m);
                        break;
                    default:
                        break;
                }
            }
        }

        pattern = trimTag(pattern, "pattern");
        that = trimTag(that, "that");
        topic = trimTag(topic, "topic");
        template = trimTag(template, "template");
        if (language.equals("JP") || language.equals("jp")) {
            pattern = JapaneseTokenizer.morphSentence(pattern);
            that = JapaneseTokenizer.morphSentence(that);
            mName = JapaneseTokenizer.morphSentence(topic);
            topic = mName;
        }

        Category c = new Category(0, pattern, that, topic, template, aimlFile);
        categories.add(c);
    }

    private static String trimTag(String s, String tagName) {
        String stag = "<" + tagName + ">";
        String etag = "</" + tagName + ">";
        if (s.startsWith(stag) && s.endsWith(etag)) {
            s = s.substring(stag.length());
            s = s.substring(0, s.length() - etag.length());
        }

        return s.trim();
    }

    static boolean validTemplate(String template) {
        try {
            template = "<template>" + template + "</template>";
            DomUtils.parseString(template);
            return true;
        } catch (Exception var2) {
            return false;
        }
    }
}
