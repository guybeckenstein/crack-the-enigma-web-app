public class test {
    public static void main(String[] args) {
        String userHomeProperty = getSystemUserHome();
        String dir = userHomeProperty + AgentMain.class.getResource(".");
        ProcessBuilder builder = new ProcessBuilder("java",
                "-jar",
                dir);
        try {
            Process process = builder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getSystemUserHome() {
        StringBuilder res = new StringBuilder();
        String[] split = System.getProperty("user.home").split("\\\\");
        for (int i = 0; i < split.length; i++) {
            if (i + 1 < split.length) {
                res.append(split[i]).append('/');
            } else {
                res.append(split[i]);
            }
        }
        return res.toString();
    }
}
