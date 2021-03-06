package pl.fbp.osk.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fbp.osk.entity.Instructor;
import pl.fbp.osk.entity.Lecture;
import pl.fbp.osk.entity.Participant;
import pl.fbp.osk.service.InstructorService;
import pl.fbp.osk.service.LectureService;
import pl.fbp.osk.service.ParticipantService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/osk/lectures")
public class LectureController {
    @Autowired
    private LectureService lectureService;
    @Autowired
    private InstructorService instructorService;
    @Autowired
    private ParticipantService participantService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<Lecture>> getAllLectures() {
        return ResponseEntity.ok(lectureService.findAll());
    }
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Lecture> getLectureById(@PathVariable Long id) {
        Optional<Lecture> lecture = lectureService.findById(id);
        return ResponseEntity.ok(lecture.get());
    }
    @GetMapping(value = "/instructor/{instructorId}/get_lectures")
    public ResponseEntity<List<Lecture>> getLectureByInstructor(@PathVariable(value = "instructorId") Long instructorId) {
        Optional<Instructor> getinstructor = instructorService.findById(instructorId);
        if(getinstructor.isPresent()) {
            Instructor instructor = getinstructor.get();
            return ResponseEntity.ok(lectureService.findByInstructor(instructor));
        }
        else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    @GetMapping(value = "/participant/{participantId}/get_lectures")
    public ResponseEntity<List<Lecture>> getLectureByParticipant(@PathVariable(value = "participantId") Long participantId) {
        Optional<Participant> getparticipant = participantService.findById(participantId);
        if(getparticipant.isPresent()) {
            Participant participant = getparticipant.get();
            return ResponseEntity.ok(lectureService.findByParticipant(participant));
        }
        else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    @GetMapping(value = "/participant/{participantId}/get_lecture_sum_hour")
    public ResponseEntity<String> getSumHourLectureByParticipant(@PathVariable(value = "participantId") Long participantId) {
        Optional<Participant> getparticipant = participantService.findById(participantId);
        if(getparticipant.isPresent()) {
            Participant participant = getparticipant.get();
            List<Lecture> lp = lectureService.findByParticipant(participant);
            Integer sumHour = 0;
            for(Lecture l : lp) {
                sumHour += l.getSumHour();
            }
            return ResponseEntity.ok("Ustalono/odbyto " + sumHour + " godzin wykladow");
        }
        else {
            return ResponseEntity.ok("Brak takiego kursanta");
        }
    }

    @PostMapping("/instructor/determine/{instructorId}/{participantId}")
    public Lecture newLecture(@PathVariable(value = "instructorId") Long instructorId,
                              @PathVariable(value = "participantId") Long participantId,
                              @RequestBody Lecture lecture) {
        Optional<Instructor> instructor = instructorService.findById(instructorId);
        lecture.setInstructor(instructor.get());
        Optional<Participant> participant = participantService.findById(participantId);
        lecture.setParticipant(participant.get());
        return lectureService.createLecture(lecture);
    }

    @PatchMapping("/instructor/update/{lectureId}")
    public ResponseEntity<Lecture> updateLecture(@RequestBody Map<String, Object> updates,
                                                 @PathVariable Long lectureId) {
        Optional<Lecture> updatedLecture = lectureService.updateLecture(updates, lectureId);
        return ResponseEntity.of(updatedLecture);
    }

    @DeleteMapping("/instructor/delete/{lectureId}")
    public void deleteLecture(@PathVariable long lectureId) {
        lectureService.deleteById(lectureId);
    }
}
