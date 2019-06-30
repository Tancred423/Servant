package zChatLib;

import java.io.*;
import java.util.*;

public class Bot {
    final Properties properties;
    final PreProcessor preProcessor;
    public final Graphmaster brain;
    final Graphmaster learnfGraph;
    private final Graphmaster patternGraph;
    private final Graphmaster deletedGraph;
    private Graphmaster unfinishedGraph;
    public String name;
    HashMap<String, AIMLSet> setMap;
    HashMap<String, AIMLMap> mapMap;

    private void setAllPaths(String root, String name) {
        MagicStrings.bot_path = root + "/bots";
        MagicStrings.bot_name_path = MagicStrings.bot_path + "/" + name;
        MagicStrings.aiml_path = MagicStrings.bot_name_path + "/aiml";
        MagicStrings.aimlif_path = MagicStrings.bot_name_path + "/aimlif";
        MagicStrings.config_path = MagicStrings.bot_name_path + "/config";
        MagicStrings.log_path = MagicStrings.bot_name_path + "/logs";
        MagicStrings.sets_path = MagicStrings.bot_name_path + "/sets";
        MagicStrings.maps_path = MagicStrings.bot_name_path + "/maps";
    }

    public Bot() {
        this(MagicStrings.default_bot);
    }

    public Bot(String name) {
        this(name, MagicStrings.root_path);
    }

    public Bot(String name, String path) {
        this(name, path, "auto");
    }

    public Bot(String name, String path, String action) {
        this.properties = new Properties();
        this.name = MagicStrings.unknown_bot_name;
        this.setMap = new HashMap<>();
        this.mapMap = new HashMap<>();
        this.name = name;
        this.setAllPaths(path, name);
        this.brain = new Graphmaster(this);
        this.learnfGraph = new Graphmaster(this);
        this.deletedGraph = new Graphmaster(this);
        this.patternGraph = new Graphmaster(this);
        this.unfinishedGraph = new Graphmaster(this);
        this.preProcessor = new PreProcessor();
        this.addProperties();
        this.addAIMLSets();
        this.addAIMLMaps();
        AIMLSet number = new AIMLSet(MagicStrings.natural_number_set_name);
        this.setMap.put(MagicStrings.natural_number_set_name, number);
        AIMLMap successor = new AIMLMap(MagicStrings.map_successor);
        this.mapMap.put(MagicStrings.map_successor, successor);
        AIMLMap predecessor = new AIMLMap(MagicStrings.map_predecessor);
        this.mapMap.put(MagicStrings.map_predecessor, predecessor);
        Date aimlDate = new Date((new File(MagicStrings.aiml_path)).lastModified());
        Date aimlIFDate = new Date((new File(MagicStrings.aimlif_path)).lastModified());
        this.readDeletedIFCategories();
        this.readUnfinishedIFCategories();
        MagicStrings.pannous_api_key = Utilities.getPannousAPIKey();
        MagicStrings.pannous_login = Utilities.getPannousLogin();
        if (action.equals("aiml2csv")) {
            this.addCategoriesFromAIML();
        } else if (action.equals("csv2aiml")) {
            this.addCategoriesFromAIMLIF();
        } else if (aimlDate.after(aimlIFDate)) {
            this.addCategoriesFromAIML();
            this.writeAIMLIFFiles();
        } else {
            this.addCategoriesFromAIMLIF();
            if (this.brain.getCategories().size() == 0) {
                this.addCategoriesFromAIML();
            }
        }
    }

    private void addMoreCategories(String file, List<Category> moreCategories) {
        Iterator<Category> i$;
        Category c;
        if (file.contains(MagicStrings.deleted_aiml_file)) {
            i$ = moreCategories.iterator();

            while(i$.hasNext()) {
                c = i$.next();
                this.deletedGraph.addCategory(c);
            }
        } else if (file.contains(MagicStrings.unfinished_aiml_file)) {
            i$ = moreCategories.iterator();

            while(i$.hasNext()) {
                c = i$.next();
                if (this.brain.findNode(c) == null) this.unfinishedGraph.addCategory(c);
            }
        } else if (file.contains(MagicStrings.learnf_aiml_file)) {
            i$ = moreCategories.iterator();

            while(i$.hasNext()) {
                c = i$.next();
                this.brain.addCategory(c);
                this.learnfGraph.addCategory(c);
                this.patternGraph.addCategory(c);
            }
        } else {
            i$ = moreCategories.iterator();

            while(i$.hasNext()) {
                c = i$.next();
                this.brain.addCategory(c);
                this.patternGraph.addCategory(c);
            }
        }
    }

    private void addCategoriesFromAIML() {
        try {
            File folder = new File(MagicStrings.aiml_path);
            if (folder.exists()) {
                File[] listOfFiles = folder.listFiles();

                assert listOfFiles != null;
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        String file = listOfFile.getName();
                        if (file.endsWith(".aiml") || file.endsWith(".AIML")) {
                            try {
                                List<Category> moreCategories = AIMLProcessor.AIMLToCategories(MagicStrings.aiml_path, file);
                                this.addMoreCategories(file, moreCategories);
                            } catch (Exception var10) {
                                var10.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    private void addCategoriesFromAIMLIF() {
        try {
            File folder = new File(MagicStrings.aimlif_path);
            if (folder.exists()) {
                File[] listOfFiles = folder.listFiles();

                assert listOfFiles != null;
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        String file = listOfFile.getName();
                        if (file.endsWith(MagicStrings.aimlif_file_suffix) || file.endsWith(MagicStrings.aimlif_file_suffix.toUpperCase())) {
                            try {
                                ArrayList<Category> moreCategories = this.readIFCategories(MagicStrings.aimlif_path + "/" + file);
                                this.addMoreCategories(file, moreCategories);
                            } catch (Exception var10) {
                                var10.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    private void readDeletedIFCategories() {
        this.readCertainIFCategories(this.deletedGraph, MagicStrings.deleted_aiml_file);
    }

    private void readUnfinishedIFCategories() {
        this.readCertainIFCategories(this.unfinishedGraph, MagicStrings.unfinished_aiml_file);
    }

    private void readCertainIFCategories(Graphmaster graph, String fileName) {
        File file = new File(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
        if (file.exists()) {
            try {
                ArrayList<Category> deletedCategories = this.readIFCategories(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
                for (Category d : deletedCategories) graph.addCategory(d);
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void writeCertainIFCategories(Graphmaster graph, String file) {
        this.writeIFCategories(graph.getCategories(), file + MagicStrings.aimlif_file_suffix);
        File dir = new File(MagicStrings.aimlif_path);
        dir.setLastModified((new Date()).getTime());
    }

    private void writeDeletedIFCategories() {
        this.writeCertainIFCategories(this.deletedGraph, MagicStrings.deleted_aiml_file);
    }

    void writeLearnfIFCategories() {
        this.writeCertainIFCategories(this.learnfGraph, MagicStrings.learnf_aiml_file);
    }

    private void writeIFCategories(ArrayList<Category> cats, String filename) {
        BufferedWriter bw = null;
        File existsPath = new File(MagicStrings.aimlif_path);
        if (existsPath.exists()) {
            try {
                bw = new BufferedWriter(new FileWriter(MagicStrings.aimlif_path + "/" + filename));

                for (Category category : cats) {
                    bw.write(Category.categoryToIF(category));
                    bw.newLine();
                }
            } catch (IOException var17) {
                var17.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.flush();
                        bw.close();
                    }
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void writeAIMLIFFiles() {
        HashMap<String, BufferedWriter> fileMap = new HashMap<>();
        if (this.deletedGraph.getCategories().size() > 0) this.writeDeletedIFCategories();

        ArrayList<Category> brainCategories = this.brain.getCategories();
        brainCategories.sort(Category.CATEGORY_NUMBER_COMPARATOR);

        for (Category c : brainCategories) {
            try {
                String fileName = c.getFilename();
                BufferedWriter bw;
                if (fileMap.containsKey(fileName)) bw = fileMap.get(fileName);
                else {
                    bw = new BufferedWriter(new FileWriter(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix));
                    fileMap.put(fileName, bw);
                }

                bw.write(Category.categoryToIF(c));
                bw.newLine();
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        Set<String> set = fileMap.keySet();

        for (String key : set) {
            BufferedWriter bw = fileMap.get(key);

            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (IOException var8) {
                var8.printStackTrace();
            }
        }

        File dir = new File(MagicStrings.aimlif_path);
        dir.setLastModified((new Date()).getTime());
    }

    private void addProperties() {
        try {
            this.properties.getProperties(MagicStrings.config_path + "/properties.txt");
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    private ArrayList<Category> readIFCategories(String filename) {
        ArrayList<Category> categories = new ArrayList<>();

        try {
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;
            while((strLine = br.readLine()) != null) {
                try {
                    Category c = Category.IFToCategory(strLine);
                    categories.add(c);
                } catch (Exception ignored) { }
            }

            br.close();
        } catch (Exception var8) {
            System.err.println("Error: " + var8.getMessage());
        }

        return categories;
    }

    private void addAIMLSets() {
        try {
            File folder = new File(MagicStrings.sets_path);
            if (folder.exists()) {
                File[] listOfFiles = folder.listFiles();

                assert listOfFiles != null;
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        String file = listOfFile.getName();
                        if (file.endsWith(".txt") || file.endsWith(".TXT")) {
                            String setName = file.substring(0, file.length() - ".txt".length());
                            AIMLSet aimlSet = new AIMLSet(setName);
                            aimlSet.readAIMLSet();
                            this.setMap.put(setName, aimlSet);
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }

    private void addAIMLMaps() {
        try {
            File folder = new File(MagicStrings.maps_path);
            if (folder.exists()) {
                File[] listOfFiles = folder.listFiles();

                assert listOfFiles != null;
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        String file = listOfFile.getName();
                        if (file.endsWith(".txt") || file.endsWith(".TXT")) {
                            String mapName = file.substring(0, file.length() - ".txt".length());
                            AIMLMap aimlMap = new AIMLMap(mapName);
                            aimlMap.readAIMLMap();
                            this.mapMap.put(mapName, aimlMap);
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }
    }
}
