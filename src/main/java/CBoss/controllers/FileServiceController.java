package CBoss.controllers;


import CBoss.configClasses.CBossDeleteJobConfig;
import CBoss.configClasses.CBossJobConfig;
import CBoss.fileService.v1.FileServiceUtils;
import CBoss.jobService.*;
import CBoss.netapp.NetappRestService;
import CBoss.utils.commons.InputUtils;
import CBoss.utils.exception.CBossException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@ControllerAdvice
@RequestMapping("api/file_service")
public class FileServiceController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileServiceController.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private NetappRestService netappRestService;

    @Autowired
    private FileServiceUtils fileServiceUtils;

    @PostMapping(value = "createDir", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createDir(@RequestBody Map<String, String> bodyMap) {
        try {

            String path = bodyMap.get("path");
            if (path == null) {
                throw new Exception("[createDir] Path cannot be empty.");
            }

            Path result = fileServiceUtils.createDirectory(path);

            JsonObject res = new JsonObject();
            res.addProperty("status", "success");
            res.addProperty("dir_path", result.toAbsolutePath().toString());

            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tree(
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String full,
            @RequestParam(required = false) String max_depth) {

        int max_int_depth = 2;

        if (max_depth != null && InputUtils.isInteger(max_depth)) {
            max_int_depth = Integer.parseInt(max_depth);
        }


        try {
            List<Path> found = fileServiceUtils.getFileFlatTree(path, 0, max_int_depth);
            JsonArray foundJson = new JsonArray();

            int dir_count = 0;
            int file_count = 0;

            for (Path file_path : found) {
                JsonObject fileJson = new JsonObject();

                boolean isDir = file_path.toFile().isDirectory();

                if (isDir) {
                    dir_count++;
                } else {
                    file_count++;
                }

                fileJson.addProperty("name", file_path.toFile().getName());
                fileJson.addProperty("isDir", isDir);
                fileJson.addProperty("size_bytes", file_path.toFile().isFile() ? Files.size(file_path) : null);
                fileJson.addProperty("paths", file_path.toFile().getAbsolutePath());
                foundJson.add(fileJson);
            }

            JsonObject response = new JsonObject();
            response.addProperty("input_path", path);
            response.addProperty("max_search_depth", max_int_depth);
            response.addProperty("file_count", file_count);
            response.addProperty("dir_count", dir_count);

            if (full != null) {
                response.add("paths", foundJson);
            }

            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (CBossException e) {
            throw e;
        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping(value = "batch_delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteDir(@RequestBody Map<String, String> bodyMap) {

        try {
            String path = bodyMap.get("path");
            int max_delete_level = InputUtils.isInteger(bodyMap.get("max_delete_level")) ? Integer.parseInt(bodyMap.get("max_delete_level")) : 2;
            if (path == null) {
                throw new Exception("Path cannot be empty.");
            }

            if (Paths.get(path).toFile().exists()) {
                CBossJob deleteJob = new CBossJob(CBossJobType.DELETE, new CBossDeleteJobConfig(path, max_delete_level));
                String job_id = jobService.addNewJob(deleteJob);
                JsonObject response = new JsonObject();
                response.addProperty("job_id", job_id);
                return new ResponseEntity<>(response.toString(), HttpStatus.ACCEPTED);
            } else {
                throw new CBossException(400, String.format("%s not exists.", path), HttpStatus.BAD_REQUEST);
            }


        } catch (CBossException e) {
            throw e;
        } catch (Exception e) {
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "clone", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cloneFile(@RequestBody Map<String, String> bodyMap) {

        try {
            String source_path = bodyMap.get("source_path");
            String destination_path = bodyMap.get("destination_path");

            if (source_path == null) {
                throw new Exception("Source_path cannot be empty.");
            }
            if (destination_path == null) {
                throw new Exception("Destination_path cannot be empty.");
            }

            CBossJob cloneJob = new CBossJob(CBossJobType.CLONE, new CBossJobConfig(source_path, destination_path));
            JsonObject response = new JsonObject();

            String job_id = jobService.addNewJob(cloneJob);
            response.addProperty("job_id", job_id);
            return new ResponseEntity<>(response.toString(), HttpStatus.ACCEPTED);
        } catch (CBossException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CBossException(500, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}