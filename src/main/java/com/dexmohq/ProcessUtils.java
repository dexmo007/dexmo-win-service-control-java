package com.dexmohq;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ProcessUtils {

    public static Stream<String> exec(String cmd) {
        try (var br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()))) {
            return br.lines().collect(toList()).stream();//this round trip is necessary to dump the input stream
        } catch (IOException e) {
            throw new InternalError(e);
        }
    }

}
