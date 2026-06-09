package com.company.training.exam.service;

import com.company.training.common.config.TrainingProperties;
import com.company.training.common.enums.ExamResult;
import com.company.training.course.entity.Course;
import com.company.training.course.service.CourseService;
import com.company.training.employee.entity.Employee;
import com.company.training.employee.service.EmployeeService;
import com.company.training.enrollment.entity.Enrollment;
import com.company.training.enrollment.service.EnrollmentService;
import com.company.training.exam.entity.Exam;
import com.company.training.exam.entity.ExamAttempt;
import com.company.training.exam.entity.ExamQuestion;
import com.company.training.exam.repository.ExamAttemptRepository;
import com.company.training.exam.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CourseService courseService;
    private final EmployeeService employeeService;
    private final EnrollmentService enrollmentService;
    private final TrainingProperties trainingProperties;

    public Exam createExam(Exam exam) {
        if (exam.getId() == null || exam.getId().isEmpty()) {
            exam.setId(UUID.randomUUID().toString());
        }
        if (exam.getPassScore() == null) {
            exam.setPassScore(trainingProperties.getExam().getPassScore());
        }
        if (exam.getTotalScore() == null) {
            exam.setTotalScore(100);
        }
        exam.setCreatedAt(LocalDateTime.now());
        return examRepository.save(exam);
    }

    public Exam getExam(String id) {
        return examRepository.findById(id);
    }

    public Exam getExamByCourse(String courseId) {
        return examRepository.findByCourseId(courseId);
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam publishExam(String examId) {
        Exam exam = examRepository.findById(examId);
        if (exam == null) {
            throw new IllegalArgumentException("考试不存在: " + examId);
        }
        exam.setPublished(true);
        return examRepository.save(exam);
    }

    public Exam updateExam(String id, Exam exam) {
        Exam existing = examRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("考试不存在: " + id);
        }
        exam.setId(id);
        return examRepository.save(exam);
    }

    public void deleteExam(String id) {
        examRepository.deleteById(id);
    }

    public ExamAttempt startExam(String examId, String employeeId) {
        Exam exam = examRepository.findById(examId);
        if (exam == null) {
            throw new IllegalArgumentException("考试不存在: " + examId);
        }
        if (!exam.isPublished()) {
            throw new IllegalStateException("考试未发布，无法参加");
        }

        Employee employee = employeeService.getEmployee(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("员工不存在: " + employeeId);
        }

        Enrollment enrollment = enrollmentService.getEnrollmentByCourseAndEmployee(
                exam.getCourseId(), employeeId);
        if (enrollment == null) {
            throw new IllegalStateException("您未报名此课程");
        }

        int attemptCount = examAttemptRepository.countAttempts(examId, employeeId);
        int maxRetries = trainingProperties.getExam().getMaxRetries();
        if (attemptCount > maxRetries) {
            throw new IllegalStateException("已达到最大考试次数限制");
        }

        ExamAttempt latest = examAttemptRepository.findLatestAttempt(examId, employeeId);
        if (latest != null && latest.getResult() == ExamResult.PASSED) {
            throw new IllegalStateException("您已通过该考试，无需重复考试");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setId(UUID.randomUUID().toString());
        attempt.setExamId(examId);
        attempt.setCourseId(exam.getCourseId());
        attempt.setEmployeeId(employeeId);
        attempt.setAttemptNumber(attemptCount + 1);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setResult(ExamResult.NOT_TAKEN);
        attempt.setRetake(attemptCount > 0);

        return examAttemptRepository.save(attempt);
    }

    public ExamAttempt submitExam(String attemptId, Map<String, String> answers) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId);
        if (attempt == null) {
            throw new IllegalArgumentException("考试记录不存在: " + attemptId);
        }
        if (attempt.getResult() != ExamResult.NOT_TAKEN) {
            throw new IllegalStateException("该考试已提交");
        }

        Exam exam = examRepository.findById(attempt.getExamId());
        if (exam == null) {
            throw new IllegalArgumentException("关联考试不存在");
        }

        int score = calculateScore(exam, answers);

        attempt.setAnswers(answers);
        attempt.setScore(score);
        attempt.setSubmitTime(LocalDateTime.now());
        attempt.setResult(score >= exam.getPassScore() ? ExamResult.PASSED : ExamResult.FAILED);

        return examAttemptRepository.save(attempt);
    }

    private int calculateScore(Exam exam, Map<String, String> answers) {
        if (exam.getQuestions() == null || exam.getQuestions().isEmpty()) {
            return 0;
        }
        int score = 0;
        for (ExamQuestion question : exam.getQuestions()) {
            String userAnswer = answers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectAnswer())) {
                score += question.getScore() != null ? question.getScore() : 0;
            }
        }
        return score;
    }

    public boolean canRetake(String examId, String employeeId) {
        ExamAttempt latest = examAttemptRepository.findLatestAttempt(examId, employeeId);
        if (latest == null) {
            return true;
        }
        if (latest.getResult() == ExamResult.PASSED) {
            return false;
        }
        int attemptCount = examAttemptRepository.countAttempts(examId, employeeId);
        return attemptCount <= trainingProperties.getExam().getMaxRetries();
    }

    public ExamAttempt getAttempt(String id) {
        return examAttemptRepository.findById(id);
    }

    public List<ExamAttempt> getAttemptsByExam(String examId) {
        return examAttemptRepository.findByExamId(examId);
    }

    public List<ExamAttempt> getAttemptsByExamAndEmployee(String examId, String employeeId) {
        return examAttemptRepository.findByExamIdAndEmployeeId(examId, employeeId);
    }

    public List<ExamAttempt> getAttemptsByCourseAndEmployee(String courseId, String employeeId) {
        return examAttemptRepository.findByCourseIdAndEmployeeId(courseId, employeeId);
    }

    public List<ExamAttempt> getAttemptsByEmployee(String employeeId) {
        return examAttemptRepository.findByEmployeeId(employeeId);
    }

    public ExamAttempt getLatestAttempt(String examId, String employeeId) {
        return examAttemptRepository.findLatestAttempt(examId, employeeId);
    }

    public boolean hasPassed(String courseId, String employeeId) {
        List<ExamAttempt> attempts = examAttemptRepository.findByCourseIdAndEmployeeId(courseId, employeeId);
        return attempts.stream().anyMatch(a -> a.getResult() == ExamResult.PASSED);
    }
}
