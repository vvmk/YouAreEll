import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleShell {


    public static void prettyPrint(String output) {
        System.out.println(output);
    }

    public static void main(String[] args) throws java.io.IOException {

        YouAreEll webber = new YouAreEll();
        String commandLine;
        BufferedReader console = new BufferedReader
                (new InputStreamReader(System.in));

        ProcessBuilder pb = new ProcessBuilder();
        List<String> history = new ArrayList<>();

        int index = 0;
        String fromId = "";
        //we break out with <ctrl c>
        while (true) {
            //read what the user enters
            System.out.println("cmd? ");
            commandLine = console.readLine();

            //input parsed into array of strings(command and arguments)
            String[] commands = commandLine.split(" ");
            List<String> list = new ArrayList<>();

            //if the user entered a return, just loop again
            if (commandLine.equals(""))
                continue;
            if (commandLine.equals("exit")) {
                System.out.println("bye!");
                break;
            }

            //loop through to see if parsing worked
            StringBuilder sb = new StringBuilder();
            int qCount = 0;
            for (int i = 0; i < commands.length; i++) {
                sb.append(commands[i]);

                Pattern p = Pattern.compile("\"");
                Matcher matcher = p.matcher(commands[i]);

                while (matcher.find())
                    qCount++;

                if (qCount % 2 == 1) {
                    sb.append(" ");
                    continue;
                }

                p = Pattern.compile("\"");
                list.add(p.matcher(sb.toString()).replaceAll(""));
                sb.setLength(0);
            }
            //System.out.println(list);
            history.addAll(list);

            try {
                //display history of shell with index
                if (list.get(list.size() - 1).equals("history")) {
                    for (String s : history)
                        System.out.println((index++) + " " + s);
                    continue;
                }

                // Specific Commands.

                // ids
                if (list.contains("ids")) {
                    String results = webber.get_ids();
                    List<User> users = new ObjectMapper().readValue(results, new TypeReference<List<User>>(){});
                    users.forEach(user -> SimpleShell.prettyPrint(user.toString()));
                    continue;
                }

                // messages
                if (list.get(0).equals("messages")) {
                    String results;

                    if (list.size() > 1) {
                        String path = "/" + list.get(1) + "/messages/" + list.get(2);
                        results = webber.get_ids(path);
                    } else
                        results = webber.get_messages();

                    List<Message> msgs = new ObjectMapper().readValue(results, new TypeReference<List<Message>>(){});
                    msgs.forEach(msg -> SimpleShell.prettyPrint(msg.toString()));
                    continue;
                }

                //send
                if (list.get(0).equals("send")) {
                    String fromid = fromId;
                    String message = list.get(1);
                    String toid = list.get(2).equalsIgnoreCase("to") ? list.get(3) : "";
                    String sequence = "-";
                    Message msg = new Message(sequence, fromid, toid, message);
                    String response = webber.post_messages(msg);
                    SimpleShell.prettyPrint(response);
                    continue;
                }

                //set fromId
                if (list.get(0).equals("set")) {
                    fromId = list.get(1);
                    continue;
                }

                //update
                if (list.get(0).equals("put")) {
                    User u = new User("", list.get(1), fromId);
                    String response = webber.put_ids(u);
                    SimpleShell.prettyPrint(response);
                    continue;
                }

                //!! command returns the last command in history
                if (list.get(list.size() - 1).equals("!!")) {
                    pb.command(history.get(history.size() - 2));

                }//!<integer value i> command
                else if (list.get(list.size() - 1).charAt(0) == '!') {
                    int b = Character.getNumericValue(list.get(list.size() - 1).charAt(1));
                    if (b <= history.size())//check if integer entered isn't bigger than history size
                        pb.command(history.get(b));
                } else {
                    pb.command(list);
                }

                // wait, wait, what curiousness is this?
                Process process = pb.start();

                //obtain the input stream
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                //read output of the process
                String line;
                while ((line = br.readLine()) != null)
                    System.out.println(line);
                br.close();


            }

            //catch ioexception, output appropriate message, resume waiting for input
            catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Input Error, Please try again!");
            }
            // So what, do you suppose, is the meaning of this comment?
            /** The steps are:
             * 1. parse the input to obtain the command and any parameters
             * 2. create a ProcessBuilder object
             * 3. start the process
             * 4. obtain the output stream
             * 5. output the contents returned by the command
             */

        }


    }

}