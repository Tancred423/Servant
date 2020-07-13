// Author: Tancred423 (https://github.com/Tancred423)
package commands.fun.profile;

public class ProfileCoordinates {
    private final int x;
    private final int y;

    public ProfileCoordinates(int i) {
        switch (i) {
            // 1st Line
            case 0:
                this.x = 110;
                this.y = 1100;
                break;
            case 1:
                this.x = 215;
                this.y = 1100;
                break;
            case 2:
                this.x = 320;
                this.y = 1100;
                break;
            case 3:
                this.x = 425;
                this.y = 1100;
                break;
            case 4:
                this.x = 530;
                this.y = 1100;
                break;
            case 5:
                this.x = 635;
                this.y = 1100;
                break;
            case 6:
                this.x = 740;
                this.y = 1100;
                break;
            case 7:
                this.x = 845;
                this.y = 1100;
                break;

            // 2nd Line
            case 8:
                this.x = 110;
                this.y = 1230;
                break;
            case 9:
                x = 215;
                this.y = 1230;
                break;
            case 10:
                this.x = 320;
                this.y = 1230;
                break;
            case 11:
                this.x = 425;
                this.y = 1230;
                break;
            case 12:
                this.x = 530;
                this.y = 1230;
                break;
            case 13:
                this.x = 635;
                this.y = 1230;
                break;
            case 14:
                this.x = 740;
                this.y = 1230;
                break;
            case 15:
                this.x = 845;
                this.y = 1230;
                break;

            // 3nd Line
            case 16:
                this.x = 110;
                this.y = 1360;
                break;
            case 17:
                this.x = 215;
                this.y = 1360;
                break;
            case 18:
                this.x = 320;
                this.y = 1360;
                break;
            case 19:
                this.x = 425;
                this.y = 1360;
                break;
            case 20:
                this.x = 530;
                this.y = 1360;
                break;
            case 21:
                this.x = 635;
                this.y = 1360;
                break;
            case 22:
                this.x = 740;
                this.y = 1360;
                break;
            case 23:
                this.x = 845;
                this.y = 1360;
                break;

            // Will never happen
            default:
                this.x = 0;
                this.y = 0;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
