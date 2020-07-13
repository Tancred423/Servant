package commands.utility.signup;

public class SignupSenderTask implements Runnable {
    private final Signup signup;

    public SignupSenderTask(Signup signup) {
        this.signup = signup;
    }

    @Override
    public void run() {
        signup.end();
    }
}
