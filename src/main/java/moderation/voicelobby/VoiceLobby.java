package moderation.voicelobby;

import files.language.LanguageHandler;
import net.dv8tion.jda.api.entities.Member;

public class VoiceLobby {
    public static String getLobbyName(Member member, String lang) {
        return "⤷ " + member.getEffectiveName() +
                (member.getEffectiveName().toLowerCase().endsWith("s") ?
                        LanguageHandler.get(lang, "apostrophe") :
                        LanguageHandler.get(lang, "apostropge_s")) + " Lobby"; }
}
