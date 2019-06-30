package zChatLib;

public class MagicStrings {
    static String programNameVersion = "Program AB 0.0.4.2 beta -- AI Foundation Reference AIML 2.0 implementation";
    static String comment = "removed some recursion from Path";
    static String aimlif_split_char = ",";
    static String default_bot = "super";
    static String default_language = "EN";
    static String aimlif_split_char_name = "\\#Comma";
    static String aimlif_file_suffix = ".csv";
    static String ab_sample_file = "sample.txt";
    static String pannous_api_key = "guest";
    static String pannous_login = "test-user";
    static String sraix_failed = "SRAIXFAILED";
    static String sraix_no_hint = "nohint";
    static String sraix_event_hint = "event";
    static String sraix_pic_hint = "pic";
    static String unknown_aiml_file = "unknown_aiml_file.aiml";
    static String deleted_aiml_file = "deleted.aiml";
    static String learnf_aiml_file = "learnf.aiml";
    static String null_aiml_file = "null.aiml";
    static String inappropriate_aiml_file = "inappropriate.aiml";
    static String profanity_aiml_file = "profanity.aiml";
    static String insult_aiml_file = "insults.aiml";
    static String reductions_update_aiml_file = "reductions_update.aiml";
    static String predicates_aiml_file = "client_profile.aiml";
    static String update_aiml_file = "update.aiml";
    static String personality_aiml_file = "personality.aiml";
    static String sraix_aiml_file = "sraix.aiml";
    static String oob_aiml_file = "oob.aiml";
    static String unfinished_aiml_file = "unfinished.aiml";
    static String inappropriate_filter = "FILTER INAPPROPRIATE";
    static String profanity_filter = "FILTER PROFANITY";
    static String insult_filter = "FILTER INSULT";
    static String deleted_template = "deleted";
    static String unfinished_template = "unfinished";
    static String unknown_history_item = "unknown";
    static String default_bot_response = "I have no answer for that.";
    static String error_bot_response = "Something is wrong with my brain.";
    static String schedule_error = "I'm unable to schedule that event.";
    static String system_failed = "Failed to execute system command.";
    static String unknown_predicate_value = "unknown";
    static String unknown_property_value = "unknown";
    static String unknown_map_value = "unknown";
    static String unknown_customer_id = "unknown";
    static String unknown_bot_name = "unknown";
    static String default_that = "unknown";
    static String default_topic = "unknown";
    static String template_failed = "Template failed.";
    static String too_much_recursion = "Too much recursion in AIML";
    static String too_much_looping = "Too much looping in AIML";
    static String blank_template = "blank template";
    static String null_input = "NORESP";
    static String null_star = "nullstar";
    static String set_member_string = "ISA";
    static String remote_map_key = "external";
    static String remote_set_key = "external";
    static String natural_number_set_name = "number";
    static String map_successor = "successor";
    static String map_predecessor = "predecessor";
    static String root_path = "c:/ab";
    static String bot_path;
    static String bot_name_path;
    static String aimlif_path;
    static String aiml_path;
    static String config_path;
    static String log_path;
    static String sets_path;
    static String maps_path;

    public MagicStrings() {
    }

    static {
        bot_path = root_path + "/bots";
        bot_name_path = bot_path + "/super";
        aimlif_path = bot_path + "/aimlif";
        aiml_path = bot_path + "/aiml";
        config_path = bot_path + "/config";
        log_path = bot_path + "/log";
        sets_path = bot_path + "/sets";
        maps_path = bot_path + "/maps";
    }
}
