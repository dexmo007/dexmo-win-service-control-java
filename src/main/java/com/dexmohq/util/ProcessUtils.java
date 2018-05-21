package com.dexmohq.util;

import com.dexmohq.cmd.parse.LineParser;
import com.dexmohq.cmd.parse.LineParsing;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.stream.Stream;

import static com.dexmohq.util.StreamUtils.Collectors.streaming;

@UtilityClass
public class ProcessUtils {


    public static ProcessResult<Stream<String>> exec(String cmd) {
        final Process process;
        try {
            process = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            return new ProcessResult<>(null, true, Stream.empty());
        }
        final Stream<String> stream = StreamUtils.fromInputStream(process.getInputStream()).collect(streaming());
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            return new ProcessResult<>(process, true, Stream.empty());//todo input or error stream?
        }
        //this round trip is necessary to dump the input stream
        if (process.exitValue() > 0) {
            return new ProcessResult<>(process, true, StreamUtils.fromInputStream(process.getErrorStream()).collect(streaming()));
        }
        return new ProcessResult<>(process, false, stream);
    }

    public static <T> ProcessResult<T> exec(String cmd, Class<T> resultType) {
        final LineParser<T> lineParser = LineParsing.createLineParser(resultType);
        return exec(cmd, lineParser);
    }

    public static <T> ProcessResult<T> exec(String cmd, LineParser<T> lineParser) {
        return exec(cmd).map(lineParser::parse);
    }

}
