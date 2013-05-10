/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.liv.srmconvertor;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 12-Mar-2013 16:20:17
 */
public class CliOptions {

    public enum OPTIONS {

        HELP("help"),
        CONVERT("convert");
        private String value;

        OPTIONS(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    private static final Options options = new Options();

    static {
        Option help = new Option(
                OPTIONS.HELP.toString(),
                "print this message.");

        Option convert = OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription("converts the given SRM csv file to an mzq file.")
                .create(OPTIONS.CONVERT.toString());


        options.addOption(help);
        options.addOption(convert);
    }

    public static Options getOptions() {
        return options;
    }

}
