package CBoss.fileService.v1;

import CBoss.utils.exception.CBossException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
public class FileServiceUtils {


    public JsonArray listDirFilesToJsonArray(String dir) throws CBossException, IOException {

        if (dir == null) {
            throw new CBossException(400, "Please provide valid path.", HttpStatus.BAD_REQUEST);
        }

        Path folderPath = Paths.get(dir);


        if (!folderPath.isAbsolute()) {
            throw new CBossException(400, "Please provide absolute path.", HttpStatus.BAD_REQUEST);
        }

        if (!folderPath.toFile().exists()) {
            throw new CBossException(400, "Path not exists.", HttpStatus.BAD_REQUEST);
        }

        File pathPoint = new File(folderPath.toAbsolutePath().toString());

        File[] files = pathPoint.listFiles();
        assert files != null;

        JsonArray result = new JsonArray();
        for (File dirFile : files) {

            JsonObject pathJson = new JsonObject();

            pathJson.addProperty("name", dirFile.getName());
            pathJson.addProperty("isDir", dirFile.isDirectory());
            pathJson.addProperty("path", dirFile.getAbsolutePath());
            pathJson.addProperty("modified_at", dirFile.lastModified());

            result.add(pathJson);

        }

        return result;
    }


    public Path createDirectory(String target_path) throws Exception {
        Path folderPath = Paths.get(target_path);
        if (!folderPath.isAbsolute()) {
            throw new Exception("Please provide absolute path.");
        }
        Path newDirPath = Files.createDirectories(folderPath);
        return newDirPath.toAbsolutePath();
    }

    @Value("${service.file_service.list.max_file_count}")
    private int max_file_count;

    private List<Path> traverse(List<Path> found, Path current_path, int current_level, int max_level) throws Exception {
        if (current_level > max_level) {
            throw new Exception(String.format("Max allowed level : %s exceeds, please modify the max_level setting or adjust the path.", max_level));
        }
        File current_file = current_path.toFile();
        File[] current_file_filelist = current_file.listFiles();
        assert current_file_filelist != null;

        for (File file : current_file_filelist) {
            if (file.isFile()) {

                if (found.size() > max_file_count) {
                    System.out.println(found.size() + " | " + max_file_count);
                    throw new Exception(String.format("Maximum file count %s exceeded, please check your input and try again.", max_file_count));
                }
                found.add(Paths.get(file.toURI()));
            } else {

                found.add(Paths.get(file.toURI()));
                traverse(found, Paths.get(file.toURI()), current_level += 1, max_level);


            }
        }

        return found;
    }


    public List<Path> getFileFlatTree(String current_path, int current_level, int max_level) throws Exception {

        if (current_path == null) {
            throw new CBossException(400, "Please provide valid path.", HttpStatus.BAD_REQUEST);
        }
        Path start_path = Paths.get(current_path);

        if (!start_path.isAbsolute()) {
            throw new CBossException(400, "Please provide absolute path.", HttpStatus.BAD_REQUEST);
        }

        if (!start_path.toFile().exists()) {
            throw new CBossException(400, "Path not exists.", HttpStatus.BAD_REQUEST);
        }

        ArrayList<Path> found = new ArrayList<>();
        found.add(Paths.get(current_path));
        return traverse(found, start_path, current_level, max_level);
    }
}
