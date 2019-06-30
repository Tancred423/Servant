package zChatLib;

import java.util.Comparator;

public class Category {
    private String pattern;
    private String that;
    private String topic;
    private String template;
    private String filename;
    private int activationCnt;
    private int categoryNumber;
    private static int categoryCnt = 0;
    private AIMLSet matches;
    static Comparator<Category> CATEGORY_NUMBER_COMPARATOR = Comparator.comparingInt(Category::getCategoryNumber);

    private int getActivationCnt() {
        return this.activationCnt;
    }
    private int getCategoryNumber() {
        return this.categoryNumber;
    }
    public String getPattern() {
        return this.pattern == null ? "*" : this.pattern;
    }
    public String getThat() {
        return this.that == null ? "*" : this.that;
    }
    public String getTopic() {
        return this.topic == null ? "*" : this.topic;
    }
    public String getTemplate() {
        return this.template == null ? "" : this.template;
    }
    String getFilename() {
        return this.filename == null ? MagicStrings.unknown_aiml_file : this.filename;
    }
    public void setTemplate(String template) {
        this.template = template;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    public void setThat(String that) {
        this.that = that;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    String inputThatTopic() {
        return Graphmaster.inputThatTopic(this.pattern, this.that, this.topic);
    }

    void addMatch(String input) {
        if (this.matches == null) {
            String setName = this.inputThatTopic().replace("*", "STAR").replace("_", "UNDERSCORE").replace(" ", "-").replace("<THAT>", "THAT").replace("<TOPIC>", "TOPIC");
            this.matches = new AIMLSet(setName);
        }

        this.matches.add(input);
    }

    private static String templateToLine(String template) {
        String result = template.replaceAll("(\r\n|\n\r|\r|\n)", "\\#Newline");
        result = result.replaceAll(MagicStrings.aimlif_split_char, MagicStrings.aimlif_split_char_name);
        return result;
    }

    private static String lineToTemplate(String line) {
        String result = line.replaceAll("#Newline", "\n");
        result = result.replaceAll(MagicStrings.aimlif_split_char_name, MagicStrings.aimlif_split_char);
        return result;
    }

    static Category IFToCategory(String IF) {
        String[] split = IF.split(MagicStrings.aimlif_split_char);
        return new Category(Integer.parseInt(split[0]), split[1], split[2], split[3], lineToTemplate(split[4]), split[5]);
    }

    static String categoryToIF(Category category) {
        String c = MagicStrings.aimlif_split_char;
        return category.getActivationCnt() + c + category.getPattern() + c + category.getThat() + c + category.getTopic() + c + templateToLine(category.getTemplate()) + c + category.getFilename();
    }

    public Category(int activationCnt, String pattern, String that, String topic, String template, String filename) {
        if (MagicBooleans.fix_excel_csv) {
            pattern = Utilities.fixCSV(pattern);
            that = Utilities.fixCSV(that);
            topic = Utilities.fixCSV(topic);
            template = Utilities.fixCSV(template);
            filename = Utilities.fixCSV(filename);
        }

        this.pattern = pattern.trim().toUpperCase();
        this.that = that.trim().toUpperCase();
        this.topic = topic.trim().toUpperCase();
        this.template = template.replace("& ", " and ");
        this.filename = filename;
        this.activationCnt = activationCnt;
        this.matches = null;
        this.categoryNumber = categoryCnt++;
    }

    public Category(int activationCnt, String patternThatTopic, String template, String filename) {
        this(activationCnt, patternThatTopic.substring(0, patternThatTopic.indexOf("<THAT>")), patternThatTopic.substring(patternThatTopic.indexOf("<THAT>") + "<THAT>".length(), patternThatTopic.indexOf("<TOPIC>")), patternThatTopic.substring(patternThatTopic.indexOf("<TOPIC>") + "<TOPIC>".length()), template, filename);
    }
}
