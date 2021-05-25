package com.dddd.SLDocs.core.controllers;

import com.dddd.SLDocs.core.entities.StudyLoad;
import com.dddd.SLDocs.core.servImpls.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Objects;

@Controller
public class ReadExcelController {

    private final CurriculumServiceImpl curriculumService;

    private final DepartmentServiceImpl departmentService;

    private final DisciplineServiceImpl disciplineService;

    private final FacultyServiceImpl facultyService;

    private final ProfessorServiceImpl professorService;

    public ReadExcelController(CurriculumServiceImpl curriculumService, DepartmentServiceImpl departmentService,
                               DisciplineServiceImpl disciplineService, FacultyServiceImpl facultyService,
                               ProfessorServiceImpl professorService) {
        this.curriculumService = curriculumService;
        this.departmentService = departmentService;
        this.disciplineService = disciplineService;
        this.facultyService = facultyService;
        this.professorService = professorService;
    }

    @PostMapping("/upload")
    public String uploadToLocalFileSystem(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Paths.get(fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read(file.getOriginalFilename());
    }

    @RequestMapping("/read")
    public String read(@RequestParam("path") String path) throws IOException {

        String[] parts = path.split("\\.");
        if(!parts[1].equals("xlsx")){
            return "errors/bad_file";
        }

        FileInputStream fis = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        try{
            String space_regex = "\\s+";
            String[] res =workbook.getSheetAt(0).getRow(3).getCell(3).toString().split(space_regex);
            if(!res[0].equals("ПЛАН")){
                return "errors/bad_file";
            }
        }catch(Exception ex){
            return "errors/bad_file";
        }
        long m = System.currentTimeMillis();
        for (int i = 0; i < 2; i++) {
            readSheet(workbook, i);
        }
        System.out.println(System.currentTimeMillis() - m);

        return "redirect:/";
    }


    public void readSheet(XSSFWorkbook workbook, int sheet_num) throws IOException {
        XSSFSheet sheet = workbook.getSheetAt(sheet_num);
        DataFormatter df = new DataFormatter();
        StudyLoad studyLoad = new StudyLoad();
        int rows = 10;
        XSSFRow row;
        while (true) {

            row = sheet.getRow(rows);
            try {
                if (df.formatCellValue(row.getCell(3)).equals("")) {
                    break;
                }
                rows++;
            } catch (NullPointerException ex) {
                break;
            }
        }
        int cols = sheet.getRow(0).getLastCellNum();

        ArrayList<Object> arrayList = new ArrayList<>();
        ArrayList<Object> dep_fac_sem = new ArrayList<>();
        try {
            row = sheet.getRow(6);
            dep_fac_sem.add(row.getCell(0));
            dep_fac_sem.add(row.getCell(16));
            if (row.getCell(31).toString().equals("ОСІННІЙ")) {
                dep_fac_sem.add("1");
            } else {
                dep_fac_sem.add("2");
            }

            String space_regex = "\\s+";
            String[] res = workbook.getSheetAt(2).getRow(0).getCell(0).toString().split(space_regex);
            StringBuffer stringBuffer = new StringBuffer();
            for (int p = 0; p < 2; p++) {
                String[] values = dep_fac_sem.get(p).toString().split(space_regex);

                for (int i = 1; i < values.length; i++) {
                    stringBuffer.append(values[i]).append(" ");
                }
                dep_fac_sem.set(p, stringBuffer);
                stringBuffer = new StringBuffer();
            }
            row = sheet.getRow(3);
            String[] values;
            for (int pp = 0; pp < row.getLastCellNum(); pp++) {
                if (!(row.getCell(pp) == null)) {
                    if (!(row.getCell(pp).getStringCellValue().equals(""))) {
                        values = row.getCell(pp).getStringCellValue().split(space_regex);
                        dep_fac_sem.add(values[6]);
                    }
                }
            }
            dep_fac_sem.add(res[0]);

            if (facultyService.findByName(dep_fac_sem.get(1).toString()) == null) {
                studyLoad.getDepartment().setName(dep_fac_sem.get(0).toString());
                studyLoad.getFaculty().setName(dep_fac_sem.get(1).toString());
                studyLoad.getFaculty().getDepartments().add(studyLoad.getDepartment());
                studyLoad.getDepartment().setFaculty(studyLoad.getFaculty());
                facultyService.save(studyLoad.getFaculty());
                departmentService.save(studyLoad.getDepartment());
            }

            for (int r = 10; r < rows; r++) {
                row = sheet.getRow(r);
                for (int c = 0; c < cols + 1; c++) {

                    XSSFCell cell = row.getCell(c);
                    if (cell == null) {
                        arrayList.add("");
                    } else {
                        switch (cell.getCellType()) {
                            case STRING:
                                arrayList.add(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                arrayList.add(cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                arrayList.add(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                switch (cell.getCachedFormulaResultType()) {
                                    case NUMERIC:
                                        arrayList.add(cell.getNumericCellValue());
                                        break;
                                    case STRING:
                                        arrayList.add(cell.getStringCellValue());
                                        break;
                                }
                                break;
                            default:
                                arrayList.add("");
                                break;
                        }
                    }
                }
                studyLoad.getCurriculum().setCourse(arrayList.get(3).toString());
                studyLoad.getCurriculum().setStudents_number(arrayList.get(4).toString());
                studyLoad.getCurriculum().setSemester(dep_fac_sem.get(2).toString());
                studyLoad.getCurriculum().setGroup_names(arrayList.get(5).toString());
                studyLoad.getCurriculum().setNumber_of_subgroups(arrayList.get(7).toString());
                studyLoad.getCurriculum().setLec_hours(arrayList.get(16).toString());
                studyLoad.getCurriculum().setConsult_hours(arrayList.get(17).toString());
                studyLoad.getCurriculum().setLab_hours(arrayList.get(18).toString());
                studyLoad.getCurriculum().setPract_hours(arrayList.get(19).toString());
                studyLoad.getCurriculum().setInd_task_hours(arrayList.get(20).toString());
                studyLoad.getCurriculum().setCp_hours(arrayList.get(21).toString());
                studyLoad.getCurriculum().setZalik_hours(arrayList.get(22).toString());
                studyLoad.getCurriculum().setExam_hours(arrayList.get(23).toString());
                studyLoad.getCurriculum().setDiploma_hours(arrayList.get(24).toString());
                studyLoad.getCurriculum().setDec_cell(arrayList.get(25).toString());
                studyLoad.getCurriculum().setNdrs(arrayList.get(26).toString());
                studyLoad.getCurriculum().setAspirant_hours(arrayList.get(27).toString());
                studyLoad.getCurriculum().setPractice(arrayList.get(28).toString());
                studyLoad.getCurriculum().setOther_forms_hours(arrayList.get(30).toString());
                studyLoad.getCurriculum().setYear(dep_fac_sem.get(4).toString());
                studyLoad.getCurriculum().setDepartment(departmentService.findByName(dep_fac_sem.get(0).toString()));


                if(arrayList.get(35).toString().equals("курсові")) {
                    System.out.println("Викладач: " + arrayList.get(35).toString());
                    System.out.println("empty: " + !(arrayList.get(35).toString().equals("") || arrayList.get(35).toString().isEmpty()));
                    System.out.println("kursov: " + (arrayList.get(35).toString().equals("курсові")));
                }
                if (professorService.findByName(arrayList.get(35).toString()) == null) {
                    if(!(arrayList.get(35).toString().equals("")||arrayList.get(35).toString().equals("курсові"))){
                        studyLoad.getProfessor().setName(arrayList.get(35).toString());
                        professorService.save(studyLoad.getProfessor());
                        studyLoad.getCurriculum().getProfessors().add(studyLoad.getProfessor());
                    }
                } else {
                    studyLoad.getCurriculum().getProfessors().add(professorService.findByName(arrayList.get(35).toString()));
                }

                if (disciplineService.findByName(arrayList.get(1).toString()) == null) {
                    studyLoad.getDiscipline().setName(arrayList.get(1).toString());
                    disciplineService.save(studyLoad.getDiscipline());
                    studyLoad.getCurriculum().getDisciplines().add(studyLoad.getDiscipline());
                } else {
                    studyLoad.getCurriculum().getDisciplines().add(disciplineService.findByName(arrayList.get(1).toString()));
                }


                curriculumService.save(studyLoad.getCurriculum());
                arrayList = new ArrayList<>();
                studyLoad = new StudyLoad();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println("end of the file or NPE");
        }
    }
}
