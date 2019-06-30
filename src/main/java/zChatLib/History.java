package zChatLib;

public class History<T> {
    private Object[] history;

    History() {
        this.history = new Object[MagicNumbers.max_history];
    }

    public void add(T item) {
        if (MagicNumbers.max_history - 1 >= 0)
            System.arraycopy(this.history, 0, this.history, 1, MagicNumbers.max_history - 1);

        this.history[0] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < MagicNumbers.max_history) {
            return (T) this.history[index];
        } else {
            return null;
        }
    }

    String getString(int index) {
        if (index < MagicNumbers.max_history) {
            return this.history[index] == null ? MagicStrings.unknown_history_item : (String)this.history[index];
        } else {
            return null;
        }
    }
}
