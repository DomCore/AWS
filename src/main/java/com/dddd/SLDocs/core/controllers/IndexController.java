package com.dddd.SLDocs.core.controllers;


import com.dddd.SLDocs.core.entities.Faculty;
import com.dddd.SLDocs.core.servImpls.*;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.dddd.SLDocs.core.utils.email.Sender.rfc5987_encode;

@Controller
public class IndexController {
    private final CurriculumServiceImpl curriculumService;
    private final DepartmentServiceImpl departmentService;
    private final DisciplineServiceImpl disciplineService;
    private final FacultyServiceImpl facultyService;
    private final ProfessorServiceImpl professorService;
    private final SpecialtyServiceImpl specialtyService;


    public IndexController(CurriculumServiceImpl curriculumService, DepartmentServiceImpl departmentService,
                           DisciplineServiceImpl disciplineService, FacultyServiceImpl facultyService,
                           ProfessorServiceImpl professorService, SpecialtyServiceImpl specialtyService) {
        this.curriculumService = curriculumService;
        this.departmentService = departmentService;
        this.disciplineService = disciplineService;
        this.facultyService = facultyService;
        this.professorService = professorService;
        this.specialtyService = specialtyService;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String viewIndexPage(Model model) {
        String easfn="";
        String pslfn="";
        String ipzipfn="";
        try {
            easfn = facultyService.ListAll().get(0).getEas_filename();
            pslfn = facultyService.ListAll().get(0).getPsl_filename();
            ipzipfn = facultyService.ListAll().get(0).getIpzip_filename();
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("no faculties");
        }
        boolean eas = true;
        boolean psl = true;
        boolean ip = true;
       if(easfn == null || easfn.isEmpty() || easfn.equals("")){
            eas = false;
        }
       if(pslfn==null || pslfn.isEmpty() || pslfn.equals("")){
            psl = false;
        }
        if(ipzipfn==null || ipzipfn.isEmpty() || ipzipfn.equals("")){
            ip = false;
        }
        model.addAttribute("eas", eas);
        model.addAttribute("psl", psl);
        model.addAttribute("ip", ip);
        return "index";
    }
    @RequestMapping(path = "/delete", method = RequestMethod.GET)
    public String viewDeletePage() {
        return "deleteAllConf";
    }
    @RequestMapping(path = "/deleteWOProfs", method = RequestMethod.GET)
    public String viewDeleteWOprofsPage() {
        return "deleteWOprofsConf";
    }

    @RequestMapping(path = "/deleteAll", method = RequestMethod.GET)
    public String deleteAll(Model model) {
        curriculumService.deleteAll();
        departmentService.deleteAll();
        disciplineService.deleteAll();
        facultyService.deleteAll();
        professorService.deleteAll();
        specialtyService.deleteAll();
        return "deleteAllSuc";
    }


    @RequestMapping(path = "/deleteWithoutProfs", method = RequestMethod.GET)
    public String deleteWithoutProfs(Model model) {
        curriculumService.deleteAll();
        departmentService.deleteAll();
        disciplineService.deleteAll();
        facultyService.deleteAll();
        specialtyService.deleteAll();
        return "deleteWOprofsSuc";
    }

    @GetMapping("/downloadEAS")
    public ResponseEntity downloadEAS() throws IOException {
        Faculty faculty = facultyService.ListAll().get(0);
        File file = new File(faculty.getEas_filename());
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rfc5987_encode(faculty.getEas_filename()) + "\"")
                .body(FileUtils.readFileToByteArray(file));
    }

    @GetMapping("/downloadPSL")
    public ResponseEntity downloadPSL() throws IOException {

        Faculty faculty = facultyService.ListAll().get(0);
        File file = new File(faculty.getPsl_filename());
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rfc5987_encode(faculty.getPsl_filename()) + "\"")
                .body(FileUtils.readFileToByteArray(file));
    }

    @GetMapping(value = "/downloadProfZip", produces = "application/zip")
    public ResponseEntity downloadIpZip() throws IOException {
        File file = new File(facultyService.ListAll().get(0).getIpzip_filename());
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rfc5987_encode(facultyService.ListAll().get(0).getIpzip_filename()) + "\"")
                .body(FileUtils.readFileToByteArray(file));
    }
}
