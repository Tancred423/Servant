package commands.fun.tictactoe;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TicTacToeMoves {
    private static final String X = "\uD83C\uDDFD";
    private static final String O = "\uD83C\uDD7E️";
    private static final String BLANK = "⬛";
    private static final String[] arrows = { "↖️", "⬆️", "↗️", "⬅️", "⏹️", "➡️", "↙️", "⬇️", "↘️" };

    public static String round2(long msgId, LinkedHashMap<String, String> entries) {
        if (entries.get(arrows[0]).equals(O)) {
            // Bot started at 0
            if (entries.get(arrows[1]).equals(X)) {
                // CardinalNear: User on 1 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[3];
            } else if (entries.get(arrows[3]).equals(X)) {
                // CardinalNear: User on 3 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[1];
            } else if (entries.get(arrows[5]).equals(X)
                    || entries.get(arrows[7]).equals(X)) {
                // CardinalFar: User on 5 or 7 - Return middle
                TicTacToeCommand.moveNames.put(msgId, "CardinalFar");
                return arrows[4];
            } else if (entries.get(arrows[2]).equals(X)
                    || entries.get(arrows[6]).equals(X)) {
                // IntercardinalNear: User on 2 or 6 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalNear");
                return arrows[8];
            } else if (entries.get(arrows[8]).equals(X)) {
                // IntercardinalFar: User on 8 - Return one of the near corners
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalFar");
                return ThreadLocalRandom.current().nextInt(2) > 0 ? arrows[2] : arrows[6];
            } else if (entries.get(arrows[4]).equals(X)) {
                // Middle: User on 4 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "Middle");
                return arrows[8];
            }
        } else if (entries.get(arrows[2]).equals(O)) {
            // Bot started at 2
            if (entries.get(arrows[1]).equals(X)) {
                // CardinalNear: User on 1 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[5];
            } else if (entries.get(arrows[5]).equals(X)) {
                // CardinalNear: User on 5 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[1];
            } else if (entries.get(arrows[3]).equals(X)
                    || entries.get(arrows[7]).equals(X)) {
                // CardinalFar: User on 3 or 7 - Return middle
                TicTacToeCommand.moveNames.put(msgId, "CardinalFar");
                return arrows[4];
            } else if (entries.get(arrows[0]).equals(X)
                    || entries.get(arrows[8]).equals(X)) {
                // IntercardinalNear: User on 0 or 8 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalNear");
                return arrows[6];
            } else if (entries.get(arrows[6]).equals(X)) {
                // IntercardinalFar: User on 6 - Return one of the near corners
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalFar");
                return ThreadLocalRandom.current().nextInt(2) > 0 ? arrows[0] : arrows[8];
            } else if (entries.get(arrows[4]).equals(X)) {
                // Middle: User on 4 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "Middle");
                return arrows[6];
            }
        } else if (entries.get(arrows[6]).equals(O)) {
            // Bot started 6
            if (entries.get(arrows[3]).equals(X)) {
                // CardinalNear: User on 3 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[7];
            } else if (entries.get(arrows[7]).equals(X)) {
                // CardinalNear: User on 7 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[3];
            } else if (entries.get(arrows[1]).equals(X)
                || entries.get(arrows[5]).equals(X)) {
                // CardinalFar: User on 1 or 5 - Return middle
                TicTacToeCommand.moveNames.put(msgId, "CardinalFar");
                return arrows[4];
            } else if (entries.get(arrows[0]).equals(X)
                || entries.get(arrows[8]).equals(X)) {
                // IntercardinalNear: User on 0 or 8 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalNear");
                return arrows[2];
            } else if (entries.get(arrows[2]).equals(X)) {
                // IntercardinalFar: User on 2 - Return one of the near corners
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalFar");
                return ThreadLocalRandom.current().nextInt(2) > 0 ? arrows[0] : arrows[8];
            } else if (entries.get(arrows[4]).equals(X)) {
                // Middle: User on 4 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "Middle");
                return arrows[2];
            }
        } else if (entries.get(arrows[8]).equals(O)) {
            // Bot started at 8
            if (entries.get(arrows[5]).equals(X)) {
                // CardinalNear: User on 5 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[7];
            } else if (entries.get(arrows[7]).equals(X)) {
                // CardinalNear: User on 7 - Return other cardinal near
                TicTacToeCommand.moveNames.put(msgId, "CardinalNear");
                return arrows[5];
            } else if (entries.get(arrows[1]).equals(X)
                    || entries.get(arrows[3]).equals(X)) {
                // CardinalFar: User on 1 or 3 - Return middle
                TicTacToeCommand.moveNames.put(msgId, "CardinalFar");
                return arrows[4];
            } else if (entries.get(arrows[2]).equals(X)
                    || entries.get(arrows[6]).equals(X)) {
                // IntercardinalNear: User on 2 or 6 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalNear");
                return arrows[0];
            } else if (entries.get(arrows[0]).equals(X)) {
                // IntercardinalFar: User on 0 - Return one of the near corners
                TicTacToeCommand.moveNames.put(msgId, "IntercardinalFar");
                return ThreadLocalRandom.current().nextInt(2) > 0 ? arrows[2] : arrows[6];
            } else if (entries.get(arrows[4]).equals(X)) {
                // Middle: User on 4 - Return corner across
                TicTacToeCommand.moveNames.put(msgId, "Middle");
                return arrows[0];
            }
        }

        // Error
        return "";
    }

    public static String round3(long msgId, LinkedHashMap<String, String> entries) {
        var userFields = new ArrayList<Integer>();
        Collections.sort(userFields);
        var aiFields = new ArrayList<Integer>();
        Collections.sort(aiFields);

        for (var entry : entries.entrySet()) {
            if (entry.getValue().equals(X))
                userFields.add(Arrays.asList(arrows).indexOf(entry.getKey()));
            else if (entry.getValue().equals(O))
                aiFields.add(Arrays.asList(arrows).indexOf(entry.getKey()));
        }

        var ai1 = aiFields.get(0);
        var ai2 = aiFields.get(1);

        var user1 = userFields.get(0);
        var user2 = userFields.get(1);

        // Try win
        var emoji = winOrParryRound3(entries, ai1, ai2);
        if (!emoji.isBlank()) return emoji;

        // Try parry
        emoji = winOrParryRound3(entries, user1, user2);
        if (!emoji.isBlank()) return emoji;

        // We couldn't win nor is there anything to parry. So we continue the master plan.
        // We do not have to handle IntercardinalNear, as Servant has already won at this point.
        // We do not have to handle Middle as this is just a win/parry game at this point.
        var moveName = TicTacToeCommand.moveNames.get(msgId);
        switch (moveName) {
            case "CardinalNear":
            case "CardinalFar":
                return arrows[4];

            case "IntercardinalFar":
                if (entries.get(arrows[0]).equals(BLANK)) return arrows[0];
                else if (entries.get(arrows[2]).equals(BLANK)) return arrows[2];
                else if (entries.get(arrows[6]).equals(BLANK)) return arrows[6];
                else if (entries.get(arrows[8]).equals(BLANK)) return arrows[8];
                break;
        }

        return "";
    }

    public static String round4(LinkedHashMap<String, String> entries) {
        var userFields = new ArrayList<Integer>();
        Collections.sort(userFields);
        var aiFields = new ArrayList<Integer>();
        Collections.sort(aiFields);

        for (var entry : entries.entrySet()) {
            if (entry.getValue().equals(X))
                userFields.add(Arrays.asList(arrows).indexOf(entry.getKey()));
            else if (entry.getValue().equals(O))
                aiFields.add(Arrays.asList(arrows).indexOf(entry.getKey()));
        }

        var ai1 = aiFields.get(0);
        var ai2 = aiFields.get(1);
        var ai3 = aiFields.get(2);

        var user1 = userFields.get(0);
        var user2 = userFields.get(1);
        var user3 = userFields.get(2);

        // Try win
        var emoji = winOrParryRound4(entries, ai1, ai2, ai3);
        if (!emoji.isBlank()) return emoji;

        // Try parry
        emoji = winOrParryRound4(entries, user1, user2, user3);
        if (!emoji.isBlank()) return emoji;

        return "";
    }

    private static String winOrParryRound3(HashMap<String, String> entries, int pos1, int pos2) {
        if (pos1 == 0 && pos2 == 1 && entries.get(arrows[2]).equals(BLANK)) {
            // Upper horizontal
            return arrows[2];
        } else if (pos1 == 0 && pos2 == 2 && entries.get(arrows[1]).equals(BLANK)) {
            // Upper horizontal
            return arrows[1];
        } else if (pos1 == 1 && pos2 == 2 && entries.get(arrows[0]).equals(BLANK)) {
            // Upper horizontal
            return arrows[0];
        } else if (pos1 == 3 && pos2 == 4 && entries.get(arrows[5]).equals(BLANK)) {
            // Middle horizontal
            return arrows[5];
        } else if (pos1 == 3 && pos2 == 5 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle horizontal
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 5 && entries.get(arrows[3]).equals(BLANK)) {
            // Middle horizontal
            return arrows[3];
        } else if (pos1 == 6 && pos2 == 7 && entries.get(arrows[8]).equals(BLANK)) {
            // Lower horizontal
            return arrows[8];
        } else if (pos1 == 6 && pos2 == 8 && entries.get(arrows[7]).equals(BLANK)) {
            // Lower horizontal
            return arrows[7];
        } else if (pos1 == 7 && pos2 == 8 && entries.get(arrows[6]).equals(BLANK)) {
            // Lower horizontal
            return arrows[6];
        } else if (pos1 == 0 && pos2 == 3 && entries.get(arrows[6]).equals(BLANK)) {
            // Left vertical
            return arrows[6];
        } else if (pos1 == 0 && pos2 == 6 && entries.get(arrows[3]).equals(BLANK)) {
            // Left vertical
            return arrows[3];
        } else if (pos1 == 3 && pos2 == 6 && entries.get(arrows[0]).equals(BLANK)) {
            // Left vertical
            return arrows[0];
        } else if (pos1 == 1 && pos2 == 4 && entries.get(arrows[7]).equals(BLANK)) {
            // Middle vertical
            return arrows[7];
        } else if (pos1 == 1 && pos2 == 7 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle vertical
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 7 && entries.get(arrows[1]).equals(BLANK)) {
            // Middle vertical
            return arrows[1];
        } else if (pos1 == 2 && pos2 == 5 && entries.get(arrows[8]).equals(BLANK)) {
            // Right vertical
            return arrows[8];
        } else if (pos1 == 2 && pos2 == 8 && entries.get(arrows[5]).equals(BLANK)) {
            // Right vertical
            return arrows[5];
        } else if (pos1 == 5 && pos2 == 8 && entries.get(arrows[2]).equals(BLANK)) {
            // Right vertical
            return arrows[2];
        } else if (pos1 == 0 && pos2 == 4 && entries.get(arrows[8]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[8];
        } else if (pos1 == 0 && pos2 == 8 && entries.get(arrows[4]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 8 && entries.get(arrows[0]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[0];
        } else if (pos1 == 2 && pos2 == 4 && entries.get(arrows[6]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[6];
        } else if (pos1 == 2 && pos2 == 6 && entries.get(arrows[4]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 6 && entries.get(arrows[2]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[2];
        }

        // No win/parry
        return "";
    }

    private static String winOrParryRound4(HashMap<String, String> entries, int pos1, int pos2, int pos3) {
        // Check pos 1 and 2
        if (pos1 == 0 && pos2 == 1 && entries.get(arrows[2]).equals(BLANK)) {
            // Upper horizontal
            return arrows[2];
        } else if (pos1 == 0 && pos2 == 2 && entries.get(arrows[1]).equals(BLANK)) {
            // Upper horizontal
            return arrows[1];
        } else if (pos1 == 1 && pos2 == 2 && entries.get(arrows[0]).equals(BLANK)) {
            // Upper horizontal
            return arrows[0];
        } else if (pos1 == 3 && pos2 == 4 && entries.get(arrows[5]).equals(BLANK)) {
            // Middle horizontal
            return arrows[5];
        } else if (pos1 == 3 && pos2 == 5 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle horizontal
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 5 && entries.get(arrows[3]).equals(BLANK)) {
            // Middle horizontal
            return arrows[3];
        } else if (pos1 == 6 && pos2 == 7 && entries.get(arrows[8]).equals(BLANK)) {
            // Lower horizontal
            return arrows[8];
        } else if (pos1 == 6 && pos2 == 8 && entries.get(arrows[7]).equals(BLANK)) {
            // Lower horizontal
            return arrows[7];
        } else if (pos1 == 7 && pos2 == 8 && entries.get(arrows[6]).equals(BLANK)) {
            // Lower horizontal
            return arrows[6];
        } else if (pos1 == 0 && pos2 == 3 && entries.get(arrows[6]).equals(BLANK)) {
            // Left vertical
            return arrows[6];
        } else if (pos1 == 0 && pos2 == 6 && entries.get(arrows[3]).equals(BLANK)) {
            // Left vertical
            return arrows[3];
        } else if (pos1 == 3 && pos2 == 6 && entries.get(arrows[0]).equals(BLANK)) {
            // Left vertical
            return arrows[0];
        } else if (pos1 == 1 && pos2 == 4 && entries.get(arrows[7]).equals(BLANK)) {
            // Middle vertical
            return arrows[7];
        } else if (pos1 == 1 && pos2 == 7 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle vertical
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 7 && entries.get(arrows[1]).equals(BLANK)) {
            // Middle vertical
            return arrows[1];
        } else if (pos1 == 2 && pos2 == 5 && entries.get(arrows[8]).equals(BLANK)) {
            // Right vertical
            return arrows[8];
        } else if (pos1 == 2 && pos2 == 8 && entries.get(arrows[5]).equals(BLANK)) {
            // Right vertical
            return arrows[5];
        } else if (pos1 == 5 && pos2 == 8 && entries.get(arrows[2]).equals(BLANK)) {
            // Right vertical
            return arrows[2];
        } else if (pos1 == 0 && pos2 == 4 && entries.get(arrows[8]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[8];
        } else if (pos1 == 0 && pos2 == 8 && entries.get(arrows[4]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 8 && entries.get(arrows[0]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[0];
        } else if (pos1 == 2 && pos2 == 4 && entries.get(arrows[6]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[6];
        } else if (pos1 == 2 && pos2 == 6 && entries.get(arrows[4]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[4];
        } else if (pos1 == 4 && pos2 == 6 && entries.get(arrows[2]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[2];
        } else if (pos1 == 0 && pos3 == 1 && entries.get(arrows[2]).equals(BLANK)) {
            // Upper horizontal
            return arrows[2];
        } else if (pos1 == 0 && pos3 == 2 && entries.get(arrows[1]).equals(BLANK)) {
            // Upper horizontal
            return arrows[1];
        } else if (pos1 == 1 && pos3 == 2 && entries.get(arrows[0]).equals(BLANK)) {
            // Upper horizontal
            return arrows[0];
        } else if (pos1 == 3 && pos3 == 4 && entries.get(arrows[5]).equals(BLANK)) {
            // Middle horizontal
            return arrows[5];
        } else if (pos1 == 3 && pos3 == 5 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle horizontal
            return arrows[4];
        } else if (pos1 == 4 && pos3 == 5 && entries.get(arrows[3]).equals(BLANK)) {
            // Middle horizontal
            return arrows[3];
        } else if (pos1 == 6 && pos3 == 7 && entries.get(arrows[8]).equals(BLANK)) {
            // Lower horizontal
            return arrows[8];
        } else if (pos1 == 6 && pos3 == 8 && entries.get(arrows[7]).equals(BLANK)) {
            // Lower horizontal
            return arrows[7];
        } else if (pos1 == 7 && pos3 == 8 && entries.get(arrows[6]).equals(BLANK)) {
            // Lower horizontal
            return arrows[6];
        } else if (pos1 == 0 && pos3 == 3 && entries.get(arrows[6]).equals(BLANK)) {
            // Left vertical
            return arrows[6];
        } else if (pos1 == 0 && pos3 == 6 && entries.get(arrows[3]).equals(BLANK)) {
            // Left vertical
            return arrows[3];
        } else if (pos1 == 3 && pos3 == 6 && entries.get(arrows[0]).equals(BLANK)) {
            // Left vertical
            return arrows[0];
        } else if (pos1 == 1 && pos3 == 4 && entries.get(arrows[7]).equals(BLANK)) {
            // Middle vertical
            return arrows[7];
        } else if (pos1 == 1 && pos3 == 7 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle vertical
            return arrows[4];
        } else if (pos1 == 4 && pos3 == 7 && entries.get(arrows[1]).equals(BLANK)) {
            // Middle vertical
            return arrows[1];
        } else if (pos1 == 2 && pos3 == 5 && entries.get(arrows[8]).equals(BLANK)) {
            // Right vertical
            return arrows[8];
        } else if (pos1 == 2 && pos3 == 8 && entries.get(arrows[5]).equals(BLANK)) {
            // Right vertical
            return arrows[5];
        } else if (pos1 == 5 && pos3 == 8 && entries.get(arrows[2]).equals(BLANK)) {
            // Right vertical
            return arrows[2];
        } else if (pos1 == 0 && pos3 == 4 && entries.get(arrows[8]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[8];
        } else if (pos1 == 0 && pos3 == 8 && entries.get(arrows[4]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[4];
        } else if (pos1 == 4 && pos3 == 8 && entries.get(arrows[0]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[0];
        } else if (pos1 == 2 && pos3 == 4 && entries.get(arrows[6]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[6];
        } else if (pos1 == 2 && pos3 == 6 && entries.get(arrows[4]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[4];
        } else if (pos1 == 4 && pos3 == 6 && entries.get(arrows[2]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[2];
        } else if (pos2 == 0 && pos3 == 1 && entries.get(arrows[2]).equals(BLANK)) {
            // Upper horizontal
            return arrows[2];
        } else if (pos2 == 0 && pos3 == 2 && entries.get(arrows[1]).equals(BLANK)) {
            // Upper horizontal
            return arrows[1];
        } else if (pos2 == 1 && pos3 == 2 && entries.get(arrows[0]).equals(BLANK)) {
            // Upper horizontal
            return arrows[0];
        } else if (pos2 == 3 && pos3 == 4 && entries.get(arrows[5]).equals(BLANK)) {
            // Middle horizontal
            return arrows[5];
        } else if (pos2 == 3 && pos3 == 5 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle horizontal
            return arrows[4];
        } else if (pos2 == 4 && pos3 == 5 && entries.get(arrows[3]).equals(BLANK)) {
            // Middle horizontal
            return arrows[3];
        } else if (pos2 == 6 && pos3 == 7 && entries.get(arrows[8]).equals(BLANK)) {
            // Lower horizontal
            return arrows[8];
        } else if (pos2 == 6 && pos3 == 8 && entries.get(arrows[7]).equals(BLANK)) {
            // Lower horizontal
            return arrows[7];
        } else if (pos2 == 7 && pos3 == 8 && entries.get(arrows[6]).equals(BLANK)) {
            // Lower horizontal
            return arrows[6];
        } else if (pos2 == 0 && pos3 == 3 && entries.get(arrows[6]).equals(BLANK)) {
            // Left vertical
            return arrows[6];
        } else if (pos2 == 0 && pos3 == 6 && entries.get(arrows[3]).equals(BLANK)) {
            // Left vertical
            return arrows[3];
        } else if (pos2 == 3 && pos3 == 6 && entries.get(arrows[0]).equals(BLANK)) {
            // Left vertical
            return arrows[0];
        } else if (pos2 == 1 && pos3 == 4 && entries.get(arrows[7]).equals(BLANK)) {
            // Middle vertical
            return arrows[7];
        } else if (pos2 == 1 && pos3 == 7 && entries.get(arrows[4]).equals(BLANK)) {
            // Middle vertical
            return arrows[4];
        } else if (pos2 == 4 && pos3 == 7 && entries.get(arrows[1]).equals(BLANK)) {
            // Middle vertical
            return arrows[1];
        } else if (pos2 == 2 && pos3 == 5 && entries.get(arrows[8]).equals(BLANK)) {
            // Right vertical
            return arrows[8];
        } else if (pos2 == 2 && pos3 == 8 && entries.get(arrows[5]).equals(BLANK)) {
            // Right vertical
            return arrows[5];
        } else if (pos2 == 5 && pos3 == 8 && entries.get(arrows[2]).equals(BLANK)) {
            // Right vertical
            return arrows[2];
        } else if (pos2 == 0 && pos3 == 4 && entries.get(arrows[8]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[8];
        } else if (pos2 == 0 && pos3 == 8 && entries.get(arrows[4]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[4];
        } else if (pos2 == 4 && pos3 == 8 && entries.get(arrows[0]).equals(BLANK)) {
            // UpLeft to DownRight diagonal
            return arrows[0];
        } else if (pos2 == 2 && pos3 == 4 && entries.get(arrows[6]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[6];
        } else if (pos2 == 2 && pos3 == 6 && entries.get(arrows[4]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[4];
        } else if (pos2 == 4 && pos3 == 6 && entries.get(arrows[2]).equals(BLANK)) {
            // UpRight to DownLeft diagonal
            return arrows[2];
        }

        // No win/parry
        return "";
    }
}
