package com.example.sentisumassignment.controller;

import com.example.sentisumassignment.model.SalarySurvey2;
import com.example.sentisumassignment.service.SalarySurvey2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/salary/2/")
@Slf4j
public class SalarySurvey2Controller {

    @Autowired
    private SalarySurvey2Service salarySurvey2Service;

    @PostMapping("/import")
    public ResponseEntity<String> importData(@RequestParam MultipartFile file) throws Exception {
        try {
            return new ResponseEntity<>(salarySurvey2Service.importData(file), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error occurred while importing data salarySurvey1" + ex.getMessage());
            throw new Exception(ex);
        }
    }

    @GetMapping("/compensation_data")
    public ResponseEntity<List<SearchHit<SalarySurvey2>>> getCompensationDataByFilter(@RequestParam HashMap<String, String> requestMap) throws Exception {
        try {
            return new ResponseEntity<>(salarySurvey2Service.getCompensationDataByFilter(requestMap), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error occurred while getting compensation data" + ex.getMessage());
            throw new Exception(ex);
        }
    }

    @GetMapping("/sparse")
    public ResponseEntity<SalarySurvey2> getSparseFieldSet (@RequestParam(name = "fields") String fields) throws Exception {
        try {
            return new ResponseEntity<>(salarySurvey2Service.getSparseFieldSet(fields), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error while getting SparseFieldSet " + ex);
            throw new Exception(ex);
        }
    }
}
