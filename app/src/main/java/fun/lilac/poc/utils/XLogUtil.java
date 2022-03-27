package fun.lilac.poc.utils;

import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.ConsolePrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

public class XLogUtil {
    private static final String TAG = "_ECOOL_";
    private static final String LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static void initXLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                .tag(TAG)                                         // Specify TAG, default: "X-LOG"
                .enableThreadInfo()                                    // Enable thread info, disabled by default
                .enableStackTrace(2)                                   // Enable stack trace info with depth 2, disabled by default
                .enableBorder()                                        // Enable border, disabled by default
                .addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
                        "TEST"))
                .build();

        Printer androidPrinter = new AndroidPrinter(true);         // Printer that print the log using android.util.Log
        Printer consolePrinter = new ConsolePrinter();             // Printer that print the log to console using System.out
        Printer filePrinter = new FilePrinter                      // Printer that print(save) the log to file
                .Builder(LOG_DIR)                         // Specify the directory path of log file(s)
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                consolePrinter,
                filePrinter);
    }

    public static void initXLog(String customTag) {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                .tag(customTag)                                         // Specify TAG, default: "X-LOG"
                .enableThreadInfo()                                    // Enable thread info, disabled by default
                .enableStackTrace(2)                                   // Enable stack trace info with depth 2, disabled by default
                .enableBorder()                                        // Enable border, disabled by default
                .addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
                        "TEST"))
                .build();

        Printer consolePrinter = new ConsolePrinter();             // Printer that print the log to console using System.out
        Printer filePrinter = new FilePrinter                      // Printer that print(save) the log to file
                .Builder(LOG_DIR)                         // Specify the directory path of log file(s)
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                consolePrinter,
                filePrinter);
    }

}
