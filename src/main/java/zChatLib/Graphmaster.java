package zChatLib;

import java.util.ArrayList;
import java.util.HashSet;

public class Graphmaster {
    public zChatLib.Bot bot;
    public final Nodemapper root = new Nodemapper();
    private HashSet<String> vocabulary;

    Graphmaster(zChatLib.Bot bot) {
        this.bot = bot;
        this.vocabulary = new HashSet<>();
    }

    static String inputThatTopic(String input, String that, String topic) {
        return input.trim() + " <THAT> " + that.trim() + " <TOPIC> " + topic.trim();
    }

    void addCategory(Category category) {
        Path p = Path.sentenceToPath(inputThatTopic(category.getPattern(), category.getThat(), category.getTopic()));
        this.addPath(p, category);
    }

    private void addSets(String type, Bot bot, Nodemapper node) {
        String typeName = Utilities.tagTrim(type).toLowerCase();
        if (bot.setMap.containsKey(typeName)) {
            if (node.sets == null) node.sets = new ArrayList<>();
            node.sets.add(typeName);
        }
    }

    private void addPath(Path path, Category category) {
        this.addPath(this.root, path, category);
    }

    private void addPath(Nodemapper node, Path path, Category category) {
        if (path == null) {
            node.category = category;
            node.height = 0;
        } else {
            Nodemapper nextNode;
            byte offset;
            if (NodemapperOperator.containsKey(node, path.word)) {
                if (path.word.startsWith("<SET>")) {
                    this.addSets(path.word, this.bot, node);
                }

                nextNode = NodemapperOperator.get(node, path.word);
                this.addPath(nextNode, path.next, category);
                offset = 1;
                if (path.word.equals("#") || path.word.equals("^")) offset = 0;

                assert nextNode != null;
                node.height = Math.min(offset + nextNode.height, node.height);
            } else {
                nextNode = new Nodemapper();
                if (path.word.startsWith("<SET>")) this.addSets(path.word, this.bot, node);

                if (node.key != null) NodemapperOperator.upgrade(node);

                NodemapperOperator.put(node, path.word, nextNode);
                this.addPath(nextNode, path.next, category);
                offset = 1;
                if (path.word.equals("#") || path.word.equals("^")) {
                    offset = 0;
                }

                node.height = Math.min(offset + nextNode.height, node.height);
            }
        }

    }

    Nodemapper findNode(Category c) {
        return this.findNode(c.getPattern(), c.getThat(), c.getTopic());
    }

    private Nodemapper findNode(String input, String that, String topic) {
        return this.findNode(this.root, Path.sentenceToPath(inputThatTopic(input, that, topic)));
    }

    private Nodemapper findNode(Nodemapper node, Path path) {
        if (path == null && node != null) return node;
        else {
            assert node != null;
            if (Path.pathToSentence(path).trim().equals("<THAT> * <TOPIC> *") && node.shortCut && path.word.equals("<THAT>"))
                return node;
            else if (NodemapperOperator.containsKey(node, path.word)) {
                Nodemapper nextNode = NodemapperOperator.get(node, path.word.toUpperCase());
                return this.findNode(nextNode, path.next);
            } else return null;
        }
    }

    final Nodemapper match(String input, String that, String topic) {
        Nodemapper n;

        try {
            String inputThatTopic = inputThatTopic(input, that, topic);
            Path p = Path.sentenceToPath(inputThatTopic);
            n = this.match(p, inputThatTopic);
        } catch (Exception var7) {
            var7.printStackTrace();
            n = null;
        }

        if (MagicBooleans.trace_mode && Chat.matchTrace.length() < MagicNumbers.max_trace_length && n != null)
            Chat.setMatchTrace(Chat.matchTrace + n.category.inputThatTopic() + "\n");

        return n;
    }

    private Nodemapper match(Path path, String inputThatTopic) {
        try {
            String[] inputStars = new String[MagicNumbers.max_stars];
            String[] thatStars = new String[MagicNumbers.max_stars];
            String[] topicStars = new String[MagicNumbers.max_stars];
            String starState = "inputStar";
            String matchTrace = "";
            Nodemapper n = this.match(path, this.root, inputThatTopic, starState, 0, inputStars, thatStars, topicStars, matchTrace);
            if (n != null) {
                StarBindings sb = new StarBindings();

                int i;
                for(i = 0; inputStars[i] != null && i < MagicNumbers.max_stars; ++i) sb.inputStars.add(inputStars[i]);
                for(i = 0; thatStars[i] != null && i < MagicNumbers.max_stars; ++i) sb.thatStars.add(thatStars[i]);
                for(i = 0; topicStars[i] != null && i < MagicNumbers.max_stars; ++i) sb.topicStars.add(topicStars[i]);

                n.starBindings = sb;
            }

            if (n != null) n.category.addMatch(inputThatTopic);

            return n;
        } catch (Exception var11) {
            var11.printStackTrace();
            return null;
        }
    }

    private Nodemapper match(Path path, Nodemapper node, String inputThatTopic, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        Nodemapper matchedNode;
        if ((matchedNode = this.nullMatch(path, node)) != null) return matchedNode;
        else if (path.length < node.height) return null;
        else if ((matchedNode = this.dollarMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else if ((matchedNode = this.sharpMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else if ((matchedNode = this.underMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else if ((matchedNode = this.wordMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else if ((matchedNode = this.setMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else if ((matchedNode = this.shortCutMatch(path, node, thatStars, topicStars)) != null)
            return matchedNode;
        else if ((matchedNode = this.caretMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else
            return (matchedNode = this.starMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null ? matchedNode : null;
    }

    private Nodemapper nullMatch(Path path, Nodemapper node) {
        if (path == null && node != null && NodemapperOperator.isLeaf(node) && node.category != null) return node;
        else return null;
    }

    private Nodemapper shortCutMatch(Path path, Nodemapper node, String[] thatStars, String[] topicStars) {
        if (node != null && node.shortCut && path.word.equals("<THAT>") && node.category != null) {
            String tail = Path.pathToSentence(path).trim();
            String that = tail.substring(tail.indexOf("<THAT>") + "<THAT>".length(), tail.indexOf("<TOPIC>")).trim();
            String topic = tail.substring(tail.indexOf("<TOPIC>") + "<TOPIC>".length()).trim();
            thatStars[0] = that;
            topicStars[0] = topic;
            return node;
        } else {
            return null;
        }
    }

    private Nodemapper wordMatch(Path path, Nodemapper node, String inputThatTopic, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        try {
            String uword = path.word.toUpperCase();
            if (uword.equals("<THAT>")) {
                starIndex = 0;
                starState = "thatStar";
            } else if (uword.equals("<TOPIC>")) {
                starIndex = 0;
                starState = "topicStar";
            }

            matchTrace = matchTrace + "[" + uword + "," + uword + "]";
            Nodemapper matchedNode;
            if (NodemapperOperator.containsKey(node, uword) && (matchedNode = this.match(path.next, NodemapperOperator.get(node, uword), inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
                return matchedNode;
            else return null;
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }

    private Nodemapper dollarMatch(Path path, Nodemapper node, String inputThatTopic, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        String uword = "$" + path.word.toUpperCase();
        Nodemapper matchedNode;
        if (NodemapperOperator.containsKey(node, uword) && (matchedNode = this.match(path.next, NodemapperOperator.get(node, uword), inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null)
            return matchedNode;
        else return null;
    }

    private Nodemapper starMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        return this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "*", matchTrace);
    }

    private Nodemapper underMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        return this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "_", matchTrace);
    }

    private Nodemapper caretMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        Nodemapper matchedNode = this.zeroMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "^", matchTrace);
        return matchedNode != null ? matchedNode : this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "^", matchTrace);
    }

    private Nodemapper sharpMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        Nodemapper matchedNode = this.zeroMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "#", matchTrace);
        return matchedNode != null ? matchedNode : this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "#", matchTrace);
    }

    private Nodemapper zeroMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String wildcard, String matchTrace) {
        matchTrace = matchTrace + "[" + wildcard + ",]";
        if (path != null && NodemapperOperator.containsKey(node, wildcard)) {
            this.setStars(this.bot.properties.get(MagicStrings.null_star), starIndex, starState, inputStars, thatStars, topicStars);
            Nodemapper nextNode = NodemapperOperator.get(node, wildcard);
            return this.match(path, nextNode, input, starState, starIndex + 1, inputStars, thatStars, topicStars, matchTrace);
        } else return null;
    }

    private Nodemapper wildMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String wildcard, String matchTrace) {
        if (!path.word.equals("<THAT>") && !path.word.equals("<TOPIC>")) {
            try {
                if (NodemapperOperator.containsKey(node, wildcard)) {
                    matchTrace = matchTrace + "[" + wildcard + "," + path.word + "]";
                    String currentWord = path.word;
                    StringBuilder starWords = new StringBuilder(currentWord + " ");
                    Path pathStart = path.next;
                    Nodemapper nextNode = NodemapperOperator.get(node, wildcard);
                    assert nextNode != null;
                    if (NodemapperOperator.isLeaf(nextNode) && !nextNode.shortCut) {
                        starWords = new StringBuilder(Path.pathToSentence(path));
                        this.setStars(starWords.toString(), starIndex, starState, inputStars, thatStars, topicStars);
                        return nextNode;
                    }

                    for(path = pathStart; path != null && !currentWord.equals("<THAT>") && !currentWord.equals("<TOPIC>"); path = path.next) {
                        matchTrace = matchTrace + "[" + wildcard + "," + path.word + "]";
                        Nodemapper matchedNode;
                        if ((matchedNode = this.match(path, nextNode, input, starState, starIndex + 1, inputStars, thatStars, topicStars, matchTrace)) != null) {
                            this.setStars(starWords.toString(), starIndex, starState, inputStars, thatStars, topicStars);
                            return matchedNode;
                        }

                        currentWord = path.word;
                        starWords.append(currentWord).append(" ");
                    }

                    return null;
                }
            } catch (Exception ignored) { }
            return null;
        } else return null;
    }

    private Nodemapper setMatch(Path path, Nodemapper node, String input, String starState, int starIndex, String[] inputStars, String[] thatStars, String[] topicStars, String matchTrace) {
        if (node.sets != null && !path.word.equals("<THAT>") && !path.word.equals("<TOPIC>")) {

            for (String setName : node.sets) {
                Nodemapper nextNode = NodemapperOperator.get(node, "<SET>" + setName.toUpperCase() + "</SET>");
                AIMLSet aimlSet = this.bot.setMap.get(setName);
                String currentWord = path.word;
                StringBuilder starWords = new StringBuilder(currentWord + " ");
                int length = 1;
                matchTrace = matchTrace + "[<set>" + setName + "</set>," + path.word + "]";

                for (Path qath = path.next; qath != null && !currentWord.equals("<THAT>") && !currentWord.equals("<TOPIC>") && length <= aimlSet.maxLength; qath = qath.next) {
                    String phrase = this.bot.preProcessor.normalize(starWords.toString().trim()).toUpperCase();
                    Nodemapper matchedNode;
                    if (aimlSet.contains(phrase) && (matchedNode = this.match(qath, nextNode, input, starState, starIndex + 1, inputStars, thatStars, topicStars, matchTrace)) != null) {
                        this.setStars(starWords.toString(), starIndex, starState, inputStars, thatStars, topicStars);
                        return matchedNode;
                    }

                    ++length;
                    currentWord = qath.word;
                    starWords.append(currentWord).append(" ");
                }
            }

            return null;
        } else return null;
    }

    private void setStars(String starWords, int starIndex, String starState, String[] inputStars, String[] thatStars, String[] topicStars) {
        if (starIndex < MagicNumbers.max_stars) {
            starWords = starWords.trim();
            switch (starState) {
                case "inputStar":
                    inputStars[starIndex] = starWords;
                    break;
                case "thatStar":
                    thatStars[starIndex] = starWords;
                    break;
                case "topicStar":
                    topicStars[starIndex] = starWords;
                    break;
            }
        }
    }

    ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        this.getCategories(this.root, categories);
        return categories;
    }

    private void getCategories(Nodemapper node, ArrayList<Category> categories) {
        if (node != null) {
            if ((NodemapperOperator.isLeaf(node) || node.shortCut) && node.category != null)
                categories.add(node.category);
            for (String key : NodemapperOperator.keySet(node))
                this.getCategories(NodemapperOperator.get(node, key), categories);
        }
    }

    public void nodeStats() {
        this.nodeStatsGraph(this.root);
    }

    private void nodeStatsGraph(Nodemapper node) {
        if (node != null) {
            NodemapperOperator.size(node);
            for (String key : NodemapperOperator.keySet(node)) this.nodeStatsGraph(NodemapperOperator.get(node, key));
        }
    }

    HashSet<String> getVocabulary() {
        this.vocabulary = new HashSet<>();
        this.getBrainVocabulary(this.root);

        for (String set : this.bot.setMap.keySet())
            this.vocabulary.addAll(this.bot.setMap.get(set));

        return this.vocabulary;
    }

    private void getBrainVocabulary(Nodemapper node) {
        if (node != null)
            for (String key : NodemapperOperator.keySet(node)) {
                this.vocabulary.add(key);
                this.getBrainVocabulary(NodemapperOperator.get(node, key));
        }
    }
}
