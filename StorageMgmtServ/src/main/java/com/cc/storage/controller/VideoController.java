package com.cc.storage.controller;

import com.cc.storage.model.VideoModel;
import com.cc.storage.model.dao.VideoDAO;
import com.cc.storage.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Video Storage API", description = "API endpoints for managing video uploads and retrievals.")
public class VideoController {

    private final VideoService userStorageService;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public VideoController(VideoService userStorageService) {
        this.userStorageService = userStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video Uploaded Successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or No Authentication Header"),
            @ApiResponse(responseCode = "409", description = "User already has video with same title, or any other conflict")
    })
    @Operation(summary = "Upload a video", description = "Uploads a video file along with an optional thumbnail, title, and description.")
    public ResponseEntity<String>
    uploadFile(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam @Parameter(description = "Title of the video", required = true) String title,
            @RequestParam @Parameter(description = "Description of the video", required = true) String description,
            @RequestPart(required = false) @Parameter(description = "Optional thumbnail image for the video", required = false) MultipartFile thumbnail,
            @RequestPart @Parameter(description = "Video file to be uploaded", required = true) MultipartFile video
    ) {
        logger.info("/api/storage/upload endpoint called");
        return userStorageService.uploadFile(authorizationHeader, new VideoDAO(title, description), thumbnail, video);
    }


    @GetMapping(value = "/get/{videoId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video Retrieved Successfully", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "409", description = "Video not found against the given Id", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    })
    @Operation(summary = "Get video by ID", description = "Retrieves video metadata and content by its ID.")
    public ResponseEntity<?>
    getVideoByID(
            @PathVariable @Parameter(description = "Unique identifier of the video", required = true) String videoId
    ) {
        logger.info("/api/storage/get endpoint called");
        return userStorageService.getVideoById(videoId);
    }

    @GetMapping(value = "/get-random", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "30 Random Video's data Retrieved Successfully"),
    })
    @Operation(summary = "Get random videos", description = "Retrieves a list of random video metadata.")
    public ResponseEntity<List<VideoModel>> checkStorageUsage() {
        logger.info("/api/storage/get-random endpoint called");
        return userStorageService.getRandomVideos();
    }

    @GetMapping(value = "/videos/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all videos by user", description = "Retrieves all videos uploaded by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video's fetched of given user Retrieved Successfully")
    })
    public ResponseEntity<List<VideoModel>> getVideos(
            @PathVariable @Parameter(description = "Unique identifier of the user", required = true) String userId
    ) {
        logger.info("/api/storage/videos endpoint called");
        return userStorageService.getAllVideosOfUser(userId);
    }


    @GetMapping(value = "/videos/my", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Operation(summary = "Get user's own videos", description = "Retrieves all videos uploaded by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video's data for given user Retrieved Successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401", description = "Invalid or missing authorization header", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
    })
    public ResponseEntity<?> getMyVideos(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        logger.info("/api/storage/videos/my videos endpoint called");
        return userStorageService.getMyVideos(authorizationHeader);
    }


    @DeleteMapping(value = "/delete/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Delete a video", description = "Deletes all videos of the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Videos Deleted Successfully whose Id found"),
    })
    public ResponseEntity<String>
    deleteMyAllVideos(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody List<String> videoIds
    ) {
        logger.info("/api/storage/delete/my endpoint called");
        return userStorageService.deleteMultipleUserVideos(authorizationHeader, videoIds);
    }
}