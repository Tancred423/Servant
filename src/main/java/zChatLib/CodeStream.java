package zChatLib;

public final class CodeStream {
    private final CharSequence src;
    private final int end;
    private int pos;
    private int code;
    private int octetPos;
    private int octetLen;

    CodeStream(CharSequence var1, int var2) {
        this.src = var1;
        this.end = this.src.length();
        this.pos = var2;
        this.code = var2 == this.end ? 0 : var1.charAt(var2);
        this.octetLen = this.octetLength(this.code);
        this.octetPos = this.octetLen;
    }

    boolean isEos() {
        return this.pos == this.end;
    }

    public char read() {
        char var1 = this.peek();
        this.eat();
        return var1;
    }

    int position() {
        return this.pos;
    }

    private int octetLength(int var1) {
        if (var1 < 128) {
            return 1;
        } else if (var1 < 2048) {
            return 2;
        } else {
            return var1 < 65536 ? 3 : 4;
        }
    }

    private char peek() {
        if (this.octetPos == this.octetLen) {
            switch(this.octetLen) {
                case 1:
                    return (char)this.code;
                case 2:
                    return (char)(192 + (byte)(this.code >> 6 & 31));
                case 3:
                    return (char)(224 + (byte)(this.code >> 12 & 15));
                default:
                    return (char)(240 + (byte)(this.code >> 18 & 7));
            }
        } else {
            int var1 = (this.octetPos - 1) * 6;
            return (char)(128 + (byte)(this.code >> var1 & 63));
        }
    }

    private void eat() {
        --this.octetPos;
        if (this.octetPos == 0) {
            ++this.pos;
            if (!this.isEos()) {
                this.code = this.src.charAt(this.pos);
                this.octetLen = this.octetLength(this.code);
                this.octetPos = this.octetLen;
            }
        }

    }
}
