package main.java.speech;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import main.java.logic.Airplane;
import main.java.logic.CommandUtils;
import main.java.resources.FontResource;
import main.java.ui.CommandPanel;
import net.sourceforge.javaflacencoder.FLACFileWriter;

import javax.swing.*;
import java.awt.*;

public class SpeechUtils {

    public static JTextField command_display;

    private static Microphone mic;
    static GSpeechDuplex duplex;

    public static void startSpeechSession () {

        command_display.setText("");
        command_display.setFont(FontResource.command_content);
        command_display.setForeground(Color.black);

        mic = new Microphone(FLACFileWriter.FLAC);
        duplex = new GSpeechDuplex("AIzaSyAPdCA3W_b4McxwqZiNQKDvYk3Hh8zxYfI");

        duplex.setLanguage("en");

        new Thread(() -> {
            try {
                duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }).start();

        duplex.addResponseListener(new GSpeechResponseListener() {
            String old_text = "";

            public void onResponse(GoogleResponse gr) {
                String output;
                output = gr.getResponse();
                if (gr.getResponse() == null) {
                    this.old_text = command_display.getText();
                    if (this.old_text.contains("(")) {
                        this.old_text = this.old_text.substring(0, this.old_text.indexOf('('));
                    }
                    System.out.println("Paragraph Line Added");
                    this.old_text = (command_display.getText() + "\n");
                    this.old_text = this.old_text.replace(")", "").replace("( ", "");
                    command_display.setText(this.old_text);
                    return;
                }
                if (output.contains("(")) {
                    output = output.substring(0, output.indexOf('('));
                }
                if (!gr.getOtherPossibleResponses().isEmpty()) {
                    output = output + " (" + (String) gr.getOtherPossibleResponses().get(0) + ")";
                }

                command_display.setText("");
                command_display.setText(this.old_text);
                command_display.setText(command_display.getText() + output);
            }
        });

    }

    public static void stopSpeechSession() {
        mic.close();
        duplex.stopSpeechRecognition();
        duplex.removeResponseListener(Object::notifyAll);

        CommandUtils.TEST_CODE(command_display.getText());

        CommandPanel.resetCommandBox(command_display, true);
    }

}
