package pl.thatisit.plotter.drivespace;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinuxDrives implements Drives {

    @Override
    public String getDrive(String path) {
        final var mounts = mountRoots(getSystemMounts());
        var length = 0;
        String result = null;
        for (var mount : mounts) {
            if (path.startsWith(mount)) {
                if (length < mount.length()) {
                    length = mount.length();
                    result = mount;
                }
            }
        }
        return Optional.ofNullable(result)
                .orElseThrow(() -> new IllegalStateException(path + " Cannot be identified as any system mount point"));
    }

    public File findFile(String path, String name) {
        var files = (Collection<File>) FileUtils.listFiles(new File(path), new String[]{"exe"}, true);
        return files.stream()
                .filter(file -> file.getName().equals(name))
                .max(Comparator.comparing(File::getAbsolutePath))
                .orElseThrow(() -> new IllegalStateException("No " + name + " file was found at " + path));
    }

    static List<String> getSystemMounts() {
        try (var fis = new FileInputStream("/proc/mounts")) {
            return Stream.of(IOUtils.toString(fis).split("\n"))
                    .collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Couldn't list mount roots");
    }

    static List<String> mountRoots(List<String> entries) {
        return entries.stream()
                .map(mount -> mount.split(" ")[1])
                .map(LinuxDrives::unescape)
                .collect(Collectors.toList());
    }

    private static String unescape(String source) {
        var pattern = Pattern.compile("\\\\\\d{3}");
        var matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.replaceAll(replacer -> "" + (char) (Integer.parseInt(replacer.group().substring(1, 4), 8)));
        }
        return source;
    }
}
