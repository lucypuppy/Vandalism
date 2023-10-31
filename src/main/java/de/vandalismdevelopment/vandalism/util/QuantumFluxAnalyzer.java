package de.vandalismdevelopment.vandalism.util;// @formatter:off
import org.jetbrains.annotations.NotNull;

/*


Powered by https://pinetools.com/rotate-image, https://www.base64-image.de/ and the N Voices

 */



/*
                                     ++++
                                     +++++
                                  +++++++++
                        ++++++++ ++++++++++++++
                         +++++++++++++++++++++++
                           *+++++++++++++++++++++++
                          ++++++++++++++++++++++++++++++++*
                      ++++++++++++++++++++++++++++++++++++++++
                   +++++++++++++++++++++++++++++++++++++++++++++++
                 +++++++++++++++++++++++++++++++++++++++++++++++++++
               +++++++++++++++++++++++++++++++++++++++++++++++++++++++
             ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            *++++++=:......-++++++++++++++++++++++++++-......:=+++++++++
            ++++-:....:--:....-++++++++++++++++++++-....:--:....:-+++++++
           +++=::..:%@@%@%@%-..:++++++++++++++++++:..:%@%@%@%%:...:=++++++
          *++=::..:%%%. .+@%%-..:++++++++++++++++-..-%%@*.  #%@-..::=+++++
          +++-::..*@%%%+#%%%%*...*++++++++++++++*:..+@%%%%+#%%%#..::-+++++
          +++-::..:%%%%%%%%%%-..:*+++++++++++++++:..-%%%%%%%%%@-..::-++++++
          +++=:::..:#@%%%%@%-...=++++++++++++++++=...:%@%%%%%%-...::=+++++
          ++++=:::.....::......-++++++++++++++++++-......::.....:::-++++++
          ++++++-::::......:::++++++++++++++++++++++:::......:::::++++++++
          ++++++++=::::::::-++++++++++++++++++++++++++-::::::::-++++++++++
           +++++++**********++++++++++++++++++++++++++**********+++++++++
           ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
           ++++++++++++--=++++++++++++++++++++++++++++++++=-:++++++++++++
           +++++++++++*=         .:.:::------::::...       .**++++++++++*
            +++++++++++*%+-:.::=*%#-          :+%%%**+++*#%%#++++++++++++
            +++++++++++++#%%%%%%%%%%%%###*##%%%%%%%%%%%%%%%*++++++++++++
            -++++++++++++++#%%%%%#**+==-------==+**#%%%%%#+++++++++++++=
           ....:=++++++++++++*#=:::::::::::::::::::::-#*+++++++++*+-:...:
          .........:=++++++++**+++=-::::::::::::---=++++++++++=:..........
         ..............:-++++++++++****************+++*+=::.. .............
        :................  ..:-=+++++++++++++++++=-:.     .................:
       ....................        ....:+%#=.           .....................:
      .........................    ...#@@@@@@*...   .........................:
     :.....:...........................-@@@@=..................................
    ....................................-@@+....................................
     ++++-.......:=----.................=@@#:............................:+++=:.
    ++++++.........::.:.................#@@@-............................:+++++
    +++++ :......:---:---:..............@@@@+............................. +++++
    +++++ .......:-::-::-:.............=@@@@#............................. +++++
   ++++++ .............................%@@@@@.............................  +++++
   ++++++ ............................-@@@@@@+...........................:  +++++
  ++++++  ............................+@@@@@@@............................  ++++++
  ++++++  :..........................:#@@@@@@@=...........................  *+++++
 *++++++  :..........................:%@@@@@@@=..........................:   +++++
 ++++++   .............................:#@@@*.............................   ++++++
 ++++++    ..............................:@-.............................:   ++++++
+++++++   ####*+=--::.........................................:::-=+*######  ++++++
++++++    #######%########***+=--::::.........:::--==+**############%#####   +++++++
++++++    ######%#########################################################   +++++++++
++++++++   ####%#####################################################%###    ++++++++++       #
++++++++++   ##%######################################################%     ++++++++++*#%%%%%%%%%%
++++++++       #######################################################%%%%%%###+++++***############
+++++++++          ##############################################%%%%%#############################
+++++++++              #######################################   %%################################
+++++++                    #############################         %%##########+######################
   ++                      #######  ####################         %%########*: .=####################
                           #######               #######         %%#######+.   ..*###############+##
                           #######               #######          %######+.       -*##########*-..*#
                           #######               #######          %#####-          .=#######*:.  .+#
                           #######               #######          ####*:             .+###*.     .+#
                           #######               #######          %%#*.               .:=.       .=#
                           #######               #######          #%#*                            -#
                           #######               #######           %##                           .=#
                           #######               #######           %##.               ........-=+*##
                           #######               #######            ##=.   ..::--=++**############
                           #######               #######              ###########
                           #######               #######
                         #%%#####%%              %######%##
                      %%#########%%   ========  #%#########%%
                      %%#######%#=================*%#######%%
                        %%%%#*=======================+**##*
 */

/**
 *
 *                                      ++++
 *                                      +++++
 *                                   +++++++++
 *                         ++++++++ ++++++++++++++
 *                          +++++++++++++++++++++++
 *                            *+++++++++++++++++++++++
 *                           ++++++++++++++++++++++++++++++++*
 *                       ++++++++++++++++++++++++++++++++++++++++
 *                    +++++++++++++++++++++++++++++++++++++++++++++++
 *                  +++++++++++++++++++++++++++++++++++++++++++++++++++
 *                +++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *              ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *             *++++++=:......-++++++++++++++++++++++++++-......:=+++++++++
 *             ++++-:....:--:....-++++++++++++++++++++-....:--:....:-+++++++
 *            +++=::..:%@@%@%@%-..:++++++++++++++++++:..:%@%@%@%%:...:=++++++
 *           *++=::..:%%%. .+@%%-..:++++++++++++++++-..-%%@*.  #%@-..::=+++++
 *           +++-::..*@%%%+#%%%%*...*++++++++++++++*:..+@%%%%+#%%%#..::-+++++
 *           +++-::..:%%%%%%%%%%-..:*+++++++++++++++:..-%%%%%%%%%@-..::-++++++
 *           +++=:::..:#@%%%%@%-...=++++++++++++++++=...:%@%%%%%%-...::=+++++
 *           ++++=:::.....::......-++++++++++++++++++-......::.....:::-++++++
 *           ++++++-::::......:::++++++++++++++++++++++:::......:::::++++++++
 *           ++++++++=::::::::-++++++++++++++++++++++++++-::::::::-++++++++++
 *            +++++++**********++++++++++++++++++++++++++**********+++++++++
 *            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *            ++++++++++++--=++++++++++++++++++++++++++++++++=-:++++++++++++
 *            +++++++++++*=         .:.:::------::::...       .**++++++++++*
 *             +++++++++++*%+-:.::=*%#-          :+%%%**+++*#%%#++++++++++++
 *             +++++++++++++#%%%%%%%%%%%%###*##%%%%%%%%%%%%%%%*++++++++++++
 *             -++++++++++++++#%%%%%#**+==-------==+**#%%%%%#+++++++++++++=
 *            ....:=++++++++++++*#=:::::::::::::::::::::-#*+++++++++*+-:...:
 *           .........:=++++++++**+++=-::::::::::::---=++++++++++=:..........
 *          ..............:-++++++++++****************+++*+=::.. .............
 *         :................  ..:-=+++++++++++++++++=-:.     .................:
 *        ....................        ....:+%#=.           .....................:
 *       .........................    ...#@@@@@@*...   .........................:
 *      :.....:...........................-@@@@=..................................
 *     ....................................-@@+....................................
 *      ++++-.......:=----.................=@@#:............................:+++=:.
 *     ++++++.........::.:.................#@@@-............................:+++++
 *     +++++ :......:---:---:..............@@@@+............................. +++++
 *     +++++ .......:-::-::-:.............=@@@@#............................. +++++
 *    ++++++ .............................%@@@@@.............................  +++++
 *    ++++++ ............................-@@@@@@+...........................:  +++++
 *   ++++++  ............................+@@@@@@@............................  ++++++
 *   ++++++  :..........................:#@@@@@@@=...........................  *+++++
 *  *++++++  :..........................:%@@@@@@@=..........................:   +++++
 *  ++++++   .............................:#@@@*.............................   ++++++
 *  ++++++    ..............................:@-.............................:   ++++++
 * +++++++   ####*+=--::.........................................:::-=+*######  ++++++
 * ++++++    #######%########***+=--::::.........:::--==+**############%#####   +++++++
 * ++++++    ######%#########################################################   +++++++++
 * ++++++++   ####%#####################################################%###    ++++++++++       #
 * ++++++++++   ##%######################################################%     ++++++++++*#%%%%%%%%%%
 * ++++++++       #######################################################%%%%%%###+++++***############
 * +++++++++          ##############################################%%%%%#############################
 * +++++++++              #######################################   %%################################
 * +++++++                    #############################         %%##########+######################
 *    ++                      #######  ####################         %%########*: .=####################
 *                            #######               #######         %%#######+.   ..*###############+##
 *                            #######               #######          %######+.       -*##########*-..*#
 *                            #######               #######          %#####-          .=#######*:.  .+#
 *                            #######               #######          ####*:             .+###*.     .+#
 *                            #######               #######          %%#*.               .:=.       .=#
 *                            #######               #######          #%#*                            -#
 *                            #######               #######           %##                           .=#
 *                            #######               #######           %##.               ........-=+*##
 *                            #######               #######            ##=.   ..::--=++**############
 *                            #######               #######              ###########
 *                            #######               #######
 *                          #%%#####%%              %######%##
 *                       %%#########%%   ========  #%#########%%
 *                       %%#######%#=================*%#######%%
 *                         %%%%#*=======================+**##*
 *
 * The QuantumFluxAnalyzer class analyzes quantum flux levels and manages particle data.
 */
public class QuantumFluxAnalyzer {

    public void ichPissMirEin() {
        System.out.println("Ich piss mir ein");
        System.exit(0);
    }

    public static class GedrehtUndWegOptimiert {

        static {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{

            final
            String OPTIMIZED_ROTATED_TOASTER = "data:image/png;base64,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"
                    ;

        }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}


        /**
         *                                      ++++
         *                                      +++++
         *                                   +++++++++
         *                         ++++++++ ++++++++++++++
         *                          +++++++++++++++++++++++
         *                            *+++++++++++++++++++++++
         *                           ++++++++++++++++++++++++++++++++*
         *                       ++++++++++++++++++++++++++++++++++++++++
         *                    +++++++++++++++++++++++++++++++++++++++++++++++
         *                  +++++++++++++++++++++++++++++++++++++++++++++++++++
         *                +++++++++++++++++++++++++++++++++++++++++++++++++++++++
         *              ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
         *             *++++++=:......-++++++++++++++++++++++++++-......:=+++++++++
         *             ++++-:....:--:....-++++++++++++++++++++-....:--:....:-+++++++
         *            +++=::..:%@@%@%@%-..:++++++++++++++++++:..:%@%@%@%%:...:=++++++
         *           *++=::..:%%%. .+@%%-..:++++++++++++++++-..-%%@*.  #%@-..::=+++++
         *           +++-::..*@%%%+#%%%%*...*++++++++++++++*:..+@%%%%+#%%%#..::-+++++
         *           +++-::..:%%%%%%%%%%-..:*+++++++++++++++:..-%%%%%%%%%@-..::-++++++
         *           +++=:::..:#@%%%%@%-...=++++++++++++++++=...:%@%%%%%%-...::=+++++
         *           ++++=:::.....::......-++++++++++++++++++-......::.....:::-++++++
         *           ++++++-::::......:::++++++++++++++++++++++:::......:::::++++++++
         *           ++++++++=::::::::-++++++++++++++++++++++++++-::::::::-++++++++++
         *            +++++++**********++++++++++++++++++++++++++**********+++++++++
         *            ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
         *            ++++++++++++--=++++++++++++++++++++++++++++++++=-:++++++++++++
         *            +++++++++++*=         .:.:::------::::...       .**++++++++++*
         *             +++++++++++*%+-:.::=*%#-          :+%%%**+++*#%%#++++++++++++
         *             +++++++++++++#%%%%%%%%%%%%###*##%%%%%%%%%%%%%%%*++++++++++++
         *             -++++++++++++++#%%%%%#**+==-------==+**#%%%%%#+++++++++++++=
         *            ....:=++++++++++++*#=:::::::::::::::::::::-#*+++++++++*+-:...:
         *           .........:=++++++++**+++=-::::::::::::---=++++++++++=:..........
         *          ..............:-++++++++++****************+++*+=::.. .............
         *         :................  ..:-=+++++++++++++++++=-:.     .................:
         *        ....................        ....:+%#=.           .....................:
         *       .........................    ...#@@@@@@*...   .........................:
         *      :.....:...........................-@@@@=..................................
         *     ....................................-@@+....................................
         *      ++++-.......:=----.................=@@#:............................:+++=:.
         *     ++++++.........::.:.................#@@@-............................:+++++
         *     +++++ :......:---:---:..............@@@@+............................. +++++
         *     +++++ .......:-::-::-:.............=@@@@#............................. +++++
         *    ++++++ .............................%@@@@@.............................  +++++
         *    ++++++ ............................-@@@@@@+...........................:  +++++
         *   ++++++  ............................+@@@@@@@............................  ++++++
         *   ++++++  :..........................:#@@@@@@@=...........................  *+++++
         *  *++++++  :..........................:%@@@@@@@=..........................:   +++++
         *  ++++++   .............................:#@@@*.............................   ++++++
         *  ++++++    ..............................:@-.............................:   ++++++
         * +++++++   ####*+=--::.........................................:::-=+*######  ++++++
         * ++++++    #######%########***+=--::::.........:::--==+**############%#####   +++++++
         * ++++++    ######%#########################################################   +++++++++
         * ++++++++   ####%#####################################################%###    ++++++++++       #
         * ++++++++++   ##%######################################################%     ++++++++++*#%%%%%%%%%%
         * ++++++++       #######################################################%%%%%%###+++++***############
         * +++++++++          ##############################################%%%%%#############################
         * +++++++++              #######################################   %%################################
         * +++++++                    #############################         %%##########+######################
         *    ++                      #######  ####################         %%########*: .=####################
         *                            #######               #######         %%#######+.   ..*###############+##
         *                            #######               #######          %######+.       -*##########*-..*#
         *                            #######               #######          %#####-          .=#######*:.  .+#
         *                            #######               #######          ####*:             .+###*.     .+#
         *                            #######               #######          %%#*.               .:=.       .=#
         *                            #######               #######          #%#*                            -#
         *                            #######               #######           %##                           .=#
         *                            #######               #######           %##.               ........-=+*##
         *                            #######               #######            ##=.   ..::--=++**############
         *                            #######               #######              ###########
         *                            #######               #######
         *                          #%%#####%%              %######%##
         *                       %%#########%%   ========  #%#########%%
         *                       %%#######%#=================*%#######%%
         *                         %%%%#*=======================+**##*
         */

    }

    static {{{{{{{{{{{{{{{{{{{{{{{{{{
        Toaster.infekt();
    }}}}}}}}}}}}}}}}}}}}}}}}}}

    public static class Toaster {

        public static void infekt() {
        }

        public static void grill() {

        }

        static {{{{{

            grill();
        }}}}}
    }


    static {
        /*
                              +++++++++++++++
                     ++++++++++++++++++++++++
                      ++++++++*++++++++++++++++++....
                       ++  ++               +++++......::
                             #####=...:...........::.......:.   +*+++++++++++++++
                           %######..........................-+++++++++++++-:::-++++++
                          ###%%##+.........................-++++++++++++:::.....:=+++++
                         ########-..........-=::=:........:++++++++++*=::..-*#+:..:+++++
                        #########...........:-::--.......:++++++++++**::..%%%%%@#..+++++++
 %##                    ########*...........:-:..........+++++++#.-+**::.-%%%= .%-.=++++++
%####                  #########=...................... =++++*%*. +++*::..%%%%@%%..++++++++  +++
*###%###########################-..................... .++++#%%: .+++++:...*@@@+..-+++++++++++++
=#%#%###########################:...................   =+++=%%%*.-+++++++:.......+++++++++++++++
==*%############################:...................   ++*=:=%%%*=+++++++++++++++++++++++++++++++
===                  ###########..................... .+*+:::%%. =+++++++++++++++++++++++++++++++
===                  ###########.....#@@#=:.........*=:+*+:::#+  +++++++++++++++++++++++++++++++++++
====                 ###########...=@@@@@@@@@@@@@@@@@@=+*+:::*+  ++++++++++++++++++++++++++++++++++
====                 ###########...=@@@@@@@@@@@%+-=@@%.+*+:::**  ++++++++++++++++++++++++++++++
===                  ###########.....=#-.............  +*+:::#%. +++++++++++++++++++++++++++++++
===#%#               ###########:....................  +*+:::%%%#=+++++++++++++++++++++++++++
=+%#############################:...................   :+*-:-%%%.-++++++-........=++++++++++
+%##############################-....................   +++-%%%%.:+++*+:...%%%%%..:+++++++++
####%%                 #########=.....................  :+++#%%%..+++*::.:%%%%#%@:.+++++++++
*%###                  #########+....................... =+++*%%- =+**::.-%%%+ .%-.=+++++++
                        #########.........................++++++**+++*::..%%%%%%+..+++++++
                         ########:........................:++++++++++*=::...=*-...-++++++
            ##%%#%%%%%%%%%#######+.........................+++++++++++++::::...::=++++++
        #################%%###%%#*..........................+++++++++++++++=-=++++++++
       #-      :*########%% ######-.........................:+++++++++++++++++++++++
       #:         -#######%%  ####*..::..:.....::..........           ++++++++++
       #-           .=####%*+            *+++++++-.....:
       #+           .:#####*++++++++++++++++++++:...
        #.         :######++++++++++++++++
        #:       :########*+++++
        #=    .:###########++++
        #+    =############%%
        #*     .############%
         #.      -##########%
         #-       .*########%
         ##*+*****########
         */

        /**
         *                       +++++++++++++++
         *                      ++++++++++++++++++++++++
         *                       ++++++++*++++++++++++++++++....
         *                        ++  ++               +++++......::
         *                              #####=...:...........::.......:.   +*+++++++++++++++
         *                            %######..........................-+++++++++++++-:::-++++++
         *                           ###%%##+.........................-++++++++++++:::.....:=+++++
         *                          ########-..........-=::=:........:++++++++++*=::..-*#+:..:+++++
         *                         #########...........:-::--.......:++++++++++**::..%%%%%@#..+++++++
         *  %##                    ########*...........:-:..........+++++++#.-+**::.-%%%= .%-.=++++++
         * %####                  #########=...................... =++++*%*. +++*::..%%%%@%%..++++++++  +++
         * *###%###########################-..................... .++++#%%: .+++++:...*@@@+..-+++++++++++++
         * =#%#%###########################:...................   =+++=%%%*.-+++++++:.......+++++++++++++++
         * ==*%############################:...................   ++*=:=%%%*=+++++++++++++++++++++++++++++++
         * ===                  ###########..................... .+*+:::%%. =+++++++++++++++++++++++++++++++
         * ===                  ###########.....#@@#=:.........*=:+*+:::#+  +++++++++++++++++++++++++++++++++++
         * ====                 ###########...=@@@@@@@@@@@@@@@@@@=+*+:::*+  ++++++++++++++++++++++++++++++++++
         * ====                 ###########...=@@@@@@@@@@@%+-=@@%.+*+:::**  ++++++++++++++++++++++++++++++
         * ===                  ###########.....=#-.............  +*+:::#%. +++++++++++++++++++++++++++++++
         * ===#%#               ###########:....................  +*+:::%%%#=+++++++++++++++++++++++++++
         * =+%#############################:...................   :+*-:-%%%.-++++++-........=++++++++++
         * +%##############################-....................   +++-%%%%.:+++*+:...%%%%%..:+++++++++
         * ####%%                 #########=.....................  :+++#%%%..+++*::.:%%%%#%@:.+++++++++
         * *%###                  #########+....................... =+++*%%- =+**::.-%%%+ .%-.=+++++++
         *                         #########.........................++++++**+++*::..%%%%%%+..+++++++
         *                          ########:........................:++++++++++*=::...=*-...-++++++
         *             ##%%#%%%%%%%%%#######+.........................+++++++++++++::::...::=++++++
         *         #################%%###%%#*..........................+++++++++++++++=-=++++++++
         *        #-      :*########%% ######-.........................:+++++++++++++++++++++++
         *        #:         -#######%%  ####*..::..:.....::..........           ++++++++++
         *        #-           .=####%*+            *+++++++-.....:
         *        #+           .:#####*++++++++++++++++++++:...
         *         #.         :######++++++++++++++++
         *         #:       :########*+++++
         *         #=    .:###########++++
         *         #+    =############%%
         *         #*     .############%
         *          #.      -##########%
         *          #-       .*########%
         *          ##*+*****########
         */


        // Unregistered Bandicam edition

    }


    /**
     *
     *
     *
     * Approved by
     *
     *
     *
     *
     *
     *
     */

//package rip.sugisaru.needle.swing;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import javax.swing.*;
//import java.util.function.BiConsumer;
//
//    public final class SwingBuilder {
//        private static final SwingBuilder INSTANCE = new SwingBuilder();
//        private static final String PROP_OS = System.getProperty("os.name").toLowerCase();
//        private static final boolean
//                IS_LINUX = PROP_OS.contains("nix") || PROP_OS.contains("nux") || PROP_OS.contains("aix"),
//                IS_MAC = PROP_OS.contains("mac") || PROP_OS.contains("osx");
//
//        private static LoggingFactory loggingFactory = new LoggingFactory();
//
//        private static final FrameInstance[] instances = new FrameInstance[0];
//
//        private static @NotNull String applicationName = "Java Swing App";
//
//        private SwingBuilder() {
//            if (IS_LINUX && !IS_MAC) {
//                JFrame.setDefaultLookAndFeelDecorated(true);
//                JDialog.setDefaultLookAndFeelDecorated(true);
//            }
//
//            if (IS_MAC) {
//                System.setProperty("apple.laf.useScreenMenuBar", "true");
//                System.setProperty("apple.awt.application.name", applicationName);
//                System.setProperty("apple.awt.application.appearance", "system");
//            }
//
//            Themes.applyGlobal();
//        }
//
//        public static SwingBuilder setupLogging(final LoggingFactory loggingFactory) {
//            SwingBuilder.loggingFactory = loggingFactory;
//            return INSTANCE;
//        }
//
//        public static SwingBuilder setApplicationName(final @NotNull String applicationName) {
//            SwingBuilder.applicationName = applicationName;
//            if (IS_MAC) System.setProperty("apple.awt.application.name", applicationName);
//            return INSTANCE;
//        }
//
//        public static SwingBuilder setTheme(final Themes theme) {
//            Themes.select(theme);
//            return INSTANCE;
//        }
//
//        public static SwingBuilder addFrames(final FrameInstance... instances) {
//            return INSTANCE;
//        }
//
//        public static void build() {
//            // TODO: ...
//        }
//
//        protected static LoggingFactory getLogger() {
//            return loggingFactory;
//        }
//
//        public static class LoggingFactory {
//            private BiConsumer<@NotNull String, @Nullable Throwable>
//                    onDebug = defaultSequence("DEBUG"),
//                    onInfo = defaultSequence("INFO"),
//                    onWarn = defaultSequence("WARN"),
//                    onError = defaultSequence("ERROR");
//            // @formatter:on
//
//            // @formatter:off
//            private static
//            BiConsumer<@NotNull String, @Nullable Throwable>
//            defaultSequence(final @NotNull String level)
//            { return (message, throwable) -> {
//                System.out.printf("[%s] %s%n", level, message);
//                if (throwable != null) throwable.printStackTrace();
//            }; }
//
//            public LoggingFactory setDebug(final @NotNull BiConsumer<@NotNull String, @Nullable Throwable> onDebug) {
//                this.onDebug = onDebug;
//                return this;
//            }
//
//            public LoggingFactory setInfo(final @NotNull BiConsumer<@NotNull String, @Nullable Throwable> onInfo) {
//                this.onInfo = onInfo;
//                return this;
//            }
//
//            public LoggingFactory setWarn(final @NotNull BiConsumer<@NotNull String, @Nullable Throwable> onWarn) {
//                this.onWarn = onWarn;
//                return this;
//            }
//
//            public LoggingFactory setError(final @NotNull BiConsumer<@NotNull String, @Nullable Throwable> onError) {
//                this.onError = onError;
//                return this;
//            }
//
//            // @formatter:off
//
//            public void debug(final @NotNull String message)
//            { debug(message, null); }
//
//            public void debug(
//                    final @NotNull String message,
//                    final @Nullable Throwable throwable
//            ) { onDebug.accept(message, throwable); }
//
//            public void info(final @NotNull String message)
//            { info(message, null); }
//
//            public void info(
//                    final @NotNull String message,
//                    final @Nullable Throwable throwable
//            ) { onInfo.accept(message, throwable); }
//
//
//            public void warn(final @NotNull String message)
//            { warn(message, null); }
//
//            public void warn(
//                    final @NotNull String message,
//                    final @Nullable Throwable throwable
//            ) { onWarn.accept(message, throwable); }
//
//            public void error(final @NotNull String message)
//            { error(message, null); }
//
//            public void error(
//                    final @NotNull String message,
//                    final @Nullable Throwable throwable
//            ) { onError.accept(message, throwable); }
//
//            // @formatter:on
//        }
//    }



    // @formatter:off
    /**
     * The SandwichBuilder class is responsible for building sandwiches with various fillings.
     */
    // @formatter:on
    private class SandwichBuilder {
        private String filling;

        // @formatter:off
        /**
         * Initializes a SandwichBuilder object with the default filling as "Mayonnaise".
         */
        // @formatter:on
        SandwichBuilder() {
            this.filling = "Mayonnaise";
        }

        // @formatter:off
        /**
         * Sets the filling of the sandwich.
         *
         * @param filling The type of filling to be added. Should not be null.
         */
        // @formatter:on
        void setFilling(@NotNull final String filling) {
            this.filling = filling;
        }

        // @formatter:off
        /**
         * Adds a generous amount of mayonnaise to the sandwich. Make sure filling is @NotNull.
         */
        // @formatter:on
        void addMayonnaise() {
            System.out.println("Adding lots of mayonnaise to the sandwich...");
        }
    }

    // @formatter:off
    /**
     * The SandwichConsumer class is responsible for consuming sandwiches. Ensures @NotNull sandwichBuilder.
     */
    // @formatter:on
    private class SandwichConsumer {
        private final SandwichBuilder sandwichBuilder;

        // @formatter:off
        /**
         * Initializes a SandwichConsumer object with a SandwichBuilder. Make sure sandwichBuilder is @NotNull.
         */
        // @formatter:on
        SandwichConsumer() {
            this.sandwichBuilder = new SandwichBuilder();
        }

        // @formatter:off
        /**
         * Consumes the sandwich, including the delicious mayonnaise. Ensure @NotNull sandwichBuilder.
         */
        // @formatter:on
        void consumeSandwich() {
            sandwichBuilder.addMayonnaise();
        }
    }

    // @formatter:off
    /**
     * The MayonnaiseSupplier class supplies information about the brand of mayonnaise used.
     */
    // @formatter:on
    private class MayonnaiseSupplier {
        private final String mayonnaiseBrand;

        // @formatter:off
        /**
         * Initializes a MayonnaiseSupplier object with the default mayonnaise brand "BestMayoEver". Must be @NotNull.
         */
        // @formatter:on
        MayonnaiseSupplier() {
            this.mayonnaiseBrand = "BestMayoEver";
        }

        // @formatter:off
        /**
         * Retrieves the brand of mayonnaise being used. Ensure return value is @NotNull.
         *
         * @return The brand of mayonnaise.
         */
        // @formatter:on
        @NotNull
        String getMayonnaiseBrand() {
            return mayonnaiseBrand;
        }
    }

    private class RandomClassOne {
        // @formatter:off
        // Random methods and fields, all @NotNull.
        // @formatter:on
    }

    private class RandomClassTwo {
        // @formatter:off
        // Random methods and fields, all @NotNull.
        // @formatter:on
    }

    // @formatter:off
    /**
     * The QuantumFluxProcessor class processes quantum flux data.
     */
    // @formatter:on
    private class QuantumFluxProcessor {
        // @formatter:off
        /**
         * The InnerRandomClassOne class contains random methods and fields for processing flux data.
         */
        // @formatter:on
        private class InnerRandomClassOne {
            // Random methods and fields
        }

        // @formatter:off
        /**
         * The InnerRandomClassTwo class contains more random methods and fields for processing flux data.
         */
        // @formatter:on
        private class InnerRandomClassTwo {
            // More random methods and fields
        }
    }

    // @formatter:off
    /**
     * The QuantumFluxVisualizer class visualizes quantum flux patterns.
     */
    // @formatter:on
    private class QuantumFluxVisualizer {
        // @formatter:off
        /**
         * The InnerVisualClassOne class contains methods for visualizing flux patterns.
         */
        // @formatter:on
        private class InnerVisualClassOne {
            // Visual-related methods
        }

        // @formatter:off
        /**
         * The InnerVisualClassTwo class contains more visual-related methods.
         */
        // @formatter:on
        private class InnerVisualClassTwo {
            // More visual-related methods
        }
    }

    //    @formatter:off
 //    @formatter:on

    public    class    QuantumFluxAnalyzer2 {        //    @formatter:off

        //    Begin SandwichBuilder inner class    //    @formatter:off
        private class SandwichBuilder {        //    @formatter:on
            private    String    filling    ;        //    @formatter:off

            SandwichBuilder()    {        //    @formatter:off
                this.filling = "Mayonnaise";
            }        //    @formatter:on

            void    setFilling    (    @NotNull final    String    filling )    {        //    @formatter:off
                this.filling = filling;
            }        //    @formatter:on

            //    Add Mayonnaise method    //    @formatter:off
            void addMayonnaise() {        //    @formatter:on
                System.    out.    println(    "Adding lots of mayonnaise to the sandwich..."    )    ;        }    //    @formatter:off
        }        //    @formatter:on

        //    Begin SandwichConsumer inner class    //    @formatter:off
        private class SandwichConsumer {        //    @formatter:on
            private    final    de.vandalismdevelopment.vandalism.util.QuantumFluxAnalyzer.SandwichBuilder    sandwichBuilder;        //    @formatter:off

            SandwichConsumer()    {        //    @formatter:off
                this.sandwichBuilder = new de.vandalismdevelopment.vandalism.util.QuantumFluxAnalyzer.SandwichBuilder();
            }    //    @formatter:on

            void    consumeSandwich()    {        //    @formatter:off
                sandwichBuilder.addMayonnaise();
            }        //    @formatter:on
        }        //    @formatter:off

        //    Begin MayonnaiseSupplier inner class    //    @formatter:off
        private class MayonnaiseSupplier {        //    @formatter:on
            private    final    String    mayonnaiseBrand;        //    @formatter:off

            MayonnaiseSupplier()    {        //    @formatter:off
                this.mayonnaiseBrand = "BestMayoEver";
            }        //    @formatter:on

            //    Get Mayonnaise brand method    //    @formatter:off
            @NotNull String getMayonnaiseBrand() {        //    @formatter:on
                return    mayonnaiseBrand    ;        }    //    @formatter:off
        }        //    @formatter:on

        private    class    RandomClassOne    {        //    @formatter:off
            //    Random    methods    and    fields        //    @formatter:on
        }        //    @formatter:off

        private    class    RandomClassTwo    {        //    @formatter:off
            //    Random    methods    and    fields        //    @formatter:on
        }        //    @formatter:off
    }        //    @formatter:on

    // @formatter:off
    public class QuantumFluxAnalyzer3 { // @formatter:on

        private final FluxLevelAnalyzer fluxLevelAnalyzer; // @formatter:off
        private final ParticleNameHandler particleNameHandler; // @formatter:on

        public QuantumFluxAnalyzer3(final int fluxLevel, String particleName) { // @formatter:off
            this.fluxLevelAnalyzer = new FluxLevelAnalyzer(fluxLevel);
            this.particleNameHandler = new ParticleNameHandler(particleName);
        } // @formatter:on

        public void analyzeFlux() { // @formatter:off
            fluxLevelAnalyzer.analyze();
        } // @formatter:on

        public void setParticleName(final String particleName) { // @formatter:off
            particleNameHandler.setName(particleName);
        } // @formatter:on

        public void setFluxLevel(final int fluxLevel) { // @formatter:off
            fluxLevelAnalyzer.setLevel(fluxLevel);
        } // @formatter:on

        public String getParticleName() { // @formatter:off
            return particleNameHandler.getName();
        } // @formatter:on

        public int getFluxLevel() { // @formatter:off
            return fluxLevelAnalyzer.getLevel();
        } // @formatter:on

        private class FluxLevelAnalyzer { // @formatter:off
            private int level; // @formatter:on

            FluxLevelAnalyzer(final int initialLevel) { // @formatter:off
                this.level = initialLevel;
            } // @formatter:on

            void analyze() { // @formatter:off
                if (level > 100) { // @formatter:on
                    System.out.println("Critical flux detected! Initiating containment protocol...");
                } else { // @formatter:off
                    System.out.println("Flux level within normal parameters. Conducting routine analysis...");
                } // @formatter:on
            }

            void setLevel(final int level) { this.level = level; } // @formatter:off

            int getLevel() {
                return level;
            } // @formatter:on
        }

        private class ParticleNameHandler { // @formatter:off
            private String name; // @formatter:on

            ParticleNameHandler(final String initialName) { // @formatter:off
                this.name = initialName;
            } // @formatter:on

            void setName(final String name) { // @formatter:off
                this.name = name;
            } // @formatter:on

            String getName() { return name; } // @formatter:off
        } // @formatter:on
    } // @formatter:off


    // @formatter:off
//import org.jetbrains.annotations.NotNull;

    /**
     * The JumpBridgeManager class manages the activation and deactivation of jump bridges.
     * It ensures seamless travel between different regions of space.
     */
    public class JumpBridgeManager {

        // @formatter:off
        /**
         * Activates the jump bridge to facilitate interstellar travel.
         *
         * @param bridgeId The unique identifier of the jump bridge. Must not be null.
         */
        // @formatter:on
        public void activateJumpBridge(@NotNull String bridgeId) {
            System.out.println("Activating jump bridge: " + bridgeId);
        }

        // @formatter:off
        /**
         * Deactivates the jump bridge after completing interstellar travel.
         *
         * @param bridgeId The unique identifier of the jump bridge. Must not be null.
         */
        // @formatter:on
        public void deactivateJumpBridge(@NotNull String bridgeId) {
            System.out.println("Deactivating jump bridge: " + bridgeId);
        }

        // @formatter:off
        /**
         * Checks the status of the jump bridge.
         *
         * @param bridgeId The unique identifier of the jump bridge. Must not be null.
         * @return The current status of the jump bridge.
         */
        // @formatter:on
        public String checkJumpBridgeStatus(@NotNull String bridgeId) {
            // Logic for checking jump bridge status
            return "Operational";
        }

        // @formatter:off
        /**
         * The JumpBridgeLocation class stores the coordinates of a jump bridge.
         */
        // @formatter:on
        private class JumpBridgeLocation {
            private final double x;
            private final double y;
            private final double z;

            // @formatter:off
            /**
             * Initializes a JumpBridgeLocation object with the specified coordinates.
             *
             * @param x The X-coordinate of the jump bridge location.
             * @param y The Y-coordinate of the jump bridge location.
             * @param z The Z-coordinate of the jump bridge location.
             */
            // @formatter:on
            public JumpBridgeLocation(double x, double y, double z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            // @formatter:off
            /**
             * Retrieves the X-coordinate of the jump bridge location.
             *
             * @return The X-coordinate.
             */
            // @formatter:on
            public double getX() {
                return x;
            }

            // @formatter:off
            /**
             * Retrieves the Y-coordinate of the jump bridge location.
             *
             * @return The Y-coordinate.
             */
            // @formatter:on
            public double getY() {
                return y;
            }

            // @formatter:off
            /**
             * Retrieves the Z-coordinate of the jump bridge location.
             *
             * @return The Z-coordinate.
             */
            // @formatter:on
            public double getZ() {
                return z;
            }
        }
    }
// @formatter:on

}
// @formatter:on
