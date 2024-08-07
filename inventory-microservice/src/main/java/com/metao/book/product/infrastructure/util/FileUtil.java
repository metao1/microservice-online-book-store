package com.metao.book.product.infrastructure.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

    public static Stream<String> readDataFromResources(Path path) throws IOException {
        return Files.lines(path, StandardCharsets.UTF_8);
    }
}
